package com.example.glucky;

import java.util.ArrayList;

import clasesObjetos.NivelGlucosa;
import clasesObjetos.Usuario;

import com.glucky.utils.DBUtils;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GraficadorGlucosa extends Activity {

	SQLiteDatabase db;
	ArrayList<NivelGlucosa> registrosGlucosa = new ArrayList<NivelGlucosa>();
	String[] fechas;
	Usuario usuario;
	final static float LIMITE = 90;
	
	protected void onDestroy(){
		db.close();
		super.onDestroy();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.glucosa);
		Bundle extras = getIntent().getExtras();
		usuario = (Usuario)extras.getSerializable("usuario");
		double x1=2.7;
		double x2= 2.3;
		double x3 =2.5;
		double x4=3.0;
		double suma =x1+x2+x3+x4;
		float resglucosa= 0;
		
		// graph with dynamically genereated horizontal and vertical labels
	/*	 GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {  
	              new GraphViewData(1, x1)  
	              , new GraphViewData(2, x2)  
	              , new GraphViewData(3, x3)  
	              , new GraphViewData(4, x4)  
	        });*/
		 
		 db = DBUtils.getStaticDb();
		 Cursor cursor = db.rawQuery("select nivelGlucosa, fecha from historialGlucosa where idUsuario="+usuario.idUsuario,null);
		 if(cursor.moveToFirst()){
			 do{
				 registrosGlucosa.add(new NivelGlucosa(cursor.getFloat(0),cursor.getString(1)));
				 resglucosa += cursor.getFloat(0);
				 
			 }while(cursor.moveToNext());
		 }
		 fechas = new String[registrosGlucosa.size()];
		 GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {});
		 if(registrosGlucosa.size()>7){
			 
		 }else{
			 for (int i=0; i<registrosGlucosa.size(); i++) {
				/*exampleSeries = new GraphViewSeries(new GraphViewData[]{
						new GraphViewData(regis, valueY)
				});*/
				 
				 GraphViewData data = new GraphViewData(i+1, registrosGlucosa.get(i).nivel);
				 exampleSeries.appendData(data, true);
				 fechas[i] = registrosGlucosa.get(i).fecha;
			}
		 }
	        GraphView graphView = new BarGraphView(  
	              this // context  
	              , "Tus Niveles de glucosa" // heading  
	        );  
	        graphView.addSeries(exampleSeries); // data  
	        
	        graphView.setHorizontalLabels(fechas);
	        graphView.setVerticalLabels(new String[]{"Alto","Medio","Bajo"});
	        //graphView.setViewPort(2, 40);
	       // graphView.setScrollable(true);
	        //graphView.setScalable(true);
	        
	        LinearLayout layout = (LinearLayout) findViewById(R.id.glucographic);
	        ImageView buenaomala=(ImageView)findViewById(R.id.imagengrafica);
	        TextView nivel= (TextView)findViewById(R.id.textView1);
	        layout.addView(graphView);
	        resglucosa = resglucosa / registrosGlucosa.size();
	        if (resglucosa>LIMITE){
		        buenaomala.setImageResource(R.drawable.tristealb);
		        nivel.setText("Tu nivel acumulado de glucosa es malo");
	        }else{
	        buenaomala.setImageResource(R.drawable.felizalb);
	        nivel.setText("Tu nivel acumulado de glucosa es bueno");
	        }
	        nivel.setTextColor(Color.WHITE);
	}

}
