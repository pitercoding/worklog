<h1 align="center">Worklog</h1>

<p align="center">
  <strong>Idiomas:</strong><br>
  <a href="README.pt.md">Portugues</a> |
  <a href="README.md">English</a>
</p>

# Worklog

Worklog é uma aplicação **full-stack** para registro diário de atividades em equipes de tecnologia. Ela permite que colaboradores registrem tarefas por **Programa, Time, Linguagem, Atividade e Subatividade**, com contagem automática de tempo por atividade.

O sistema oferece:

- Início de nova atividade com fechamento automático da anterior em andamento  
- Finalização do dia com uma única ação (`Save Day`)  
- Histórico diário com durações e destaque da atividade atual  
- Tratamento centralizado de erros da API e notificações no frontend

## Motivação do Projeto

Este projeto nasceu de uma necessidade real de registro operacional de funcionários no dia a dia.

A proposta foi manter uma **UX simples e objetiva**, com regras de negócio consistentes no backend para garantir:

- Apenas uma atividade aberta por vez  
- Apenas um *workday* por funcionário por data  
- Finalização explícita do dia

## Funcionalidades Atuais

### Backend (Spring Boot + PostgreSQL)

- API REST para fluxo de worklog:
  - `POST /api/worklog/{employeeId}/start`
  - `POST /api/worklog/{employeeId}/save`
  - `GET /api/worklog/{employeeId}?date=YYYY-MM-DD`
- API de lookups:
  - `GET /api/lookups`
  - `GET /api/lookups/subactivities?activityId=...`
- API de funcionários:
  - `GET /api/employees`
- Regras de negócio:
  - Fecha atividade em andamento ao iniciar uma nova  
  - Bloqueia início de atividades após o dia estar finalizado  
  - Valida relação entre Atividade e Subatividade  

- Seed de dados (`data.sql`) com programas, times, linguagens, atividades, subatividades e funcionários  

- Handler global de exceções com resposta de erro padronizada  

- Configuração de CORS para ambiente Angular local  

## Frontend (Angular)

- Página de Worklog com:
  - Seletor de funcionário  
  - Formulário de atividade  
  - Timer em execução e resumo  
  - Histórico de atividades  

- Comportamento do formulário:
  - Subatividades dependentes da atividade selecionada  
  - Limpeza do formulário após `New`  
  - Bloqueio do formulário quando o dia está finalizado  

- UX:
  - Barra global de loading no topo  
  - Toasts para feedback da API  
  - Banner de dia finalizado e estados de botão claros  
  - Layout responsivo com scroll horizontal na tabela em telas pequenas  

## Fluxo de Uso

1. Selecione o funcionário  
2. Escolha Programa, Time, Linguagem, Atividade e Subatividade  
3. Clique em `New` para iniciar o tracking  
4. Inicie outra atividade para fechar automaticamente a anterior  
5. Clique em `Save Day` para finalizar o dia  

## Tecnologias

### Backend

- Java 21  
- Spring Boot  
- Spring Data JPA  
- PostgreSQL  
- Maven  

### Frontend

- Angular (componentes standalone)  
- TypeScript  
- SCSS  
- RxJS  

## Como Rodar Localmente

1. Clone o repositório:

```bash
git clone https://github.com/pitercoding/worklog.git
cd worklog
```

2. Configure credenciais do banco em variáveis de ambiente:

```bash
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
```

3. Execute o backend:

```bash
cd backend
./mvnw spring-boot:run
```

4. Execute o frontend:

```bash
cd frontend
npm install
ng serve
```

5. Acesse:

- Frontend: `http://localhost:4200`
- Backend: `http://localhost:8080`

## Observações da API

- O frontend ainda utiliza seleção de funcionário (sem autenticação).
- O workday é baseado em data; após finalizar, só é possível iniciar novamente na próxima data.

## Status de Testes

Status atual:

- Integração frontend/backend validada manualmente  
- Cobertura de testes unitários e de integração ainda pendente  

Escopo recomendado para a próxima etapa:

