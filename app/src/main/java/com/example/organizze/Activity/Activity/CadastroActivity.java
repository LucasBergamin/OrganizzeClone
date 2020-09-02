package com.example.organizze.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.Activity.Model.Usuario;
import com.example.organizze.Activity.config.ConfiguracaoFirabase;
import com.example.organizze.Activity.helper.Base64Custom;
import com.example.organizze.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import org.w3c.dom.Text;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private Button btnCadastrar;
    private FirebaseAuth autentificacao;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        campoNome = findViewById(R.id.editNome);
        campoSenha = findViewById(R.id.editSenha);
        campoEmail = findViewById(R.id.editEmail);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String TextoNome = campoNome.getText().toString();
                String TextoEmail = campoEmail.getText().toString();
                String TextoSenha= campoSenha.getText().toString();

                if(!TextoNome.isEmpty()){
                    if(!TextoEmail.isEmpty()){
                        if(!TextoSenha.isEmpty()){
                            usuario = new Usuario();

                            usuario.setNome(TextoNome);
                            usuario.setEmail(TextoEmail);
                            usuario.setSenha(TextoSenha);

                            cadastrarUsuario();

                        }else{
                            Toast.makeText(getApplicationContext(), "Preencha a senha!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Preencha o Email!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Preencha o nome!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void cadastrarUsuario(){
        autentificacao = ConfiguracaoFirabase.getFirebaseAutentificacao();
        autentificacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String idUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                    usuario.setIdUsuario(idUsuario);
                    usuario.salvar();
                    finish();
                }else{
                    String exececao = "";
                    try{
                        throw task.getException();
                    }catch(FirebaseAuthWeakPasswordException e){
                        exececao = "Digite uma senha mais forte!";
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        exececao = "Digite um email válido";
                    }catch(FirebaseAuthUserCollisionException e){
                        exececao = "Esta conta já foi cadastrada";
                    }catch(Exception e){
                        exececao = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), exececao , Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}