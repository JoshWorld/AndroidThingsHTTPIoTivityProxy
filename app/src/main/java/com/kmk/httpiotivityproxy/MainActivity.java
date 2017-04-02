package com.kmk.httpiotivityproxy;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private final Activity mActivity = this;
    private static Context mContext;

    public static Context getMyContext() {
        return MainActivity.mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        MainActivity.mContext = getApplicationContext();
        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(server, "/");
        context.addServlet(new ServletHolder(new ProxyServlet()), "/proxy");
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}