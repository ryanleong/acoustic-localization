package com.comp90017.teamA.assignment.Listener;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.DistanceEstimator;
import com.AniFichadia.AniFichadiaToolkitAndroid.Timing.ScheduledTask;
import com.AniFichadia.AniFichadiaToolkitAndroid.Timing.TaskedTimer;
import com.AniFichadia.Toolkit.Utilities.ReceiveData;
import com.AniFichadia.Toolkit.Utilities.SendData;
import com.comp90017.teamA.assignment.MyParcelable;
import com.comp90017.teamA.assignment.R;
import com.comp90017.teamA.assignment.Emitter.PulseTimeredTask;
import com.comp90017.teamA.assignment.Emitter.ToneGenerator;
import com.comp90017.teamA.assignment.Graph.GraphView;

public class ListenerActivity extends Activity implements SensorEventListener,
		OnItemSelectedListener, View.OnClickListener {
	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private final float NOISE = 1f;

	private int listenerID = 1;

	private double duration = 1.0; // seconds
	private final int sampleRate = 44100;
	private double freq = 1000; // hz

	private byte generatedSnd[];

	private GraphView gv;

	private double accelX = 0;
	private double accelY = 0;
	private double accelZ = 0;

	public static double maxDB = -10000;
	private String sessionID;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		MyParcelable data = intent.getParcelableExtra("DATA");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listener);
		mInitialized = false;
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);

		Spinner listenerIDSpinner = (Spinner) findViewById(R.id.listener_id_spinner);
		listenerIDSpinner.setOnItemSelectedListener(this);
		
		Button trackEmitterButton = (Button) findViewById(R.id.TrackEmitterBtn);
		Button queryButton = (Button) findViewById(R.id.queryBtn);
		trackEmitterButton.setOnClickListener(this);
		queryButton.setOnClickListener(this);

		gv = (GraphView) findViewById(R.id.testGraphView);

		// TO be placed in setupListeners()
		sessionID = "123"; //Math.random() + "";
	}
	
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.TrackEmitterBtn :
				Toast.makeText (getApplicationContext (), "Tracking Emitter...", Toast.LENGTH_LONG).show ();
				trackEmitter();
			break;
		
		case R.id.queryBtn:
			Toast.makeText (getApplicationContext (), "Querying for data", Toast.LENGTH_LONG).show ();
			query();
			break;

		default:
			break;
		}
	}
	
	public boolean isStationary() {
		return (accelX == 0 && accelY == 0 && accelZ == 0);
	}

	public void setupListeners() {
		if (!isStationary()) {
			Toast.makeText(
					getApplicationContext(),
					"Landmark not stationary. Listening can only occur with stationary landmarks",
					Toast.LENGTH_LONG).show();
			return;
		}

		// TODO Start listener thread or whatever to capture other listeners
		// pulses
		generateTone();
		long scheduledEmit = listenerID * 3 * 1000;
		final TaskedTimer t = new TaskedTimer(null);

		// TODO schedule task to stop listener before emitting?

		// Schedule sound to occur
		t.addTask(scheduledEmit, new PulseTimeredTask(generatedSnd, sampleRate));
		// Schedule Timer termination
		t.addTask(scheduledEmit, new ScheduledTask() {
			private static final long serialVersionUID = 1L;

			@Override
			public void doTask(Object... params) {
				t.stopTimer();
				// TODO: restart listener?
			}

			@Override
			public void undoTask(Object... params) {
			}
		});

		t.startTimer();
	}

	public void trackEmitter() {
		if (!isStationary()) {
			Toast.makeText(
					getApplicationContext(),
					"Landmark not stationary. Listening can only occur with stationary landmarks",
					Toast.LENGTH_LONG).show();
			return;
		}
		gv.resetGraph();

		// start up listener thread.
		final TaskedTimer tt = new TaskedTimer(null);
		
		final int listeningDuration = 10 * 1000;

		tt.addTask(0, new SoundMeterTask(listeningDuration));
		tt.startTimer();

		tt.addTask(listeningDuration, new ScheduledTask() {

			@Override
			public void doTask(Object... arg0) {
				tt.stopTimer();

				// Calculate Distance based on Signal Strength
				double distance = new DistanceEstimator().calculateDistance(
						maxDB, 1000);
				
				
				// Setup sending data to server
				SendData sendData = new SendData() {
					
					@Override
					protected void onPostExecute(String result) {
						
						final ReceiveData receiveData = new ReceiveData() {
							
							@Override
							protected void onPostExecute(String result) {
								// TODO Auto-generated method stub
								super.onPostExecute(result);
								int count = 0, max = 2;

								ArrayList<String> landmarks = new ArrayList<String>();
								
								landmarks.add(GraphView.landmark1);
								landmarks.add(GraphView.landmark2);
								landmarks.add(GraphView.landmark3);
								
								boolean[] isFound = {false, false, false};
								
								for (twitter4j.Status status : this.feed.getTweets()) {

									// Get Current DateTime
									Date now = new Date();
									
									// Creation DateTime of Data
									Date createdDate = status.getCreatedAt();

//									// Check that data is at most 3 minutes old
//									if (createdDate.getDate() == now.getDate() &&
//										createdDate.getHours() == now.getHours() &&
//										(now.getMinutes() - createdDate.getMinutes()) > 3) {
//										
//										Log.d("Twitter Feed", "Data created at " + createdDate.toString() + 
//												" is too old to be used");
//										continue;
//									}
									
									String temp2 = status.getText();
									String[] feedData = temp2.split(",");
									
									// Check if in this session
									if (!feedData[3].equals(sessionID)) {
										continue;
									}
									
									Log.d("Received", temp2);
									Log.d("ID", feedData[0]);
									
									if (feedData[0].equals("1")) {
										gv.addEmitterEdge(landmarks.get(Integer.parseInt(feedData[0])-1), 
												Double.parseDouble(feedData[1]) * 10);
										isFound[0] = true;
									}
									else if (feedData[0].equals("2")) {
										gv.addEmitterEdge(landmarks.get(Integer.parseInt(feedData[0])-1), 
												Double.parseDouble(feedData[1]) * 10);
										isFound[1] = true;
									}
									else {
										gv.addEmitterEdge(landmarks.get(Integer.parseInt(feedData[0])-1), 
												Double.parseDouble(feedData[1]) * 10);
										isFound[2] = true;
									}

									boolean allFound = true;
									for (int i = 0; i < isFound.length; i++) {
										if (isFound[i] == false ) {
											allFound = false;
										}
										
										Log.d("Data Found", isFound[i]+ "");
									}
								}
								
								
							}
						};
						
						
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						receiveData.execute();
					};
				};
				
				// Send data to server
				sendData.execute(listenerID + "," + distance + ","
						+ System.currentTimeMillis() + "," + sessionID);
				

			}
		});

		// when audio peaks levels hit the apex of its amplitude,
		// save/store/relay amplitude and
		// calculate distance.
		// emit distance
		// Collect distances
		// build graph


		gv.invalidate ();
	}
	
	private void query() {
		
		final ReceiveData receiveData = new ReceiveData() {
			
			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				int count = 0, max = 2;

				ArrayList<String> landmarks = new ArrayList<String>();
				
				landmarks.add(GraphView.landmark1);
				landmarks.add(GraphView.landmark2);
				landmarks.add(GraphView.landmark3);
				
				boolean[] isFound = {false, false, false};
				
				for (twitter4j.Status status : this.feed.getTweets()) {

					// Get Current DateTime
					Date now = new Date();
					
					// Creation DateTime of Data
					Date createdDate = status.getCreatedAt();

					
					String temp2 = status.getText();
					String[] feedData = temp2.split(",");
					
					// Check if in this session
					if (!feedData[3].equals(sessionID)) {
						continue;
					}
					
					Log.d("Received", temp2);
					Log.d("ID", feedData[0]);
					
					if (feedData[0].equals("1")) {
						gv.addEmitterEdge(landmarks.get(Integer.parseInt(feedData[0])-1), 
								Double.parseDouble(feedData[1]) * 10);
						isFound[0] = true;
					}
					else if (feedData[0].equals("2")) {
						gv.addEmitterEdge(landmarks.get(Integer.parseInt(feedData[0])-1), 
								Double.parseDouble(feedData[1]) * 10);
						isFound[1] = true;
					}
					else {
						gv.addEmitterEdge(landmarks.get(Integer.parseInt(feedData[0])-1), 
								Double.parseDouble(feedData[1]) * 10);
						isFound[2] = true;
					}

					boolean allFound = true;
					for (int i = 0; i < isFound.length; i++) {
						if (isFound[i] == false ) {
							allFound = false;
						}
						
						Log.d("Data Found", isFound[i]+ "");
					}
					
					gv.addLandmarkEdge (GraphView.landmark1, GraphView.landmark2, 2 * 10);
					gv.addLandmarkEdge (GraphView.landmark2, GraphView.landmark3, 2 * 10);
					gv.addLandmarkEdge (GraphView.landmark3, GraphView.landmark1, 2 * 10);


					gv.invalidate ();
				}
				
				
			}
		};
		
		receiveData.execute();
	}

	private void generateTone() {
		Log.d("asd", "Generating tone using freq: " + freq + "\tdur: "
				+ duration);
		generatedSnd = null;
		generatedSnd = ToneGenerator.generateTone(freq, sampleRate, duration);
	}

	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// can be safely ignored for this demo
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		TextView tvX = (TextView) findViewById(R.id.x_axis);
		TextView tvY = (TextView) findViewById(R.id.y_axis);
		TextView tvZ = (TextView) findViewById(R.id.z_axis);
		// ImageView iv = (ImageView) findViewById (R.id.image);
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		if (!mInitialized) {
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			tvX.setText("0.0");
			tvY.setText("0.0");
			tvZ.setText("0.0");
			mInitialized = true;
		} else {
			float deltaX = Math.abs(mLastX - x);
			float deltaY = Math.abs(mLastY - y);
			float deltaZ = Math.abs(mLastZ - z);
			if (deltaX < NOISE)
				deltaX = (float) 0.0;
			if (deltaY < NOISE)
				deltaY = (float) 0.0;
			if (deltaZ < NOISE)
				deltaZ = (float) 0.0;
			mLastX = x;
			mLastY = y;
			mLastZ = z;

			tvX.setText(Float.toString(deltaX));
			tvY.setText(Float.toString(deltaY));
			tvZ.setText(Float.toString(deltaZ));

			accelX = deltaX;
			accelY = deltaY;
			accelZ = deltaZ;

			// iv.setVisibility (View.VISIBLE);
			// if (deltaX > deltaY)
			// {
			// iv.setImageResource (R.drawable.horizontal);
			// }
			// else if (deltaY > deltaX)
			// {
			// iv.setImageResource (R.drawable.vertical);
			// }
			// else
			// {
			// iv.setVisibility (View.INVISIBLE);
			// }
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		listenerID = Integer.parseInt(parent.getItemAtPosition(pos).toString());
		Toast.makeText(getApplicationContext(), "Selected ID: " + listenerID,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

}