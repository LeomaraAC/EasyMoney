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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class DevedoresActivity extends AppCompatActivity {
    private SQLiteDatabase database;
    private ListView listaDevedores;
    private ArrayList<Integer> arrayIds;
    private AlertDialog.Builder builder;
    private Integer idApagar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devedores);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbrirActivityAddDevedor();
            }
        });
        listaDevedores = (ListView) findViewById(R.id.listaDevedores);
        listaDevedores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                carregarDividas(arrayIds.get(i));
            }
        });
        listaDevedores.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                idApagar = arrayIds.get(i);
                builder = new AlertDialog.Builder(DevedoresActivity.this);
                builder.setMessage("Deseja apagar esse devedor?")
                        .setPositiveButton("Apagar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                apagarDevedor(idApagar);
                            }
                        })
                        .setNegativeButton("Cancelar", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
        criarBancoDados();
        carregarDevedores();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarDevedores();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_devedores, menu);
        return true;
    }

    private void apagarDevedor(Integer idDevedor) {
        try{
            database = openOrCreateDatabase("easy_money", MODE_PRIVATE, null);
            database.execSQL("DELETE FROM devedor WHERE id = " +idDevedor);
            database.close();
            carregarDevedores();
            Toast.makeText(this, "Devedor apagado com sucesso.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void criarBancoDados() {
        try {
            database = openOrCreateDatabase("easy_money", MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS devedor(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nome VARCHAR(100))");
            database.execSQL("CREATE TABLE IF NOT EXISTS divida(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "id_devedor INTEGER," +
                    "data VARCHAR NOT NULL," +
                    "descricao VARCHAR(255) NOT NULL," +
                    "valor REAL NOT NULL," +
                    "FOREIGN KEY (id_devedor) REFERENCES devedor(id))");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void AbrirActivityAddDevedor(){
        Intent intentAddDevedor = new Intent(this,AddDevedorActivity.class);
        startActivity(intentAddDevedor);
    }

    private void carregarDevedores() {
        try {
            database = openOrCreateDatabase("easy_money", MODE_PRIVATE, null);
            Cursor cursor = database.rawQuery("SELECT devedor.id, devedor.nome, IFNULL(SUM(divida.valor),0) " +
                    "as valor FROM devedor LEFT JOIN divida ON devedor.id = divida.id_devedor " +
                    "GROUP BY devedor.nome ORDER BY valor DESC", null);
            ArrayList<String> linhas = new ArrayList<String>();
            arrayIds = new ArrayList<Integer>();
            ArrayAdapter adapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    linhas
            );
            listaDevedores.setAdapter(adapter);
            cursor.moveToFirst();
            while (cursor != null) {
                linhas.add(cursor.getString(1) + " - R$ " + cursor.getString(2));
                arrayIds.add(cursor.getInt(0));
                cursor.moveToNext();
            }
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void carregarDividas(Integer devedorId){
        Intent intentAbriDivida = new Intent(this, DividasActivity.class);
        intentAbriDivida.putExtra("devedorId", Integer.toString(devedorId));
        startActivity(intentAbriDivida);
    }
}
