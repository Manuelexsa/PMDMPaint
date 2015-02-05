package com.example.kronos.pmdmpaint;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by kronos on 03/02/2015.
 */
public class Vista extends View implements ColorPicker.OnColorChangedListener{

    private float x0 = -1, y0 = -1, xi = -1, yi = -1, f = 10;
    private Paint pincel;
    private int alto, ancho;
    private List<Recta> rectas = new ArrayList<Recta>();
    private Bitmap mapaDeBits;
    private Canvas lienzoFondo;
    private Path rectaPoligonal = new Path();
    private Path[] rutasPath = new Path[3];
    private Coordenadas[] coordenadasPath = new Coordenadas[3];

    private Context contx;
    private int accion;
    private double radio = 0;
    private int grosor = 8;
    private int color = Color.BLACK;

    class Recta {
        public float x0, y0, xi, yi;

        Recta(float x0, float y0, float xi, float yi) {
            this.x0 = x0;
            this.y0 = y0;
            this.xi = xi;
            this.yi = yi;
        }
    }

    class Coordenadas {
        public float x0, y0, xi, yi;

        Coordenadas(float x0, float y0, float xi, float yi) {
            this.x0 = x0;
            this.y0 = y0;
            this.xi = xi;
            this.yi = yi;
        }
    }

    public Vista(Context context) {
        super(context);
        pincel = new Paint();
        pincel.setStrokeWidth(grosor);
        pincel.setStyle(Paint.Style.STROKE);
        pincel.setAntiAlias(true);
        contx = context;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mapaDeBits = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        lienzoFondo = new Canvas(mapaDeBits);
        alto = h;
        ancho = w;
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        c.drawColor(Color.WHITE);
        c.drawBitmap(mapaDeBits, 0, 0, null);
        //lienzoFondo.drawBitmap(mapaDeBits, 0, 0, null);
       /* for (Recta r : rectas) {
            c.drawLine(r.x0, r.y0, r.xi, r.yi, pincel);
        }*/
        switch (accion) {
                case 0://Linea recta
                    pincel.setColor(color);
                    pincel.setStrokeWidth(grosor);
                    c.drawLine(x0, y0, xi, yi, pincel);
                    break;
            case 1://A mano alzada
                pincel.setColor(color);
                pincel.setStrokeWidth(grosor);
                c.drawPath(rectaPoligonal, pincel);
                break;
            case 2://Circulo
                pincel.setColor(color);
                pincel.setStrokeWidth(grosor);
                c.drawCircle(x0, y0, (float) radio, pincel);
                break;
            case 3://Rectangulo
                pincel.setColor(color);
                pincel.setStrokeWidth(grosor);

                float xe0=Math.min(x0,xi);
                float xei=Math.max(x0,xi);
                float ye0=Math.min(y0,yi);
                float yei=Math.max(y0,yi);
                c.drawRect(xe0, ye0, xei, yei, pincel);
                break;
            case 4://Borrar
                pincel.setColor(Color.WHITE);
                pincel.setStrokeWidth(50);
                c.drawPath(rectaPoligonal, pincel);
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (accion) {
                    case 0://Linea recta
                        x0 = x;
                        y0 = y;
                        break;
                    case 1://A mano alzada
                        x0 = xi = event.getX();
                        y0 = yi = event.getY();
                        rectaPoligonal.reset();
                        rectaPoligonal.moveTo(x0, y0);
                        break;
                    case 2://Circulo
                        x0 = x;
                        y0 = y;
                        break;
                    case 3://Rectangulo
                        x0 = x;
                        y0 = y;
                        break;
                    case 4://Borrar
                        x0 = xi = event.getX();
                        y0 = yi = event.getY();
                        rectaPoligonal.reset();
                        rectaPoligonal.moveTo(x0, y0);
                        break;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                switch (accion) {
                    case 0://recta
                        xi = x;
                        yi = y;
                        //lienzoFondo.drawLine(x0,y0,x1,y1,pincel);
                        invalidate();
                        break;
                    case 1://mano
                        rectaPoligonal.quadTo(xi, yi, (x + xi) / 2, (y + yi) / 2);
                        xi = x;
                        yi = y;
                        x0 = xi;
                        y0 = yi;
                        lienzoFondo.drawLine(x0, y0, xi, yi, pincel);
                        invalidate();
                        break;
                    case 2://Circulo
                        xi = x;
                        yi = y;
                        radio = Math.sqrt(Math.pow((xi - x0), 2) + Math.pow((yi - y0), 2));
                        //lienzoFondo.drawCircle(x0,y0,(float)radio,pincel);
                        invalidate();
                        break;
                    case 3://Rectangulo
                        xi = x;
                        yi = y;
                        invalidate();
                        break;
                    case 4://borrar
                        rectaPoligonal.quadTo(xi, yi, (x + xi) / 2, (y + yi) / 2);
                        xi = x;
                        yi = y;
                        x0 = xi;
                        y0 = yi;
                        lienzoFondo.drawLine(x0, y0, xi, yi, pincel);
                        invalidate();
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                switch (accion) {
                    case 0://recta
                        lienzoFondo.drawLine(x0, y0, xi, yi, pincel);
                        break;
                    case 1://mano
                        xi = x;
                        yi = y;
                        lienzoFondo.drawPath(rectaPoligonal, pincel);
                        x0 = y0 = xi = yi = -1;
                        break;
                    case 2://Circulo
                        xi = x;
                        yi = y;
                        radio = Math.sqrt(Math.pow((x0 - xi), 2) + Math.pow((y0 - yi), 2));
                        lienzoFondo.drawCircle(x0, y0, (float) radio, pincel);
                        break;
                    case 3://Rectangulo
                        xi = x;
                        yi = y;
                        lienzoFondo.drawRect(x0, y0, xi, yi, pincel);
                        break;
                    case 4://Borrar
                        xi = x;
                        yi = y;
                        lienzoFondo.drawPath(rectaPoligonal, pincel);
                        x0 = y0 = xi = yi = -1;
                        break;
                }
                invalidate();
                break;
        }

        return true;
    }

    public void color() {
        new ColorPicker(this.getContext(), Vista.this, Color.BLACK).show();
    }


    @Override
    public void colorChanged(int color) {
        this.color = color;
    }

    public void setAccion(int accion) {
        this.accion = accion;
    }



    public void setBitmap(Bitmap bm) {
        this.mapaDeBits = bm;
    }

    public Bitmap getBitmap() {
        return mapaDeBits;
    }

    public void setFondo(Canvas lienzoFondo) {
        this.lienzoFondo = lienzoFondo;
    }
}




