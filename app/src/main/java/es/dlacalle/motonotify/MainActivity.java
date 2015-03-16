package es.dlacalle.motonotify;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends ActionBarActivity

{

    /* Intent Request Codes, son los códigos devueltos cuando llamamos a una actividad  */
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    //private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    private static final int REQUEST_CONFIGURATION = 4;

    private NotificationReceiver nReceiver;
    private ArrayAdapter<String> listaNotifyAdapter;
    private ImageView visor;

    private BluetoothArduino DispBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        visor = (ImageView) findViewById(R.id.Preview);

        ListView listaNotify = (ListView) findViewById(R.id.ListaNotificaciones);
        listaNotifyAdapter = new ArrayAdapter<>(this, R.layout.textview_alone_layout);
        listaNotify.setAdapter(listaNotifyAdapter);

        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("es.dlacalle.motonotify.NOTIFICATION_LISTENER_EXAMPLE");
        registerReceiver(nReceiver,filter);

        // Enciende Bluetooth si está apagado

        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
        getMenuInflater().inflate(R.menu.main, menu);
            return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_bluetooth:
                Intent btIntent = new Intent(this, BtDeviceListActivity.class);
                startActivityForResult(btIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            case R.id.action_settings:
                Intent cfgIntent = new Intent(this, ConfiguracionActivity.class);
                startActivityForResult(cfgIntent, REQUEST_CONFIGURATION);
                return true;
        }

        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {

                    Toast.makeText(this, "Conexión segura con "+ data.getExtras()
                            .getString(BtDeviceListActivity.EXTRA_DEVICE_ADDRESS), Toast.LENGTH_SHORT).show();
                } else Toast.makeText(this, "No ha seleccionado ningún dispositivo", Toast.LENGTH_SHORT).show();
                break;

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    Toast.makeText(this, "Bluetooth ACTIVADO",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(this, "Bluetooth no disponible",
                            Toast.LENGTH_SHORT).show();

                }
            case REQUEST_CONFIGURATION:
                Toast.makeText(this, "Aplicación seleccionada: "+data.getExtras().getString("aplicaciones_marcadas"),
                        Toast.LENGTH_SHORT).show();

        }
    }

    public void buttonClickedMain(View v){

        if(v.getId() == R.id.ServicioOnOff){
            Intent i = new Intent("es.dlacalle.motonotify.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
            i.putExtra("command","list");
            sendBroadcast(i);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nReceiver);
    }

    public Bitmap ParseoTxt2Bmp(String text, float textSize, int textColor) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);

        Bitmap fondo = Bitmap.createBitmap(128, 64, Bitmap.Config.ARGB_8888);
        Bitmap derecha = Bitmap.createBitmap(80, 56, Bitmap.Config.ARGB_8888);
        Bitmap izquierda = Bitmap.createBitmap(34, 34, Bitmap.Config.ARGB_8888);
        Bitmap superior = Bitmap.createBitmap(128, 8, Bitmap.Config.ARGB_8888);

        //Creo la parte derecha
        Canvas canvasDer = new Canvas(derecha);

        /* Solución para poner texto multilinea extraida de StackOverflow,
        * Simple, elegante y funcional
        * http://stackoverflow.com/questions/6756975/draw-multi-line-text-to-canvas
        * */
        TextPaint mTextPaint=new TextPaint();
        StaticLayout mTextLayout = new StaticLayout(text, mTextPaint, canvasDer.getWidth(),
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvasDer.save();

        //Posición del texto
        int textX = 0;
        int textY = 0;

        canvasDer.translate(textX, textY);
        mTextLayout.draw(canvasDer);
        canvasDer.restore();

        //Creo la parte izquierda
        Canvas canvasIzq = new Canvas (izquierda);
        //Pinto la flecha
        fillArrow(canvasIzq, 17, 33, 17, 1, 0.3f, 8);

        //Rotamos la flecha
        Matrix mat = new Matrix();
        int rotacion = 45;
        mat.postRotate(rotacion);

        Bitmap izquierdaGirada = Bitmap.createBitmap(izquierda, 0, 0,
                izquierda.getWidth(), izquierda.getHeight(), mat, true);

        //Creo la parte superior
        Canvas canvasSup = new Canvas (superior);
        DateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String ahora = df.format(Calendar.getInstance().getTime());
        paint.setTextSize(8.0f);
        canvasSup.drawText("                 "+ahora,0,7.0f,paint);

        //Lo pongo junto sobre el fondo
        Canvas canvasFondo = new Canvas(fondo);
        canvasFondo.drawRGB(255,255,255);
        canvasFondo.drawBitmap(derecha, 48, 8, null);
        //if (rotacion==45 || rotacion == 135 || rotacion == 225 || rotacion == 315)
            canvasFondo.drawBitmap(izquierdaGirada, 0, 12, null);
        //else
            //canvasFondo.drawBitmap(izquierdaGirada, 7, 19, null);
        canvasFondo.drawBitmap(superior, 0, 0, null);
        return fondo;
    }

    /**
    * Código de la función extraido de StackOverflow
    * http://stackoverflow.com/questions/11975636/how-to-draw-an-arrow-using-android-graphic-class
    * Añadido:
    *   - Paso por argumentos del tamaño de la punta y el grosor del cuerpo
    *   - Pintado de línea además de la punta
    * */

    private void fillArrow(Canvas canvas, float x0, float y0, float x1, float y1, float frac, float grosor) {

        Paint cabeza = new Paint();
        cabeza.setStyle(Paint.Style.FILL);

        float deltaX = x1 - x0;
        float deltaY = y1 - y0;

        //Vertice izquierdo
        float point_x_1 = x0 + (1 - frac) * deltaX + frac * deltaY;
        float point_y_1 = y0 + (1 - frac) * deltaY - frac * deltaX;

        //Vertice derecho
        float point_x_3 = x0 + (1 - frac) * deltaX - frac * deltaY;
        float point_y_3 = y0 + (1 - frac) * deltaY + frac * deltaX;

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);

        path.moveTo(point_x_1, point_y_1);
        path.lineTo(x1, y1); //Vertice superior
        path.lineTo(point_x_3, point_y_3);
        path.lineTo(point_x_1, point_y_1);
        path.close();

        //Pinto la linea de la flecha
        cabeza.setStrokeWidth(grosor);
        canvas.drawLine(x0, y0, x1, y1+6, cabeza);

        //Pinto la punta de la flecha
        canvas.drawPath(path, cabeza);
    }


    class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            visor.setImageBitmap(ParseoTxt2Bmp(intent.getStringExtra("notification_event"), 12.0f, Color.BLACK));
            listaNotifyAdapter.add(intent.getStringExtra("notification_event"));

        }
    }

}
