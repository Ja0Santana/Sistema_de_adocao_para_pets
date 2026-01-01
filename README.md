  🐾 Sistema de Adoção de Pets
</h1>

  Um sistema de gerenciamento de adoções via console, focado em organização, persistência de dados e usabilidade.
</p>

  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21">
  <img src="https://img.shields.io/badge/IDE-IntelliJ_IDEA-000000?style=for-the-badge&logo=intellij-idea&logoColor=white" alt="IntelliJ IDEA">
  <img src="https://img.shields.io/badge/Arquitetura-MVC-blue?style=for-the-badge" alt="MVC Architecture">
</p>

---

## Sobre o Projeto

Este projeto é uma aplicação de **Gerenciamento de Adoções** desenvolvida em **Java Puro**, simulando o dia a dia de uma ONG ou clínica veterinária.

O objetivo principal foi criar uma solução para petshops utilizando **manipulação avançada de arquivos (`.txt`)** para garantir que nenhum dado seja perdido ao fechar o programa.
O projeto segue a base da arquitetura **MVC (Model-View-Controller)** para garantir um código limpo e escalável.

## ✨ Funcionalidades Principais

🐶 **Gestão Completa de Pets**
- Cadastro detalhado (Nome, Tipo, Raça, Idade, Peso, etc.).
- Edição segura de dados existentes.
- **Exclusão Lógica/Segura:** Mecanismos que previnem a perda acidental de registros importantes.

🔍 **Busca Inteligente (Filtro Funil)**
- O sistema possui um motor de busca avançado que permite refinar resultados.
- **Exemplo:** Você pode filtrar por "Cachorros" e, dentro desse resultado, filtrar novamente apenas por "Machos".

📝 **Formulário Dinâmico**
- Diferencial técnico: As perguntas do formulário de cadastro não são fixas no código ("hardcoded").
- O sistema lê as perguntas de um arquivo de configuração, permitindo que o administrador altere as perguntas válidas do arquivo sem precisar recompilar o programa.

📂 **Persistência de Arquivos (NIO)**
- Uso da biblioteca `java.nio` para leitura e escrita eficiente de dados.
- Estrutura de pastas organizada automaticamente (`arquivos/petsCadastrados`).
- Garantia de que todos os dados sejam salvos corretamente ao encerrar o programa.

## Tecnologias Utilizadas

* **Java JDK 21** - Versão LTS mais recente com recursos modernos.
* **IntelliJ IDEA** - IDE utilizada para desenvolvimento e refatoração.
* **Java IO/NIO** - Para manipulação de arquivos e caminhos (Path/Files).
* **Java Stream API** - Para filtros e processamento de coleções de dados de forma funcional.

### 🚀 Como executar
Para rodar o projeto, não é necessário abrir o terminal.
Basta acessar a pasta do projeto **SistemaDePets** e dar um **duplo clique** no arquivo:
> **`Iniciar.bat`**

O projeto conta com um script de automação para Windows.

* **Execução via Script:** Execute o arquivo `Iniciar.bat` para rodar a aplicação diretamente.
* **Execução Manual:** Caso prefira o terminal, utilize: `java -jar "Sistema de adocao para pets.jar"`

## Arquitetura do Projeto

O código está organizado seguindo o padrão de mercado **MVC**:

```text
src/com/br/adocao
├── 📁 application  # Entry Point (Classe Main)
├── 📁 controller   # Regras de Negócio e Controle de Fluxo
├── 📁 exception    # Tratamento de Erros Personalizados
├── 📁 model        # Representação dos Dados (Pet, Endereco)
     └── 📁 enums    # Constantes (TipoPet, Sexo)
