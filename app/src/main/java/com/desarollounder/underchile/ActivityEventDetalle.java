package com.desarollounder.underchile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.ExceptionCatchingInputStream;

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

public class ActivityEventDetalle extends AppCompatActivity {

    private TextView txtNombre,txtDir,txtComuna,txtTelefono,txtCorreo;
    private ImageView imgLocal;


    ObtenerDatos hiloconexion;

    String IP = "http://undermetal.esy.es";
    String GET_ID = IP + "/android/obtener_local_id.php";
    String GET_IMAGE = IP + "/resourses/images/local/";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detalle);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        String Datos = intent.getExtras().getString("IdLocal");

        String[] Id = Datos.split("-");

        txtNombre = (TextView)findViewById(R.id.txtNombre);
        txtDir = (TextView)findViewById(R.id.txtDir);
        txtComuna = (TextView)findViewById(R.id.txtComuna);
        txtTelefono = (TextView)findViewById(R.id.txtTelefono);
        txtCorreo = (TextView)findViewById(R.id.txtCorreo);
        imgLocal = (ImageView)findViewById(R.id.imgLocal);

        cargaInicio(Id[0].trim());
    }

    private void cargaInicio(String id){
        //final ProgressDialog progres = new ProgressDialog(ActivityEventDetalle.this);
        //progres.setMessage("Cargando...");
        //progres.show();

        String cadenaLlamada = GET_ID + "?IdLocal=" + id;
        hiloconexion = new ObtenerDatos();
        hiloconexion.execute(cadenaLlamada,"1");
    }

    public class ObtenerDatos extends AsyncTask<String,Void,String> {

        Bitmap bitmap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            String datLocal[] = s.split(";");

            txtNombre.setText(datLocal[4]);
            txtDir.setText(datLocal[0]);
            txtComuna.setText(datLocal[3]);
            txtTelefono.setText(datLocal[1]);
            txtCorreo.setText(datLocal[2]);

            imgLocal.setImageBitmap(bitmap);

            //progres.hide();
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

            if (params[1]=="1"){ //Busqueda por ID

                //bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.hand);

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

                        devuelve =  devuelve +
                                    respuestaJSON.getJSONObject("Local").getString("dirLocal") + ";" +
                                    respuestaJSON.getJSONObject("Local").getString("telefonoLocal") + ";" +
                                    respuestaJSON.getJSONObject("Local").getString("correoLocal") + ";" +
                                    respuestaJSON.getJSONObject("Local").getString("nombreComuna") + ";" +
                                    respuestaJSON.getJSONObject("Local").getString("nombreLocal");

                        Log.d("Imagen: ",GET_IMAGE + respuestaJSON.getJSONObject("Local").getString("IdLocal") + ".jpg");

                        URL urlImage = new URL (GET_IMAGE + respuestaJSON.getJSONObject("Local").getString("IdLocal") + ".jpg");
                        HttpURLConnection conImage = (HttpURLConnection) urlImage.openConnection();
                        conImage.connect();


                        bitmap = BitmapFactory.decodeStream(conImage.getInputStream());
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    //imgLocal.setImageBitmap(bitmap);
                    bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.hand);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //progres.hide();
                return devuelve;
            }

            return null;
        }
    }
}
