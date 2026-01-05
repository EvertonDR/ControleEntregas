# ERDelivery - Controle de Entregas

[![Kotlin](https://img.shields.io/badge/kotlin-1.9%2B-blue.svg?logo=kotlin)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.6%2B-blue?logo=jetpack-compose)](https://developer.android.com/jetpack/compose)
[![Room](https://img.shields.io/badge/Room-2.6%2B-red?logo=android-studio)](https://developer.android.com/training/data-storage/room)

Um aplicativo Android para gerenciamento e controle de entregas, desenvolvido com foco em simplicidade e eficiência.

---

## Sumário

- [Visão Geral](#visão-geral)
- [Funcionalidades Principais](#funcionalidades-principais)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Como Compilar e Executar](#como-compilar-e-executar)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Owner do Projeto](#owner-do-projeto)

---

## Visão Geral

O ERDelivery é um sistema completo para motoboys e pequenas empresas que precisam de um controle detalhado sobre suas entregas. O aplicativo permite o cadastro de clientes e bairros, o lançamento de novas entregas e um acompanhamento financeiro preciso através de abas de status e relatórios em PDF.

## Funcionalidades Principais

-   **Dashboard "Em Aberto"**: Visualize rapidamente as entregas pendentes e o valor total a receber.
-   **Cadastro Inteligente**: Cadastre clientes e bairros. O menu de bairros é filtrado automaticamente com base na cidade selecionada.
-   **Controle de Status**: Marque entregas como **realizadas** ou **pagas** com um simples clique. A lógica de negócio move as entregas entre as abas automaticamente.
-   **Abas de Status**:
    -   **Realizadas**: Histórico de entregas concluídas, separadas por cliente e com sub-seções de "Pagas" e "Não Pagas".
    -   **Não Pagas**: Visão consolidada de todas as pendências financeiras, com valor total e subtotal por cliente.
    -   **Pagas**: Arquivo de todas as entregas já quitadas.
-   **Relatórios em PDF**:
    -   **Relatório de Cobrança**: Na aba "Não Pagas", gere um PDF detalhado para um cliente específico.
    -   **Resumo Financeiro**: Na aba "Realizadas", baixe um resumo com o balanço de entregas pagas e não pagas do cliente.
    -   **Backup Total**: Na tela principal, gere um backup completo de todas as entregas já registradas no aplicativo.
-   **Persistência de Dados**: Utiliza `rememberSaveable` para manter os dados digitados mesmo ao rotacionar a tela.

---

## Tecnologias Utilizadas

-   **Linguagem**: [Kotlin](https://kotlinlang.org/)
-   **Interface de Usuário**: [Jetpack Compose](https://developer.android.com/jetpack/compose) para uma UI declarativa e moderna.
-   **Arquitetura**: MVVM (Model-View-ViewModel) com fluxos de dados reativos usando StateFlow.
-   **Banco de Dados**: [Room Persistence Library](https://developer.android.com/training/data-storage/room) para armazenamento local e robusto.
-   **Navegação**: [Navigation Compose](https://developer.android.com/jetpack/compose/navigation) para gerenciar as transições entre as telas.

---

## Como Compilar e Executar

### Pré-requisitos

-   [Android Studio (versão Hedgehog ou superior)](https://developer.android.com/studio)
-   Um emulador Android ou um dispositivo físico com depuração USB ativada.

### Passos

1.  **Clone o repositório**:
    ```sh
    git clone <URL_DO_SEU_REPOSITORIO>
    ```
2.  **Abra no Android Studio**:
    -   Abra o Android Studio.
    -   Selecione "Open an existing project".
    -   Navegue até a pasta onde você clonou o projeto e selecione-a.
3.  **Sincronize o Gradle**: O Android Studio irá sincronizar o projeto com os arquivos Gradle automaticamente. Aguarde a conclusão.
4.  **Execute o Aplicativo**:
    -   Selecione um dispositivo (emulador ou físico) na barra de ferramentas.
    -   Clique no ícone de "Play" (▶️) para compilar e instalar o aplicativo no dispositivo.

---

## Estrutura do Projeto

-   `com.example.controleentregas.data`: Contém as entidades do Room (`EntregaEntity`, `ClienteEntity`, `BairroEntity`), os DAOs (Data Access Objects) e o `AppDatabase`.
-   `com.example.controleentregas.ui`: Contém todas as telas (Composables) e o `MainViewModel`, que gerencia o estado e a lógica da UI.
-   `com.example.controleentregas.util`: Inclui classes utilitárias, como o `PdfExporter`.

---

## Owner do Projeto

-   [Everton Ribeiro](https://github.com/EvertonDR)
