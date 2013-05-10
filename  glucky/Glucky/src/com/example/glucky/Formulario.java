package com.example.glucky;


import java.util.Calendar;

import clasesObjetos.Usuario;

import com.glucky.Principal.PantallaPrincipal;
import com.glucky.utils.DBUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Formulario extends Activity {

	EditText etPeso,etEstatura;
	public DatePicker dpEdad;
	Spinner spSexo;
	ArrayAdapter adapter;
	Button btnGuardar;
	Usuario usuario;
	Button btnGustos;
	Calendar cumple;
	Button btnIngresar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_formulario);
		dpEdad = (DatePicker)findViewById(R.id.dpEdad);
		etPeso = (EditText)findViewById(R.id.etPeso);
		etEstatura = (EditText)findViewById(R.id.etEstatura);
		btnGuardar = (Button)findViewById(R.id.btnGuardar);
		btnGustos = (Button)findViewById(R.id.btnGustos);
		btnIngresar = (Button)findViewById(R.id.btnIngresar);
		btnIngresar.setVisibility(View.INVISIBLE);
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
		cumple = Calendar.getInstance();
		dpEdad.init(cumple.get(Calendar.YEAR), cumple.get(Calendar.MONTH), cumple.get(Calendar.DATE), new OnDateChangedListener() {
			
			@Override
			public void onDateChanged(DatePicker arg0, int year, int month, int day) {
				// TODO Auto-generated method stub
				cumple.set(Calendar.YEAR, year);
				cumple.set(Calendar.MONTH, month);
				cumple.set(Calendar.DATE, day);
			}
		});
		btnGuardar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//DateFormat frm = new DateFormat();
				int yr = cumple.get(Calendar.YEAR);
				int mn = cumple.get(Calendar.MONTH);
				int dt = cumple.get(Calendar.DATE);
				// TODO Auto-generated method stub
				if(checkInput()){
					SQLiteDatabase db = DBUtils.getStaticDb();
					ContentValues cv = new ContentValues();
					int edad = calcularEdad();
					//Toast.makeText(Formulario.this, edad+"-", Toast.LENGTH_SHORT).show();
					cv.put("cumpleanos",dt+"/"+mn+"/"+yr );
					cv.put("edad", edad);
					cv.put("peso",etPeso.getText().toString());
					cv.put("estatura", etEstatura.getText().toString());
					cv.put("sexo",spSexo.getSelectedItem().toString());
					if(db.update("usuarios", cv, "idUsuario = ?",
							new String[] { Integer.toString(usuario.idUsuario) })>0){
						
						Toast.makeText(Formulario.this, "Datos agregados con Žxito", Toast.LENGTH_SHORT).show();
						btnGustos.setVisibility(View.VISIBLE);
						btnIngresar.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent in = new Intent(Formulario.this, PantallaPrincipal.class);
								in.putExtra("usuario",usuario);
								startActivity(in);
							}
						});
						btnIngresar.setVisibility(View.VISIBLE);
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
	private int calcularEdad(){
		Calendar ahora = Calendar.getInstance();
		int diff = ahora.get(Calendar.YEAR)-cumple.get(Calendar.YEAR);
	    if (cumple.get(Calendar.MONTH) > ahora.get(Calendar.MONTH) || 
	            (cumple.get(Calendar.MONTH) == ahora.get(Calendar.MONTH) && cumple.get(Calendar.DATE) > ahora.get(Calendar.DATE))) {
	            diff--;
	        }
		return diff;
	}
	private boolean checkInput(){
		return  etEstatura.getText().toString().length()>0&&
				etPeso.getText().toString().length()>0;
	}

	
}
