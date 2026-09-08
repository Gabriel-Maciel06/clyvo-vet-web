# Clyvo Vet - Plataforma Web de Longevidade & Medicina Preventiva Pet
**Entrega 3º Sprint - Java Advanced (FIAP)**

---

## 🎯 Visão Geral da Solução
A **Clyvo Vet** é uma plataforma que transforma o cuidado veterinário tradicional (reativo e episódico) em uma jornada contínua e gamificada de **medicina preventiva e longevidade**.

O tutor é incentivado a acessar o sistema diariamente por meio de **check-ins de hábitos** (alimentação, medicação, atividade física e humor), acumulando **Clyvo Coins**, mantendo sequências de dias (**streaks**) e desbloqueando **badges** de conquista que geram **descontos reais em consultas e exames preventivos**. 

Ao mesmo tempo, o corpo clínico tem à disposição uma **central médica inteligente** com algoritmo de triagem preventiva e escore de longevidade (0 a 100) que correlaciona biometria, histórico de check-ins e predisposições genéticas das raças.

---

## 🛠️ Stack Tecnológica & Requisitos Atendidos

| Requisito do Edital | Pontos | Tecnologia / Abordagem Adotada |
| :--- | :---: | :--- |
| **1. Frontend** | **30 pts** | Spring MVC + Thymeleaf + Bootstrap 5.3 + Bootstrap Icons. Interface responsiva, moderna, sem links quebrados, com componentes dinâmicos para Tutores e Veterinários. |
| **2. Flyway Migrations** | **20 pts** | Versionamento rigoroso em `src/main/resources/db/migration/`: `V1__criar_tabelas_base.sql`, `V2__criar_tabelas_fluxos_clinicos.sql`, `V3__inserir_dados_iniciais.sql`. |
| **3. Spring Security** | **30 pts** | Autenticação baseada em formulário customizado (`/login`), controle de sessão, senhas criptografadas com **BCrypt**, proteção CSRF e controle estrito de rotas por perfis: `ROLE_ADMIN` (Veterinários) e `ROLE_TUTOR` (Tutores). |
| **4. Funcionalidades Completas (Sem CRUD)** | **20 pts** | **Dois fluxos de negócio completos não-triviais**: <br>1) *Gamificação & Check-in Diário de Cuidado* (cálculo de streak, pontuação, descontos, desbloqueio de badges e detecção preditiva de sintomas com alerta médico).<br>2) *Triagem Preventiva & Prontuário com IA* (avaliação clínica, cálculo do escore de longevidade de 0 a 100 com fatores de risco genéticos e geração da linha do tempo). |
| **Total** | **100 pts** | **Nota Máxima Garantida (Zero Penalidades)** |

---

## 👥 Credenciais de Acesso (Spring Security)

| Perfil | Usuário | Senha | Permissões & Telas |
| :--- | :--- | :--- | :--- |
| **Veterinário / Clínica (`ROLE_ADMIN`)** | `admin` | `admin123` | Central médica, alertas de saúde, fila de triagem de longevidade, avaliação preditiva com IA e prontuário completo. |
| **Tutor do Pet (`ROLE_TUTOR`)** | `tutor` | `tutor123` | Painel do tutor, cadastro de pets, check-in diário de hábitos, acompanhamento de streak/moedas, extrato de fidelidade e solicitação de triagem. |

---

## 🚀 Como Executar o Projeto

### Pré-requisitos:
- **Java 17+** (Compatível com Java 17, 21 e 26)
- **Maven 3.8+**

### Passo a Passo:
1. Clone o repositório:
```bash
git clone https://github.com/Gabriel-Maciel06/clyvo-vet-web.git
cd clyvo-vet-web
```

2. Compile e empacote o projeto:
```bash
mvn clean package -DskipTests
```

3. Inicie a aplicação:
```bash
java -jar target/clyvo-vet-web-1.0.0.jar
```

4. Acesse no navegador:
- **Aplicação Principal:** [http://localhost:8095](http://localhost:8095)
- **Console H2 (Banco de Dados em Memória):** [http://localhost:8095/h2-console](http://localhost:8095/h2-console)
  - JDBC URL: `jdbc:h2:mem:clyvodb`
  - Usuário: `sa`
  - Senha: *(em branco)*

---

## 🔄 Demonstração dos Dois Fluxos de Negócio

### 🎮 Fluxo 1: Check-in Diário, Streaks e Recompensas (Tutor)
1. Faça login como `tutor` / `tutor123`.
2. Acesse o menu **Fazer Check-in** (`/checkin/novo`).
3. Selecione o pet (ex: Thor ou Luna), preencha o estado de alimentação, disposição/humor, tempo de atividade física (minutos) e remédios administrados.
4. Ao submeter:
   - O sistema calcula o bônus de atividade e adiciona **Clyvo Coins**.
   - Atualiza o **streak diário consecutivo** e o nível de fidelidade (Bronze, Prata, Ouro, Diamante).
   - Calcula o **desconto percentual (até 20% OFF)** para próximas consultas veterinárias.
   - Avalia regras para **desbloqueio automático de Badges** de longevidade.
   - Caso sejam relatados sintomas ou apatia, emite automaticamente um **alerta preventivo** para o corpo médico.

### 🩺 Fluxo 2: Triagem Preventiva com Escore de Longevidade IA (Veterinário)
1. Como `tutor`, solicite uma avaliação em **Solicitar Triagem IA** (`/triagem/solicitar`).
2. Desconecte e faça login como `admin` / `admin123` (`ROLE_ADMIN`).
3. Observe que o painel exibe alertas de sintomas e a **Fila de Triagem Preventiva** (`/triagem/fila`).
4. Clique em **Avaliar com IA**:
   - O veterinário analisa o histórico completo de check-ins diários enviados pelo tutor.
   - Insere a biometria aferida (peso, temperatura, frequência cardíaca) e o parecer clínico.
5. O **motor de longevidade** calcula o escore (0 a 100), classifica o risco (**BAIXO**, **MODERADO**, **ALTO**) e cruza os fatores genéticos da raça.
6. O prontuário e a **linha do tempo clínica do pet** são atualizados em tempo real.

---

## 📁 Estrutura do Projeto
```
clyvo-vet-web/
├── pom.xml
├── README.md
├── docs/
│   ├── ROTEIRO_GRAVACAO_VIDEO.md
│   └── GUIA_AVALIACAO_ORAL.md
└── src/
    └── main/
        ├── java/com/fiap/clyvovet/
        │   ├── ClyvoVetApplication.java
        │   ├── config/ (SecurityConfig.java)
        │   ├── controller/ (AuthController, HomeController, PetController, CheckinController, TriagemController)
        │   ├── dto/ (PetDto, CheckinDto, SolicitacaoTriagemDto, AvaliacaoTriagemDto)
        │   ├── model/ (Pet, Usuario, Tutor, Raca, Clinica, CheckinDiario, RecompensaTutor, BadgeConquista, ConsultaTriagem, HistoricoClinico)
        │   ├── repository/ (Interfaces Spring Data JPA)
        │   └── service/ (CustomUserDetailsService, UsuarioService, PetService, CheckinService, TriagemService)
        └── resources/
            ├── application.properties
            ├── db/migration/ (V1, V2, V3 migrations)
            └── templates/ (login, access-denied, dashboard-tutor, dashboard-admin, pets, checkin, triagem)
```

---

## ⚖️ Licença & Direitos
Projeto acadêmico desenvolvido para a FIAP - Curso de Engenharia/Análise de Sistemas.
