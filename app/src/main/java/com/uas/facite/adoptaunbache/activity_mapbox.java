package com.uas.facite.adoptaunbache;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.core.exceptions.ServicesException;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class activity_mapbox extends AppCompatActivity {
    private MapView mapa;
    private  MapboxMap mapboxMap;
    private FloatingActionButton BotonAgregarBache;
    private String WEB_SERVICE = "http://facite.uas.edu.mx/adoptaunbache/api/insertar_bache.php";

    //variables para mostrar el dise;o de capturar bache
    BottomSheetBehavior botomSheet;
    LinearLayout layout_capturarBache;
    TextView txt_direccion, txt_latitud, txt_longitud;
    ImageButton botonCamara;
    Button botonAdoptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoiYWNtZWxkYW4iLCJhIjoiY2sxbGIycGE1MDE0ejNtbmkyYjB4dzJuaCJ9.escxYIM3oi91-VKBrMDTuQ");
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        setContentView(R.layout.activity_mapbox);
        mapa = findViewById(R.id.mapViewbox);
        //identificar las variables de nuestro diseno
        layout_capturarBache = (LinearLayout)findViewById(R.id.capturar_bache);
        botomSheet = BottomSheetBehavior.from(layout_capturarBache);
        txt_direccion = (TextView)findViewById(R.id.txtDireccion);
        txt_latitud = (TextView)findViewById(R.id.txtLatitud);
        txt_longitud = (TextView)findViewById(R.id.txtLongitud);
        botonCamara = (ImageButton)findViewById(R.id.botonCamara);
        botonAdoptar = (Button)findViewById(R.id.boton_adoptar);





        mapa.onCreate(savedInstanceState);
        mapa.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                activity_mapbox.this.mapboxMap=mapboxMap;
                mapboxMap.setStyle(Style.DARK, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments.
                        try {
                        style.addSource(new GeoJsonSource("GEOJASON_PUNTOS",
                                new URI("http://facite.uas.edu.mx/adoptaunbache/api/getlugares.php")));
                        }catch (URISyntaxException e){
                            Log.i("ERROR GEOJASON", e.toString());
                        }                        //creamos el icono personalizado
                        Bitmap icono = BitmapFactory.decodeResource(getResources(),R.drawable.alarm);
                        //agregar el icono al estilo del map
                        style.addImage("BACHE_ICONO",icono);
                        //crar una capa layer con los datos cargados desde geojason
                        SymbolLayer BachesCapa = new SymbolLayer("BACHES", "GEOJASON_PUNTOS");
                        //ASIGANAMOS EL INCONO PERSONALIZADO A LA CAPA DE BACHES
                        BachesCapa.setProperties(PropertyFactory.iconImage("BACHE_ICONO"));
                        //ASIGNAMOS LA CAPA DE BACHE AL MAPA
                        style.addLayer(BachesCapa);
                        //posicionar marcador estatico en el centro del mapa
                        ImageView MarcadorPin;
                        MarcadorPin = new ImageView(activity_mapbox.this);
                        MarcadorPin.setImageResource(R.drawable.ic_pinwarning);
                        //posisionar el marcadorPin  en el centro del mapa
                        FrameLayout.LayoutParams parametros = new FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                        //APLICAMOS ESOS PARAMETROS PARAMETROS AL MARCADOR
                        MarcadorPin.setLayoutParams(parametros);
                        //agregamos el marcador al mapa cargado
                        mapa.addView(MarcadorPin);
                        //identificamos el boton flotante del layout
                        BotonAgregarBache = (FloatingActionButton)findViewById(R.id.btnAgregarBache);
                        //capturar el click del boton agregar bache
                        BotonAgregarBache.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //obtener las coordeadas x y del centro del mapa
                                final LatLng coordenadas = mapboxMap.getCameraPosition().target;
                                //mandamos una alerta bonita con las coordenadas
                                /*new SweetAlertDialog( activity_mapbox.this, SweetAlertDialog.NORMAL_TYPE)
                                        .setTitleText("Coordenadas del marcador Pin")
                                        .setContentText("Latitud: "+coordenadas.getLatitude() +
                                        "             Longitud: "+coordenadas.getLongitude()).show();*/

                        //obtener la direccion con el metodo le emviamos el view boton
                                ObtenerDireccion(view);
                                //mostrar el layout de capturar bache
                                botomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
                                txt_latitud.setText("Latitud: " +coordenadas.getLatitude());
                                txt_longitud.setText("Longitud: " + coordenadas.getLongitude());


                            }
                        });
                        //funcionalidad para el boton adoptar
                        botonAdoptar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });
                        //funcionalidad del boton de la camara
                        botonCamara.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //desplegar una alaerta con posibles opciones a realizar
                                final CharSequence[] opciones = {"Tomar fotografía", "Desde galeria", "Cancelar"};
                                AlertDialog.Builder alerta = new AlertDialog.Builder(activity_mapbox.this);
                                //titulo de la alerta
                                alerta.setTitle("Adjuntar foto del bache");

                                alerta.setItems(opciones, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //programar la funcionalidad de las opcines
                                        if (opciones[i].equals("Tomar fotografía")){
                                            //solicitar permiso a la camara en caso de que no lo tenga
                                            //verificar el SDK del telefono donde se esta ejecutando nuestra app
                                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                                //verificar si ya tiene permisos para la camara
                                                if(ContextCompat.checkSelfPermission(activity_mapbox.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED) {
                                                    ActivityCompat.requestPermissions(activity_mapbox.this, new String[]{Manifest.permission.CAMERA}, 507);
                                                }
                                                else{
                                                Intent camara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                startActivityForResult(camara, 1);
                                                }
                                            }
                                            else {
                                                //SI ES UN TELEFONO CON ANDROID 5 O MENOR ABRIMOS DIRECTO LA CAMARA
                                                Intent camara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                startActivityForResult(camara, 1);
                                            }
                                        }
                                        else if (opciones[i].equals("Desde galeria")) {
                                        //solicitamos los permisos para abrir la galeria
                                            //ver si ua tiene permisos
                                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                                if (ContextCompat.checkSelfPermission(activity_mapbox.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                                                    Intent  galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                                    startActivityForResult(galeria, 2);
                                                 }
                                                else {
                                                    //solicitamos los permisos para la galeria
                                                    ActivityCompat.requestPermissions(activity_mapbox.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 507);
                                                    return;
                                                }
                                            }
                                        }
                                        else{
                                            Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                            startActivityForResult(galeria, 2);
                                        }
                                    }
                                });
                                //mostrar la alerta
                                alerta.show();
                            }

                        });


                    }
                });
                //LLEVAR A LA POSICION DE CULIACAN
                CameraPosition posicion= new CameraPosition.Builder()
                        .target(new LatLng(24.8087148, -107.3941223)) //establecer coordenadas
                        .zoom(13)  //establecer el zoom
                        .bearing(-15)//rota la camara
                        .tilt(30) //angulo de inclinacion
                        .build();
                //mover la posicion del mapa
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(posicion), 1000);
            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        activity_mapbox.super.onActivityResult(requestCode, resultCode, intent);
        //si se tomo una foto con la camara
        if(requestCode==1){
            Bitmap foto = (Bitmap)intent.getExtras().get("data");
            Drawable fotodrawable = new BitmapDrawable(foto);
            botonCamara.setImageDrawable(fotodrawable);
        }
        else if(requestCode==2){
            Uri fotoseleccionada = intent.getData();
            String[] rutaImagen= {MediaStore.Images.Media.DATA};
            Cursor cursor = activity_mapbox.this.getApplicationContext().
                    getContentResolver().query(fotoseleccionada, rutaImagen, null, null, null);
                    cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(rutaImagen[0]);
            String archivoFoto = cursor.getString(columnIndex);
            cursor.close();
            Bitmap foto = (BitmapFactory.decodeFile(archivoFoto));
            Drawable fotodrawable = new BitmapDrawable(foto);
            botonCamara.setImageDrawable(fotodrawable);
        }
    }

    //metodo para obtener la direccion con base en la latitud y la longitud
    public void ObtenerDireccion(final View vi) {
            //obtener las coordenadas del marcador pin en el mapa
            final LatLng coordenadas = mapboxMap.getCameraPosition().target;
            final Point punto = Point.fromLngLat(coordenadas.getLongitude(), coordenadas.getLatitude());
            //utilizar los servicios de mapbox para geocodificar la direccion con base en el punto
            MapboxGeocoding servicio = MapboxGeocoding.builder()
                    .accessToken("pk.eyJ1IjoiYWNtZWxkYW4iLCJhIjoiY2sxbGIycGE1MDE0ejNtbmkyYjB4dzJuaCJ9.escxYIM3oi91-VKBrMDTuQ")
                    .query(Point.fromLngLat(punto.longitude(), punto.latitude()))
                    .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                    .build();
            //ejecutar el servicio con los parametros que establecimos
            servicio.enqueueCall(new Callback<GeocodingResponse>() {
                @Override
                public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                    //si el resultado no fue nulo osea que si encontro una direccion
                    if (response.body() != null) {
                        List<CarmenFeature> resultado = response.body().features();
                        //obtenemos la direccion
                        CarmenFeature direccion = resultado.get(0);
                        //agregar direccion a la ventana bache
                        txt_direccion.setText("" + direccion.placeName());
                        //mostramos la direccion obtenida con una barra de notificacion
                        /*Snackbar.make(vi,
                                "Direccion: " + direccion.placeName(),
                                Snackbar.LENGTH_LONG).show();*/
                    }
                }

                @Override
                public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                    Snackbar.make(vi,
                            "No hay direccion ",
                            Snackbar.LENGTH_LONG).show();
                }


            });
            //clase encargada de resgistrar el bache
            class RegistrarBache extends AsyncTask<Void, Void, String>{
                //crear las variables de los parametros que se ocupan en el web service
                String direccion, latitud, longitud, foto;
                //creamos el constructor de la clase
                RegistrarBache(String direccion, String latitud, String longitud, String foto)
                {
                    this.direccion = direccion;
                    this.latitud = latitud;
                    this.longitud = longitud;
                    this.foto = foto;
                }
                @Override
                protected String doInBackground(Void... voids) {
                    //crar un objeto de la clase requestHandler
                    RequestHandler requestHandler = new RequestHandler();
                    //creamos un hashmap con los parametros que se enviaran
                    HashMap<String, String > parametros = new HashMap<>();
                    parametros.put("nombre", direccion);
                    parametros.put("lat", latitud);
                    parametros.put("lon", longitud);
                    parametros.put("img", foto);
                    //Retornamos la repsuesta que nos regreso el web service
                    return requestHandler.sendPostRequest(WEB_SERVICE, parametros);
                }

                @Override
                protected void onPostExecute(String respuesta){
                    super.onPostExecute(respuesta);
                    //convertimos la respuesta en un objeto JSON
                    try {
                        JSONObject object = new JSONObject(respuesta);
                        //obtenemos el codigo del status
                        int status = object.getInt("status");
                        //si el codigo fue 1 entonces se registro correctamente el bache
                        if (status==1){
                            new SweetAlertDialog(activity_mapbox.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Excelente!")
                                    .setContentText(object.getString("message"))
                                    .show();
                        }
                        else {
                            new SweetAlertDialog(activity_mapbox.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Ahorita no joven")
                                    .setContentText(object.getString("message"))
                                    .show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

         }

    @Override
    public void onStart(){
        super.onStart();
        mapa.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapa.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapa.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapa.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapa.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapa.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapa.onSaveInstanceState(outState);
    }
}