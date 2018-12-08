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
        ed_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int ano = calendar.get(Calendar.YEAR);
                int mes = calendar.get(Calendar.MONTH);
                int dia = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        AddDividaActivity.this,
                        onDateSetListener,
                        ano, mes, dia
                );
                dialog.show();
            }
        });

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int ano, int mes, int dia) {
                String data = dia +"/"+ (mes+1) +"/"+ano;
                ed_data.setText(data);
            }
        };
        btn_addDivida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = ed_data.getText().toString().trim();
                String desc = ed_desc.getText().toString().trim();
                String valorString = ed_valor.getText().toString().trim();
                if (!data.equals("") && !desc.equals("") && !valorString.equals("")) {
                    if (desc.length() <= 255) {
                        Float valor = Float.parseFloat(valorString);
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            Date date = format.parse(data);
                            if (valor > 0) {
                                if (new Date().after(date)) {
                                    addDivida(valor, data, desc);
                                }else {
                                    Toast.makeText(AddDividaActivity.this, "A data da dívida deve ser anterior a data atual.", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(AddDividaActivity.this, "O valor da dívida deve ser maior do que 0.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(AddDividaActivity.this, "A descrição deve ter no máximo 255 caracteres.", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(AddDividaActivity.this,
                            "Todos os campos devem ser preenchidos.",
                            Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void addDivida(Float valor, String data, String desc){
        database = openOrCreateDatabase("easy_money", MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("SELECT nome " +
                "FROM devedor WHERE id="+devedorId, null);
        if(cursor != null){
            database.execSQL("INSERT INTO divida (id_devedor,data,descricao,valor) VALUES ("
                    + devedorId+",'" +
                    data +"','" +
                    desc +"'," +
                    valor+")");
            database.close();
            finish();
            Toast.makeText(this, "Dívida inserida com sucesso.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Erro ao inserir divida.", Toast.LENGTH_SHORT).show();
        }
        database.close();
    }


}
