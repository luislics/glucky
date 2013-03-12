package com.example.glucky;

import clasesObjetos.Usuario;

import com.glucky.Principal.PantallaPrincipal;
import com.glucky.registro.NuevoUsuarioActivity;
import com.glucky.utils.DBUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private TextView nuevo_usuario;
	private Button ingresar;
	EditText etUsuario, etClave;
	Usuario usuario;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ingresar=(Button)findViewById(R.id.botonlogin);
		etUsuario = (EditText)findViewById(R.id.etUsuario);
		etClave = (EditText)findViewById(R.id.etClave);
		usuario = new Usuario();
		ingresar.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View arg0) {
				if(checkLogin()){
					Intent in;
					if(usuario.edad==0){
						in = new Intent(MainActivity.this, Formulario.class);
					}else{
						in = new Intent(MainActivity.this, PantallaPrincipal.class);
					}
					
					in.putExtra("usuario", usuario);
					startActivity(in);
				}
				
				
			}
		});
		nuevo_usuario=(TextView)findViewById(R.id.BtnNvoUser);
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
	public boolean checkLogin(){
		SQLiteDatabase db = DBUtils.getStaticDb();
		Cursor cursor = db.rawQuery("select password, idUsuario,nombre,edad,peso,estatura,sexo,tipoDiabetes from usuarios where nombre = '"+etUsuario.getText().toString()+"'", null);
		
		if(cursor.moveToFirst()){
			if(cursor.getString(0).equals(etClave.getText().toString())){
				usuario.password = cursor.getString(0);
				usuario.idUsuario = cursor.getInt(1);
				usuario.nombre = cursor.getString(2);
				if(cursor.isNull(3)){
					usuario.edad = 0;
				}else{
					usuario.edad = cursor.getInt(3);
					usuario.peso = cursor.getInt(4);
					usuario.estatura = cursor.getInt(5);
					usuario.sexo = cursor.getString(6);
					usuario.tipoDiabetes = cursor.getInt(7);
				}

				db.close();
				Toast.makeText(MainActivity.this, "Bienvenido "+usuario.nombre, Toast.LENGTH_SHORT).show();
				return true;
			}else{
				Toast.makeText(MainActivity.this, "Contrase–a incorrecta", Toast.LENGTH_SHORT).show();
				db.close();
				return false;
			}
		}else{
			Toast.makeText(MainActivity.this, "No existe el usuario", Toast.LENGTH_SHORT).show();
			db.close();
			return false;
		}
	}

}
