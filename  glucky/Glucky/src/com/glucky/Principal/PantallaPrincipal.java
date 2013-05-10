package com.glucky.Principal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import clasesObjetos.Usuario;

import com.example.glucky.ComidasAct;
import com.example.glucky.GraficadorGlucosa;
import com.example.glucky.GustosUsuario;
import com.example.glucky.R;
import com.glucky.utils.DBUtils;

import feedrss.AndroidRssReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PantallaPrincipal extends Activity{
	TextView tvBienvenida;
	Usuario usuario;
	Button btnGustos;
	Button btnnoticias;
	Button btnglucosa;
	Button btnComidas;
	Dialog dialog;
	SQLiteDatabase db;
	float nivelGlucosa;
	Calendar cal;
	Date fechaUltimaGlucosa;
	Date fechaHoy;
	boolean pasaron24Horas = false;
	boolean justRecorded = false;
	EditText etGlucosa;
	long diffHours;
	public void onDestroy(){
		db.close();
		super.onDestroy();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activityprincipal);
		
		tvBienvenida = (TextView)findViewById(R.id.tvBienvenida);
		
		
		
		Bundle extras = getIntent().getExtras();
		usuario = (Usuario) extras.getSerializable("usuario");
		cal = Calendar.getInstance();
		db = DBUtils.getStaticDb();
		Cursor cursor = db.rawQuery("select nivelGlucosa,fecha from historialGlucosa where idUsuario="+usuario.idUsuario+" order by fecha desc",null);
		//nivelGlucosa = cursor.moveToFirst()?cursor.getFloat(0):0;
		if(cursor.moveToFirst()){
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			nivelGlucosa = cursor.getFloat(0);
			//Toast.makeText(PantallaPrincipal.this, cursor.getString(0),Toast.LENGTH_LONG).show();
			try {
				fechaUltimaGlucosa = formatter.parse(cursor.getString(1));
				String m = cursor.getString(1);
				m.toCharArray();
				diffHours = calcularDifHoras();
				 pasaron24Horas = diffHours > 24;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fechaHoy = null;
			}
		}else{
			//Toast.makeText(PantallaPrincipal.this, "no hay",Toast.LENGTH_LONG).show();
		}
		cursor.close();
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
				startActivity(new Intent(new Intent(PantallaPrincipal.this, AndroidRssReader.class)));
			}
		});
		btnglucosa = (Button)findViewById(R.id.button2);
		btnglucosa.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				dialog = new Dialog(PantallaPrincipal.this);
				dialog.setContentView(R.layout.dialog_glucosa);
				dialog.setTitle("Glucosa");
				dialog.setCancelable(true);
				dialog.show();
				final TextView tvGluc = (TextView)dialog.findViewById(R.id.txtGlucosa);
				etGlucosa =(EditText)dialog.findViewById(R.id.etGlucosa);

				final Button btnGuardarGlucosa = (Button)dialog.findViewById(R.id.btnGuardar);
				btnGuardarGlucosa.setEnabled(false);
				etGlucosa.addTextChangedListener(new TextWatcher() {
					
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,
							int after) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void afterTextChanged(Editable s) {
						// TODO Auto-generated method stub
						btnGuardarGlucosa.setEnabled(s.length()>0);
					}
				});
				if((fechaUltimaGlucosa != null && !pasaron24Horas)||justRecorded){
					
					tvGluc.setText("Ya registraste tu glucosa por hoy, intenta de nuevo en "+(24-calcularDifHoras())+" horas");
					//Toast.makeText(PantallaPrincipal.this, Float.toString(nivelGlucosa),Toast.LENGTH_LONG).show();
					etGlucosa.setText(Float.toString(nivelGlucosa));
					etGlucosa.setEnabled(false);
					btnGuardarGlucosa.setVisibility(View.INVISIBLE);
				}else{
					etGlucosa.setEnabled(true);
					
					btnGuardarGlucosa.setVisibility(View.VISIBLE);
				}
				
				btnGuardarGlucosa.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						float valor =Float.parseFloat(etGlucosa.getText().toString());
						if(valor>0){
							if(guardarGlucosaBD(valor)){
								nivelGlucosa = valor;
								justRecorded = true;
								
								etGlucosa.setEnabled(false);
								btnGuardarGlucosa.setEnabled(false);
								tvGluc.setText("Nivel de glucosa de hoy guardado correctamente");
							}else{
								tvGluc.setText("Ocurri— un error, vuelve a intentarlo");
							}
						}else{
							tvGluc.setText("Escribe un valor mayor a 0");
						}
						

					}
				});
				
				((Button)dialog.findViewById(R.id.btnHistorial)).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						startActivity(new Intent(new Intent(PantallaPrincipal.this, GraficadorGlucosa.class)).putExtra("usuario", usuario));
					}
				});
			}
		});
		btnComidas = (Button)findViewById(R.id.btnComidas);
		btnComidas.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog = new Dialog(PantallaPrincipal.this);
				dialog.setContentView(R.layout.comidas_dialog);
				dialog.setTitle("Selecciona el tipo");
				dialog.setCancelable(true);
				dialog.show();
				Button dialDesayuno = (Button)dialog.findViewById(R.id.btnDesayuno);
				Button dialComida = (Button)dialog.findViewById(R.id.btnComida);
				Button dialCena = (Button)dialog.findViewById(R.id.btnCena);
				Button dialSnacks = (Button)dialog.findViewById(R.id.btnSnacks);
				dialDesayuno.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent in = new Intent(PantallaPrincipal.this, ComidasAct.class);
						in.putExtra("tipo", 0);
						in.putExtra("usuario", usuario);
						startActivity(in);
					}
				});
				dialComida.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent in = new Intent(PantallaPrincipal.this, ComidasAct.class);
						in.putExtra("tipo", 1);
						in.putExtra("usuario", usuario);
						startActivity(in);
					}
				});
				dialCena.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent in = new Intent(PantallaPrincipal.this, ComidasAct.class);
						in.putExtra("tipo", 2);
						in.putExtra("usuario", usuario);
						startActivity(in);
					}
				});
				dialSnacks.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent in = new Intent(PantallaPrincipal.this, ComidasAct.class);
						in.putExtra("tipo", 3);
						in.putExtra("usuario", usuario);
						startActivity(in);
					}
				});
				
			}
		});
	
		getRecomendaciones();
		esTuCumpleanos();
	}
	private void esTuCumpleanos(){

	}
	private void getRecomendaciones(){
		String query = "select gustosUsuario.idComida,catalogo_comidas.nombre, gustosUsuario.nivelGusto from gustosUsuario,catalogo_comidas where gustosUsuario.idUsuario="+usuario.idUsuario+" and gustosUsuario.idComida = catalogo_comidas.idComida order by nivelGusto desc";
		Cursor cursor = db.rawQuery(query, null);
		if(cursor.moveToFirst()){
			tvBienvenida.setText("Hoy te recomiendo comer "+cursor.getString(1));
		}else{
			ArrayList<Integer> idsComidas = new ArrayList<Integer>();
			
			cursor = db.rawQuery("select idComida,nombre from catalogo_comidas", null);
			if(cursor.moveToFirst()){
				
				do{
					idsComidas.add(cursor.getInt(0));
				}while(cursor.moveToNext());
				
			}
			Random ran = new Random();
			int idcom = ran.nextInt(idsComidas.size());
			cursor = db.rawQuery("select nombre from catalogo_comidas where idComida ="+idcom, null);
			if(cursor.moveToFirst()){
				tvBienvenida.setText("Hola, aœn no has agregado tus gustos, pero te recomiendo comer "+cursor.getString(0));
			}else{
				tvBienvenida.setText("Hola, aœn no has agregado tus gustos, pero te recomiendo comer naranja");
			}
			
			
		}
		cursor.close();
	}
	private long calcularDifHoras(){
		fechaHoy = cal.getTime();
		long diff = fechaHoy.getTime()-fechaUltimaGlucosa.getTime();
		 return (diff / (60 * 60 * 1000)); 
	}
	private boolean guardarGlucosaBD(float nivel){
		cal = Calendar.getInstance();
		fechaHoy = cal.getTime();
		fechaUltimaGlucosa = fechaHoy;
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		ContentValues row = new ContentValues();
		row.put("idUsuario", usuario.idUsuario);
		row.put("nivelGlucosa", nivel);
		row.put("fecha", formatter.format(fechaHoy));
		return db.insert("historialGlucosa",null, row) != -1;
	}

}
