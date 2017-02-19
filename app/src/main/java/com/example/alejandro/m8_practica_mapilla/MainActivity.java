package com.example.alejandro.m8_practica_mapilla;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnConcreto;
    Button btnTodos;
    EditText etMatricula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         btnConcreto = (Button) findViewById(R.id.concreto);
         btnTodos = (Button) findViewById(R.id.todos);
         etMatricula = (EditText) findViewById(R.id.matricula);


    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case (R.id.concreto):
                if(verificarVacio()) {
                    Autobuses autobuses = new Autobuses();
                    autobuses.execute();
                    if (autobuses.doInBackground()) {
                        Intent intent1 = new Intent(this, MapaConcreto.class);
                        intent1.putExtra("matricula", etMatricula.getText().toString().trim());
                        startActivity(intent1);
                    } else {
                        Toast t2 = Toast.makeText(getApplicationContext(), "No coincide con ninguna matricula de la BBDD", Toast.LENGTH_SHORT);
                        t2.show();
                    }
                }else{ break;}

            case (R.id.todos):
                Intent intent = new Intent(this, MapaTodos.class);
                startActivity(intent);


        }
    }


    private class Autobuses extends AsyncTask<Void, Void, Boolean> {

        String matriculaComparar = etMatricula.getText().toString().trim();

        @Override
        protected Boolean doInBackground(Void... voids) {

            boolean correcto = false;
            HttpClient httpC = new DefaultHttpClient();//Esta deprecated
            HttpGet httpG = new HttpGet("http://192.168.180.10:8080/WebserviceYEISON/webresources/generic");//Ojo Deprecated
            httpG.setHeader("content-type", "application/json");

            try {

                String respStr = EntityUtils.toString(httpC.execute(httpG).getEntity());
                JSONArray matriculas = new JSONArray(respStr);

                for (int i = 0; i < matriculas.length(); i++) {
                    JSONObject bus = matriculas.getJSONObject(i);
                    String matricula = bus.getString("matricula");
                    if(matricula.equals(matriculaComparar)){
                        correcto = true;
                        break;
                    }else{
                        correcto = false;
                    }
                }

            } catch (ClientProtocolException c) {
                c.printStackTrace();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return correcto;

        }

    }

        private boolean verificarVacio() {

        String matricula = etMatricula.getText().toString().trim();

        if(matricula.isEmpty() || matricula.length() == 0 || matricula.equals("") || matricula == null)
        {
            Toast t1 = Toast.makeText(getApplicationContext(),"El campo de matricula está vacío", Toast.LENGTH_SHORT);
            t1.show();
            return false;
        }
        else
        {
            return true;
        }
    }
}
