package com.example.kronos.pmdmpaint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


public class Principal extends Activity {

    private static final int SELECCION_FOTO = 1;
    Vista v;
    private AlertDialog alerta;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECCION_FOTO) {
            if (resultCode == Activity.RESULT_OK) {
                Uri seleccion = data.getData();
                File archivo = new File(getPathFromURI(this, seleccion));
                if (archivo.exists()) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inMutable = true;
                    v.setBitmap(BitmapFactory.decodeFile(archivo.getAbsolutePath(), options));
                }
                v.setFondo(new Canvas(v.getBitmap()));
                v.invalidate();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = new Vista(this);
        setContentView(v);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*Paint pincel = new Paint();
        pincel.setAntiAlias(true);
        pincel.setStyle(Paint.Style.FILL_AND_STROKE);*/
        int id = item.getItemId();
        if (id == R.id.recta) {
            v.setAccion(0);
            return true;
        }else if (id == R.id.mano) {
            v.setAccion(1);
            return true;
        }else if (id == R.id.circulo) {
            v.setAccion(2);
            return true;
        }else if (id == R.id.rectangulo) {
            v.setAccion(3);
            return true;
        }else if (id == R.id.color) {
            v.color();
            return true;
        }else if (id == R.id.goma) {
            v.setAccion(4);
            return true;
        }else if (id == R.id.save) {
            guardarDibujo();
            return true;
        }else if (id == R.id.load) {
            cargarDibujo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String getPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }



    private void guardarDibujo() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        alert.setTitle(getString(R.string.nombreguardar));
        final View vista = inflater.inflate(R.layout.guardar_dibujo, null);
        alert.setView(vista);

        final EditText nomb = (EditText) vista.findViewById(R.id.etFichero);

        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String nombre = nomb.getText().toString();
                if(nombre.equals("")){
                    Toast.makeText(Principal.this, getString(R.string.errornombre), Toast.LENGTH_SHORT).show();
                }else {
                    File carpeta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath());
                    File archivo = new File(carpeta, nombre + ".png");
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(archivo);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    v.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, fos);

                    agregarGaleria(archivo);
                }

            }
        });

        alert.setNegativeButton(android.R.string.no, null);
        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                alerta.dismiss();
            }
        });

        alerta = alert.create();
        alerta.show();
    }
    private void cargarDibujo() {
        Intent pickMedia = new Intent(Intent.ACTION_PICK);
        pickMedia.setType("image/*");
        startActivityForResult(pickMedia, SELECCION_FOTO);
    }
    private void agregarGaleria(File f) {
        Intent intent = new Intent (Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(f);
        intent.setData(uri);
        this.sendBroadcast(intent);
    }

}
