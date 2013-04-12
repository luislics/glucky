package com.glucky.Principal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import clasesObjetos.Usuario;

import com.example.glucky.ComidasAct;
import com.example.glucky.GraficadorGlucosa;
import com.example.glucky.GustosUsuario;
import com.example.glucky.R;

import feedrss.AndroidRssReader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class PantallaPrincipal extends ListActivity{
	TextView tvBienvenida;
	Usuario usuario;
	Button btnGustos;
	Button btnnoticias;
	Button btnglucosa;
	List headlines;
	List links;
	Button btnComidas;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activityprincipal);
		//aquí inicia el rss
		headlines = new ArrayList();
		links = new ArrayList();
/**
		try {
			URL url = new URL("http://feeds.guiainfantil.com/guia_infantil_ninos_bebes");

			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(false);
			XmlPullParser xpp = factory.newPullParser();

		        // We will get the XML from an input stream
			xpp.setInput(getInputStream(url), "utf-8");

	
			boolean insideItem = false;

		        // Returns the type of current event: START_TAG, END_TAG, etc..
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {

					if (xpp.getName().equalsIgnoreCase("item")) {
						insideItem = true;
					} else if (xpp.getName().equalsIgnoreCase("title")) {
						if (insideItem)
							headlines.add(xpp.nextText()); //extract the headline
					} else if (xpp.getName().equalsIgnoreCase("link")) {
						if (insideItem)
							links.add(xpp.nextText()); //extract the link of article
					}
				}else if(eventType==XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")){
					insideItem=false;
				}

				eventType = xpp.next(); //move to next element
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		// Binding data
		ArrayAdapter adapter = new ArrayAdapter(this,
				android.R.layout.simple_list_item_1, headlines);
		
		setListAdapter(adapter);
		//aqui acaba el rss 
		
		**/
		
		tvBienvenida = (TextView)findViewById(R.id.tvBienvenida);
		Bundle extras = getIntent().getExtras();
		usuario = (Usuario) extras.getSerializable("usuario");
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
				startActivity(new Intent(new Intent(PantallaPrincipal.this, GraficadorGlucosa.class)));
			}
		});
		btnComidas = (Button)findViewById(R.id.btnComidas);
		btnComidas.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Dialog dialog = new Dialog(PantallaPrincipal.this);
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
	
	}
	public InputStream getInputStream(URL url) {
		   try {
		       return url.openConnection().getInputStream();
		   } catch (IOException e) {
		       return null;
		     }
		}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		   Uri uri = Uri.parse((String) links.get(position));
		   Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		   startActivity(intent);
		}

}
