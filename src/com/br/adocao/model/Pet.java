package com.br.adocao.model;

import com.br.adocao.exception.IdadeInvalidaException;
import com.br.adocao.exception.NomeInvalidoException;
import com.br.adocao.exception.PesoInvalidoException;
import com.br.adocao.exception.RacaInvalidaException;
import com.br.adocao.model.enums.Sexo;
import com.br.adocao.model.enums.TipoPet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pet {
    private static final String VAZIO = "NÃO INFORMADO";

    private String nome;
    private String sobrenome;
    private TipoPet tipo;
    private Sexo sexo;
    private String idade;
    private String peso;
    private String raca;
    private Endereco endereco;
    private Path caminhoArquivo;

    public Pet(String nome, String sobrenome, String tipo, String sexo, String idade, String peso, String raca, Endereco endereco) {
        this.setNome(nome);
        this.setSobrenome(sobrenome);
        this.setTipo(TipoPet.valueOf(tipo.toUpperCase()));
        this.setSexo(Sexo.valueOf(sexo.toUpperCase()));
        this.setIdade(idade);
        this.setPeso(peso);
        this.setRaca(raca);
        this.setEndereco(endereco);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) throws NomeInvalidoException {
        if (nome.matches(".*[^A-Za-zÀ-ú\\s].*")) {
            throw new NomeInvalidoException("Nome inválido!");
        }
        if (nome.matches("\\.")) {
            this.nome = VAZIO;
        }else {
            this.nome = nome;
        }
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        if (sobrenome.matches(".*[^A-Za-zÀ-ú\\s].*")) {
            if (sobrenome.matches("\\.")) {
                this.sobrenome = VAZIO;
            }else {
                throw new NomeInvalidoException("Sobrenome inválido!");
            }
        }
        this.sobrenome = sobrenome;
    }

    public TipoPet getTipo() {
        return tipo;
    }

    public void setTipo(TipoPet tipo) {
        this.tipo = tipo;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        String idadeFormatada = idade.replace(',', '.');
        if (!(idade.matches("^\\d+([.,]\\d+)?$"))) {
            if (idade.matches("\\.")) {
                this.idade = VAZIO;
                return;
            }else {
                throw new IdadeInvalidaException("Idade inválida, deve conter apenas números, pontos ou vírgulas!");
            }
        }
        try {
            double idadeValor = Double.parseDouble(idadeFormatada);
            if (idadeValor < 0 || idadeValor > 30) {
                throw new IdadeInvalidaException("Idade inválida! A idade deve estar entre 0 e 30.");
            }
            this.idade = idade + " anos";
        } catch (NumberFormatException e) {
            throw new IdadeInvalidaException("Idade inválida! Formato numérico incorreto.");
        }

    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        String pesoFormatado = peso.replace(',', '.');
        if (!(peso.matches("^\\d+([.,]\\d+)?$"))) {
            if (peso.matches("\\.")) {
                this.peso = VAZIO;
                return;
            }else {
                throw new PesoInvalidoException("Peso inválido, deve conter apenas números, pontos ou vírgulas!");
            }
        }
        try {
            double pesoValor = Double.parseDouble(pesoFormatado);
            if (pesoValor < 0.5 || pesoValor > 100) {
                throw new PesoInvalidoException("Peso inválido! O peso deve estar entre 0.5 kg e 100 kg.");
            }
            this.peso = peso + " kg";
        } catch (NumberFormatException e) {
            throw new PesoInvalidoException("Peso inválido! Formato numérico incorreto.");
        }

    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        if (raca.matches(".*[^A-Za-zÀ-ú\\s].*")) {
            if (raca.matches("\\.")) {
                this.raca = VAZIO;
            } else {
                throw new RacaInvalidaException("Raça inválida! Apenas letras e espaços.");
            }
        } else {
            this.raca = raca;
        }
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Path getCaminhoArquivo() {
        return caminhoArquivo;
    }

    public void setCaminhoArquivo(Path caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    @Override
    public String toString() {
        return "1 - " + getNome() + " " + getSobrenome()+
                "\n2 - " + getTipo() +
                "\n3 - " + getSexo() +
                "\n4 - " + endereco.toString() +
                "\n5 - " + getIdade() +
                "\n6 - " + getPeso() +
                "\n7 - " + getRaca();
    }

    public String resumo() {

        List<String> informacoesTxt;
        String informacoesReduzidas = "";
        String informacoesFormatadas = "";
        try {
            informacoesTxt = Files.readAllLines(getCaminhoArquivo());
            informacoesReduzidas = informacoesTxt.stream().map(line -> line.replaceFirst("\\d", ""))
                    .reduce("", (acumulador, elemento) -> {
                        if(acumulador.isEmpty()){
                            return elemento;
                        }
                        return acumulador + elemento;
                    });
            informacoesFormatadas = informacoesReduzidas.replaceAll("\\[EXTRA - PERGUNTA NOVA ADICIONADA]", "[EXTRA]");
        } catch (IOException e) {
            System.out.println("Impossivel ler o arquivo!");
        }
        return informacoesFormatadas;
    }
}
