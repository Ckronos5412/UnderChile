package com.desarollounder.underchile;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.snowdream.android.widget.SmartImageView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentListV.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentListV#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentListV extends Fragment{

    private OnFragmentInteractionListener mListener;

    //TextView txtListV;

    RequestQueue requestQueue;
    String showUrl = "http://undermetal.esy.es/android/obtener_locales.php";
    String retorna = "";

    //Método 2 BD
    ArrayList titulo = new ArrayList();
    ArrayList descripicon = new ArrayList();
    ArrayList imagen = new ArrayList();

    private ListView listview;

    public FragmentListV() {

    }

    public static FragmentListV newInstance(String param1, String param2) {
        FragmentListV fragment = new FragmentListV();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_v, container, false);

        listview = (ListView) view.findViewById(R.id.listViewNoticias);
        //txtListV = (TextView)view.findViewById(R.id.txtListV);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String asd = Integer.toString(position);
                int duration = Toast.LENGTH_SHORT;


                TextView textView = (TextView) view.findViewById(R.id.tvHidden);
                String text = textView.getText().toString();

                Toast toast = Toast.makeText(getActivity(), "Datos: "  + text, duration);
                toast.show();
            }
        });

        cargaDatos();
        return view;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and namem
        void onFragmentInteraction(Uri uri);
    }

    private void cargaDatos(){
        titulo.clear();
        descripicon.clear();
        imagen.clear();

        final ProgressDialog progres = new ProgressDialog(getActivity());
        progres.setMessage("Cargando...");
        progres.show();

        requestQueue = com.android.volley.toolbox.Volley.newRequestQueue(getActivity());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                showUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response.toString());

                progres.dismiss();

                try {
                    JSONArray locales = response.getJSONArray("Local");
                    for (int i = 0; i < locales.length(); i++) {
                        JSONObject local = locales.getJSONObject(i);
                        titulo.add(local.getString("IdLocal"));
                        descripicon.add(local.getString("dirLocal"));
                        imagen.add(local.getString("IdLocal") + ".jpg");
                    }

                    listview.setAdapter(new ImagenAdapter(getActivity()));
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



    private class ImagenAdapter extends BaseAdapter {
        Context ctx;
        LayoutInflater layoutInflater;
        SmartImageView smartImage;
        TextView txTitulo,txDescripcion, txHidden;

        public ImagenAdapter(Context aplicationContext){
            this.ctx = aplicationContext;
            layoutInflater = (LayoutInflater)ctx.getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
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

            ViewGroup viewGroup = (ViewGroup)layoutInflater.inflate(R.layout.activity_main_item,null);
            smartImage = (SmartImageView)viewGroup.findViewById(R.id.imagen1);
            txTitulo = (TextView)viewGroup.findViewById(R.id.tvTitulo);
            txDescripcion = (TextView)viewGroup.findViewById(R.id.tvDescripcion);

            txHidden = (TextView)viewGroup.findViewById(R.id.tvHidden);
            txHidden.setVisibility(View.GONE);

            String urlFinal = "http://undermetal.esy.es/resourses/images/local/"+imagen.get(position).toString();
            Rect rect = new Rect (smartImage.getLeft(),smartImage.getTop(),smartImage.getRight(),smartImage.getBottom());

            smartImage.setImageUrl(urlFinal,rect);

            txTitulo.setText(titulo.get(position).toString());
            txDescripcion.setText(descripicon.get(position).toString());
            txHidden.setText("Estoy escondido " + titulo.get(position).toString());

            return viewGroup;
        }
    }
}
