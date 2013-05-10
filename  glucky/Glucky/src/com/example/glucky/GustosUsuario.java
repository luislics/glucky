package com.example.glucky;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import clasesObjetos.Comida;
import clasesObjetos.GustoUsuario;
import clasesObjetos.Usuario;

import com.glucky.registro.NuevoUsuarioActivity;
import com.glucky.utils.DBUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RatingBar.OnRatingBarChangeListener;

public class GustosUsuario extends Activity {

	ListView lvGustos;
	SimpleAdapter adapter;
	ArrayList<Comida> comidas = new ArrayList<Comida>();
	ArrayList<GustoUsuario> gustosUsuario = new ArrayList<GustoUsuario>();
	Calendar cal = Calendar.getInstance();
	String fechaHoy;
	Usuario usuario;
	Button btnGuardar;
	SQLiteDatabase db;
	@Override
	protected void onDestroy(){
		db.close();
		super.onDestroy();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gustos_usuario);
		Bundle extras = getIntent().getExtras();
		usuario = (Usuario)extras.getSerializable("usuario");
		lvGustos = (ListView)findViewById(R.id.lvGustos);
		fechaHoy = Integer.toString(cal.get(Calendar.DATE))+"/"+Integer.toString(cal.get(Calendar.MONTH))+"/"+Integer.toString(cal.get(Calendar.YEAR));
		btnGuardar = (Button)findViewById(R.id.btnGuardar);
		btnGuardar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				guardarGustos();
				finish();
			}
		});
		db = DBUtils.getStaticDb();
		Cursor cursor = db.rawQuery("select idComida, nombre, glucosa,Nutriente from catalogo_comidas", null);
		if(cursor.moveToFirst()){
			do{
				Comida comida = new Comida();
				comida.idComida = cursor.getInt(0);
				comida.nombre = cursor.getString(1);
				comida.glucosa = cursor.getInt(2);
				comida.nutriente = cursor.getInt(3);
				comidas.add(comida);
			}while(cursor.moveToNext());
		}
		cursor.close();
		cursor = db.rawQuery("select idComida,nivelGusto,fechaAgregado from gustosUsuario where idUsuario = "+usuario.idUsuario, null);
		if(cursor.moveToFirst()){
			do{
				GustoUsuario gusto = new GustoUsuario(cursor.getInt(0),cursor.getInt(1),cursor.getString(2));
				
				gustosUsuario.add(gusto);
			}while(cursor.moveToNext());
		}
		cursor.close();
		
		loadAdapter();
	}
	private void guardarGustos(){
		
		db.delete("gustosUsuario", "idUsuario = ?", new String[]{Integer.toString(usuario.idUsuario)});
		ContentValues row = new ContentValues();
		boolean exito = true;
		for(int i=0; i<gustosUsuario.size(); i++){
			row.put("idUsuario", usuario.idUsuario);
			row.put("idComida", gustosUsuario.get(i).idComida);
			row.put("fechaAgregado", gustosUsuario.get(i).fechaAgregado);
			row.put("nivelGusto", gustosUsuario.get(i).nivelGusto);
			exito = db.insert("gustosUsuario", null, row) != -1;
			
		}
		if(exito){
			Toast.makeText(GustosUsuario.this, "Gustos guardados correctamente", Toast.LENGTH_SHORT).show();
		}
		
		
	}
	private void loadAdapter() {
		adapter = new SimpleAdapter(this, null, R.layout.row, null,null) {

			@Override
			public View getView(final int pos, View convertView,
					ViewGroup parent) {
				View v = convertView;

				if (v == null) {
					LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					v = vi.inflate(R.layout.row, null);
				}
				ImageView img = (ImageView) v.findViewById(R.id.imgSintoma);

				
				//img.setImageResource(android.R.drawable.btn_star);
				String nombreRecurso = ComidasAct.removeAcentos(comidas.get(pos).nombre).replace(" ", "").toLowerCase();
				int imgid = getResources().getIdentifier(nombreRecurso, "drawable", getPackageName());
				img.setImageBitmap(ComidasAct.decodeSampledBitmapFromResource(getResources(),imgid , 50, 50));
				
				TextView tv = (TextView) v.findViewById(R.id.label);
				tv.setText(comidas.get(pos).nombre);
				RatingBar rb = (RatingBar) v.findViewById(R.id.rate);
				rb.setRating(GustoUsuario.getNivel(comidas.get(pos).idComida,gustosUsuario));
				rb.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

					@Override
					public void onRatingChanged(RatingBar ratingBar,
							float rating, boolean fromUser) {
						// TODO Auto-generated method stub
						if (fromUser) {
							//sintomasCatalogo.get(pos).nivel = (int) rating;
							//cambios = true;
							GustoUsuario.setNivel(comidas.get(pos).idComida, gustosUsuario,(int) rating,fechaHoy);
						}

					}
				});

				return v;
			}

			@Override
			public int getCount() {
				return comidas.size();
			}
		};

		lvGustos.setAdapter(adapter);
	}


}
