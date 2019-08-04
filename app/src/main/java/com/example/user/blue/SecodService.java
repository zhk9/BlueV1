package com.example.user.blue;

import android.Manifest;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.rvalerio.fgchecker.AppChecker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SecodService extends Service {
    private AppChecker appChecker;
    private List<String> listPackageName = new ArrayList<>();
    private List<String> listAppName = new ArrayList<>();
    private Timer timer = new Timer();
    private android.os.Handler handler = new android.os.Handler();

    String current = "NULL";
    String previous = "NULL";
    String timeleft = "NULL";

    long startTime = 0;
    long previousStartTime = 0;
    long endTime = 0;
    long totlaTime = 0;

    long scrrenoff=0;
    private long screenOnTime;
    private final long TIME_ERROR = 1000;
    long screentime=0;

    public void onCreate() {
        super.onCreate();
        installedapp();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        aggregationapp();


                    }
                });
            }
        }, 0, 1000);


        }

    public void installedapp() {
        List<PackageInfo> packageList = getPackageManager().getInstalledPackages(0);
        //  List<ApplicationInfo> applications = getPackageManager().getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        // Log.d("pkg inofo->", appInfo.packageName);
        for (int i = 0; i < packageList.size(); i++) {
            PackageInfo packageInfo = packageList.get(i);

            String appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
            String pacName = packageInfo.packageName;

            listAppName.add(appName);
            listPackageName.add(pacName);


            Log.e("APPNAME", "app is " + appName + "----" + pacName + "\n");

            }

    }

    public void aggregationapp() {
        String lastknown = "NULL";
        String appName = "NULL";

        AppChecker appChecker = new AppChecker();
        current = appChecker.getForegroundApp(getBaseContext());
        //  screentime=System.currentTimeMillis();

        java.text.DateFormat df = new java.text.SimpleDateFormat("hh:mm:ss");
        {
            if (current != null) {
                screentime=System.currentTimeMillis();
                if(screentime!=0 && startTime!=screenOnTime){
                    endTime=screentime-startTime;
                    Log.d("Glass", "TISS " + current + " App time" + totlaTime + "\t" + endTime + "\t" + startTime + "\t" + screentime );
                }
                if (!current.equals(previous)) {
                    Log.d("panda", "zebra" + previous);
                    Log.d("side", "dish" + current);
                //    Log.d("tims", "Horton" + myDate);
//
//
                    //  previous = appChecker.getForegroundApp(getBaseContext());
                    startTime = System.currentTimeMillis();


//
                    int index = listPackageName.indexOf(previous);
                    if (index < 0) {
                        appName = "Null";
                    } else {
                        appName = listAppName.get(index);
                    }


                    if (startTime != previousStartTime && previousStartTime != 0) {
                        totlaTime = 0;

                        totlaTime = startTime - previousStartTime;

                        //  totlaTime=previousStartTime-startTime;
//
                    }
//                    endTime=screentime-previousStartTime;
//
//                    Log.d("fuss", String.valueOf(endTime));


//
                    // long startTime=0;
//                        previous = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
//
////
//                       if (startTime != previousStartTime) {
//                           totlaTime = startTime - previousStartTime;
//                         //  totlaTime=previousStartTime-startTime;
////
//                       }
////
                    Log.d("FinalZ2", "app name " + previous + " App time" + totlaTime + "\t" + previousStartTime + "\t" + startTime );
//

                    if (!current.equals("NULL")){
//
                            previousStartTime = startTime;

                    }} else if (current.equals(previous)) {
//
//
//                        //endTime = startTime;
//
//                        lastknown = String.valueOf(new Date(mySortedMap.get(mySortedMap.lastKey()).getLastTimeUsed()));
                    Log.d("Birds", "crow" + lastknown);
                }
                previous = current;

                Log.d("zoo", "animals" + previous);
//
//
            }

            if (endTime > 60000 && !previous.equals("%launcher%") ) {
                        Toast.makeText(getApplicationContext(), "STOP!!", Toast.LENGTH_SHORT).show();
                        Log.d("jet", String.valueOf(totlaTime));
                    }
        }
//
//
//
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
