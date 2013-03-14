package com.example.glucky;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class GraficadorGlucosa extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.glucosa);
		// graph with dynamically genereated horizontal and vertical labels
		 GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {  
	              new GraphViewData(1, 2.0d)  
	              , new GraphViewData(2, 1.5d)  
	              , new GraphViewData(3, 2.5d)  
	              , new GraphViewData(4, 1.0d)  
	        });  


	        GraphView graphView = new BarGraphView(  
	              this // context  
	              , "GraphViewDemo" // heading  
	        );  
	        graphView.addSeries(exampleSeries); // data  
	        graphView.setHorizontalLabels(new String[]{"Antier","Ayer","Hoy"});
	        graphView.setVerticalLabels(new String[]{"Bajo","Medio","Alto"});
	        graphView.setViewPort(2, 40);
	        graphView.setScrollable(true);
	        graphView.setScalable(true);
	        LinearLayout layout = (LinearLayout) findViewById(R.id.glucographic);  
	        layout.addView(graphView);
	}

}
