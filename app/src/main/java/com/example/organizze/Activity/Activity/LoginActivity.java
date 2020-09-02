package com.example.organizze.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.Activity.Model.Usuario;
import com.example.organizze.Activity.config.ConfiguracaoFirabase;
import com.example.organizze.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class LoginActivity extends AppCompatActivity {
    private EditText campoEmail, campoSenha;
    private Button btnLogar;
    private Usuario usuario;
    private FirebaseAuth autentificacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);
        btnLogar = findViewById(R.id.btnEntrar);

        btnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String TextoEmail = campoEmail.getText().toString();
                String TextoSenha= campoSenha.getText().toString();

                if(!TextoEmail.isEmpty()){
                    if(!TextoSenha.isEmpty()){

                        usuario = new Usuario();
                        usuario.setEmail(TextoEmail);
                        usuario.setSenha(TextoSenha);
                        validarLogin();

                    }else{
                        Toast.makeText(getApplicationContext(), "Preencha a senha!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Preencha o Email!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void validarLogin(){

        autentificacao = ConfiguracaoFirabase.getFirebaseAutentificacao();
        autentificacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    abrirTelaPrincipal();
                }else{
                    String exececao = "";
                    try{
                        throw task.getException();
                    }catch(FirebaseAuthInvalidUserException e){
                        exececao = "Usuário não cadastrado";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        exececao = "Email ou senha não correspondem a um usuário cadastrado";
                    }catch(Exception e){
                        exececao = "Erro ao logar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), exececao, Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    public void abrirTelaPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }
}