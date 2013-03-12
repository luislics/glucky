package com.glucky.registro;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.glucky.R;
import com.glucky.utils.DBUtils;

public class NuevoUsuarioActivity extends Activity {
	EditText etUsuario, etPass;
	Button btnCrear;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nuevo_user);
		etUsuario = (EditText)findViewById(R.id.etUs);
		etPass =(EditText)findViewById(R.id.etPas);
		btnCrear = (Button)findViewById(R.id.btnCrear);
		btnCrear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SQLiteDatabase db = DBUtils.getStaticDb();
				Cursor cursor = db.rawQuery("select * from usuarios where nombre = ?",new String[]{etUsuario.getText().toString()});
				if(cursor.moveToFirst()){
					Toast.makeText(NuevoUsuarioActivity.this, cursor.getInt(0)+"Ya existe un usuario con el mismo nombre", Toast.LENGTH_SHORT).show();
				}else{
					cursor.close();
					ContentValues row = new ContentValues();
					row.put("nombre", etUsuario.getText().toString());
					row.put("password",etPass.getText().toString());
					if(db.insert("usuarios", null, row)!=-1){
						Toast.makeText(NuevoUsuarioActivity.this, "Usuario "+etUsuario.getText().toString()+" creado exitosamente.", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(NuevoUsuarioActivity.this, "Ocurri— un error al crear el usuario", Toast.LENGTH_SHORT).show();
					}
				}
				db.close();
			}
		});


		
		
	}

}
