package com.example.organizze.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.Activity.Model.Movimentacao;
import com.example.organizze.Activity.Model.Usuario;
import com.example.organizze.Activity.config.ConfiguracaoFirabase;
import com.example.organizze.Activity.helper.Base64Custom;
import com.example.organizze.Activity.helper.DateCustom;
import com.example.organizze.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ReceitasActivity extends AppCompatActivity {
    private EditText campoValor;
    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private Movimentacao movimentacao;
    private FirebaseAuth autentificacao = ConfiguracaoFirabase.getFirebaseAutentificacao();
    private DatabaseReference reference = ConfiguracaoFirabase.getFirabaseDatasabe();
    private Double receitaTotal = 0.0;
    private Double receitaAtualizada = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        campoData = findViewById(R.id.editData);
        campoCategoria = findViewById(R.id.editCategoria);
        campoDescricao = findViewById(R.id.editDescricao);
        campoValor = findViewById(R.id.editValor);

        campoData.setText(DateCustom.DataAtual());

        recuperarReceita();

    }

    public void salvarReceita(View view){
        if(validarCampos()){
            movimentacao = new Movimentacao();

            String data = campoData.getText().toString();
            Double valor = Double.valueOf(campoValor.getText().toString());

            movimentacao.setValor(valor);
            movimentacao.setCategoria(campoCategoria.getText().toString());
            movimentacao.setDescricao(campoDescricao.getText().toString());
            movimentacao.setData(data);
            movimentacao.setTipo("r");

            receitaAtualizada = receitaTotal + valor;
            atualizarReceita(receitaAtualizada);

            movimentacao.salvar(data);
            finish();
        }
    }

    public boolean validarCampos(){
        if(!campoValor.getText().toString().isEmpty()){
            if(!campoData.getText().toString().isEmpty()){
                if(!campoCategoria.getText().toString().isEmpty()){
                    if(!campoDescricao.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "Receita preenchida com sucesso", Toast.LENGTH_SHORT).show();
                        return true;
                    }else{
                        Toast.makeText(getApplicationContext(), "descrição não preenchida", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Categoria não preenchida", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else{
                Toast.makeText(getApplicationContext(), "data não preenchida", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(getApplicationContext(), "valor não preenchida", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void recuperarReceita(){
        String emailUsuario = autentificacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = reference.child("Usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                receitaTotal = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void atualizarReceita(double receita){
        String emailUsuario = autentificacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = reference.child("Usuarios").child(idUsuario);

        usuarioRef.child("receitaTotal").setValue(receita);

    }
}