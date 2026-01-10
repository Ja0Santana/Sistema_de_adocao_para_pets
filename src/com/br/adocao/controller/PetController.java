package com.br.adocao.controller;

import com.br.adocao.exception.OpcaoInvalidaException;
import com.br.adocao.interfaces.MenuRepository;
import com.br.adocao.interfaces.PetRepository;
import com.br.adocao.model.ArquivosEPerguntas;
import com.br.adocao.model.Endereco;
import com.br.adocao.model.Pet;
import com.br.adocao.model.enums.Sexo;
import com.br.adocao.model.enums.TipoPet;
import com.br.adocao.model.enums.Criterios;
import com.br.adocao.exception.ArquivoVazioOuInvalidoException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PetController {
    private static Scanner sc;
    private static final AtomicInteger contador = new AtomicInteger(1);
    private final ArquivosEPerguntas arquivosEPerguntas = new ArquivosEPerguntas();
    private final PetRepository petRepository;
    private final MenuRepository menuRepository;
    private final int TOTAL_DE_PERGUNTAS = 7;

    public PetController(PetRepository petRepository, MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
        this.petRepository = petRepository;
        sc = new Scanner(System.in);
    }

    public void sistemaDePets(){
        try {
            List<String> opcoes = menuRepository.carregarOpcoesMenu();
            String opcao;
            do {
                do {
                    System.out.println("-----------------------------------------------------------------------------------");
                    System.out.println("                                CADASTRO DE PETS                                   ");
                    System.out.println("----------------------------Selecione a opcao desejada-----------------------------");
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
            System.out.println("Erro ao processar a informação");
        }
    }

    private void lerFormulario() throws IOException {
        try{
            List<String> perguntas = arquivosEPerguntas.carregarFormulario();
            System.out.println("\n---Preencha o formulário---");
            contador.set(1);
            perguntas.forEach(pergunta -> System.out.println(contador.getAndIncrement() + " - " + pergunta));
            responderFormulario(perguntas.size());
        }catch (IOException e) {
            System.out.println("Erro ao processar a informação");
        }
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
            salvarPets(animal);
        }catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Todas as perguntas precisam ser respondidas!");
        }catch (IOException e) {
            System.out.println("Erro ao processar a informação!");
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
            return null;
        }
    }

    private void salvarPets(Pet pet) throws IOException {
        try{
            petRepository.salvar(pet);
            System.out.println("\nPet criado!");
        }catch (Exception e) {
            System.out.println("Erro ao processar a informação!");
        }

    }

    private void editarInformacoesPets() throws IOException {
        listarPets();
        System.out.println("\nQual dos pets cadastrados voce quer editar (Digite o número dele)?");
        List<Pet> pets = List.of();
        try{
            pets = petRepository.carregarPets();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.print("R: ");
        int escolhaPet = sc.nextInt();
        sc.nextLine();
        Pet animalAtual = pets.get(escolhaPet - 1);
        animalAtual.toString();
        List<String> extras = animalAtual.getInformacoesExtras();
        if (extras != null || !extras.isEmpty()) {
            for (int i = 0; i < extras.size(); i++) {
                System.out.println((i+8) + " - " + extras.get(i));
            }
        }
        System.out.println(animalAtual);
        System.out.print("Qual informação deseja alterar?\nR: ");
        int escolhaInfo = sc.nextInt();
        sc.nextLine();
        if(escolhaInfo <= 0) {
            throw new RuntimeException("Digite uma opção válida!");
        }
        if(escolhaInfo <= TOTAL_DE_PERGUNTAS) {
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
        } else {
            int indiceReal = escolhaInfo - 8;
            if(indiceReal < extras.size()) {
                modificarAtributoBonus(animalAtual, indiceReal);
            }else{
                System.out.println("Opção inválida!");
                return;
            }
        }
        try {
            petRepository.salvar(animalAtual);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void modificarAtributoBonus (Pet pet, int indice) {
        if (indice < 0 || indice >= (pet.getInformacoesExtras().size())) {
            System.out.println("Opção inválida!");
            return;
        }
        System.out.println("Qual a informação nova?");
        String novaInformacao = sc.nextLine();
        List<String> linhasExtras = pet.getInformacoesExtras();
        String linhaAntiga = pet.getInformacoesExtras().get(indice);
        try{
            int ultimoSeparador = linhaAntiga.lastIndexOf(" - ");
            if(ultimoSeparador != -1) {
                String prefixoDaPergunta = linhaAntiga.substring(0, ultimoSeparador);
                String linhaAtualizada = prefixoDaPergunta + " - " + novaInformacao;
                linhasExtras.set(indice, linhaAtualizada);
            }else {
                linhasExtras.set(indice, novaInformacao);
            }
        }catch(Exception e){
            System.out.println("Erro ao processar a informação!");
            linhasExtras.set(indice, novaInformacao);
        }
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

    private void deletarPet () {
        System.out.println("\nQual pet voce deseja apagar?");
        try {
            listarPets();
        } catch (ArquivoVazioOuInvalidoException e) {
            System.out.println(e.getMessage());
            return;
        }
        System.out.print("R: ");
        int escolhaPet = sc.nextInt();
        int petEscolhido = escolhaPet - 1;
        Pet animalSelecionado = null;
        try {
            animalSelecionado = petRepository.carregarPets().get(petEscolhido);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
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
        try{
            petRepository.deletar(pet);
        } catch (IOException e) {
            System.out.println(e.getMessage());
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
        List<Pet> pets = new ArrayList<>();
        try {
            pets = petRepository.carregarPets();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
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
            System.out.println("\nPets encontrados:");
            resultado.forEach(pet -> System.out.println("Pet " + contador.getAndIncrement() + ": " + pet.resumo()));
        }
    }

    private List<Pet> criteriosUnicos (String criterios, List<Pet> pets) {
        return switch (Criterios.valueOf(criterios.toUpperCase())) {
            case Criterios.NOME -> listarPetPorNome(pets);
            case Criterios.TIPO -> listarPetPorTipo(pets);
            case Criterios.SEXO -> listarPetPorSexo(pets);
            case Criterios.IDADE -> listarPetPorIdade(pets);
            case Criterios.PESO -> listarPetPorPeso(pets);
            case Criterios.ENDERECO -> listarPetPorEndereco(pets);
            case Criterios.RACA -> listarPetPorRaca(pets);
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
            String idadeDoPetLimpa = pet.getIdade().replaceAll("[^0-9.]", "");
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
        List<Pet> pets= new ArrayList<>();
        System.out.println("\nTodos os pets cadastrados:");
        System.out.println("-----------------------------------------------------------------------------------");
        try{
            pets = carregarPets();
        }catch (ArquivoVazioOuInvalidoException e) {
            System.out.println(e.getMessage());
            System.out.println();
        }
        contador.set(1);
        pets.forEach(pet -> System.out.println(contador.getAndIncrement() + pet.resumo()));
    }

    private List<Pet> carregarPets() {
        List<Pet> pets = new ArrayList<>();
        try{
            pets = petRepository.carregarPets();
        }catch (ArquivoVazioOuInvalidoException | IOException e) {
            System.out.println(e.getMessage());
            return pets;
        }
        return pets;
    }
}
