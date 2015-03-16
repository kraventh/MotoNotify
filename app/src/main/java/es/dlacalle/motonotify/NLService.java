package es.dlacalle.motonotify;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class NLService extends NotificationListenerService {

    private NLServiceReceiver nlservicereciver;
    @Override
    public void onCreate() {
        super.onCreate();
        nlservicereciver = new NLServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("es.dlacalle.motonotify.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
        registerReceiver(nlservicereciver,filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nlservicereciver);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        Intent i = new  Intent("es.dlacalle.motonotify.NOTIFICATION_LISTENER_EXAMPLE");
        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString("android.title");
        String text="";
        CharSequence[] multiline;
        if(extras.getCharSequence(Notification.EXTRA_TEXT)==null) {
            multiline = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
            //text = (String) extras.getCharSequence("android.textLines");//.toString();
            for(CharSequence line: multiline ){
                text+=line.toString()+"\n";
            }

        }
        else {
            text = extras.getCharSequence(Notification.EXTRA_TEXT).toString();

        }

        //i.putExtra("notification_event","onNotificationPosted :" + sbn.getPackageName() + "\n");
        i.putExtra("notification_event", title + ": " + text);
        sendBroadcast(i);

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Intent i = new  Intent("es.dlacalle.motonotify.NOTIFICATION_LISTENER_EXAMPLE");
        i.putExtra("notification_event","onNotificationRemoved :" + sbn.getPackageName() + "\n");

        sendBroadcast(i);
    }

    class NLServiceReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("command").equals("clearall")){
                NLService.this.cancelAllNotifications();
            }
            else if(intent.getStringExtra("command").equals("list")){
                Intent i1 = new  Intent("es.dlacalle.motonotify.NOTIFICATION_LISTENER_EXAMPLE");
                i1.putExtra("notification_event","========================");
                sendBroadcast(i1);
                int i=1;
                for (StatusBarNotification sbn : NLService.this.getActiveNotifications()) {
                    Intent i2 = new  Intent("es.dlacalle.motonotify.NOTIFICATION_LISTENER_EXAMPLE");
                    //i2.putExtra("notification_event", i + sbn.toString());
                    Bundle extras = sbn.getNotification().extras;
                    String title = extras.getString(Notification.EXTRA_TITLE);
                    String text="";
                    CharSequence[] multiline;
                    if(extras.getCharSequence(Notification.EXTRA_TEXT)==null) {
                        multiline = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
                        //text = (String) extras.getCharSequence("android.textLines");//.toString();
                        for(CharSequence line: multiline ){
                            text+=line.toString()+"\n";
                        }

                    }
                    else {
                        text = extras.getCharSequence(Notification.EXTRA_TEXT).toString();

                    }

                    i2.putExtra("notification_event",i +" - " +
                            title + ": " +
                            text + "");

                    sendBroadcast(i2);
                    i++;
                }
                Intent i3 = new  Intent("es.dlacalle.motonotify.NOTIFICATION_LISTENER_EXAMPLE");
                i3.putExtra("notification_event","===== Notification List ====");
                sendBroadcast(i3);

            }

        }
    }

}