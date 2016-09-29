package com.desarollounder.underchile;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Volley extends AppCompatActivity {

    TextView txt;
    RequestQueue requestQueue;
    String showUrl = "http://crisgaray.esy.es/obtener_locales.php";
    String retorna = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volley);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txt = (TextView)findViewById(R.id.textView2);

        Log.d("Test: ","Paso1");
        cargaDatos();

    }

    private void cargaDatos(){

        requestQueue = com.android.volley.toolbox.Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                showUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response.toString());

                try {
                    JSONArray students = response.getJSONArray("Local");
                    for (int i = 0; i < students.length(); i++) {
                        JSONObject student = students.getJSONObject(i);

                        retorna = retorna +
                                student.getString("IdLocal") + " "  +student.getString("dirLocal") + " ";
                    }

                    Log.d("Test: ",retorna);
                    txt.setText(retorna);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.append(error.getMessage());

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

}
