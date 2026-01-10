package com.br.adocao.model;

import com.br.adocao.interfaces.PetRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArquivoPetRepository implements PetRepository {
    private static final AtomicInteger contador = new AtomicInteger(1);

    @Override
    public void salvar(Pet pet) throws IOException {
        if (pet == null) return;
        List<String> linhasParaSalvar = new ArrayList<>();
        linhasParaSalvar.add(pet.toString());
        if (!(pet.getInformacoesExtras().isEmpty())) {
            linhasParaSalvar.addAll(pet.getInformacoesExtras());
        }
        Path caminhoFinal = definirCaminhoArquivo(pet);
        if (Files.notExists(ArquivosEPerguntas.ARQUIVO_PETS)) {
            Files.createDirectories(ArquivosEPerguntas.ARQUIVO_PETS);
        }
        Files.write(caminhoFinal, linhasParaSalvar);
        if (pet.getCaminhoArquivo() != null) {
            caminhoFinal = pet.getCaminhoArquivo();
        } else {
            LocalDateTime data = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
            String dataFormatada = formatter.format(data);
            String nomeDoArquivo = dataFormatada + "-" + pet.getNome() + pet.getSobrenome() + ".txt";
            caminhoFinal = ArquivosEPerguntas.ARQUIVO_PETS.resolve(nomeDoArquivo);
            pet.setCaminhoArquivo(caminhoFinal);
        }

        if (Files.notExists(caminhoFinal)) {
            Files.createFile(caminhoFinal);
        }

    }

    private Path definirCaminhoArquivo(Pet pet) {
        if (pet.getCaminhoArquivo() != null) {
            return pet.getCaminhoArquivo();
        } else {
            LocalDateTime data = LocalDateTime.now();
            String nomeDoArquivo = DateTimeFormatter.ofPattern("yyyyMMddHHmm").format(data) + "-" + pet.getNome() + pet.getSobrenome() + ".txt";
            Path novoCaminho = ArquivosEPerguntas.ARQUIVO_PETS.resolve(nomeDoArquivo);
            pet.setCaminhoArquivo(novoCaminho);
            return ArquivosEPerguntas.ARQUIVO_PETS.resolve(nomeDoArquivo);
        }
    }

    @Override
    public void deletar (Pet pet) throws IOException{
        if(Files.exists(pet.getCaminhoArquivo())) {
            Files.delete(pet.getCaminhoArquivo());
        }else {
            throw new IOException("Arquivo do pet não encontrado.");
        }
    }

    @Override
    public List<Pet> carregarPets() throws IOException {
        if (Files.notExists(ArquivosEPerguntas.ARQUIVO_PETS)) {
            return new ArrayList<>();
        }
        try(Stream<Path> caminhos = Files.list(ArquivosEPerguntas.ARQUIVO_PETS)) {
            return caminhos.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".txt"))
                    .map(this::converterEmPet)
                    .collect(Collectors.toList());
        }catch (IOException | RuntimeException e) {
            throw new IOException("Erro ao listar arquivos de pets!");
        }
    }

    private Pet converterEmPet(Path path) {
        List<String> pet;
        try {
            pet = Files.readAllLines(path).stream()
                    .filter(linha -> !linha.trim().isEmpty())
                    .toList();
        }catch (IOException e) {
            throw new RuntimeException("Erro ao ler o arquivo do Pet: " + path, e);
        }

        if(pet.isEmpty() || pet.size() < 7) {
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
        String idade = (valorIdade.equals(".") || valorIdade.isEmpty()) ? "." : valorIdade.replaceAll("[^0-9.,]", "").replace(",", ".");
        String valorPeso = extrairValor(pet.get(5));
        String peso = (valorPeso.equals(".") || valorPeso.isEmpty()) ? "." : valorPeso.replaceAll("[^0-9.,]", "").replace(",", ".");
        String raca = extrairValor(pet.get(6));
        Endereco endereco = new Endereco(deixarSeguro(endPartes, 0), deixarSeguro(endPartes, 1), deixarSeguro(endPartes, 2));
        Pet animal;
        try {
            animal = new Pet(nome, sobrenome, tipo, sexo, idade, peso, raca, endereco);
        } catch (Exception e) {
            throw new RuntimeException("Impossivel criar o pet a partir do arquivo: " + path);
        }
        animal.setCaminhoArquivo(path);
        return animal;
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
        return (valor.equals("NÃO INFORMADO") || valor.isEmpty()) ? "." : valor;
    }

    private String deixarSeguro(String[] partes, int posicao) {
        if (posicao < partes.length) {
            return normalizarCampo(partes[posicao].trim());
        }
        return ".";
    }

    private String normalizarCampo(String campo) {
        return campo.replaceAll("NÃO INFORMADO", ".");
    }
}

