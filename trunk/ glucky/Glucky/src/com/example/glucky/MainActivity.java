package com.example.glucky;

import com.glucky.Principal.PantallaPrincipal;
import com.glucky.registro.NuevoUsuarioActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView nuevo_usuario;
	private Button ingresar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ingresar=(Button)findViewById(R.id.botonlogin);
		ingresar.setVisibility(View.VISIBLE);
		ingresar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(new Intent(MainActivity.this, PantallaPrincipal.class)));
				
			}
		});
		nuevo_usuario=(TextView)findViewById(R.id.BtnNvoUser);
		nuevo_usuario.setVisibility(View.VISIBLE);
		nuevo_usuario.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(new Intent(MainActivity.this, NuevoUsuarioActivity.class)));
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
