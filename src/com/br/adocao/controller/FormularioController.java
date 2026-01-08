package com.br.adocao.controller;

import com.br.adocao.model.ArquivosEPerguntas;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;

public class FormularioController {
    private static final int QUANTIDADE_PERGUNTAS_INICIAIS = 6;
    private final Scanner sc = new Scanner(System.in);
    private final ArquivosEPerguntas arquivosEPerguntas = new ArquivosEPerguntas();

    public void informarMenu() {
        int resposta;
        do {
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.println("                            MODIFICAÇÃO DO FORMULÁRIO                              ");
            System.out.println("----------------------------Selecione a opcao desejada-----------------------------");
            System.out.println("1 - Criar nova pergunta");
            System.out.println("2 - Alterar pergunta existente");
            System.out.println("3 - Excluir pergunta existente");
            System.out.println("4 - Voltar ao menu principal");
            System.out.print("R: ");
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
        List<String> perguntas = arquivosEPerguntas.carregarFormulario();
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("Digite a sua nova pergunta:");
        System.out.print("R: ");
        String pergunta = sc.nextLine();
        if (perguntas.contains(pergunta)) {
            System.out.println("Pergunta ja existe no arquivo!");
            return;
        }
        perguntas.add(pergunta);
        try {
            Files.write(ArquivosEPerguntas.FORMULARIO, perguntas);
        }catch (IOException e) {
            System.out.println("Não foi possivel salvar o arquivo");
        }
        System.out.println("\nPergunta adicionada!");
    }

    private void alterarPergunta() {
        if(!(verificarVazio())) {
            List<String> perguntas = arquivosEPerguntas.carregarFormulario();
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.println("Qual pergunta voce deseja alterar?");
            listarPerguntasDisponiveis();
            int resposta = sc.nextInt();
            sc.nextLine();
            if (isIndicePerguntaValido(resposta)) {
                System.out.println("Digite a pergunta modificada:");
                System.out.print("R: ");
                String perguntaMod = sc.nextLine();
                perguntas.set((resposta + QUANTIDADE_PERGUNTAS_INICIAIS), perguntaMod);
                try {
                    Files.write(ArquivosEPerguntas.FORMULARIO, perguntas);
                } catch (IOException e) {
                    System.out.println("Impossivel ler o arquivo");
                }
                System.out.println("Pergunta modificada!");
            }
        }
    }

    private void excluirPergunta() {
        if(!(verificarVazio())) {
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.println("Qual pergunta voce quer excluir?");
            listarPerguntasDisponiveis();
            int resposta = sc.nextInt();
            sc.nextLine();
            if(isIndicePerguntaValido(resposta)) {
                List<String> perguntasBrutas = arquivosEPerguntas.carregarPerguntasBrutas();
                System.out.println("Deseja excluir e pergunta " + resposta + "?(Sim) (Nao)");
                String validacao = sc.nextLine();
                if (!(validacao.equalsIgnoreCase("sim"))) {
                    System.out.println("\nCancelando...");
                    return;
                }
                perguntasBrutas.set((resposta + QUANTIDADE_PERGUNTAS_INICIAIS), "");
                List<String> perguntasFinais = perguntasBrutas.stream().filter(pergunta -> !(pergunta.trim().isEmpty())).toList();
                try {
                    Files.write(ArquivosEPerguntas.FORMULARIO, perguntasFinais);
                } catch (IOException e) {
                    System.out.println("Impossivel acessar o arquivo!");
                }
                System.out.println("\nPergunta excluida!");
            }
        }
    }

    private boolean verificarVazio () {
        System.out.println("-----------------------------------------------------------------------------------");
        if (arquivosEPerguntas.carregarPerguntasValidas().isEmpty()) {
            System.out.println("Nenhuma pergunta adicionada!");
            return true;
        }
        return false;
    }

    private void listarPerguntasDisponiveis () {
        List<String> validas = arquivosEPerguntas.carregarPerguntasValidas();
        for (int i = 0; i < validas.size(); i++) {
            System.out.println((i + 1) + " - " + validas.get(i));
        }
        System.out.print("R: ");
    }

    private boolean isIndicePerguntaValido (int resposta) {
        if (resposta > arquivosEPerguntas.carregarPerguntasValidas().size()) {
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.println("Digite uma opção válida!");
            return false;
        }
        if (resposta <= 0) {
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.println("Digite uma opção válida!");
            return false;
        }
        return true;
    }
}
