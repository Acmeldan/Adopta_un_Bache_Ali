package com.uas.facite.adoptaunbache;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.navigation.NavigationView;
import com.mapbox.mapboxsdk.maps.MapboxMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //crear variables para los controles de navegacion
    private NavigationView navegacion;
    private ImageButton botonMenu;
    private DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //identificar el drawer layout
        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        //identificar el boton
        botonMenu= (ImageButton)findViewById(R.id.botonMenu);
        //identificarmos el navigation view
        navegacion = (NavigationView)findViewById(R.id.nav_view);

        navegacion.setNavigationItemSelectedListener(this);
        //capturamos el evento
        botonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //verificar si el drawe se encuentra abierto
                if(drawer.isDrawerOpen(Gravity.LEFT))
                    drawer.closeDrawer(Gravity.LEFT);
                else
                    drawer.openDrawer(Gravity.LEFT);
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        //obtenemos el id del elemento del menu seleccionado
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.nav_google:
                //abrimos el activity del google maps
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_mapbox:
                //abrimos el activity del mapbox
                Intent intentMapbox = new Intent(MainActivity.this, activity_mapbox.class);
                startActivity(intentMapbox);
                break;
            default:
                return true;
        }
        return true;
        }
    }

