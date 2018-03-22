package com.mobileappclass.assignment3;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;


import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class MyService extends Service {
    private boolean running;
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int onStartCommand(Intent intent, int flags, int id) {

        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) { //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
                // log it when the location changes
                if (location != null) {
                    Log.i("SuperMap", "Location changed : Lat: "
                            + location.getLatitude() + " Lng: "
                            + location.getLongitude());
                }
            }

            public void onProviderDisabled(String provider) {
                // Provider被disable时触发此函数，比如GPS被关闭
            }

            public void onProviderEnabled(String provider) {
                //  Provider被enable时触发此函数，比如GPS被打开
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Provider的转态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
            }
        };
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);

        final Thread thread = new Thread(new Runnable() {
            public void run() {

                double mylat = 0.0;
                double mylng = 0.0;


                while(running) {
                    SQLiteDatabase db = openOrCreateDatabase("test.db", MODE_PRIVATE, null);
                    db.execSQL("CREATE TABLE IF NOT EXISTS location (id INTEGER PRIMARY KEY AUTOINCREMENT,time VARCHAR(20), lat DOUBLE, long DOUBLE)");
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        //LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

                        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (loc == null) {
                            loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }

                        if (loc != null) {
                            mylat = loc.getLatitude();
                            mylng = loc.getLongitude();
                        }

                        Long tsLong = System.currentTimeMillis();
                        String ts = getDate(tsLong);

                        ContentValues cv = new ContentValues();
                        cv.put("time", ts);
                        cv.put("lat", mylat);
                        cv.put("long", mylng);
                        db.insert("location", null, cv);
                        db.close();

                        String tmp = ts + " " + mylat + " " + mylng;
                        Intent done = new Intent();
                        done.setAction("local_action");
                        done.putExtra("local_broad", tmp);
                        sendBroadcast(done);

                        try {
                            TimeUnit.SECONDS.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }


        });
        running = true;
        thread.start();


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
        //stopSelf();
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        String date = df.format("dd-MM-yyyy hh:mm:ss a", cal).toString();
        return date;
    }

}
