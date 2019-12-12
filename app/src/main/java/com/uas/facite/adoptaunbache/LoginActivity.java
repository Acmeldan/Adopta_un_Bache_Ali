package com.uas.facite.adoptaunbache;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Build;
import android.support.v4.app.INotificationSideChannel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.channels.InterruptedByTimeoutException;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    //creamos las variables para los controles que usaremos
    EditText txt_usuario;
    EditText txt_password;
    Button boton_login;
    ProgressDialog progressDialog;
    String URL_WEB_SERVICE = "http://facite.uas.edu.mx/adoptaunbache/api/get_usuarios.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //identificar los controles del layout
        txt_usuario = (EditText)findViewById(R.id.editTextUsuario);
        txt_password = (EditText)findViewById(R.id.editTextPassword);
        boton_login = (Button)findViewById(R.id.cirLoginButton);
        //capturamos el evento click del boton login
        boton_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mandamos llamar el metodo hacer login
                hacerLogin();
            }
        });
        //for changing status bar icon colors
       // if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
           //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //}
        //setContentView(R.layout.activity_login);


    }
    public void hacerLogin() {
        //obetener los calores delos EDIT TEXT
        String usuario = txt_usuario.getText().toString();
        String pass = txt_password.getText().toString();
        Log.i("Valores", usuario);
        Log.i("Password", pass);
        UsuarioLogin ul = new UsuarioLogin(usuario, pass);
        ul.execute();
    }
    public void onLoginClick(View View){
        startActivity(new Intent(this,RegistroActivity.class));


    }
    //CLASE PARA HACER LA PETICION AL WEB SERVICE EN SEGUNDO PLANO
    class UsuarioLogin extends AsyncTask<Void, Void, String>{
        //ProgressBar barra;
        String usuario, password;
        //Constructor de la clase Usuario Login
        UsuarioLogin(String usuario, String password){
            this.usuario = usuario;
            this.password = password;
        }
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //barra = findViewById()
        }
        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            try {
                //convertir la respuesta del web service a un objeto JSON
                Log.e("respuesta", s);
                JSONObject obj = new JSONObject(s);
                //VERIFICAMOS QUE ESTADO NOS REGRESO
                if(obj.getInt("status") == 0){
                    //si el status del web service fue 0 entonces es un login correcto
                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    //Aqui pondremos el intent para que nos lleve al activity del mapa
                    //Intent intent = new Intent(LoginActivity.this, MapBoxActivity.class);
                    //startActivity(intent);
                }
                else
                {
                    //quiere decir que el estatus fue 1 o 2
                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            //CREAMOS UN OBJETO DE LA CLASE RequestHandler
            RequestHandler2 requestHandler = new RequestHandler2();
            //Crear los parametros que se enviaran al web service
            HashMap<String, String> parametros = new HashMap<>();
            parametros.put("usuario", usuario);
            parametros.put("password", password);
            //retornamos la respuesta del web service
            return requestHandler.sendPostRequest(URL_WEB_SERVICE, parametros);
        }
    }
    //clase para hacer la peticioon al web service en segundo plano

    /*class UsuarioLogin extends AsyncTask<Void, Void, String>{
        //progress bar barra;
        String usuario, password;
        //constructor de la clase usuario login
        UsuarioLogin(String usuario, String password){
            this.usuario = usuario;
            this.password= password;
        }

        @Override
        protected String doInBackground(Void... voids) {
            //creamos un objeto de la clase RequestHandler
            RequestHandler2 requestHandler = new RequestHandler2();
            //crear los parametros que se enviaran al web servic
            HashMap<String, String> parametros = new HashMap<>();
            parametros.put("usuario", usuario);
            parametros.put("password", password);
            //retornamos la respuesta del web service
            return requestHandler.sendPostRequest(URL_WEB_SERVICE, parametros);
        }

        //@Override
        //protected void OnPreExecute(){
        //    super.onPreExecute();
            //barra = findViewById()
       // }
        @Override
        protected  void onPostExecute(String s){
            super.onPostExecute(s);
            try{
                //convertir la respuesta del web service a un objeto JSON
                JSONObject obj = new JSONObject(s);
                //verificamos el estado que nos regreso
                /*if(obj.getInt("status") == 0){
                    //si el status del web service fue 0 entonces es un login correcto
                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    //aqui pondremos el intent para que nos lleve al activity del mapa
                    //Intent intent = new Intent( LoginActivity.this, MapsActivity.class);
                    //startActivity(intent);
                }
                else {
                    //quiere decir que el status fue 1 o 2
                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                }

            } catch ( JSONException e){
                e.printStackTrace();
            }
        }


    }*/
}
