package com.example.glucky;


import clasesObjetos.Usuario;

import com.glucky.utils.DBUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Formulario extends Activity {

	EditText etEdad,etPeso,etEstatura;
	Spinner spSexo;
	ArrayAdapter adapter;
	Button btnGuardar;
	Usuario usuario;
	Button btnGustos;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_formulario);
		etEdad = (EditText)findViewById(R.id.etEdad);
		etPeso = (EditText)findViewById(R.id.etPeso);
		etEstatura = (EditText)findViewById(R.id.etEstatura);
		btnGuardar = (Button)findViewById(R.id.btnGuardar);
		btnGustos = (Button)findViewById(R.id.btnGustos);
		String[] sexos = {"Hombre","Mujer"};
		spSexo = (Spinner)findViewById(R.id.spSexo);
		Bundle extras = getIntent().getExtras();
		usuario = (Usuario)extras.getSerializable("usuario");
		btnGustos.setVisibility(View.INVISIBLE);
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, sexos){
	    	@Override
	        public View getView(final int position, View convertView, ViewGroup parent) {
	           View v = super.getView(position, convertView, parent);           
	           // change the color here of your v
	          // ((TextView)v).setTextColor(Color.WHITE);
	           ((TextView)v).setGravity(Gravity.CENTER);
	           ((TextView)v).setTextSize(12);
	           return v;
	        }
	    };
	    adapter.setDropDownViewResource(R.layout.spinnerlayout);
	    spSexo.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
	    	
		});
		spSexo.setAdapter(adapter);
		
		btnGuardar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(checkInput()){
					SQLiteDatabase db = DBUtils.getStaticDb();
					ContentValues cv = new ContentValues();
					cv.put("edad", etEdad.getText().toString());
					cv.put("peso",etPeso.getText().toString());
					cv.put("estatura", etEstatura.getText().toString());
					cv.put("sexo",spSexo.getSelectedItem().toString());
					if(db.update("usuarios", cv, "idUsuario = ?",
							new String[] { Integer.toString(usuario.idUsuario) })>0){
						
						Toast.makeText(Formulario.this, "Datos agregados con Žxito", Toast.LENGTH_SHORT).show();
						btnGustos.setVisibility(View.VISIBLE);
					}else{
						Toast.makeText(Formulario.this, "Ocurri— un error al guardar tus datos", Toast.LENGTH_SHORT).show();
					}
					db.close();
					
				}else{
					Toast.makeText(Formulario.this, "Por favor completa todos los datos", Toast.LENGTH_SHORT).show();
				}
			}
		});
		btnGustos.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent in = new Intent(Formulario.this, GustosUsuario.class);
				in.putExtra("usuario", usuario);
				startActivity(in);
			}
		});
	}
	private boolean checkInput(){
		return etEdad.getText().toString().length()>0 && etEstatura.getText().toString().length()>0&&
				etPeso.getText().toString().length()>0;
	}

	
}
