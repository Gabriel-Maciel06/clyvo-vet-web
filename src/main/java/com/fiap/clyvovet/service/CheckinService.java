package com.fiap.clyvovet.service;

import com.fiap.clyvovet.dto.CheckinDto;
import com.fiap.clyvovet.model.*;
import com.fiap.clyvovet.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class CheckinService {

    private final CheckinDiarioRepository checkinRepository;
    private final PetRepository petRepository;
    private final RecompensaTutorRepository recompensaRepository;
    private final BadgeConquistaRepository badgeRepository;
    private final HistoricoClinicoRepository historicoClinicoRepository;

    public CheckinService(CheckinDiarioRepository checkinRepository,
                          PetRepository petRepository,
                          RecompensaTutorRepository recompensaRepository,
                          BadgeConquistaRepository badgeRepository,
                          HistoricoClinicoRepository historicoClinicoRepository) {
        this.checkinRepository = checkinRepository;
        this.petRepository = petRepository;
        this.recompensaRepository = recompensaRepository;
        this.badgeRepository = badgeRepository;
        this.historicoClinicoRepository = historicoClinicoRepository;
    }

    public List<CheckinDiario> listarHistoricoPorPet(Long petId) {
        return checkinRepository.findByPetIdOrderByDataCheckinDesc(petId);
    }

    public List<CheckinDiario> listarAlertasAtivos() {
        return checkinRepository.findByAlertaGeradoTrueOrderByDataCheckinDesc();
    }

    public RecompensaTutor obterOuCriarRecompensa(String tutorCpf) {
        return recompensaRepository.findByTutorCpf(tutorCpf)
                .orElseGet(() -> {
                    RecompensaTutor nova = new RecompensaTutor(null, tutorCpf, 0, 0, null, 0, "BRONZE");
                    return recompensaRepository.save(nova);
                });
    }

    public List<BadgeConquista> listarBadgesPorPet(Long petId) {
        return badgeRepository.findByPetIdOrderByDataConquistaDesc(petId);
    }

    @Transactional
    public CheckinDiario registrarCheckin(CheckinDto dto) {
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new IllegalArgumentException("Pet não encontrado: " + dto.getPetId()));

        LocalDate hoje = LocalDate.now();

        // 1. Evitar check-in duplicado no mesmo dia
        Optional<CheckinDiario> existente = checkinRepository.findByPetIdAndDataCheckin(pet.getId(), hoje);
        if (existente.isPresent()) {
            throw new IllegalStateException("O check-in diário para " + pet.getNome() + " já foi realizado hoje!");
        }

        // 2. Análise de Sintomas / Alerta de Saúde
        boolean geraAlerta = false;
        if (dto.getHumorPet() == HumorPet.APATICO || dto.getHumorPet() == HumorPet.DOR ||
            dto.getAlimentacaoStatus() == AlimentacaoStatus.POUCO_APETITE ||
            (dto.getSintomasObservados() != null && !dto.getSintomasObservados().isBlank())) {
            geraAlerta = true;
        }

        // 3. Salva Check-in
        int pontosGanhos = 10;
        if (dto.getMinutosAtividade() != null && dto.getMinutosAtividade() >= 30) {
            pontosGanhos += 5; // Bônus atividade
        }
        if (Boolean.TRUE.equals(dto.getRemedioAdministrado())) {
            pontosGanhos += 5; // Bônus medicação rigorosa
        }

        CheckinDiario checkin = new CheckinDiario(
                null, pet, hoje, dto.getAlimentacaoStatus(),
                dto.getRemedioAdministrado(), dto.getMinutosAtividade(),
                dto.getHumorPet(), dto.getSintomasObservados(),
                pontosGanhos, geraAlerta
        );
        CheckinDiario salvo = checkinRepository.save(checkin);

        // 4. Atualizar Gamificação do Tutor (Streak, Pontos, Desconto, Nível)
        atualizarGamificacaoTutor(pet.getTutor().getCpf(), hoje, pontosGanhos);

        // 5. Avaliar e Destravar Badges para o Pet
        avaliarBadges(pet);

        // 6. Registrar Evento na Linha do Tempo Clínica
        String desc = String.format("Check-in diário realizado: Humor %s, Dieta %s, Atividade %d min.",
                dto.getHumorPet().getDescricao(), dto.getAlimentacaoStatus().getDescricao(), dto.getMinutosAtividade());
        String conduta = geraAlerta
                ? "ALERTA CLÍNICO: Sintomas ou apatia relatados pelo tutor. Notificação enviada à clínica e veterinário responsável."
                : "Parâmetros diários dentro da normalidade de prevenção e bem-estar.";

        HistoricoClinico hist = new HistoricoClinico(
                null, pet, LocalDateTime.now(),
                geraAlerta ? "ALERTA_SAUDE" : "CHECKIN_DIARIO",
                desc, conduta
        );
        historicoClinicoRepository.save(hist);

        return salvo;
    }

    private void atualizarGamificacaoTutor(String tutorCpf, LocalDate hoje, int pontosGanhos) {
        RecompensaTutor recompensa = obterOuCriarRecompensa(tutorCpf);

        // Streak
        if (recompensa.getUltimoCheckin() != null) {
            long diasDiferenca = ChronoUnit.DAYS.between(recompensa.getUltimoCheckin(), hoje);
            if (diasDiferenca == 1) {
                recompensa.setStreakDias(recompensa.getStreakDias() + 1);
            } else if (diasDiferenca > 1) {
                recompensa.setStreakDias(1); // Perdeu o streak, reinicia
            }
        } else {
            recompensa.setStreakDias(1);
        }

        recompensa.setUltimoCheckin(hoje);
        recompensa.setPontosAcumulados(recompensa.getPontosAcumulados() + pontosGanhos);

        // Cálculo de desconto e Nível
        int streak = recompensa.getStreakDias();
        int pontos = recompensa.getPontosAcumulados();

        if (streak >= 30 || pontos >= 500) {
            recompensa.setNivelFidelidade("DIAMANTE");
            recompensa.setDescontoPercentual(20);
        } else if (streak >= 14 || pontos >= 250) {
            recompensa.setNivelFidelidade("OURO");
            recompensa.setDescontoPercentual(15);
        } else if (streak >= 7 || pontos >= 100) {
            recompensa.setNivelFidelidade("PRATA");
            recompensa.setDescontoPercentual(10);
        } else {
            recompensa.setNivelFidelidade("BRONZE");
            recompensa.setDescontoPercentual(5);
        }

        recompensaRepository.save(recompensa);
    }

    private void avaliarBadges(Pet pet) {
        long totalCheckins = checkinRepository.countByPetId(pet.getId());

        // Badge 1: Primeiro Passo
        if (totalCheckins >= 1 && !badgeRepository.existsByPetIdAndCodigoBadge(pet.getId(), "PRIMEIRO_PASSO")) {
            badgeRepository.save(new BadgeConquista(
                    null, pet, "PRIMEIRO_PASSO", "Primeiro Passo", "bi-award",
                    "Completou o 1º check-in de saúde e longevidade na plataforma!", LocalDate.now()
            ));
        }

        // Badge 2: Tutor Dedicado (3 check-ins)
        if (totalCheckins >= 3 && !badgeRepository.existsByPetIdAndCodigoBadge(pet.getId(), "TUTOR_DEDICADO")) {
            badgeRepository.save(new BadgeConquista(
                    null, pet, "TUTOR_DEDICADO", "Tutor Dedicado", "bi-heart-pulse",
                    "Registrou 3 ou mais check-ins, garantindo histórico contínuo.", LocalDate.now()
            ));
        }

        // Badge 3: Guardião da Longevidade (7 check-ins)
        if (totalCheckins >= 7 && !badgeRepository.existsByPetIdAndCodigoBadge(pet.getId(), "GUARDIAO_LONGEVIDADE")) {
            badgeRepository.save(new BadgeConquista(
                    null, pet, "GUARDIAO_LONGEVIDADE", "Guardião da Longevidade", "bi-shield-check",
                    "1 semana completa de dados biométricos e hábitos saudáveis monitorados!", LocalDate.now()
            ));
        }
    }
}
