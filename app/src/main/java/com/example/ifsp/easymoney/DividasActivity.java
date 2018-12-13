package com.example.ifsp.easymoney;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DividasActivity extends AppCompatActivity {

    private ListView listaDividas;
    private TextView tvNomeDevedor;
    private TextView tvValor;
    private SQLiteDatabase database;
    private Integer devedorId;
    private ArrayList<Integer> arrayIds;
    private Integer idApagar;
    private AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dividas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbrirActivityAddDivida();
            }
        });
        listaDividas = (ListView) findViewById(R.id.listaDividas);
        tvNomeDevedor = (TextView) findViewById(R.id.tvNomeDevedor);
        tvValor = (TextView) findViewById(R.id.tvValor);
        Intent intent = getIntent();
        devedorId = Integer.parseInt(intent.getStringExtra("devedorId"));
        listaDividas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                idApagar = arrayIds.get(i);
                builder = new AlertDialog.Builder(DividasActivity.this);
                builder.setMessage("Deseja apagar esse devedor?")
                        .setPositiveButton("Apagar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                apagarDivida(idApagar);
                            }
                        })
                        .setNegativeButton("Cancelar", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
        carregarNomeDevedor();
        carregarDividas();
    }
    private void carregarNomeDevedor() {
        database = openOrCreateDatabase("easy_money", MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("SELECT nome " +
                "FROM devedor WHERE id="+devedorId, null);
        if (cursor.moveToFirst()) {
            tvNomeDevedor.setText(cursor.getString(0));
        }
        database.close();
    }

    private void carregarTotalDivida() {
        database = openOrCreateDatabase("easy_money", MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("SELECT IFNULL(SUM(divida.valor),0) as valor " +
                "FROM devedor LEFT JOIN divida ON devedor.id = divida.id_devedor " +
                "WHERE devedor.id="+devedorId +" GROUP BY devedor.nome", null);
        if (cursor.moveToFirst()) {
            tvValor.setText("Total: R$ "+cursor.getString(0));
        }
        database.close();
    }

    private void carregarDividas() {
        try {
            carregarTotalDivida();
            database = openOrCreateDatabase("easy_money", MODE_PRIVATE, null);
            Cursor cursor = database.rawQuery("SELECT id, data, descricao, valor " +
                    "FROM divida WHERE id_devedor="+devedorId+" ORDER BY id DESC", null);
            ArrayList<String> linhas = new ArrayList<String>();
            arrayIds = new ArrayList<Integer>();
            ArrayAdapter adapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    linhas
            );
            listaDividas.setAdapter(adapter);
            cursor.moveToFirst();
            while (cursor != null) {
                linhas.add(cursor.getString(1) + " - " + cursor.getString(2) + " - R$ "+cursor.getString(3));
                arrayIds.add(cursor.getInt(0));
                cursor.moveToNext();
            }
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void apagarDivida(Integer idDivida) {
        try{
            database = openOrCreateDatabase("easy_money", MODE_PRIVATE, null);
            database.execSQL("DELETE FROM divida WHERE id = " +idDivida);
            database.close();
            carregarDividas();
            Toast.makeText(this, "Dívida apagada com sucesso.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void AbrirActivityAddDivida(){
        Intent intentAddDivida = new Intent(this,AddDividaActivity.class);
        intentAddDivida.putExtra("devedorId", Integer.toString(devedorId));
        startActivity(intentAddDivida);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarDividas();
    }
}
