package com.example.ifsp.easymoney;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddDividaActivity extends AppCompatActivity {

    private FloatingActionButton btn_addDivida;
    private Integer devedorId;
    private SQLiteDatabase database;
    private EditText ed_data, ed_valor, ed_desc;
    private DatePickerDialog.OnDateSetListener onDateSetListener;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_divida);
        Intent intent = getIntent();
        devedorId = Integer.parseInt(intent.getStringExtra("devedorId"));
        btn_addDivida = (FloatingActionButton) findViewById(R.id.btn_adicionarDivida);
        ed_data = (EditText) findViewById(R.id.ed_data);
        ed_valor = (EditText) findViewById(R.id.ed_valor);
        ed_desc = (EditText) findViewById(R.id.ed_desc);

        btn_addDivida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDivida();
            }
        });

    }


    private void addDivida(){
        database = openOrCreateDatabase("easy_money", MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("SELECT nome " +
                "FROM devedor WHERE id="+devedorId, null);
       if(cursor != null){
           database.execSQL("INSERT INTO divida (id_devedor,data,descricao,valor) VALUES (idDevedor,data,descricao,valor)");
           database.close();
           finish();
       }else{
           Toast.makeText(this, "Erro ao inserir divida.", Toast.LENGTH_SHORT).show();
       }
        database.close();


    }


}
