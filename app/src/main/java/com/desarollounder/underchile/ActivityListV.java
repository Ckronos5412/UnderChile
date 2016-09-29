package com.desarollounder.underchile;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.bumptech.glide.util.ExceptionCatchingInputStream;
import com.github.snowdream.android.widget.SmartImage;
import com.github.snowdream.android.widget.SmartImageView;
import com.loopj.android.http.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import cz.msebera.android.httpclient.Header;

public class ActivityListV extends AppCompatActivity {

    RequestQueue requestQueue;

    //Método 2 BD
    ArrayList titulo = new ArrayList();
    ArrayList descripicon = new ArrayList();
    ArrayList imagen = new ArrayList();

    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_v);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        listview = (ListView) findViewById(R.id.listViewNoticias);
        descargarDatos();
    }

    Integer in;
    private void descargarDatos() {
        titulo.clear();
        descripicon.clear();
        imagen.clear();

        final ProgressDialog progres = new ProgressDialog(ActivityListV.this);
        progres.setMessage("Cargando...");
        progres.show();

        AsyncHttpClient client = new AsyncHttpClient();

        Log.d("Test: ","yeah1");

        client.get("http://crisgaray.esy.es/obtener_locales.php", new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {


                if (statusCode == 200){
                    progres.dismiss();
                    try {
                        Log.d("Test: ","yeah2");
                        JSONArray json = new JSONArray(new String(responseBody));
                        Log.d("Test: ","yeah3");

                        in = json.length();
                        Log.d("Test: ",in.toString());

                        for (int i=0; i<json.length();i++){
                            Log.d("Test: ","entró");
                            titulo.add(json.getJSONObject(i).getString("IdLocal"));
                            descripicon.add(json.getJSONObject(i).getString("dirLocal"));
                            imagen.add(json.getJSONObject(i).getString("IdLocal") + ".jpg");
                            Log.d("Test: ",json.getJSONObject(i).getString("IdLocal") + ".jpg");
                        }

                        Log.d("Test: ","yeah4");

                        listview.setAdapter(new ImagenAdapter(getApplicationContext()));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private class ImagenAdapter extends BaseAdapter{
        Context ctx;
        LayoutInflater layoutInflater;
        SmartImageView smartImage;
        TextView txTitulo,txDescripcion;

        public ImagenAdapter(Context aplicationContext){
            Log.d("Test: ","yeah5");
            this.ctx = aplicationContext;
            Log.d("Test: ","yeah6");
            layoutInflater = (LayoutInflater)ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
            Log.d("Test: ","yeah7");
        }

        @Override
        public int getCount() {
            return imagen.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d("Test: ","yeah8");
            ViewGroup viewGroup = (ViewGroup)layoutInflater.inflate(R.layout.activity_main_item,null);
            Log.d("Test: ","yeah9");
            smartImage = (SmartImageView)viewGroup.findViewById(R.id.imagen1);
            txTitulo = (TextView)viewGroup.findViewById(R.id.tvTitulo);
            txDescripcion = (TextView)viewGroup.findViewById(R.id.tvDescripcion);

            String urlFinal = "http://crisgaray.esy.es/resourses/images/local/"+imagen.get(position).toString();

            Log.d("Test: ",imagen.get(position).toString());

            Rect rect = new Rect (smartImage.getLeft(),smartImage.getTop(),smartImage.getRight(),smartImage.getBottom());

            smartImage.setImageUrl(urlFinal,rect);

            txTitulo.setText(titulo.get(position).toString());
            txDescripcion.setText(descripicon.get(position).toString());

            return viewGroup;
        }
    }

}
