package com.glucky.Principal;

import clasesObjetos.Usuario;

import com.example.glucky.GraficadorGlucosa;
import com.example.glucky.GustosUsuario;
import com.example.glucky.MainActivity;
import com.example.glucky.R;
import com.example.glucky.Splashscreen;
import com.glucky.utils.DBUtils;

import feedrss.SimpleRSSReaderActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PantallaPrincipal extends Activity{
	TextView tvBienvenida;
	Usuario usuario;
	Button btnGustos;
	Button btnnoticias;
	Button btnglucosa;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activityprincipal);
		tvBienvenida = (TextView)findViewById(R.id.tvBienvenida);
		Bundle extras = getIntent().getExtras();
		usuario = (Usuario) extras.getSerializable("usuario");
		btnGustos = (Button)findViewById(R.id.btnGustos);
		
		btnGustos.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(PantallaPrincipal.this,GustosUsuario.class);
				in.putExtra("usuario", usuario);
				startActivity(in);
			}
		});
		btnnoticias = (Button)findViewById(R.id.button1);
		btnnoticias.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(new Intent(PantallaPrincipal.this, SimpleRSSReaderActivity.class)));
			}
		});
		btnglucosa = (Button)findViewById(R.id.button2);
		btnglucosa.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(new Intent(PantallaPrincipal.this, GraficadorGlucosa.class)));
			}
		});
	
	}

}