- Testes de service no backend para regras de start/save day  
- Testes de controller para validação e respostas  
- Testes de store/component no frontend para transições de estado  

## Próximas Melhorias

### Autenticação e Segurança

- Adicionar login com JWT  
- Substituir seletor de funcionário por usuário autenticado  
- Adicionar controle de acesso por perfil (admin/user)  

### Produto e UX

- Adicionar seletor de data para consultar dias anteriores  
- Adicionar fluxo de correção/edição de registros  
- Adicionar dashboard e relatórios (tempo por atividade, time, linguagem)  

### Engenharia e Qualidade

- Adicionar testes unitários e de integração (backend e frontend)  
- Introduzir Flyway/Liquibase para versionamento de schema  
- Adicionar pipeline de CI (lint, test, build)  

### Deploy

- Projeto ainda sem deploy  

Sugestão futura:

- Backend: Render ou Railway  
- Frontend: Vercel ou Netlify  
- Banco: PostgreSQL gerenciado (Neon, Supabase ou Aiven)  

## Estrutura de Pastas

```text
worklog/
├─ backend/                              # API Spring Boot
│  ├─ src/main/java/com/pitercoding/backend/
│  │  ├─ config/                         # CORS e configurações da aplicação
│  │  ├─ controller/                     # Endpoints REST (worklog, lookups, employees)
│  │  ├─ dto/                            # Payloads de request/response
│  │  ├─ exception/                      # Tratamento global de exceções
│  │  ├─ mapper/                         # Conversão Entity -> DTO
│  │  ├─ model/                          # Entidades JPA e enums
│  │  ├─ repository/                     # Repositórios Spring Data
│  │  ├─ service/                        # Regras de negócio e lógica da aplicação
│  │  └─ BackendApplication.java         # Entrypoint do Spring Boot
│  ├─ src/main/resources/
│  │  ├─ application.properties          # Configuração base
│  │  ├─ application-dev.properties      # Configuração do perfil dev
│  │  └─ data.sql                        # Seed de dados (lookups, employees)
│  └─ pom.xml                            # Dependências e build Maven
├─ frontend/                             # Aplicação Angular
│  ├─ src/app/
│  │  ├─ core/                           # Constantes, interceptors e serviços globais de UI
│  │  ├─ features/worklog/               # Domínio Worklog (pages, components, state, services)
│  │  ├─ shared/                         # Pipes e utilitários reutilizáveis
│  │  ├─ app.config.ts                   # Providers globais
│  │  └─ app.routes.ts                   # Configuração de rotas
│  ├─ src/assets/
│  │  ├─ images/                         # Logos e imagens estáticas
│  │  └─ screenshots/                    # Screenshots do projeto e plano de captura
│  ├─ src/main.ts                        # Bootstrap do Angular
│  └─ package.json                       # Scripts e dependências do frontend
├─ README.md                             # Documentação em inglês
└─ README.pt.md                          # Documentação em português
```

## Screenshots & Visuals

### Tela Inicial (Sem Funcionário Selecionado)

![Worklog Home Empty](frontend/src/assets/screenshots/worklog-home-empty.png)

### Formulário de Atividade Preenchido

![Worklog Form Filled](frontend/src/assets/screenshots/worklog-form-filled.png)

### Atividade em Andamento & Histórico de Atividades

![Worklog Running Activity](frontend/src/assets/screenshots/worklog-running-activity.png)

### Dia Finalizado

![Worklog Day Finished](frontend/src/assets/screenshots/worklog-day-finished.png)

## **Licença**

Este projeto está sob a licença **MIT**.

## Autor

**Piter Gomes** — Aluno de Ciências da Computação (6º Semestre) & Desenvolvedor Full-Stack

📧 [Email](mailto:piterg.bio@gmail.com) | 💼 [LinkedIn](https://www.linkedin.com/in/piter-gomes-4a39281a1/) | 💻 [GitHub](https://github.com/pitercoding) | 🌐 [Portfolio](https://portfolio-pitergomes.vercel.app/)
