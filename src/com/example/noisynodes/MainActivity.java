package com.example.noisynodes;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView firstText;
	private TextView orientation;
	private SensorManager mSensorManager;
	private SensorEventListener mEventListnerOrient;
	static MediaPlayer mp;
	AssetFileDescriptor afd;
	float azimuth;
    float pitch;
    float roll;

	private void updateUI() {
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				firstText.setText("Azimuth: "+azimuth+"/n Pitch: "+pitch+"/n Roll: "+roll);
				// tilt left
				if (roll > 30) {
					playAudio(getApplicationContext().getResources().openRawResourceFd(R.raw.left));
					orientation.setText("Tilted Left");
				}
				// tile right
				if (roll < -30) {
					playAudio(getApplicationContext().getResources().openRawResourceFd(R.raw.right));
					orientation.setText("Tilted Right");
				}
				// tilt bottom up
				if (pitch > 30) {
					playAudio(getApplicationContext().getResources().openRawResourceFd(R.raw.tbu));
					orientation.setText("Bottom Tilted Upward");
				}
				// tilt bottom down
				if (pitch < -30) {
					playAudio(getApplicationContext().getResources().openRawResourceFd(R.raw.tbd));
					orientation.setText("Bottom Tilted Downward");
				}
				// lay flat
				if (roll < 10 && roll > -10 && pitch < 10 && pitch > -10) {
					playAudio(getApplicationContext().getResources().openRawResourceFd(R.raw.flat));
					orientation.setText("Laying Flat");
				}

			}
		});
	}
	
	synchronized void playAudio(AssetFileDescriptor afd) {
		if (mp.isPlaying()) {
			return;
		}
		mp.reset();
		try {
			mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			mp.prepare();
		}
		catch (Exception e) {
			Log.d("playAudio", "Exception:" + e.getStackTrace()[0].toString() + "afd: " + afd.toString());
		}
		mp.start();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		firstText = (TextView) findViewById(R.id.editText1);
		orientation = (TextView) findViewById(R.id.editText2);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		mEventListnerOrient = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent event) {
				azimuth = event.values[0];
			    pitch = event.values[1];
			    roll = event.values[2];
			    
				updateUI();
			}
			

			@Override
			public void onAccuracyChanged(Sensor arg0, int arg1) {
			}
		};
	}

	@Override
	public void onResume() {
		super.onResume();
		mSensorManager.registerListener(mEventListnerOrient, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
		
		mp = new MediaPlayer();
		afd = getApplicationContext().getResources().openRawResourceFd(R.raw.spin1);
	}

	@Override
	public void onStop() {
		mSensorManager.unregisterListener(mEventListnerOrient);
		super.onStop();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
