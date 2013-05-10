package com.example.glucky;


import java.util.ArrayList;
import java.util.Calendar;

import clasesObjetos.Comida;
import clasesObjetos.Usuario;

import com.glucky.utils.DBUtils;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
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
	String fechaHoy;
	float nivelGlucosa;
	int ndx;
	
	static float LIMITE = 180;
	static float NIVELMEDIO = 130;
	static float NIVELNORMAL = 100;
	ArrayList<Float> cantidades = new ArrayList<Float>();
	ArrayList<Integer> intss = new ArrayList<Integer>();
	private static final int MY_NOTIFICATION_ID=1;
	private NotificationManager notificationManager;
	private Notification myNotification;
	@Override
	protected void onDestroy(){
		db.close();
		super.onDestroy();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_comidas);
		
		lvComidas = (ListView)findViewById(R.id.lvComidas);
		cal = Calendar.getInstance();
		db = DBUtils.getStaticDb();
		
		fechaHoy = cal.get(Calendar.DATE)+"/"+cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.YEAR);
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
		String query = "select idComida, nombre, glucosa, Nutriente, horaComida,Cantidad,Unidad from catalogo_comidas order by horaComida like "+tipoComida+" desc, horaComida like '%"+tipoComida+"%' desc";
		Cursor cursor = db.rawQuery(query, null);
		if(cursor.moveToFirst()){
			do{
				Comida comida = new Comida();
				comida.idComida = cursor.getInt(0);
				comida.nombre = cursor.getString(1);
				comida.glucosa = cursor.getInt(2);
				comida.nutriente = cursor.getInt(3);
				comida.tipoComida = cursor.getString(4);
				comida.cantidad = cursor.getInt(5);
				comida.unidad = cursor.getString(6);
				comidas.add(comida);	
			}while(cursor.moveToNext());
		}

		cursor.close();
		
		//String dbug = "";
		query = "select comidas_desglosadas.idAlimento, fecha,comidas_desglosadas.idComida from comidas_desglosadas, comidas where comidas_desglosadas.idComida = comidas.idComida and comidas.idUsuario = "+usuario.idUsuario;
		cursor = db.rawQuery(query, null);//
		if(cursor.moveToFirst()){
			do{
				
				String fecha = cursor.getString(1);
				
				String[] fchParts = fecha.split(" ");					
				if(esDeHoy(fchParts[0])&&!pasaron3Horas(fchParts[1])){
					comidasActualesUsuario.add(cursor.getInt(0));
					intss.add(cursor.getInt(2));
					cantidades.add(new Float(5));
				}
			//	Toast.makeText(ComidasAct.this, fchParts[1], Toast.LENGTH_LONG).show();

				//dbug += Integer.toString(cursor.getInt(0))+"-";
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
		
		//borra
		for (Integer idC : intss) {
			db.delete("comidas", "idComida = ?", new String[]{Integer.toString(idC)});
		}
		
		
		
		ContentValues row = new ContentValues();
		row.put("idComida", nuevoId);
		row.put("idUsuario",usuario.idUsuario);
		row.put("fecha", cal.get(Calendar.DATE)+"/"+cal.get(Calendar.MONTH)+"/"+
		cal.get(Calendar.YEAR)+" "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE));
		db.insert("comidas", null, row);
		
		for (Integer idCom : comidasActualesUsuario) {
			row = new ContentValues();
			row.put("idComida",nuevoId);
			row.put("idAlimento",idCom);
			//dbug += nuevoId+"/"+idCom+"-";
			db.insert("comidas_desglosadas", null, row);
		}
		
		nivelGlucosa = getSumaGlucosa();
		if(nivelGlucosa>LIMITE){
			sendNotification(3);
		}else{
			if(nivelGlucosa > NIVELMEDIO){
				sendNotification(2);
			}else{
				sendNotification(1);
			}
		}
		
		
	//	Toast.makeText(ComidasAct.this, dbug, Toast.LENGTH_LONG).show();
		
	}
	private float getSumaGlucosa(){
		float tmp = 0;
		for(int i=0; i<comidasActualesUsuario.size(); i++){
			for(int j=0; j<comidas.size(); j++){
				if(comidas.get(j).idComida == comidasActualesUsuario.get(i)){
					tmp += (comidas.get(j).glucosa)*.5*(cantidades.get(i));
					break;
				}
			}
		}
		return tmp+100;
	}
	private void sendNotification(int grado){
		final String myBlog = "http://www.informador.com.mx/2746/diabetes";
		notificationManager =
			    (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			   myNotification = new Notification(R.drawable.logo,
			     "Glucky!",
			     System.currentTimeMillis());
			   Context context = getApplicationContext();
			   String notificationText = "";
			   String notificationTitle = "";
			   if(grado==1){
				   notificationText = "Comida rica y saludable";
				   notificationTitle = "Excelente "+usuario.nombre;
			   }
			   if(grado == 2){
				   notificationText = "¡Mídete en la siguiente comida"+usuario.nombre+"!";
				   notificationTitle = "Cuidado";
			   }
			   if(grado==3){
				   notificationText = "¡Cuidate "+usuario.nombre+"!";
				   notificationTitle = "¡Te pasaste!";
			   }
			   
			   
			   Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(myBlog));
			   PendingIntent pendingIntent = PendingIntent.getActivity(ComidasAct.this,
			       0, myIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
			   myNotification.defaults |= Notification.DEFAULT_SOUND;
			   myNotification.flags |=  Notification.FLAG_NO_CLEAR;
			   myNotification.setLatestEventInfo(context,
			      notificationTitle,
			      notificationText,
			      pendingIntent);
			   notificationManager.notify(MY_NOTIFICATION_ID, myNotification);
			  
	}
	private boolean esDeHoy(String fecha){
		
		return fechaHoy.equals(fecha);
	}
	private boolean pasaron3Horas(String fecha){
		int horaActual = cal.get(Calendar.HOUR_OF_DAY);
		int horaComida = Integer.parseInt(fecha.split(":")[0]);
		return (horaActual - horaComida) >= 3;
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
				
				
				//img.setImageResource(android.R.drawable.btn_star);
				String nombreRecurso = removeAcentos(comidas.get(pos).nombre).replace(" ", "").toLowerCase();
				int imgid = getResources().getIdentifier(nombreRecurso, "drawable", getPackageName());
				img.setImageBitmap(decodeSampledBitmapFromResource(getResources(),imgid , 50, 50));
				
				/*
				 * 
				 * int imgid= getResources().getIdentifier(removeAcentos(nombre.replace(" ", "").toLowerCase().replace(".", "")),"drawable", this.getPackageName());
        	imgView.setImageBitmap(decodeSampledBitmapFromResource(getResources(), imgid, width, 400));
				 */
				TextView tv = (TextView) v.findViewById(R.id.label);
				tv.setText(comidas.get(pos).nombre);
				final CheckBox cb = (CheckBox)v.findViewById(R.id.cbComida);
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
						if(cb.isChecked()){
							comidasActualesUsuario.add(comidas.get(pos).idComida);
							cantidades.add(new Float(1));

							//Toast.makeText(ComidasAct.this, "cl", Toast.LENGTH_LONG).show();
						}else{
							//cantidades.remove(cantidades.size()-1);
							int indx = comidasActualesUsuario.indexOf(new Integer(comidas.get(pos).idComida));
							cantidades.remove(indx);
							comidasActualesUsuario.remove(new Integer(comidas.get(pos).idComida));

						}
						
					}
				});
				v.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						final Dialog dialog = new Dialog(ComidasAct.this);
						dialog.setContentView(R.layout.dialog_cantidad);
						dialog.setTitle("Cantidad");
						dialog.setCancelable(true);
						if(cb.isChecked()){
							dialog.show();
						}
						
						SeekBar sbar = (SeekBar)dialog.findViewById(R.id.sbCantidad);
						final TextView tv = (TextView)dialog.findViewById(R.id.tvCantidad);
						sbar.setMax(10);
						Button btnOK = (Button)dialog.findViewById(R.id.btngg);
						btnOK.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
						
						if(cb.isChecked()){
							sbar.setEnabled(true);
							ndx =comidasActualesUsuario.indexOf(new Integer(comidas.get(pos).idComida));
							sbar.setProgress(Math.round(cantidades.get(ndx)));
							tv.setText(comidas.get(pos).cantidad+" "+comidas.get(pos).unidad);
						}else{
							sbar.setEnabled(false);
						}
						
						sbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
							
							@Override
							public void onStopTrackingTouch(SeekBar seekBar) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onStartTrackingTouch(SeekBar seekBar) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onProgressChanged(SeekBar seekBar, int progress,
									boolean fromUser) {
								// TODO Auto-generated method stub
								if(fromUser){
									tv.setText(progress*comidas.get(pos).cantidad+" "+comidas.get(pos).unidad);
									cantidades.set(ndx, new Float(progress));
								}
							}
						});
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

    public static String removeAcentos(String input) {
        // Cadena de caracteres original a sustituir.
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = input;
        for (int i=0; i<original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }//for i
        return output;
    }//remove1
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(res, resId, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeResource(res, resId, options);
	}
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {
        if (width > height) {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        } else {
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }
    }
    return inSampleSize;
}

}
