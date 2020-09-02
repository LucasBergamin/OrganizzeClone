package com.example.organizze.Activity.Activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.organizze.Activity.Adapter.AdapterMovimentacao;
import com.example.organizze.Activity.Model.Movimentacao;
import com.example.organizze.Activity.Model.Usuario;
import com.example.organizze.Activity.config.ConfiguracaoFirabase;
import com.example.organizze.Activity.helper.Base64Custom;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;

import com.example.organizze.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private TextView textoSaudacao, textoSaldo;
    private Double despesaTotal = 0.0;
    private Double ReceitaTotal = 0.0;
    private Double resumoUsuario = 0.0;

    private DatabaseReference reference = ConfiguracaoFirabase.getFirabaseDatasabe();
    private FirebaseAuth autentificacao = ConfiguracaoFirabase.getFirebaseAutentificacao();
    private DatabaseReference usuarioRef;
    private ValueEventListener valueEventListenerUsuario;
    private ValueEventListener valueEventListenerMovimentacoes;

    private RecyclerView recyclerView;
    private AdapterMovimentacao adapterMovimentacao;

    private List<Movimentacao> movimentacaoList = new ArrayList<>();

    private DatabaseReference movimentacaoRef;

    private String mesAnoSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        textoSaudacao = findViewById(R.id.textSaudacao);
        textoSaldo = findViewById(R.id.textSaldo);

        recyclerView = findViewById(R.id.recyclerMovimentos);

        calendarView = findViewById(R.id.calendarView);
        configurarCalendario();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapterMovimentacao = new AdapterMovimentacao(movimentacaoList, this);
        recyclerView.setAdapter(adapterMovimentacao);

        recuperarResumo();

    }



    public void adicionarReceita(View view){
        startActivity(new Intent(this, ReceitasActivity.class));
    }

    public void adicionarDespesa(View view){
        startActivity(new Intent(this, DespesaActivity.class));
    }

    public void configurarCalendario(){
        CharSequence meses[] = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        calendarView.setTitleMonths(meses);

        CalendarDay dataAtual = calendarView.getCurrentDate();
        mesAnoSelecionado = String.valueOf(dataAtual.getMonth() + "" + dataAtual.getYear());


        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String mesSelecionado = String.format("%02d",date.getMonth() + 1);
                mesAnoSelecionado = String.valueOf(mesSelecionado + "" + date.getYear());
            }
        });
    }

    public void recuperarMovimentacoes(){
        String emailUsuario = autentificacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);

        movimentacaoRef = reference.child("Movimentacao")
                .child(idUsuario)
                .child( mesAnoSelecionado );

        valueEventListenerMovimentacoes = movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                movimentacaoList.clear();
                for(DataSnapshot dados: dataSnapshot.getChildren()){
                    Movimentacao movimentacao = dados.getValue( Movimentacao.class );

                    movimentacaoList.add(movimentacao);
                }

                adapterMovimentacao.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void recuperarResumo(){
        String emailUsuario = autentificacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        usuarioRef = reference.child("Usuarios").child(idUsuario);

        valueEventListenerUsuario = usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                despesaTotal = usuario.getDespesaTotal();
                ReceitaTotal = usuario.getReceitaTotal();
                resumoUsuario = ReceitaTotal - despesaTotal;

                DecimalFormat decimalFormat = new DecimalFormat("0.##");
                String resultadoFormatado = decimalFormat.format(resumoUsuario);

                textoSaudacao.setText("Olá " + usuario.getNome());
                textoSaldo.setText("R$ " + resultadoFormatado);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarResumo();
        recuperarMovimentacoes();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioRef.removeEventListener( valueEventListenerUsuario );
        movimentacaoRef.removeEventListener(valueEventListenerMovimentacoes);
    }
}