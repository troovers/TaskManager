package nl.geekk.taskmanager.model;

import android.app.Application;

import nl.geekk.taskmanager.receivers.ConnectionReceiver;

/**
 * Created by Thomas on 3-7-2016.
 */
public class MyApplication extends Application {
    private static MyApplication myApplication;

    @Override
    public void onCreate() {
        super.onCreate();

        myApplication = this;
    }

    public static synchronized MyApplication getInstance() {
        return myApplication;
    }

    public void setConnectivityListener(ConnectionReceiver.ConnectivityReceiverListener listener) {
        ConnectionReceiver.connectivityReceiverListener = listener;
    }
}
