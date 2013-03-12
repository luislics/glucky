package com.example.glucky;

import java.util.ArrayList;
import java.util.HashMap;

import clasesObjetos.Comida;

import com.glucky.utils.DBUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.RatingBar.OnRatingBarChangeListener;

public class GustosUsuario extends Activity {

	ListView lvGustos;
	SimpleAdapter adapter;
	ArrayList<Comida> comidas = new ArrayList<Comida>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gustos_usuario);
		lvGustos = (ListView)findViewById(R.id.lvGustos);
		SQLiteDatabase db = DBUtils.getStaticDb();
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
		db.close();
		loadAdapter();
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

				
				img.setImageResource(android.R.drawable.btn_star);
				TextView tv = (TextView) v.findViewById(R.id.label);
				tv.setText(comidas.get(pos).nombre);
				RatingBar rb = (RatingBar) v.findViewById(R.id.rate);
				rb.setRating(3);
				rb.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

					@Override
					public void onRatingChanged(RatingBar ratingBar,
							float rating, boolean fromUser) {
						// TODO Auto-generated method stub
						if (fromUser) {
							//sintomasCatalogo.get(pos).nivel = (int) rating;
							//cambios = true;
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
