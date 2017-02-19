package com.example.alejandro.m8_practica_mapilla;

import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class MapaConcreto extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mapa;

    ArrayList<LatLng> arrayPosiciones;
    String matricula, fecha;
    ObtenerUbicaciones ou = new ObtenerUbicaciones();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        matricula = getIntent().getStringExtra("matricula");
        ou.execute();

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("MapaConcreto Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
 AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    //public void pintarLineaMapa(ArrayList<LatLng> ubicaciones) {
    //    mMap.addPolyline(new PolylineOptions().addAll(ubicaciones).color(Color.GREEN));
    //}

    private class ObtenerUbicaciones extends AsyncTask<Void, Void, Boolean> {


        protected Boolean doInBackground(Void... params) {

            boolean vaBien = false;

            HttpClient httpC = new DefaultHttpClient();

            HttpGet httpG = new HttpGet("http://192.168.1.37:8080/WebServiceYEISON/webresources/ultima/" + matricula);

            httpG.setHeader("content-type", "application/json");

            try {

                String ejecutado = EntityUtils.toString(httpC.execute(httpG).getEntity());

                JSONArray matriculas = new JSONArray(ejecutado);
                arrayPosiciones = new ArrayList<>();

                for (int i = 0; i < matriculas.length(); i++) {
                    double latitud = 0, altitud = 0;

                    JSONObject pos = matriculas.getJSONObject(i);
                    matricula = pos.getString("matricula");
                    latitud = pos.getDouble("latitud");
                    altitud = pos.getDouble("altitud");
                    fecha = pos.getString("data");
                    arrayPosiciones.add(new LatLng(latitud, altitud));

                    PolylineOptions polylineOptions = new PolylineOptions().add(new LatLng(latitud, altitud));
                    mapa.addPolyline(polylineOptions);


                }
                if (!ejecutado.equals("true")) {
                    vaBien = true;
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return vaBien;
        }


    }
}