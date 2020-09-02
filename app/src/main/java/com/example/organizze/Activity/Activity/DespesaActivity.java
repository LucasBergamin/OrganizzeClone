package com.example.organizze.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.organizze.Activity.Model.Movimentacao;
import com.example.organizze.Activity.Model.Usuario;
import com.example.organizze.Activity.config.ConfiguracaoFirabase;
import com.example.organizze.Activity.helper.Base64Custom;
import com.example.organizze.Activity.helper.DateCustom;
import com.example.organizze.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DespesaActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private Movimentacao movimentacao;
    private DatabaseReference reference = ConfiguracaoFirabase.getFirabaseDatasabe();
    private FirebaseAuth autentificacao = ConfiguracaoFirabase.getFirebaseAutentificacao();
    private Double despesaTotal = 0.0;
    private Double despesaAtualizada = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesa);

        campoData = findViewById(R.id.editData);
        campoCategoria = findViewById(R.id.editCategoria);
        campoDescricao = findViewById(R.id.editDescricao);
        campoValor = findViewById(R.id.editValor);

        campoData.setText(DateCustom.DataAtual());

        recuperarDespesaTotal();

    }

    public void salvarDespesa(View view){
        if(validarCampos()){

            movimentacao = new Movimentacao();

            String data = campoData.getText().toString();
            Double valor = Double.parseDouble(campoValor.getText().toString());

            movimentacao.setValor( valor );
            movimentacao.setCategoria( campoCategoria.getText().toString());
            movimentacao.setDescricao( campoDescricao.getText().toString());
            movimentacao.setData( data);
            movimentacao.setTipo( "d" );

            despesaAtualizada =  despesaTotal + valor;
            atualizarDespesa(despesaAtualizada);

            movimentacao.salvar(data);
            finish();
        }
    }

    public Boolean validarCampos() {
        if (!campoValor.getText().toString().isEmpty()) {
            if (!campoData.getText().toString().isEmpty()) {
                if (!campoCategoria.getText().toString().isEmpty()) {
                    if (!campoDescricao.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Despesa preenchida com sucesso", Toast.LENGTH_SHORT).show();
                        return true;
                    } else {
                        Toast.makeText(getApplicationContext(), "descrição não preenchida", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Campo categoria não preenchido", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                Toast.makeText(getApplicationContext(), "data não preenchida", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(getApplicationContext(), "Valor não preenchido", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void recuperarDespesaTotal(){

        String emailUsuario = autentificacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = reference.child("Usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                despesaTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void atualizarDespesa(double despesa){

        String emailUsuario = autentificacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = reference.child("Usuarios").child(idUsuario);

        usuarioRef.child("despesaTotal").setValue(despesa);

    }
}