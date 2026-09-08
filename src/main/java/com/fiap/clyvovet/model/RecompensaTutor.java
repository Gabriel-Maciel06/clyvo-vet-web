package com.fiap.clyvovet.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "T_RECOMPENSA_TUTOR")
public class RecompensaTutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TUTOR_CPF", nullable = false, unique = true, length = 14)
    private String tutorCpf;

    @Column(name = "PONTOS_ACUMULADOS")
    private Integer pontosAcumulados = 0;

    @Column(name = "STREAK_DIAS")
    private Integer streakDias = 0;

    @Column(name = "ULTIMO_CHECKIN")
    private LocalDate ultimoCheckin;

    @Column(name = "DESCONTO_PERCENTUAL")
    private Integer descontoPercentual = 0;

    @Column(name = "NIVEL_FIDELIDADE", length = 30)
    private String nivelFidelidade = "BRONZE";

    public RecompensaTutor() {}

    public RecompensaTutor(Long id, String tutorCpf, Integer pontosAcumulados, Integer streakDias, LocalDate ultimoCheckin, Integer descontoPercentual, String nivelFidelidade) {
        this.id = id;
        this.tutorCpf = tutorCpf;
        this.pontosAcumulados = pontosAcumulados;
        this.streakDias = streakDias;
        this.ultimoCheckin = ultimoCheckin;
        this.descontoPercentual = descontoPercentual;
        this.nivelFidelidade = nivelFidelidade;
    }

    public void adicionarPontos(int pontos) {
        this.pontosAcumulados = (this.pontosAcumulados == null ? 0 : this.pontosAcumulados) + pontos;
        atualizarNivelEDesconto();
    }

    public void atualizarNivelEDesconto() {
        if (pontosAcumulados >= 500) {
            this.nivelFidelidade = "DIAMANTE";
            this.descontoPercentual = 25;
        } else if (pontosAcumulados >= 300) {
            this.nivelFidelidade = "OURO";
            this.descontoPercentual = 20;
        } else if (pontosAcumulados >= 150) {
            this.nivelFidelidade = "PRATA";
            this.descontoPercentual = 15;
        } else {
            this.nivelFidelidade = "BRONZE";
            this.descontoPercentual = 5;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTutorCpf() { return tutorCpf; }
    public void setTutorCpf(String tutorCpf) { this.tutorCpf = tutorCpf; }
    public Integer getPontosAcumulados() { return pontosAcumulados; }
    public void setPontosAcumulados(Integer pontosAcumulados) { this.pontosAcumulados = pontosAcumulados; }
    public Integer getStreakDias() { return streakDias; }
    public void setStreakDias(Integer streakDias) { this.streakDias = streakDias; }
    public LocalDate getUltimoCheckin() { return ultimoCheckin; }
    public void setUltimoCheckin(LocalDate ultimoCheckin) { this.ultimoCheckin = ultimoCheckin; }
    public Integer getDescontoPercentual() { return descontoPercentual; }
    public void setDescontoPercentual(Integer descontoPercentual) { this.descontoPercentual = descontoPercentual; }
    public String getNivelFidelidade() { return nivelFidelidade; }
    public void setNivelFidelidade(String nivelFidelidade) { this.nivelFidelidade = nivelFidelidade; }
}
