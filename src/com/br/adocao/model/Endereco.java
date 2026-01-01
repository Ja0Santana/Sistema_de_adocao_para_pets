package com.br.adocao.model;

public class Endereco {
    private static final String VAZIO = "NÃO INFORMADO";

    private String numero;
    private String cidade;
    private String rua;

    public Endereco(String numero, String cidade, String rua) {
        this.setNumero(numero);
        this.setCidade(cidade);
        this.setRua(rua);
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        if (numero.matches(".*[^A-Za-z0-9\\\\-\\\\/\\\\.].*")) {
            this.numero = VAZIO;
        }else {
            this.numero = numero;
        }
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        if (cidade != null) {
            this.cidade = cidade.replace(",", "");
        } else {
            this.cidade = VAZIO;
        }
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        if (rua != null) {
            this.rua = rua.replace(",", "");
        } else {
            this.rua = VAZIO;
        }
    }


    @Override
    public String toString() {
        String numeroVisual = getNumero().equals(".") ? "NÃO INFORMADO" : getNumero();
        return numeroVisual + ", " +
                getCidade() + ", " +
                getRua() + ", ";
    }
}
