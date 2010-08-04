package com.cyanogenmod.cmparts.activities;

import com.cyanogenmod.cmparts.R;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.io.*;
import android.widget.TextView;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.widget.Chronometer;

public class BatteryTester extends Activity implements OnClickListener {
	private static int currentBattery;
	private static boolean hasStarted;
	private Calendar time;
	private Chronometer tickTimer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
    		super.onCreate(savedInstanceState);
		setContentView(R.layout.battery_tester);
		Button startButton = (Button)findViewById(R.id.widget29);
		startButton.setOnClickListener(this);
		registerReceiver( batteryInfo, new IntentFilter(Intent.ACTION_BATTERY_CHANGED) );
		tickTimer = (Chronometer)findViewById(R.id.chronometer);
	try{
    		// Open the file that is the first 
   	 	// command line parameter
		FileInputStream fstream = openFileInput("start_time");
   		// Get the object of DataInputStream
		DataInputStream in = new DataInputStream(fstream);
       		BufferedReader br = new BufferedReader(new InputStreamReader(in));
    		String strLine;
    		//Read File Line By Line
		TextView percentStarted = (TextView)findViewById(R.id.widget32);
		hasStarted = Integer.parseInt(br.readLine()) == 1 ? true : false;
		tickTimer.setBase(Integer.parseInt(br.readLine()));
		percentStarted.setText(""+br.readLine());
   		in.close();
		}catch (Exception e){//Catch exception if any
      			//System.err.println("Error: " + e.getMessage());
    		}
                if(hasStarted) {
                        tickTimer.start();
                }


  	}

	public void onClick(View v) {
		if(!hasStarted) {
			try {
				FileOutputStream fos = openFileOutput("start_time", MODE_PRIVATE);
				String time = "1\n" + System.currentTimeMillis() + "\n" + currentBattery;
				fos.write(time.getBytes());
				fos.close();
			} catch (Exception e) {
			}
			tickTimer.start();
			Button button = (Button)findViewById(R.id.widget29);
			button.setText("Stop Test");
			TextView percentStarted = (TextView)findViewById(R.id.widget32);
			percentStarted.setText(""+currentBattery);
			hasStarted = true;
		} else { //We end it!
                        try {
                                FileOutputStream fos = openFileOutput("end_time", MODE_PRIVATE);
                                String time = "0\n" + System.currentTimeMillis() + "\n" + currentBattery;
                                fos.write(time.getBytes());
                                fos.close();
                        } catch (Exception e) {
                        }
			tickTimer.stop();
			Button button = (Button)findViewById(R.id.widget29);
                        button.setText("Start Test");
                        TextView percentEnded = (TextView)findViewById(R.id.widget44);
                        percentEnded.setText(""+currentBattery);
                        hasStarted = false;
		}
	}

	public static void setPercent(int percent) {
		currentBattery = percent;
		return;
	}

        private static BroadcastReceiver batteryInfo = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                        String action = intent.getAction();

                        if(Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                                int reading = intent.getIntExtra("level", 0);
                                int percentage = intent.getIntExtra("scale", 100);
                                int percent = reading*100/percentage;
                                setPercent(percent);
                        }
                }
        };

}
