# Roteiro de Gravação do Vídeo Demonstrativo (Clyvo Vet)
**Duração Máxima:** Até 10 minutos (Recomendado: 5 a 7 minutos)
**Objetivo:** Demonstrar de forma dinâmica o atendimento de 100% da rubrica técnica da FIAP (Frontend, Flyway, Spring Security e 2 Fluxos Completos).

---

## ⏱️ Cronograma Minuto a Minuto

### 🎬 Bloco 1: Abertura & Visão Geral da Clyvo Vet (0:00 - 1:00)
- **Fala:** "Olá professores e avaliadores! Apresentamos o **Clyvo Vet**, nossa aplicação web desenvolvida em Spring Boot para a 3ª Sprint de Java Advanced. A Clyvo Vet inova ao transformar o cuidado veterinário através da **medicina preventiva e longevidade pet gamificada**."
- **Visual:** Mostrar a tela de login customizada com visual moderno e responsivo.
- **Destaque:** Explicar a proposta de valor: engajar o tutor diariamente através de check-ins, streaks, acúmulo de pontos e descontos reais em consultas, gerando dados contínuos para o veterinário agir preventivamente.

---

### 🗄️ Bloco 2: Arquitetura, Flyway & Banco de Dados (1:00 - 2:00)
- **Visual:** Abrir o IDE e mostrar a pasta `src/main/resources/db/migration/`.
- **Pontos a destacar:**
  1. `V1__criar_tabelas_base.sql`: Tabelas de usuários, tutores, raças, clínicas e pets.
  2. `V2__criar_tabelas_fluxos_clinicos.sql`: Tabelas dos fluxos de check-in, gamificação, badges, triagem e linha do tempo clínica.
  3. `V3__inserir_dados_iniciais.sql`: Carga inicial com senhas criptografadas em BCrypt.
- **Demonstração rápida:** Mostrar o console do Spring Boot iniciando o Flyway sem erros e aplicando as 3 versões com sucesso.

---

### 🔐 Bloco 3: Spring Security & Perfis de Acesso (2:00 - 3:00)
- **Visual:** Mostrar o arquivo `SecurityConfig.java` e a tela `/login`.
- **Demonstração:**
  1. Login com o perfil **Tutor** (`tutor` / `tutor123`).
  2. Mostrar que o Tutor acessa seu painel personalizado com seus pets (Thor).
  3. Tentar acessar a URL restrita de veterinários `/triagem/fila` e mostrar o bloqueio/403 (Acesso Negado), provando a proteção por roles (`ROLE_ADMIN` vs `ROLE_TUTOR`).

---

### 🎮 Bloco 4: Fluxo de Negócio 1 - Gamificação & Check-in Diário (3:00 - 5:00)
- **Visual:** No papel do Tutor, clicar em **Fazer Check-in Diário** (`/checkin/novo`).
- **Demonstração:**
  1. Selecionar o pet "Thor".
  2. Preencher a dieta (ex: "Alimentação Recomendada"), humor ("Enérgico"), tempo de atividade física ("45 minutos") e marcar medicamento administrado.
  3. Submeter o formulário com validações Bean Validation.
  4. Mostrar a notificação de sucesso: **+20 Clyvo Coins recebidas**, streak incrementado e atualização do nível de fidelidade.
  5. Ir na tela **Clyvo Rewards** (`/checkin/recompensas`) para mostrar a tabela de níveis (Bronze, Prata, Ouro, Diamante) e o desconto acumulado em consultas.
  6. Mostrar na **Linha do Tempo do Pet** (`/pets/1`) o evento de check-in registrado cronologicamente.

---

### 🩺 Bloco 5: Fluxo de Negócio 2 - Triagem Preventiva com IA (5:00 - 7:30)
- **Visual:**
  1. Ainda como Tutor, clicar em **Solicitar Triagem IA** para o pet "Luna" informando a queixa respiratória.
  2. Fazer Logout e logar como **Veterinário** (`admin` / `admin123`).
  3. Mostrar o painel médico com os **Alertas de Saúde** e a **Fila de Triagem Preventiva** (`/triagem/fila`).
  4. Clicar em **Avaliar com IA** na triagem da Luna.
  5. Explicar como o algoritmo da Clyvo Vet cruza:
     - Biometria física (peso, temperatura, frequência cardíaca);
     - Predisposição genética da raça cadastrada no banco (ex: Buldogue Francês e síndrome braquiocefálica);
     - Histórico dos check-ins reportados pelo tutor.
  6. Submeter o exame e mostrar a conclusão: cálculo automático do **Escore de Longevidade (0 a 100)**, classificação do **Risco Clínico** e geração do prontuário na linha do tempo.

---

### 🏁 Bloco 6: Conclusão & Encerramento (7:30 - 8:00)
- **Fala:** "Demonstramos todos os requisitos do edital: interface web moderna e sem links quebrados com Thymeleaf e Bootstrap 5.3, versionamento com Flyway, autenticação e autorização robustas com Spring Security e dois fluxos de negócio ricos que resolvem uma dor real do mercado pet. Muito obrigado!"
