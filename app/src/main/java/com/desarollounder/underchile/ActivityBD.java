package com.desarollounder.underchile;

import android.*;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ActivityBD extends AppCompatActivity implements View.OnClickListener,OnMapReadyCallback {

    private GoogleMap mMap;
    TextView txtTodo;
    Button btnTodo, btnId;

    //Método BD 1
    ObtenerWebService hiloconexion;

    String IP = "http://crisgaray.esy.es";
    String GET = IP + "/obtener_locales.php";
    String GET_ID = IP + "/obtener_local_id.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bd);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        txtTodo = (TextView)findViewById(R.id.txtTodo);
        btnTodo = (Button)findViewById(R.id.btnTodos);
        btnId = (Button)findViewById(R.id.btnId);
        btnTodo.setOnClickListener(this);
        btnId.setOnClickListener(this);
    }

    private void miUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //actualizarUbicacion(location);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        LatLng coordenadas = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 14);
        //if (marcador != null) marcador.remove();
        mMap.moveCamera(miUbicacion);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnTodos:
                txtTodo.setText("");
                hiloconexion = new ObtenerWebService();
                hiloconexion.execute(GET,"1");
                break;
            case R.id.btnId:



                //String cadenaLlamada = GET_ID + "?IdLocal=1";
                //hiloconexion = new ObtenerWebService();
                //hiloconexion.execute(cadenaLlamada,"2");
            default:
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        final ProgressDialog progres = new ProgressDialog(ActivityBD.this);
        progres.setMessage("Cargando...");
        progres.show();

        miUbicacion();
        cargaInicio();

        progres.dismiss();

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent1 = new Intent(getApplicationContext(),ActivityEventDetalle.class);
                String title = marker.getTitle();
                intent1.putExtra("IdLocal",title);
                startActivity(intent1);
            }
        });
    }

    private void añadirDeptos(String s){
        mMap.clear();
        String datLocal[] = s.split(";");

        int e = 0;
        for (int i=0; i < datLocal.length; i+=4){
            e = i;
            LatLng coordenadas = new LatLng(Double.parseDouble(datLocal[e]), Double.parseDouble(datLocal[e+=1]));
            Marker mimark = mMap.addMarker(new MarkerOptions()
                    .position(coordenadas)
                    .title(datLocal[e+=1] + " - " + datLocal[e+=1])
                    .snippet("Pinche aquí para más información.")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.hand)));
            //mimark.showInfoWindow();
            }
    }

    private void cargaInicio(){
        hiloconexion = new ObtenerWebService();
        hiloconexion.execute(GET,"1");
    }

    public class ObtenerWebService extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            txtTodo.setText(s);
            añadirDeptos(s);
            //super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @Override
        protected String doInBackground(String... params) {
            String cadena = params[0];
            URL url = null; //URL donde obtendremos información
            String devuelve = "";

            if (params[1]=="1"){ //Consultar todos los locales
                try{
                    url = new URL(cadena);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Abrimos conexión
                    connection.setRequestProperty("User-Agent","Mozilla/5.0" + "(Linux; Android 1.5; es-Es) Ejemplo HTTP");

                    int respuesta = connection.getResponseCode();
                    StringBuilder result = new StringBuilder();

                    if (respuesta == HttpsURLConnection.HTTP_OK){
                        InputStream in = new BufferedInputStream(connection.getInputStream()); //Preparo la cadena de entrada
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in)); //La instroduzco en un BufferedReader

                        //Lo siguiente se hace porque el JSONObject necesita un string y se tiene que transformar
                        //el BufferedReader a string. Se hace a través de un StringBuilder

                        String line;
                        while ((line = reader.readLine()) != null){
                            result.append(line);     //Pasamos toda la entrada al StringBuilder
                        }

                        //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto
                        JSONObject respuestaJSON = new JSONObject(result.toString()); //Se crea un JSONObject a partir del StringBuilder

                        //Accedemos al vector de resultados
                        JSONArray resultJSON = respuestaJSON.getJSONArray("Local"); //results es el nombre del campo en el JSON
                        for (int i=0; i< resultJSON.length();i++){
                            devuelve = devuelve +
                                    resultJSON.getJSONObject(i).getString("latLocal") + ";" +
                                    resultJSON.getJSONObject(i).getString("lonLocal") + ";" +
                                    resultJSON.getJSONObject(i).getString("IdLocal") + ";" +
                                    resultJSON.getJSONObject(i).getString("nombreLocal") + ";";

                            //mMap.clear();
                            //LatLng coordenadas = new LatLng(Double.parseDouble(resultJSON.getJSONObject(i).getString("lat")),
                            //                            Double.parseDouble(resultJSON.getJSONObject(i).getString("lon")));
                            //mMap.addMarker(new MarkerOptions()
                            //    .position(coordenadas)
                            //    .title(resultJSON.getJSONObject(i).getString("nombre"))
                            //    .snippet("Pinche aquí para más información."));
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return devuelve;
            }
            else if (params[1]=="2"){ //Busqueda por ID
                try{
                    url = new URL(cadena);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Abrimos conexión
                    connection.setRequestProperty("User-Agent","Mozilla/5.0" + "(Linux; Android 1.5; es-Es) Ejemplo HTTP");

                    int respuesta = connection.getResponseCode();
                    StringBuilder result = new StringBuilder();

                    if (respuesta == HttpsURLConnection.HTTP_OK){
                        InputStream in = new BufferedInputStream(connection.getInputStream()); //Preparo la cadena de entrada
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in)); //La instroduzco en un BufferedReader

                        //Lo siguiente se hace porque el JSONObject necesita un string y se tiene que transformar
                        //el BufferedReader a string. Se hace a través de un StringBuilder

                        String line;
                        while ((line = reader.readLine()) != null){
                            result.append(line);     //Pasamos toda la entrada al StringBuilder
                        }

                        //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto
                        JSONObject respuestaJSON = new JSONObject(result.toString()); //Se crea un JSONObject a partir del StringBuilder

                        //Accedemos al vector de resultados
                        //JSONArray resultJSON = respuestaJSON.getJSONArray("Local"); //results es el nombre del campo en el JSON
                        //for (int i=0; i< resultJSON.length();i++){
                            devuelve = devuelve +
                                    respuestaJSON.getJSONObject("Local").getString("dirLocal") + ";" +
                                    respuestaJSON.getJSONObject("Local").getString("telefonoLocal") + ";" +
                                    respuestaJSON.getJSONObject("Local").getString("correoLocal") + ";" +
                                    respuestaJSON.getJSONObject("Local").getString("nombreComuna") + ";" +
                                    respuestaJSON.getJSONObject("Local").getString("nombreLocal");
                        //}
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return devuelve;
            }

            return null;
        }
    }
}
