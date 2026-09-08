# Guia de Preparação para Avaliação Oral (Banca FIAP)
**Projeto:** Clyvo Vet - Plataforma Web de Longevidade & Gamificação Pet
**Tema:** Java Advanced - 3º Sprint

---

## 💡 1. Pitch do Projeto (30 a 60 segundos)
> "O Clyvo Vet é uma plataforma de medicina preventiva e longevidade pet desenvolvida em Spring Boot. O mercado pet hoje sofre com consultas tardias e episódicas, que encarecem tratamentos e reduzem a sobrevida dos animais. Nossa solução usa gamificação (check-ins diários, streaks e Clyvo Coins com descontos reais em consultas) para criar recorrência e fidelização do tutor, alimentando uma central médica com algoritmo preditivo que antecipa riscos genéticos e comportamentais antes que se tornem emergências graves."

---

## ❓ 2. Perguntas Técnicas Prováveis & Respostas Perfeitas

### P1: Como o Flyway foi configurado e por que ele é crucial?
- **Resposta:** "Utilizamos o Flyway para garantir versionamento declarativo e reprodutibilidade total do banco de dados em qualquer ambiente. As migrações residem em `src/main/resources/db/migration/` com prefixos ordenados (`V1`, `V2`, `V3`). O `V1` estrutura as tabelas mestras, o `V2` define as tabelas de gamificação e triagem clínica, e o `V3` executa o seed de dados com senhas criptografadas em BCrypt. Desabilitamos a criação mágica do Hibernate (`ddl-auto=none`) para que o banco seja estritamente gerido pelas migrações SQL do Flyway."

### P2: Como o Spring Security trata a autenticação e autorização por perfis?
- **Resposta:** "Implementamos um `SecurityConfig` declarativo com `DaoAuthenticationProvider` e `CustomUserDetailsService`. As senhas são protegidas com o algoritmo de hashing `BCryptPasswordEncoder`. Criamos duas roles principais: `ROLE_TUTOR` e `ROLE_ADMIN` (corpo clínico). Rotas médicas como `/triagem/fila` e `/triagem/avaliar/**` são estritamente restritas a administradores (`hasRole('ADMIN')`). Se um tutor tenta acessá-las diretamente, o Spring Security intercepta e retorna HTTP 403 / Access Denied. Além disso, a proteção CSRF está ativa em todos os formulários via Thymeleaf."

### P3: Quais são os dois fluxos completos de negócio (não-CRUD)?
- **Resposta:** 
  1. **Fluxo 1 (Gamificação & Check-in Diário de Cuidado):** O tutor registra dados diários de alimentação, medicação, minutos de exercício e humor do animal. O sistema calcula a pontuação, avalia a sequência consecutiva (`streak`), ajusta o nível de fidelidade (Bronze a Diamante) e percentual de desconto em consultas, avalia regras automáticas para desbloqueio de badges de longevidade e, caso detecte anomalias (como dor ou recusa de apetite), despacha um alerta clínico para a equipe médica.
  2. **Fluxo 2 (Triagem Preventiva e Escore de Longevidade):** O veterinário atende à fila de triagem e preenche os dados do exame físico. O motor da aplicação processa esses dados cruzando com a predisposição genética da raça e com o histórico dos check-ins diários, gerando um **Escore de Longevidade (0 a 100)** e classificação de risco (**BAIXO**, **MODERADO**, **ALTO**), consolidando o prontuário na linha do tempo do pet.

### P4: Como foram evitadas as penalidades de código (SOLID, Clean Code)?
- **Resposta:** "Seguimos rigorosamente os princípios de Clean Code: injeção de dependências estritamente por construtor, separação clara em camadas (Controller, Service, Repository, DTO, Model), isolamento das regras de negócio dentro dos Services (sem lógica pesada em controllers), uso de Bean Validation nos DTOs (`@NotNull`, `@Size`, `@DecimalMin`), sem 'God methods' e sem acoplamento indevido."
