package com.example.organizze.Activity.Model;

import com.example.organizze.Activity.config.ConfiguracaoFirabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public class Usuario {

    private String nome;
    private String email;
    private String senha;
    private String idUsuario;
    private double receitaTotal = 0.0;
    private double despesaTotal = 0.0;

    public Usuario() {
    }

    @Exclude
    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void salvar(){
        DatabaseReference firebase = ConfiguracaoFirabase.getFirabaseDatasabe();
        firebase.child("Usuarios")
                .child(this.idUsuario)
                .setValue(this);
    }

    public double getReceitaTotal() {
        return receitaTotal;
    }

    public void setReceitaTotal(double receitaTotal) {
        this.receitaTotal = receitaTotal;
    }

    public double getDespesaTotal() {
        return despesaTotal;
    }

    public void setDespesaTotal(double despesaTotal) {
        this.despesaTotal = despesaTotal;
    }
}
