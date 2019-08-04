package com.example.user.blue;

import android.Manifest;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
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

public class MyServiceActivity extends Service {

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


    @Override
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

        Intent intent5 = new Intent(this, MainActivity.class);
        intent5.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ///  pendingIntent = PendingIntent.getActivity(this, 0, intent5, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("use", "new ");
            startMyOwnForeground();

        } else {
            Log.d("use", "old");
            startForeground(1, new Notification());

        }
        Log.d("fafa", "WifiTwoWay made");


    }

    private void startMyOwnForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
            String channelName = "My Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.noti)
                    .setContentTitle("Blue app running in the backg")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(2, notification);

        }

    }

    //Get the isntalled apps

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

            String app = appName + "\t" + pacName + "\t" + "\n";


            try {
                File data3 = new File("app_list.txt");
                FileOutputStream fos = openFileOutput("app_list.txt", Context.MODE_APPEND);
                fos.write((app).getBytes());
                fos.close();
//                FileWriter fw =new FileWriter("appname.txt", false);
//                fw.write(app);
//                fw.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    //Getting Time spend On app

    public void aggregationapp() {
        String lastknown = "NULL";
        String appName = "NULL";
        String previous1 = "NULL";
//        UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
//        long time = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        Date systemDate = Calendar.getInstance().getTime();
        String myDate = sdf.format(systemDate);
//        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
//        if (appList != null && appList.size() > 0) {
//            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
//            for (UsageStats usageStats : appList) {
//                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
//            }
//            if (mySortedMap != null && !mySortedMap.isEmpty()) {
//                String dateFormat = String.valueOf(android.text.format.DateFormat.format("dd-MM-yyyy HH:mm:ss", new java.util.Date()));
//                current = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
//
//                //  lastknown = String.valueOf(new Date(mySortedMap.get(mySortedMap.lastKey()).getLastTimeUsed()));
//                //  int index = listPackageName.indexOf(previous);
//                // appName = listAppName.get(index);
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
                    Log.d("tims", "Horton" + myDate);
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
                    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                            // TODO: Consider calling
//                            //    ActivityCompat#requestPermissions
//                            // here to request the missing permissions, and then overriding
//                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                            //                                          int[] grantResults)
//                            // to handle the case where the user grants the permission. See the documentation
//                            // for ActivityCompat#requestPermissions for more details.
//                            return;
//                        }
//
//                        // Added to chcke if the phone is locked vs unlocked//
//
                    String status = "NULL";
                    KeyguardManager myKM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                    if (myKM.inKeyguardRestrictedInputMode()) {
                        status = "locked";
                    } else {
                        status = "unlocked";
                    }
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (!current.equals("NULL")){
                        if (location != null) {
                            double longitude = location.getLongitude();
                            double latitude = location.getLatitude();
                            Log.d("Umm","work"+longitude+"\t"+latitude);
                            String date = String.valueOf(android.text.format.DateFormat.format("dd-MM-yyyy HH:mm:ss", new java.util.Date()));
                            String appt = date + "\t" + latitude + "\t" + longitude + "\t" + previous + "\t" + appName + "\t" + totlaTime + "\t" + status + "\n";
                            try {
                                File data7 = new File("individual.txt");
                                FileOutputStream fos = openFileOutput("individual.txt", Context.MODE_APPEND);
                                fos.write((appt).getBytes());
                                fos.close();
                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
//
                            previousStartTime = startTime;
                        }
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

//            if(statusy==4||statusy==5 && statusy!=8){
//                KeyguardManager myKM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//                if (myKM.inKeyguardRestrictedInputMode()) {
//                    Toast.makeText(getApplicationContext(), "Loacked", Toast.LENGTH_SHORT).show();
//
//                }else {
//                    if (endTime > 60000 && !previous.equals("%launcher%") ) {
//                        Toast.makeText(getApplicationContext(), "STOP!!", Toast.LENGTH_SHORT).show();
//                        Log.d("jet", String.valueOf(totlaTime));
//                    }
//
//                }
//            }


        }
//
//
//            } else {
//                ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
//                List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
//            }
//        }
    }

    //Method for running the aggregation app evey 1s and call it onCreate

//    public void tim(){
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        aggregationapp();
//
//
//                    }
//                });
//            }
//        }, 0, 1000);
//    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
