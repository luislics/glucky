package com.glucky.Principal;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import clasesObjetos.Usuario;

import com.example.glucky.GraficadorGlucosa;
import com.example.glucky.GustosUsuario;
import com.example.glucky.MainActivity;
import com.example.glucky.R;
import com.example.glucky.Splashscreen;
import com.glucky.utils.DBUtils;

import feedrss.SimpleRSSReaderActivity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PantallaPrincipal extends ListActivity{
	TextView tvBienvenida;
	Usuario usuario;
	Button btnGustos;
	Button btnnoticias;
	Button btnglucosa;
	List headlines;
	List links;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activityprincipal);
		//aquí inicia el rss
		headlines = new ArrayList();
		links = new ArrayList();

		try {
			URL url = new URL("http://feeds.guiainfantil.com/guia_infantil_ninos_bebes");

			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(false);
			XmlPullParser xpp = factory.newPullParser();

		        // We will get the XML from an input stream
			xpp.setInput(getInputStream(url), "utf-8");

		        /* We will parse the XML content looking for the "<title>" tag which appears inside the "<item>" tag.
		         * However, we should take in consideration that the rss feed name also is enclosed in a "<title>" tag.
		         * As we know, every feed begins with these lines: "<channel><title>Feed_Name</title>...."
		         * so we should skip the "<title>" tag which is a child of "<channel>" tag,
		         * and take in consideration only "<title>" tag which is a child of "<item>"
		         *
		         * In order to achieve this, we will make use of a boolean variable.
		         */
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
				startActivity(new Intent(new Intent(PantallaPrincipal.this, SimpleRSSReaderActivity.class)));
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
	
	}
	public InputStream getInputStream(URL url) {
		   try {
		       return url.openConnection().getInputStream();
		   } catch (IOException e) {
		       return null;
		     }
		}
	protected void onListItemClick(ListView l, View v, int position, long id) {
		   Uri uri = Uri.parse((String) links.get(position));
		   Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		   startActivity(intent);
		}

}
