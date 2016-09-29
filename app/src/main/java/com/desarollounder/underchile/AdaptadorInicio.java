package com.desarollounder.underchile;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;
import com.desarollounder.underchile.Evento;

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
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Desarollo4 on 16-09-2016.
 */
public class AdaptadorInicio extends RecyclerView.Adapter<AdaptadorInicio.ViewHolder>{

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
        public TextView nombre;
        public TextView precio;
        public ImageView imagen;

        public ViewHolder(View v) {
            super(v);
            nombre = (TextView) v.findViewById(R.id.nombre_comida);
            precio = (TextView) v.findViewById(R.id.precio_comida);
            imagen = (ImageView) v.findViewById(R.id.miniatura_comida);
        }
    }

    public AdaptadorInicio() {
    }

    @Override
    public int getItemCount() {
        return 15;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_inicio, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        List<Evento> COMIDAS_POPULARES = new ArrayList<>();

        COMIDAS_POPULARES.add(new Evento(5, "Camarones ", R.mipmap.logo));
        COMIDAS_POPULARES.add(new Evento(3.2f, "Rosca ", R.mipmap.local));
        COMIDAS_POPULARES.add(new Evento(12f, "Sushi ", R.mipmap.hand));
        COMIDAS_POPULARES.add(new Evento(9, "Sandwich ", R.mipmap.seba));
        COMIDAS_POPULARES.add(new Evento(34f, "Lomo ", R.mipmap.logo));
        COMIDAS_POPULARES.add(new Evento(5, "Camarones ", R.mipmap.logo));
        COMIDAS_POPULARES.add(new Evento(3.2f, "Rosca ", R.mipmap.local));
        COMIDAS_POPULARES.add(new Evento(12f, "Sushi ", R.mipmap.hand));
        COMIDAS_POPULARES.add(new Evento(9, "Sandwich ", R.mipmap.seba));
        COMIDAS_POPULARES.add(new Evento(34f, "Lomo ", R.mipmap.logo));
        COMIDAS_POPULARES.add(new Evento(5, "Camarones ", R.mipmap.logo));
        COMIDAS_POPULARES.add(new Evento(3.2f, "Rosca ", R.mipmap.local));
        COMIDAS_POPULARES.add(new Evento(12f, "Sushi ", R.mipmap.hand));
        COMIDAS_POPULARES.add(new Evento(9, "Sandwich ", R.mipmap.seba));
        COMIDAS_POPULARES.add(new Evento(34f, "Lomo ", R.mipmap.logo));

        Evento item = COMIDAS_POPULARES.get(i);

        Glide.with(viewHolder.itemView.getContext())
                .load(item.getIdDrawable())
                .centerCrop()
                .into(viewHolder.imagen);
        viewHolder.nombre.setText(item.getNombre());
        viewHolder.precio.setText("$" + item.getPrecio());
    }
}
