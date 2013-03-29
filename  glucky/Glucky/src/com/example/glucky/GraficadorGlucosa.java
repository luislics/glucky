package com.example.glucky;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GraficadorGlucosa extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.glucosa);
		double x1=2.0;
		double x2= 1.5;
		double x3 =2.5;
		double x4=1.0;
		double suma =x1+x2+x3+x4;
		double resglucosa=suma/4;
		// graph with dynamically genereated horizontal and vertical labels
		 GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {  
	              new GraphViewData(1, x1)  
	              , new GraphViewData(2, x2)  
	              , new GraphViewData(3, x3)  
	              , new GraphViewData(4, x4)  
	        });  


	        GraphView graphView = new BarGraphView(  
	              this // context  
	              , "Tus Niveles de glucosa" // heading  
	        );  
	        graphView.addSeries(exampleSeries); // data  
	        graphView.setHorizontalLabels(new String[]{"Antier","Ayer","Hoy"});
	        graphView.setVerticalLabels(new String[]{"Bajo","Medio","Alto"});
	        graphView.setViewPort(2, 40);
	       // graphView.setScrollable(true);
	        //graphView.setScalable(true);
	        LinearLayout layout = (LinearLayout) findViewById(R.id.glucographic);
	        ImageView buenaomala=(ImageView)findViewById(R.id.imagengrafica);
	        TextView nivel= (TextView)findViewById(R.id.textView1);
	        layout.addView(graphView);
	        if (resglucosa>1){
		        buenaomala.setImageResource(R.drawable.sad);
		        nivel.setText("Tu nivel actual es malo");
	        }else{
	        buenaomala.setImageResource(R.drawable.happy);
	        nivel.setText("Tu nivel actual es bueno");
	        }
	        nivel.setTextColor(Color.WHITE);
	}

}
