package com.omarjaloul.maaalmanbaa;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    static final String PREF = "cycle_settings";
    static final long DEFAULT_START = new GregorianCalendar(2026, Calendar.NOVEMBER, 17, 3, 30).getTimeInMillis();
    static final long DEFAULT_DURATION = 3L*24*60*60*1000 + 6L*60*60*1000 + 30L*60*1000;
    TextView nextDay, nextDate, nextTime, countdown, startText, durationText;
    EditText daysInput, hoursInput, minutesInput;
    long startMillis, durationMillis;
    final SimpleDateFormat dateFmt = new SimpleDateFormat("dd/MM/yyyy", new Locale("ar"));
    final SimpleDateFormat timeFmt = new SimpleDateFormat("hh:mm a", new Locale("ar"));
    final SimpleDateFormat dayFmt = new SimpleDateFormat("EEEE", new Locale("ar"));

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.rgb(6,59,112));
        getWindow().setNavigationBarColor(Color.rgb(6,59,112));
        load(); buildUi(); askNotificationPermission(); refresh();
    }
    void load(){
        android.content.SharedPreferences p=getSharedPreferences(PREF,MODE_PRIVATE);
        startMillis=p.getLong("start",DEFAULT_START); durationMillis=p.getLong("duration",DEFAULT_DURATION);
    }
    int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+0.5f);}
    TextView text(String s,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setGravity(Gravity.CENTER);if(bold)t.setTypeface(null,Typeface.BOLD);return t;}
    LinearLayout column(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);l.setGravity(Gravity.CENTER);return l;}
    LinearLayout.LayoutParams lp(int w,int h){return new LinearLayout.LayoutParams(w<0?w:dp(w),h<0?h:dp(h));}
    void buildUi(){
        ScrollView scroll=new ScrollView(this); scroll.setFillViewport(true);
        LinearLayout root=column(); root.setPadding(dp(20),dp(12),dp(20),dp(18)); root.setBackgroundColor(Color.rgb(241,248,253));
        ImageView logo=new ImageView(this); logo.setImageResource(R.drawable.logo); logo.setAdjustViewBounds(true); logo.setScaleType(ImageView.ScaleType.FIT_CENTER); root.addView(logo,lp(170,145));
        TextView title=text("ماء المنبع",25,Color.rgb(5,61,111),true); root.addView(title,lp(-1,42));
        TextView label=text("الدور القادم",18,Color.rgb(54,83,105),true); root.addView(label,lp(-1,36));
        LinearLayout hero=column(); hero.setPadding(dp(12),dp(18),dp(12),dp(18)); hero.setBackground(round(Color.WHITE,18));
        nextDay=text("—",30,Color.rgb(0,112,184),true); hero.addView(nextDay,lp(-1,52));
        nextDate=text("—",20,Color.DKGRAY,true); hero.addView(nextDate,lp(-1,38));
        nextTime=text("—",25,Color.rgb(5,61,111),true); hero.addView(nextTime,lp(-1,44));
        countdown=text("",15,Color.GRAY,false); hero.addView(countdown,lp(-1,32)); root.addView(hero,lp(-1,-2));
        TextView settings=text("إعدادات الدورة",19,Color.rgb(5,61,111),true); settings.setPadding(0,dp(15),0,dp(8)); root.addView(settings,lp(-1,42));
        startText=text("بداية الدورة: —",16,Color.DKGRAY,true); root.addView(startText,lp(-1,40));
        Button startBtn=new Button(this); startBtn.setText("تغيير تاريخ ووقت البداية"); startBtn.setOnClickListener(v->pickStart()); root.addView(startBtn,lp(-1,52));
        root.addView(text("مدة الدورة (قابلة للتغيير)",16,Color.DKGRAY,true),lp(-1,38));
        LinearLayout fields=new LinearLayout(this); fields.setGravity(Gravity.CENTER); fields.setOrientation(LinearLayout.HORIZONTAL);
        daysInput=field("3"); hoursInput=field("6"); minutesInput=field("30");
        addField(fields,daysInput,"أيام"); addField(fields,hoursInput,"ساعات"); addField(fields,minutesInput,"دقائق"); root.addView(fields,lp(-1,80));
        Button save=new Button(this); save.setText("حفظ مدة الدورة وتحديث الإشعار"); save.setOnClickListener(v->saveDuration()); root.addView(save,lp(-1,54));
        TextView note=text("سيصلك إشعار قبل الدور القادم بـ 20 دقيقة",14,Color.rgb(38,91,126),false); note.setPadding(0,dp(10),0,dp(10)); root.addView(note,lp(-1,42));
        TextView dev=text("تطوير وبرمجة: عمر جلول",13,Color.GRAY,false); root.addView(dev,lp(-1,36));
        scroll.addView(root); setContentView(scroll);
    }
    android.graphics.drawable.GradientDrawable round(int color,int radius){android.graphics.drawable.GradientDrawable d=new android.graphics.drawable.GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));d.setStroke(dp(1),Color.rgb(216,231,241));return d;}
    EditText field(String value){EditText e=new EditText(this);e.setText(value);e.setTextSize(18);e.setInputType(2);e.setGravity(Gravity.CENTER);e.setSingleLine(true);e.setBackground(round(Color.WHITE,10));return e;}
    void addField(LinearLayout parent,EditText e,String label){LinearLayout box=column();box.setPadding(dp(4),0,dp(4),0);box.addView(e,lp(-1,48));box.addView(text(label,12,Color.GRAY,false),lp(-1,25));parent.addView(box,new LinearLayout.LayoutParams(0,dp(78),1));}
    void pickStart(){Calendar c=Calendar.getInstance();c.setTimeInMillis(startMillis);new DatePickerDialog(this,(view,y,m,d)->{Calendar chosen=Calendar.getInstance();chosen.set(y,m,d);Calendar old=Calendar.getInstance();old.setTimeInMillis(startMillis);new TimePickerDialog(this,(tv,h,min)->{chosen.set(Calendar.HOUR_OF_DAY,h);chosen.set(Calendar.MINUTE,min);chosen.set(Calendar.SECOND,0);startMillis=chosen.getTimeInMillis();getSharedPreferences(PREF,0).edit().putLong("start",startMillis).apply();refresh();},old.get(Calendar.HOUR_OF_DAY),old.get(Calendar.MINUTE),false).show();},c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();}
    void saveDuration(){try{long d=Long.parseLong(daysInput.getText().toString().trim());long h=Long.parseLong(hoursInput.getText().toString().trim());long m=Long.parseLong(minutesInput.getText().toString().trim());if(d<0||h<0||h>23||m<0||m>59||(d==0&&h==0&&m==0)){throw new Exception();}durationMillis=d*86400000L+h*3600000L+m*60000L;getSharedPreferences(PREF,0).edit().putLong("duration",durationMillis).apply();refresh();Toast.makeText(this,"تم حفظ مدة الدورة",Toast.LENGTH_SHORT).show();}catch(Exception ex){Toast.makeText(this,"أدخل مدة صحيحة: الساعات 0-23 والدقائق 0-59",Toast.LENGTH_LONG).show();}}
    long nextCycle(){long now=System.currentTimeMillis();if(now<startMillis)return startMillis;if(durationMillis<=0)return startMillis;long n=(now-startMillis)/durationMillis+1;return startMillis+n*durationMillis;}
    void refresh(){long next=nextCycle();nextDay.setText(dayFmt.format(new Date(next)));nextDate.setText(dateFmt.format(new Date(next)));nextTime.setText(timeFmt.format(new Date(next)));long remain=next-System.currentTimeMillis();long totalM=Math.max(0,remain/60000);countdown.setText("متبقٍ: "+(totalM/1440)+" يوم، "+((totalM%1440)/60)+" ساعة، "+(totalM%60)+" دقيقة");startText.setText("بداية الدورة: "+dayFmt.format(new Date(startMillis))+" "+dateFmt.format(new Date(startMillis))+" - "+timeFmt.format(new Date(startMillis)));daysInput.setText(String.valueOf(durationMillis/86400000L));hoursInput.setText(String.valueOf((durationMillis%86400000L)/3600000L));minutesInput.setText(String.valueOf((durationMillis%3600000L)/60000L));schedule(next);}
    void schedule(long next){long trigger=next-20*60*1000L;if(trigger<=System.currentTimeMillis())return;AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);Intent i=new Intent(this,CycleAlarmReceiver.class);PendingIntent pi=PendingIntent.getBroadcast(this,1001,i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);if(Build.VERSION.SDK_INT>=31&&!am.canScheduleExactAlarms()){try{startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));}catch(Exception ignored){}return;}if(Build.VERSION.SDK_INT>=23)am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,trigger,pi);else am.setExact(AlarmManager.RTC_WAKEUP,trigger,pi);}
    @Override protected void onResume(){super.onResume();if(startMillis>0&&durationMillis>0&&Build.VERSION.SDK_INT>=31){AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);if(am.canScheduleExactAlarms())schedule(nextCycle());}}
    void askNotificationPermission(){if(Build.VERSION.SDK_INT>=33&&checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},12);}
}
