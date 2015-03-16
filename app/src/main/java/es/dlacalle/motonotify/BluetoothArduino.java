/* Codigo original de Aaron Bordin
 * Modificado y adaptado por Pedro de la Calle para trabajar con la MAC en lugar del nombre
 * del dispositivo y realizar la conexión
 * Read more here: http://bytedebugger.wordpress.com/2014/06/27/tutorial-how-to-connect-an-android-device-with-arduino-and-bluetooth/ */

package es.dlacalle.motonotify;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BluetoothArduino extends Thread {
    private BluetoothAdapter AdaptadorBT = null;
    private BluetoothDevice DispositivoBT = null;
    OutputStream StreamSalida;
    InputStream StreamEntrada;
    private boolean DispFound = false;
    private boolean conectado = false;
    private List<String> mMessages = new ArrayList<>();
    private String TAG = "BluetoothConnector";
    private char DELIMITER = '#';

    private static BluetoothArduino __blue = null;

    public static BluetoothArduino getInstance(String a){
        return __blue == null ? new BluetoothArduino(a) : __blue;
    }


    private  BluetoothArduino(String Mac){
        __blue = this;
        try {
            for(int i = 0; i < 2048; i++){
                mMessages.add("");
            }
            AdaptadorBT = BluetoothAdapter.getDefaultAdapter();
            if (AdaptadorBT == null) {
                LogError("\t\t[#]El dispositivo no soporta bluetooth");
                return;
            }
            if (!isBluetoothEnabled()) {
                LogError("[#]Bluetooth inactivo");
            }

            Set<BluetoothDevice> paired = AdaptadorBT.getBondedDevices();
            if (paired.size() > 0) {
                for (BluetoothDevice d : paired) {
                    if (d.getAddress().equals(Mac)) {
                        DispositivoBT = d;
                        DispFound = true;
                        break;
                    }
                }
            }

            if (!DispFound)
                LogError("\t\t[#]No hay ningun dispositivo conocido");

        }catch (Exception e){
            LogError("\t\t[#]Error iniciando bluetooth: " + e.getMessage());
        }

    }

    public boolean isBluetoothEnabled(){
        return AdaptadorBT.isEnabled();
    }

    public boolean Connect(){
        if(!DispFound)
            return false;
        try{
            LogMessage("\t\tConectando al dispositivo...");

            UUID uuid = UUID.fromString("99999999-8877-6610-8000-008000000001");
            BluetoothSocket socketBT = DispositivoBT.createRfcommSocketToServiceRecord(uuid);
            socketBT.connect();
            StreamSalida = socketBT.getOutputStream();
            StreamEntrada = socketBT.getInputStream();
            conectado = true;
            this.start();

            LogMessage("\t\t\t" + AdaptadorBT.getName());
            LogMessage("\t\tConectado!!");
            return true;

        }catch (Exception e){
            LogError("\t\t[#]Error al intentar conectar: " + e.getMessage());
            return false;
        }
    }

    public void run(){

        while (true) {
            if(conectado) {
                try {
                    byte ch, buffer[] = new byte[1024];
                    int i = 0;

                    while((ch=(byte) StreamEntrada.read()) != DELIMITER){
                        buffer[i++] = ch;
                    }
                    buffer[i] = '\0';

                    final String msg = new String(buffer);

                    MessageReceived(msg.trim());
                    LogMessage("[Bluetooth]:" + msg);

                } catch (IOException e) {
                    LogError("->[#]Falló al recibir mensaje: " + e.getMessage());
                }
            }
        }
    }

    private void MessageReceived(String msg){
        try {

            mMessages.add(msg);
            try {
                this.notify();
            }catch (IllegalMonitorStateException e){
                //
            }
        } catch (Exception e){
            LogError("->[#] Error recepción de mensaje: " + e.getMessage());
        }
    }

    public boolean hasMensagem(int i){
        try{
            String s = mMessages.get(i);
            if(s.length() > 0)
                return true;
            else
                return false;
        } catch (Exception e){
            return false;
        }
    }

    public String getMenssage(int i){
        return mMessages.get(i);
    }

    public void clearMessages(){
        mMessages.clear();
    }

    public int countMessages(){
        return mMessages.size();
    }

    public String getLastMessage(){
        if(countMessages() == 0)
            return "";
        return mMessages.get(countMessages()-1);
    }

    public void SendMessage(String msg){
        try {
            if(conectado) {
                StreamSalida.write(msg.getBytes());
            }

        } catch (IOException e){
            LogError("->[#]Error enviando mensaje: " + e.getMessage());
        }
    }

    private void LogMessage(String msg){
        Log.d(TAG, msg);
    }

    private void LogError(String msg){
        Log.e(TAG, msg);
    }

    public void setDelimiter(char d){
        DELIMITER = d;
    }
    public char getDelimiter(){
        return DELIMITER;
    }

}