package com.esucri.mobile.androidcrud;

import java.io.Serializable;

public class Cliente implements Serializable {

    private int id;
    private String nome;
    private Integer idade;
    private String sexo;
    private String uf;
    private boolean vip;

    public Cliente(int id, String nome, Integer idade, String sexo, String uf, boolean vip) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.sexo = sexo;
        this.uf = uf;
        this.vip = vip;
    }

    public int getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public Integer getIdade() {
        return this.idade;
    }

    public String getSexo() {
        return this.sexo;
    }

    public boolean getVip() {
        return this.vip;
    }

    public String getUf() {
        return this.uf;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    @Override
    public boolean equals(Object o) {
        return this.id == ((Cliente) o).id;
    }

    @Override
    public int hashCode() {
        return this.id;
    }
}