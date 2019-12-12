package com.uas.facite.adoptaunbache;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegistroActivity extends AppCompatActivity {
    EditText nombre, usuario, contra;
    Button boton_registrar;
    String URL_WEB_SERVICE = "http://facite.uas.edu.mx/adoptaunbache/api/registro_usuario.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        nombre = (EditText)findViewById(R.id.editTextName);
        usuario = (EditText)findViewById(R.id.editTextUsuario);
        contra = (EditText)findViewById(R.id.editTextPassword);
        boton_registrar = (Button)findViewById(R.id.cirRegisterButton);
        //capturar el evento en el clicl del boton registrar
        boton_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resgistrarUsuario();
            }
        });
        //changeStatusBarColor();
    }
    //metodo para realizar el registro en segundo plano
    private void resgistrarUsuario(){
        String nom = nombre.getText().toString();
        String usu = usuario.getText().toString();
        String pas = contra.getText().toString();
        //realizamos el registro en segundo plano llamando a la clase resgistrarAsync
        RegistroUsuario registroObject = new RegistroUsuario(nom, usu, pas);
        registroObject.execute();

    }
    //clase para hacer la peticion al web service en segundo plano
    class RegistroUsuario extends AsyncTask<Void, Void, String> {
        String nombre, usuario, password;
        //constructor de la clase usuario registro
        RegistroUsuario(String nombre, String usuario, String password)     {
            this.nombre = nombre;
            this.usuario = usuario;
            this.password = password;
        }
        @Override
        protected String doInBackground(Void... voids) {
            //creamos un objeto de la clase RequestHandler
            RequestHandler requestHandler = new RequestHandler();
            //crear los parametros que se enviaran al web service
            HashMap<String, String> parametros = new HashMap<>();
            parametros.put("nombre", nombre);
            parametros.put("usuario", usuario);
            parametros.put("pass", password);
            //retornar la respuesta al web service
            return requestHandler.sendPostRequest(URL_WEB_SERVICE, parametros);
        }
        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            try {
                JSONObject obj = new JSONObject(s);
                if(obj.getInt("status")== 1){
                    new SweetAlertDialog(RegistroActivity.this,SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Excelente")
                            .setContentText(obj.getString("message"))
                            .show();

                }
                else{
                    new SweetAlertDialog(RegistroActivity.this,SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Ups")
                            .setContentText(obj.getString("message"))
                            .show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
   /* private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }*/

    public void onLoginClick(View view){
        startActivity(new Intent(this,LoginActivity.class));
    }
}
