package com.example.glucky;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import clasesObjetos.Comida;
import clasesObjetos.GustoUsuario;
import clasesObjetos.Usuario;

import com.glucky.utils.DBUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RatingBar.OnRatingBarChangeListener;

public class ComidasAct extends Activity {
	ArrayList<Comida> comidas = new ArrayList<Comida>();
	SimpleAdapter adapter;
	ListView lvComidas;
	ArrayList<Integer> comidasActualesUsuario = new ArrayList<Integer>();
	SQLiteDatabase db;
	Usuario usuario;
	Calendar cal;
	Button btnGuardar;
	String m = "";
	@Override
	protected void onDestroy(){
		db.close();
		super.onDestroy();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comidas);
		lvComidas = (ListView)findViewById(R.id.lvComidas);
		cal = Calendar.getInstance();
		db = DBUtils.getStaticDb();
		btnGuardar = (Button)findViewById(R.id.btnGuardar);

		btnGuardar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				guardarComidas();
				finish();
			}
		});
		Bundle extras = getIntent().getExtras();
		int tipoComida = extras.getInt("tipo"); // 0 desayuno- 1 comida- 2 cena- 3 snack- 4 todo
		usuario = (Usuario)extras.getSerializable("usuario");
		String query = "select idComida, nombre, glucosa, Nutriente, horaComida from catalogo_comidas order by horaComida like '%"+tipoComida+"%' desc";
		Cursor cursor = db.rawQuery(query, null);
		if(cursor.moveToFirst()){
			do{
				Comida comida = new Comida();
				comida.idComida = cursor.getInt(0);
				comida.nombre = cursor.getString(1);
				comida.glucosa = cursor.getInt(2);
				comida.nutriente = cursor.getInt(3);
				comida.tipoComida = cursor.getString(4);
				comidas.add(comida);	
			}while(cursor.moveToNext());
		}

		cursor.close();
		
		
		cursor = db.rawQuery("select comidas_desglosadas.idAlimento, fecha, nombre from comidas, comidas_desglosadas, " +
				"catalogo_comidas where comidas.idComida = comidas_desglosadas.idComida and comidas_desglosadas.idAlimento = catalogo_comidas.idComida and idUsuario="+usuario.idUsuario, null);
		if(cursor.moveToFirst()){
			do{
				
				String fecha = cursor.getString(1);
				//SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				//formatter.format(fecha);
				/*
				if(esDeHoy(fecha.substring(0, fecha.indexOf(' ')))){
					if(pasaron3Horas(fecha.substring(fecha.indexOf(' ')))){
						
						comidasActualesUsuario.add(cursor.getInt(0));
					}
				} */
				comidasActualesUsuario.add(cursor.getInt(0));
				/*m += cursor.getInt(0)+"-"+cursor.getString(1)+"-"+cursor.getString(2);
				Toast.makeText(ComidasAct.this, m, Toast.LENGTH_LONG).show();*/
			}while(cursor.moveToNext());
		}
		
		cursor.close();
		loadAdapter();
	}
	private void guardarComidas(){
		
		Cursor cursor = db.rawQuery("select max(idComida) from comidas", null);
		int nuevoId;
		if(cursor.moveToFirst()){
			nuevoId = cursor.getInt(0)+1;
		}else{
			nuevoId = 1;
		}
		cursor.close();
		ContentValues row = new ContentValues();
		row.put("idComida", nuevoId);
		row.put("idUsuario",usuario.idUsuario);
		row.put("fecha", cal.get(Calendar.DATE)+"/"+cal.get(Calendar.MONTH)+"/"+
		cal.get(Calendar.YEAR)+" "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE));
		db.insert("comidas", null, row);
		row = new ContentValues();
		for (Integer idCom : comidasActualesUsuario) {
			row.put("idComida",nuevoId);
			row.put("idAlimento",idCom);
		}
		db.insert("comidas_desglosadas", null, row);
	}
	private boolean esDeHoy(String fecha){
		int dd = cal.get(Calendar.DATE);
		int mm = cal.get(Calendar.MONTH);
		int yyyy = cal.get(Calendar.YEAR);
		String fch = dd+"/"+mm+"/"+yyyy;
		return fch.equals(fecha);
	}
	private boolean pasaron3Horas(String fecha){
		int horaActual = cal.get(Calendar.HOUR_OF_DAY);
		int horaComida = Integer.parseInt(fecha.substring(0, fecha.indexOf(":")).replace(" ", ""));
		return true;
	}
	private void loadAdapter() {
		adapter = new SimpleAdapter(this, null, R.layout.row, null,null) {

			@Override
			public View getView(final int pos, View convertView,
					ViewGroup parent) {
				View v = convertView;

				if (v == null) {
					LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					v = vi.inflate(R.layout.row_comidas, null);
				}
				ImageView img = (ImageView) v.findViewById(R.id.imgSintoma);

				
				img.setImageResource(android.R.drawable.btn_star);
				TextView tv = (TextView) v.findViewById(R.id.label);
				tv.setText(comidas.get(pos).nombre);
				CheckBox cb = (CheckBox)v.findViewById(R.id.cbComida);
				//cb.setSelected());
				cb.setChecked(comidasActualesUsuario.contains(comidas.get(pos).idComida));
				
			/*	cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// TODO Auto-generated method stub
						if(isChecked){
							comidasActualesUsuario.add(comidas.get(pos).idComida);
						}else{
							//comidasActualesUsuario.remove(new Integer(comidas.get(pos).idComida));
						}
						
					}
				});*/
				cb.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
					}
				});
				return v;
			}

			@Override
			public int getCount() {
				return comidas.size();
			}
		};

		lvComidas.setAdapter(adapter);
	}



}
