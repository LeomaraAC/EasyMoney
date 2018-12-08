package com.example.ifsp.easymoney;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddDevedorActivity extends AppCompatActivity {
    private SQLiteDatabase database;
    private FloatingActionButton addDevedor;
    private EditText nomeDevedor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_devedor);

        nomeDevedor = (EditText) findViewById(R.id.edt_Nome);
        addDevedor = (FloatingActionButton) findViewById(R.id.btn_addDevedor);
        addDevedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDevedor();
            }
        });
    }

    public void addDevedor(){
        String nome = nomeDevedor.getText().toString();
        boolean ret = verificaDevedorExistente(nome);
        if(ret == true){
            try {
                database = openOrCreateDatabase("easy_money",MODE_PRIVATE,null);
                database.execSQL("INSERT INTO devedor (nome) VALUES ('"+nome+"')");
                database.close();
                finish();
                Toast.makeText(this, "Devedor Adicionado com Sucesso", Toast.LENGTH_LONG).show();
            } catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, "Erro ao adicionar devedor ", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this, "Devedor já existente", Toast.LENGTH_SHORT).show();
        }

    }


    private boolean verificaDevedorExistente(String nome){

        try{
            database = openOrCreateDatabase("easy_money",MODE_PRIVATE,null);
            Cursor cursor = database.rawQuery("SELECT count(devedor.nome) " +
                    "FROM devedor WHERE devedor.nome ='"+ nome +"'", null);
            cursor.moveToFirst();
            int cont = cursor.getInt(0);

            if(cont == 0){
                database.close();
                return true;
            }else{
                database.close();
                return false;
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return false;
    }
}
