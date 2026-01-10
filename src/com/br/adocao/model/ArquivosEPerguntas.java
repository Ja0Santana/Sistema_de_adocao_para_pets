package com.br.adocao.model;

import com.br.adocao.interfaces.MenuRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ArquivosEPerguntas implements MenuRepository {
    public static final Path OPCOES = Path.of("arquivos/opcoes.txt");
    public static final Path FORMULARIO = Path.of("arquivos/formulario.txt");
    public static final Path ARQUIVO_PETS = Path.of("arquivos/petsCadastrados");

    public List<String> carregarPerguntasValidas() {
        return carregarPerguntasBrutas().stream().skip(7).toList();
    }

    public List<String> carregarPerguntasBrutas() {
        return carregarFormulario();
    }

    public List<String> carregarFormulario(){
        List<String> perguntas = new ArrayList<>();
        try{
            perguntas = Files.readAllLines(FORMULARIO);
        }catch (IOException e) {
            System.out.println("Impossível ler o arquivo");
        }
        return perguntas;
    }

    @Override
    public List<String> carregarOpcoesMenu() throws IOException{
        return Files.readAllLines(OPCOES);
    }
}
