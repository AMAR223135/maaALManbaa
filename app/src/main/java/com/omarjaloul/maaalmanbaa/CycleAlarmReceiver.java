package com.omarjaloul.maaalmanbaa;
import android.app.*;import android.content.*;import android.os.Build;import java.util.*;
public class CycleAlarmReceiver extends BroadcastReceiver {
 @Override public void onReceive(Context c,Intent i){
  if(Build.VERSION.SDK_INT>=26){NotificationChannel ch=new NotificationChannel("cycle_notice","تنبيهات الدور",NotificationManager.IMPORTANCE_HIGH);((NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(ch);}
  android.content.SharedPreferences p=c.getSharedPreferences(MainActivity.PREF,0);long start=p.getLong("start",MainActivity.DEFAULT_START),dur=p.getLong("duration",MainActivity.DEFAULT_DURATION),now=System.currentTimeMillis();long next=now<start?start:start+((now-start)/dur+1)*dur;
  java.text.SimpleDateFormat day=new java.text.SimpleDateFormat("EEEE",new Locale("ar"));java.text.SimpleDateFormat dt=new java.text.SimpleDateFormat("dd/MM/yyyy - hh:mm a",new Locale("ar"));
  Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(c,"cycle_notice"):new Notification.Builder(c);
  b.setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle("ماء المنبع - تنبيه الدور").setContentText("بقي 20 دقيقة على دورك القادم: "+day.format(new Date(next))).setStyle(new Notification.BigTextStyle().bigText("بقي 20 دقيقة على الدور القادم يوم "+day.format(new Date(next))+" بتاريخ "+dt.format(new Date(next)))).setAutoCancel(true);
  ((NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE)).notify(2001,b.build());
  AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);Intent alarm=new Intent(c,CycleAlarmReceiver.class);PendingIntent pi=PendingIntent.getBroadcast(c,1001,alarm,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);long trigger=next+dur-20*60*1000L;if(Build.VERSION.SDK_INT>=23)am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,trigger,pi);else am.setExact(AlarmManager.RTC_WAKEUP,trigger,pi);
 }
}
