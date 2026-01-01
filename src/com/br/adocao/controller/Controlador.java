package com.br.adocao.controller;

import com.br.adocao.exception.AnimalInvalidoException;
import com.br.adocao.exception.OpcaoInvalidaException;
import com.br.adocao.model.Endereco;
import com.br.adocao.model.Pet;
import com.br.adocao.model.enums.Sexo;
import com.br.adocao.model.enums.TipoPet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controlador {
    private static final Scanner sc = new Scanner(System.in);
    private static final Path FORMULARIO = Path.of("arquivos/formulario.txt");
    private static final Path OPCOES = Path.of("arquivos/opcoes.txt");
    private static final Path ARQUIVO_PETS = Path.of("arquivos/petsCadastrados");
    private static final AtomicInteger contador = new AtomicInteger(1);

    public void inicio () {
        int resposta;
        do {
            System.out.println("----------------------------------------------");
            System.out.println("1 - Iniciar sistema para cadastro de PETS");
            System.out.println("2 - Iniciar sistema para alterar formulario");
            System.out.println("3 - Encerrar programa");
            System.out.print("R: ");
            resposta = sc.nextInt();
            sc.nextLine();
            switch (resposta) {
                case 1:
                    sistemaDePets();
                    break;
                case 2:
                    alterarFormulario();
                    break;
            }
            if (resposta > 3) System.out.println("Digite uma opção válida!");
        }while (resposta != 3);
    }

    private void alterarFormulario () {
        int resposta;
        do {
            System.out.println("----------------------------------------------");
            System.out.println("1 - Criar nova pergunta");
            System.out.println("2 - Alterar pergunta existente");
            System.out.println("3 - Excluir pergunta existente");
            System.out.println("4 - Voltar ao menu principal");
            resposta = sc.nextInt();
            sc.nextLine();
            switch (resposta) {
                case 1:
                    criarNovaPergunta();
                    break;
                case 2:
                    alterarPergunta();
                    break;
                case 3:
                    excluirPergunta();
                    break;
            }
            if (resposta > 4) System.out.println("Digite uma opção válida!");
        }while (resposta != 4);
    }

    private void criarNovaPergunta() {
        List<String> perguntas = carregarFormulario();
        System.out.println("----------------------------------------------");
        System.out.println("Digite a sua nova pergunta:");
        System.out.print("R: ");
        String pergunta = sc.nextLine();
        if (perguntas.contains(pergunta)) {
            System.out.println("Pergunta ja existe no arquivo!");
            return;
        }
        perguntas.add(pergunta);
        try {
            Files.write(FORMULARIO, perguntas);
        }catch (IOException e) {
            System.out.println("Não foi possivel salvar o arquivo");
        }
        System.out.println("\nPergunta adicionada!");
    }

    private void alterarPergunta() {
        if(!(verificarVazio())) {
            List<String> perguntas = carregarFormulario();
            System.out.println("Qual pergunta voce deseja alterar?");
            listarPerguntasDisponiveis();
            int resposta = sc.nextInt();
            sc.nextLine();
            if (isIndicePerguntaValido(resposta)) {
                System.out.println("Digite a pergunta modificada:");
                System.out.print("R: ");
                String perguntaMod = sc.nextLine();
                perguntas.set((resposta + 6), perguntaMod);
                try {
                    Files.write(FORMULARIO, perguntas);
                } catch (IOException e) {
                    System.out.println("Impossivel ler o arquivo");
                }
                System.out.println("Pergunta modificada!");
            }
        }
    }

    private void excluirPergunta() {
        if(!(verificarVazio())) {
            System.out.println("Qual pergunta voce quer excluir?");
            listarPerguntasDisponiveis();
            int resposta = sc.nextInt();
            sc.nextLine();
            if(isIndicePerguntaValido(resposta)) {
                List<String> perguntasBrutas = carregarPerguntasBrutas();
                System.out.println("Deseja excluir e pergunta " + resposta + "?(Sim) (Nao)");
                String validacao = sc.nextLine();
                if (!(validacao.equalsIgnoreCase("sim"))) {
                    System.out.println("\nCancelando...");
                    return;
                }
                perguntasBrutas.set((resposta + 6), "");
                List<String> perguntasFinais = perguntasBrutas.stream().filter(pergunta -> !(pergunta.trim().isEmpty())).toList();
                try {
                    Files.write(FORMULARIO, perguntasFinais);
                } catch (IOException e) {
                    System.out.println("Impossivel acessar o arquivo!");
                }
                System.out.println("\nPergunta excluida!");
            }
        }
    }

    private boolean verificarVazio () {
        System.out.println("----------------------------------------------");
        if (carregarPerguntasValidas().isEmpty()) {
            System.out.println("Nenhuma pergunta adicionada!");
            return true;
        }
        return false;
    }

    private List<String> carregarPerguntasBrutas () {
        return carregarFormulario();
    }

    private List<String> carregarPerguntasValidas () {
        return carregarPerguntasBrutas().stream().skip(7).toList();
    }

    private void listarPerguntasDisponiveis () {
        contador.set(1);
        carregarPerguntasValidas().forEach(pergunta -> System.out.println(contador.getAndIncrement() + " - " + pergunta));
        System.out.print("R: ");
    }

    private boolean isIndicePerguntaValido (int resposta) {
        if (resposta > carregarPerguntasValidas().size()) {
            System.out.println("Digite uma opção válida!");
            return false;
        }
        if (resposta <= 0) {
            System.out.println("Digite uma opção válida!");
            return false;
        }
        return true;
    }

    private void sistemaDePets(){
        try {
            List<String> opcoes = Files.readAllLines(OPCOES);
            String opcao;
            do {
                do {
                    System.out.println("----------------------------------------------");
                    System.out.println("         SISTEMA DE CADASTRO DE PETS");
                    System.out.println("----------Selecione a opcao desejada----------");
                    contador.set(1);
                    opcoes.forEach(pergunta -> System.out.println(contador.getAndIncrement() + " - " + pergunta));
                    System.out.print("R: ");
                    opcao = sc.nextLine();
                    if (!opcao.matches("[1-6]")){
                        System.out.println("Opção inválida, digite um número de 1 a 6");
                    }
                } while (!opcao.matches("[1-6]"));
                switch (opcao) {
                    case "1":
                        lerFormulario();
                        break;
                    case "2":
                        editarInformacoesPets();
                        break;
                    case "3":
                        deletarPet();
                        break;
                    case "4":
                        listarPets();
                        break;
                    case "5":
                        listarPetsPorCriterio();
                        break;
                }
                if (!(opcao.equals("6"))) {
                    System.out.println("-----------------------------------------------------------------------------------\n");
                }
            }while (!opcao.equals("6"));
        }catch (OpcaoInvalidaException e) {
            System.out.println("Opção inválida, digite novamente");
        }catch (IOException e) {
            System.out.println("Impossivel ler o arquivo");
        }
    }

    private void lerFormulario() throws IOException {
        try{
            List<String> perguntas = carregarFormulario();
            System.out.println("\n---Preencha o formulário---");
            contador.set(1);
            perguntas.forEach(pergunta -> System.out.println(contador.getAndIncrement() + " - " + pergunta));
            responderFormulario(perguntas.size());
        }catch (IOException e) {
            System.out.println("Impossivel ler o arquivo");
        }
    }

    private List<String> carregarFormulario (){
        List<String> perguntas = new ArrayList<>();
        try{
            perguntas = Files.readAllLines(FORMULARIO);
        }catch (IOException e) {
            System.out.println("Impossível ler o arquivo");
        }
        return perguntas;
    }

    private void responderFormulario(int quantidadePerguntas) throws IOException {
        StringBuilder respostas = new StringBuilder();
        System.out.println();
        for (int pergunta = 0; pergunta < quantidadePerguntas; pergunta++) {
            System.out.print((pergunta + 1) + " - ");
            respostas.append(sc.nextLine()).append("; ");
        }
        salvarRespostasECriarPets(respostas);
    }

    private void salvarRespostasECriarPets(StringBuilder respostas) {
        try {
            String[] partes = respostas.toString().split("; ");
            String[] partesEnd = partes[3].split("\\s*,\\s*");
            String[] partesNomePet = partes[0].split("\\s+");
            Endereco endereco = criarEndereco(partesEnd[0], partesEnd[1], partesEnd[2]);
            Pet animal = criarPets(partesNomePet[0], partesNomePet[1], partes[1], partes[2], partes[4], partes[5], partes[6], endereco);
            salvarPets(animal, partes);
        }catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Todas as perguntas precisam ser respondidas!");
        }catch (IOException e) {
            System.out.println("Impossivel ler o arquivo!");
        }
    }

    private Endereco criarEndereco(String numero, String cidade, String rua) {
        return new Endereco(numero, cidade, rua);
    }

    private Pet criarPets(String nome, String sobrenome, String tipo, String sexo, String idade, String peso, String raca, Endereco endereco) {
        try {
            return new Pet(nome, sobrenome, tipo, sexo, idade, peso, raca, endereco);
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private void salvarPets(Pet pet, String[] respostas) throws IOException {
        List<String> animal = new ArrayList<>();
        List<String> respostasAtt;
        if (pet != null) {
            if(respostas.length > 7) {
                contador.set(8);
                respostasAtt = Arrays.stream(respostas).skip(7).map(resposta -> contador.getAndIncrement() + " - [EXTRA - PERGUNTA NOVA ADICIONADA] - " + resposta).toList();
                animal.add(pet.toString());
                animal.addAll(respostasAtt);
            }else {
                animal.add(pet.toString());
            }
            Path caminhoFinal;
            if(pet.getCaminhoArquivo() != null) {
                caminhoFinal = pet.getCaminhoArquivo();
            }else{
                LocalDateTime data = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
                String dataFormatada = formatter.format(data);
                String nomeDoArquivo = dataFormatada + "-" + pet.getNome() + pet.getSobrenome() + ".txt";
                caminhoFinal = ARQUIVO_PETS.resolve(nomeDoArquivo);
                pet.setCaminhoArquivo(caminhoFinal);
            }
            if (Files.notExists(ARQUIVO_PETS)) {
                Files.createDirectories(ARQUIVO_PETS);
            }
            if (Files.notExists(caminhoFinal)) {
                Files.createFile(caminhoFinal);
            }
            Files.write(caminhoFinal, animal);
            System.out.println("\nPet criado!");
        }
    }

    private void editarInformacoesPets() throws IOException {
        String[] respostas = new String[50];
        listarPets();
        System.out.println("\nQual dos pets cadastrados voce quer editar (Digite o número dele)?");
        List<Pet> animais = carregarPets();
        System.out.print("R: ");
        int escolhaPet = sc.nextInt();
        sc.nextLine();
        List<String> informacoes;
        Path caminhoOriginal;
        Pet animalAtual;
        try {
            animalAtual = animais.get(escolhaPet - 1);
            caminhoOriginal = animalAtual.getCaminhoArquivo();
            informacoes = Files.readAllLines(caminhoOriginal);
            informacoes.forEach(System.out::println);
        }catch (IndexOutOfBoundsException e) {
            throw new AnimalInvalidoException("Digite um numero de animal válido!");
        }catch (IOException e) {
            throw new IOException("Erro ao ler o arquivo");
        }
        System.out.println("Qual informação deseja alterar?");
        int escolhaInfo = sc.nextInt();
        sc.nextLine();
        if(escolhaInfo > informacoes.size() || escolhaInfo <= 0) {
            throw new RuntimeException("Digite uma opção válida!");
        }
        switch (escolhaInfo) {
            case 1:
                modificarNome(animalAtual);
                break;
            case 2:
                modificarTipo(animalAtual);
                break;
            case 3:
                modificarSexo(animalAtual);
                break;
            case 4:
                modificarEndereco(animalAtual);
                break;
            case 5:
                modificarIdade(animalAtual);
                break;
            case 6:
                modificarPeso(animalAtual);
                break;
            case 7:
                modificarRaca(animalAtual);
                break;
        }
        if (escolhaInfo > 7) {
            modificarAtributoBonus(animalAtual, informacoes, escolhaInfo);
            return;
        }
        salvarPets(animalAtual, respostas);
        System.out.println("Informação atualizada com sucesso!");
    }

    private void modificarNome(Pet pet) {
        System.out.println("Qual o nome e sobrenome do pet?");
        String nome = sc.nextLine();
        String[] nomePartes = nome.split(" ", 2);
        pet.setNome(nomePartes[0]);
        pet.setSobrenome((nomePartes.length > 1) ? nomePartes[1] : ".");
        System.out.println("Nome atualizado para: " + pet.getNome());
    }

    private void modificarTipo(Pet pet) {
        System.out.println("Qual o tipo do pet?");
        String tipo = sc.nextLine();
        try {
            pet.setTipo(TipoPet.valueOf(tipo.toUpperCase()));
        }catch (IllegalArgumentException e) {
            System.out.println("Tipo invalido!");
        }
        System.out.println("Tipo atualizado para: " + pet.getTipo());
    }

    private void modificarSexo(Pet pet) {
        System.out.println("Qual o sexo do pet?");
        String sexo = sc.nextLine();
        try {
            pet.setSexo(Sexo.valueOf(sexo.toUpperCase()));
        }catch (IllegalArgumentException e) {
            System.out.println("Sexo invalido!");
        }
        System.out.println("Sexo atualizado para: " + pet.getSexo());
    }

    private void modificarIdade(Pet pet) {
        System.out.println("Qual a idade do pet?");
        String idade = sc.nextLine();
        pet.setIdade(idade);
        System.out.println("Idade atualizada para: " + pet.getIdade());
    }

    private void modificarPeso(Pet pet) {
        System.out.println("Qual o peso do pet?");
        String peso = sc.nextLine();
        pet.setPeso(peso);
        System.out.println("Peso atualizado para: " + pet.getPeso());
    }

    private void modificarRaca(Pet pet) {
        System.out.println("Qual a raça do pet?");
        String raca = sc.nextLine();
        pet.setRaca(raca);
        System.out.println("Raça atualizada para: " + pet.getRaca());
    }

    private void modificarEndereco(Pet pet) {
        System.out.println("Qual o endereço do pet?");
        String endereco = sc.nextLine();
        String[] enderecoPartes = endereco.split(", ");
        if (enderecoPartes.length == 3) {
            Endereco novoEndereco = new Endereco(enderecoPartes[0].trim(), enderecoPartes[1].trim(), enderecoPartes[2].trim());
            pet.setEndereco(novoEndereco);
        } else {
            System.out.println("Formato de endereço inválido. Use: numero, cidade, rua");
        }
        System.out.println("Endereço atualizado para: " + pet.getEndereco());
    }

    private void modificarAtributoBonus (Pet pet, List<String> atributos, int indice) {
        System.out.println("Qual a informação nova?");
        String atributo = sc.nextLine();
        String[] atributosPartes = atributos.get(indice-1).split(" - ");
        String novoAtributo = atributosPartes[0] +" - " + atributosPartes[1] + " - " + atributosPartes[2] + " - " + atributo;
        atributos.set(indice-1, novoAtributo);
        try{
            Files.write(pet.getCaminhoArquivo(), atributos);
            System.out.println("Informação modificada com sucesso!");
        }catch(IOException e){
            System.out.println("Impossivel ler o arquivo!");
        }
    }

    private void deletarPet () {
        List<Pet> pets = carregarPets();
        System.out.println("\nQual pet voce deseja apagar?");
        listarPets();
        System.out.print("R: ");
        int petSelecionado = sc.nextInt();
        Pet animalSelecionado = pets.get(petSelecionado - 1);
        sc.nextLine();
        System.out.println("Pet: "+ animalSelecionado.resumo() +"\nDeseja confirmar a escolha (Sim) (Não)?");
        System.out.print("R: ");
        String escolha = sc.nextLine();
        if(!(escolha.equalsIgnoreCase("sim"))) {
            System.out.println("Cancelando...");
            return;
        }
        confirmarDeletarPet(animalSelecionado);
    }

    private void confirmarDeletarPet(Pet pet) {
        try {
            Files.deleteIfExists(pet.getCaminhoArquivo());
        }catch (IOException e) {
            System.out.println("Não foi possível apagar o arquivo do pet!");
        }
        System.out.println("Pet deletado com sucesso!");
    }

    private void listarPetsPorCriterio() {
        System.out.println("\nPor quais critérios procurar os pets? (divida os critérios por vírgulas, max. 2 critérios)");
        System.out.print("R: ");
        String criterios = sc.nextLine();
        String[] criteriosDiv = criterios.split("\\s*,\\s*");
        selecionarCriterios(criteriosDiv.length, criteriosDiv);
    }

    private void selecionarCriterios(int tamanho, String[] criteriosDiv) {
        List<Pet> pets = carregarPets();
        if (tamanho > 2) {
            System.out.println("Apenas dois criterios de parâmetro!");
            return;
        }
        resultadoDosCriterios(pets, criteriosDiv, tamanho);
    }

    private void resultadoDosCriterios (List<Pet> pets, String[] criteriosDiv, int tamanho) {
        List<Pet> resultado = criteriosUnicos(criteriosDiv[0], pets);
        if (tamanho == 2) {
            resultado = criteriosUnicos(criteriosDiv[1], resultado);
        }
        if (resultado.isEmpty()) {
            System.out.println("Nenhum pet encontrado com esses critérios.");
        } else {
            contador.set(1);
            resultado.forEach(pet -> System.out.println("Pet " + contador.getAndIncrement() + ": " + pet.resumo()));
        }
    }

    private List<Pet> criteriosUnicos (String criterios, List<Pet> pets) {
        return switch (criterios.toUpperCase().trim()) {
            case "NOME" -> listarPetPorNome(pets);
            case "TIPO" -> listarPetPorTipo(pets);
            case "SEXO" -> listarPetPorSexo(pets);
            case "IDADE" -> listarPetPorIdade(pets);
            case "PESO" -> listarPetPorPeso(pets);
            case "ENDEREÇO" -> listarPetPorEndereco(pets);
            case "RAÇA" -> listarPetPorRaca(pets);
            default -> new ArrayList<>();
        };
    }

    private List<Pet> listarPetPorNome (List<Pet> pets) {
        System.out.println("Qual nome voce deseja buscar?");
        String nomeAnimal = sc.nextLine();
        return pets.stream().filter(pet -> (pet.getNome()+pet.getSobrenome()).toUpperCase().contains(nomeAnimal.toUpperCase())).toList();
    }

    private List<Pet> listarPetPorTipo (List<Pet> pets) {
        System.out.println("Qual tipo de pet voce deseja buscar?");
        String tipoAnimal = sc.nextLine();
        return pets.stream().filter(pet -> pet.getTipo().name().equals(tipoAnimal.toUpperCase())).toList();
    }

    private List<Pet> listarPetPorSexo (List<Pet> pets) {
        System.out.println("Por qual sexo voce deseja buscar?");
        String sexoAnimal = sc.nextLine();
        return pets.stream().filter(pet -> pet.getSexo().name().equals(sexoAnimal.toUpperCase())).toList();
    }

    private List<Pet> listarPetPorEndereco (List<Pet> pets) {
        System.out.println("Por qual valor voce deseja buscar? (Número, cidade ou rua)");
        String enderecoAnimal = sc.nextLine();
        return pets.stream().filter(pet -> pet.getEndereco().toString().toUpperCase().contains(enderecoAnimal.toUpperCase())).toList();
    }

    private List<Pet> listarPetPorIdade (List<Pet> pets) {
        System.out.println("Por qual idade voce deseja buscar? (Ex: 3 para buscar acima de 3 anos)");
        String idadeAnimal = sc.nextLine();
        return pets.stream().filter(pet -> {
            String idadeDoPetLimpa = pet.getIdade().replaceAll("[^0-9]", "");
            if (!idadeDoPetLimpa.matches("[0-9.]+")) return false;
            try {
                return Double.parseDouble(idadeDoPetLimpa) > Double.parseDouble(idadeAnimal);
            } catch (NumberFormatException e) {
                return false;
            }
        }).toList();
    }

    private List<Pet> listarPetPorPeso (List<Pet> pets) {
        System.out.println("Por qual peso voce deseja buscar? (Ex: 4.5 para buscar acima de 4.5kg)");
        String pesoAnimal = sc.nextLine();
        return pets.stream().filter(pet -> {
            String pesoDoPetLimpo = pet.getPeso().replaceAll("[^0-9.]", "");
            if (!pesoDoPetLimpo.matches("[0-9.]+")) return false;
            try {
                return Double.parseDouble(pesoDoPetLimpo) >= Double.parseDouble(pesoAnimal);
            } catch (NumberFormatException e) {
                return false;
            }
        }).toList();
    }

    private List<Pet> listarPetPorRaca (List<Pet> pets) {
        System.out.println("Por qual raça voce deseja buscar?");
        String racaAnimal = sc.nextLine();
        return pets.stream().filter(pet -> pet.getRaca().toUpperCase().contains(racaAnimal.toUpperCase())).toList();
    }

    private void listarPets() {
        System.out.println("\nTodos os pets cadastrados:");
        List<Pet> pets = carregarPets();
        if (pets.isEmpty()) {
            System.out.println("Nenhum pet cadastrado ou erro ao carregar.");
            return;
        }
        System.out.println();
        contador.set(1);
        pets.forEach(pet -> System.out.println(contador.getAndIncrement() + pet.resumo()));
    }

    private List<Pet> carregarPets() {
        if (Files.notExists(ARQUIVO_PETS)) {
            return new ArrayList<>();
        }
        try(Stream<Path> caminhos = Files.list(ARQUIVO_PETS)) {
            return caminhos.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".txt"))
                    .map(this::converterEmPet)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }catch (IOException e) {
            System.err.println("Erro ao listar arquivos de pets: " + e.getMessage());
            return new ArrayList<>();
        }catch (RuntimeException e) {
            System.err.println("Erro ao carregar um pet: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private Pet converterEmPet(Path path) {
        List<String> pet;
        try {
            pet = Files.readAllLines(path);
        }catch (IOException e) {
            throw new RuntimeException("Erro ao ler o arquivo do Pet: " + path, e);
        }
        if(pet.isEmpty()) {
            throw new RuntimeException("Erro ao ler o arquivo do Pet: " + path);
        }
        String nomeCompleto = extrairValor(pet.getFirst());
        String[] nomePartes = nomeCompleto.split(" ", 2);
        String nome = nomePartes[0];
        String sobrenome = (nomePartes.length > 1) ? nomePartes[1] : ".";
        String tipo = extrairValor(pet.get(1));
        String sexo = extrairValor(pet.get(2));
        String linhaEndereco = extrairValor(pet.get(3));
        String[] endPartes = linhaEndereco.split(",");
        String valorIdade = extrairValor(pet.get(4));
        String idade = valorIdade.equals(".") ? "." : valorIdade.replaceAll("[^0-9]", "");
        String valorPeso = extrairValor(pet.get(5));
        String peso = valorPeso.equals(".") ? "." : valorPeso.replaceAll("[^0-9.]", "");
        String raca = extrairValor(pet.get(6));
        Endereco endereco = new Endereco(deixarSeguro(endPartes, 0), deixarSeguro(endPartes, 1), deixarSeguro(endPartes, 2));
        Pet animal = criarPets(nome, sobrenome, tipo, sexo, idade, peso, raca, endereco);
        if (animal != null) {
            animal.setCaminhoArquivo(path);
        }
        return animal;
    }

    private String normalizarCampo(String campo) {
        return campo.replaceAll("NÃO INFORMADO", ".");
    }

    private String extrairValor(String linha) {
        String[] partes = linha.split(" - ", 2);
        if (partes.length < 2) {
            return ".";
        }
        String valor = partes[1].trim();
        if (valor.endsWith(",")) {
            valor = valor.substring(0, valor.length() - 1);
        }
        return valor.equals("NÃO INFORMADO") ? "." : valor;
    }

    private String deixarSeguro(String[] partes, int posicao) {
        if (posicao < partes.length) {
            return normalizarCampo(partes[posicao].trim());
        }
        return ".";
    }
}
