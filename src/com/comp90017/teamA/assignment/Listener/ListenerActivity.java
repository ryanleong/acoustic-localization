package com.comp90017.teamA.assignment.Listener;


import java.util.ArrayList;


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
import com.AniFichadia.Toolkit.Utilities.SoundMeter;
import com.comp90017.teamA.assignment.MyParcelable;
import com.comp90017.teamA.assignment.R;
import com.comp90017.teamA.assignment.Emitter.PulseTimeredTask;
import com.comp90017.teamA.assignment.Emitter.ToneGenerator;
import com.comp90017.teamA.assignment.Graph.GraphView;


public class ListenerActivity extends Activity implements SensorEventListener, OnItemSelectedListener, View.OnClickListener
{
	private float				mLastX, mLastY, mLastZ;
	private boolean				mInitialized;
	private SensorManager		mSensorManager;
	private Sensor				mAccelerometer;
	private final float			NOISE		= 1f;

	private int					listenerID	= 1;

	private double				duration	= 2.0;		// seconds
	private final int			sampleRate	= 44100;
	private double				freq		= 1000;	// hz

	private byte				generatedSnd[];

	private GraphView			gv;

	private double				accelX		= 0;
	private double				accelY		= 0;
	private double				accelZ		= 0;

	public static double		maxDB		= -10000;
	private String				sessionID;
	private int					numOfPulses	= 3;

	// Used for keeping order of setup
	TaskedTimer					t;
	int							step		= 0;
	private ArrayList<Double>	sensorDistances;


	/** Called when the activity is first created. */
	@ Override
	public void onCreate(Bundle savedInstanceState)
	{
		Intent intent = getIntent ();
		MyParcelable data = intent.getParcelableExtra ("DATA");

		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_listener);
		mInitialized = false;
		mSensorManager = (SensorManager) getSystemService (Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor (Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener (this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

		Spinner listenerIDSpinner = (Spinner) findViewById (R.id.listener_id_spinner);
		listenerIDSpinner.setOnItemSelectedListener (this);

		Button trackEmitterButton = (Button) findViewById (R.id.TrackEmitterBtn);
		Button queryButton = (Button) findViewById (R.id.queryBtn);
		Button setupEmitButton = (Button) findViewById (R.id.setupEmitBtn);
		Button setupReceiveButton = (Button) findViewById (R.id.setupQueryBtn);

		trackEmitterButton.setOnClickListener (this);
		queryButton.setOnClickListener (this);
		setupEmitButton.setOnClickListener (this);
		setupReceiveButton.setOnClickListener (this);

		gv = (GraphView) findViewById (R.id.testGraphView);

		// TO be placed in setupListeners()
		sessionID = "grkperjop"; // Math.random() + "";
	}


	public void onClick(View v)
	{

		switch (v.getId ())
		{
			case R.id.setupEmitBtn :
				// Toast.makeText (getApplicationContext (), "Starting setup...",
				// Toast.LENGTH_LONG).show ();
				// setupListeners();
				setupListeners ();
				break;

			case R.id.setupQueryBtn :
				Toast.makeText (getApplicationContext (), "Querying for landmark data.", Toast.LENGTH_LONG).show ();

				if (t == null)
					this.t = new TaskedTimer ();
				
				queueDownloadSetup (0);
				t.startTimer ();
				break;
			case R.id.TrackEmitterBtn :
				Toast.makeText (getApplicationContext (), "Tracking Emitter...", Toast.LENGTH_LONG).show ();
				trackEmitter ();
				break;

			case R.id.queryBtn :
				Toast.makeText (getApplicationContext (), "Querying for emitter data", Toast.LENGTH_LONG).show ();
				query ();
				break;

			default :
				break;
		}
	}


	public boolean isStationary()
	{
		return (accelX == 0 && accelY == 0 && accelZ == 0);
	}


	public void setupListeners()
	{
		t = new TaskedTimer (null);
		sensorDistances = new ArrayList<Double> ();
		generateTone ();

		switch (listenerID)
		{
			case 1 :
				queueEmit (0);
				queueReceive (3);
				queueReceive (6);
				break;

			case 2 :
				queueReceive (0);
				queueEmit (3);
				queueReceive (6);
				break;

			case 3 :
				queueReceive (0);
				queueReceive (3);
				queueEmit (6);
				break;
			default :
				break;
		}

		queueUpload (9);

		queueDownloadSetup (12);

		// Task to stop all
		t.addTask (15 * 1000, new ScheduledTask () {

			@ Override
			public void doTask(Object... arg0)
			{

				Log.d ("Size of", "" + sensorDistances.size ());

				t.stopTimer ();
			}
		});

		// Start all task
		t.startTimer ();
	}


	private void queueReceive(int startTime)
	{

		t.addTask ((long) (startTime * 1000), new ScheduledTask () {

			@ Override
			public void doTask(Object... arg0)
			{
				SoundMeter sm = new SoundMeter () {

					@ Override
					protected void onPostExecute(String result)
					{
						super.onPostExecute (result);

						// Calculate Distance based on Signal Strength
						sensorDistances.add (new DistanceEstimator ().calculateDistance (maxDB, 1000));

						Log.d ("Sensor reading  (DB)", maxDB + "");
						Log.d ("Est. Dist:", sensorDistances.get (0) + "");
						// Toast.makeText(getApplicationContext(), "Est. Dist 1: " +
						// sensorDistances.get(0), Toast.LENGTH_LONG).show();
					}
				};

				sm.execute ((int) (duration * 1000));
			}
		});
	}


	private void queueEmit(int startTime)
	{

		// Schedule sound to occur straight away for 2 seconds
		t.addTask (startTime * 1000, new PulseTimeredTask (generatedSnd, sampleRate));

	}


	private void queueUpload(int startTime)
	{
		t.addTask (startTime * 1000, new ScheduledTask () {

			@ Override
			public void doTask(Object... arg0)
			{
				// TODO Auto-generated method stub

				String dataUploadString = "setup," + listenerID + ",";

				// Data for distance of other 2 landmarks in ascending order
				for (int i = 0; i < sensorDistances.size (); i++)
				{
					dataUploadString += sensorDistances.get (i) + ",";

					if (i > 1)
					{
						break;
					}

				}

				// Add Time and Session ID
				dataUploadString += System.currentTimeMillis () + "," + sessionID;

				Log.d ("Setup Data Send, ID " + listenerID, dataUploadString);
				Toast.makeText (getApplicationContext (), dataUploadString, Toast.LENGTH_LONG).show ();

				// Send data to server
				new SendData ().execute (dataUploadString);
			}
		});

	}


	private void queueDownloadSetup(int startTime)
	{
		if (t == null)
			t = new TaskedTimer (null);
		
		t.addTask (startTime * 1000, new ScheduledTask () {

			@ Override
			public void doTask(Object... arg0)
			{
				// TODO Auto-generated method stub

				final ReceiveData receiveData = new ReceiveData () {

					@ Override
					protected void onPostExecute(String result)
					{
						// TODO Auto-generated method stub
						super.onPostExecute (result);

						double id1l1l2 = 0, id1l1l3 = 0;
						double id2l2l1 = 0, id2l2l3 = 0;
						double id3l3l1 = 0, id3l3l2 = 0;

						// Go through all data
						for (twitter4j.Status status : this.feed.getTweets ())
						{
							String[] statusData = status.getText ().split (",");

							Log.d ("Setup pulled data", status.getText ());

							// Check if data is for setup
							if ( !statusData[0].equals ("setup"))
							{
								continue;
							}

							// Check for current session
							if ( !statusData[statusData.length - 1].equals (sessionID))
							{
								continue;
							}

							int dataID = Integer.parseInt (statusData[1]);

							// Place data in Graph
							if (listenerID == dataID)
							{
								continue;
							}

							switch (dataID)
							{
								case 1 :
									id1l1l2 = Double.parseDouble (statusData[2]);
									id1l1l3 = Double.parseDouble (statusData[3]);

									break;

								case 2 :
									id2l2l1 = Double.parseDouble (statusData[2]);
									id2l2l3 = Double.parseDouble (statusData[3]);

									break;
								case 3 :
									id3l3l1 = Double.parseDouble (statusData[2]);
									id3l3l2 = Double.parseDouble (statusData[3]);

									break;
								default :
									break;
							}
						}

						Log.d ("000", id1l1l2 + " " + id1l1l3 + " " + id2l2l1 + " " + id2l2l3 + id3l3l1 + " " + id3l3l2);

						double l1l2 = 0, l1l3 = 0, l2l3 = 0;

						gv.resetGraph ();
						// Store average distance
						switch (listenerID)
						{
							case 1 :
								l1l2 = average (id2l2l1, sensorDistances.get (0));
								l1l3 = average (id3l3l1, sensorDistances.get (1));
								l2l3 = average (id2l2l3, id3l3l2);

								break;
							case 2 :
								l1l2 = average (id1l1l2, sensorDistances.get (0));
								l2l3 = average (id3l3l2, sensorDistances.get (1));
								l1l3 = average (id3l3l1, id1l1l3);

								break;
							case 3 :
								l1l2 = average (id1l1l2, id2l2l1);
								l2l3 = average (id2l2l3, sensorDistances.get (0));
								l1l3 = average (id1l1l3, sensorDistances.get (1));

								break;
							default :
								break;
						}

						Log.d ("Graphing Distances", 100 * l1l2 + "");
						Log.d ("Graphing Distances", 100 * l2l3 + "");
						Log.d ("Graphing Distances", 100 * l1l3 + "");

						gv.addLandmarkEdge (GraphView.landmark1, GraphView.landmark2, 100 * l1l2);
						gv.addLandmarkEdge (GraphView.landmark2, GraphView.landmark3, 100 * l2l3);
						gv.addLandmarkEdge (GraphView.landmark3, GraphView.landmark1, 100 * l1l3);

						gv.invalidate ();
					}
				};

				receiveData.execute ();
			}
		});

	}


	public static double average(Double... vals)
	{
		double sum = 0;

		for (int i = 0; i < vals.length; i++)
		{
			sum += vals[i];
		}

		return sum / vals.length;
	}


	public void trackEmitter()
	{
		if ( !isStationary ())
		{
			Toast.makeText (getApplicationContext (), "Landmark not stationary. Listening can only occur with stationary landmarks",
					Toast.LENGTH_LONG).show ();
			return;
		}

		// Reset Graph
		// gv.resetGraph();
		maxDB = -10000;
		
		gv.setScale (100);

		// start up listener thread.
		final TaskedTimer tt = new TaskedTimer (null);

		final int listeningDuration = 10 * 1000;

		tt.addTask (0, new SoundMeterTask (listeningDuration));
		tt.startTimer ();

		tt.addTask (listeningDuration, new ScheduledTask () {

			@ Override
			public void doTask(Object... arg0)
			{
				tt.stopTimer ();

				// Calculate Distance based on Signal Strength
				double distance = new DistanceEstimator ().calculateDistance (maxDB, 1000);

				Toast.makeText (getApplicationContext (), "Estimated distance: " + distance, Toast.LENGTH_SHORT).show ();

				// Setup sending data to server
				SendData sendData = new SendData () {

					@ Override
					protected void onPostExecute(String result)
					{

						final ReceiveData receiveData = new ReceiveData () {

							@ Override
							protected void onPostExecute(String result)
							{
								// TODO Auto-generated method stub
								super.onPostExecute (result);

								ArrayList<String> landmarks = new ArrayList<String> ();

								landmarks.add (GraphView.landmark1);
								landmarks.add (GraphView.landmark2);
								landmarks.add (GraphView.landmark3);

								boolean[] isFound = {false, false, false};

								for (twitter4j.Status status : this.feed.getTweets ())
								{

									// // Get Current DateTime
									// Date now = new Date();
									//
									// // Creation DateTime of Data
									// Date createdDate = status.getCreatedAt();
									//
									// // Check that data is at most 3 minutes old
									// if (createdDate.getDate() == now.getDate() &&
									// createdDate.getHours() == now.getHours() &&
									// (now.getMinutes() - createdDate.getMinutes()) > 3) {
									//
									// Log.d("Twitter Feed", "Data created at " +
									// createdDate.toString() +
									// " is too old to be used");
									// continue;
									// }

									String temp2 = status.getText ();
									String[] feedData = temp2.split (",");

									// Check if in this session
									if ( !feedData[4].equals (sessionID))
									{
										continue;
									}

									Log.d ("Received", temp2);

									if (feedData[1].equals ("1"))
									{
										gv.addEmitterEdge (landmarks.get (Integer.parseInt (feedData[1]) - 1), Double.parseDouble (feedData[2]) * 10);
										isFound[0] = true;
									}
									else if (feedData[1].equals ("2"))
									{
										gv.addEmitterEdge (landmarks.get (Integer.parseInt (feedData[1]) - 1), Double.parseDouble (feedData[2]) * 10);
										isFound[1] = true;
									}
									else
									{
										gv.addEmitterEdge (landmarks.get (Integer.parseInt (feedData[1]) - 1), Double.parseDouble (feedData[2]) * 10);
										isFound[2] = true;
									}

									boolean allFound = true;
									for (int i = 0; i < isFound.length; i++)
									{
										if (isFound[i] == false)
										{
											allFound = false;
											break;
										}
									}
									
									if (allFound)
									{
										Toast.makeText (getApplicationContext(), "Found all data", Toast.LENGTH_SHORT).show();
									}
									gv.invalidate ();
								}
							}
						};

						try
						{
							Thread.sleep (5000);
						}
						catch (InterruptedException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace ();
						}

						receiveData.execute ();
					};
				};

				// Send data to server
				sendData.execute ("track," + listenerID + "," + distance + "," + System.currentTimeMillis () + "," + sessionID);

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


	private void query()
	{

		final ReceiveData receiveData = new ReceiveData () {

			@ Override
			protected void onPostExecute(String result)
			{
				// TODO Auto-generated method stub
				super.onPostExecute (result);

				ArrayList<String> landmarks = new ArrayList<String> ();

				landmarks.add (GraphView.landmark1);
				landmarks.add (GraphView.landmark2);
				landmarks.add (GraphView.landmark3);

				boolean[] isFound = {false, false, false};

				for (twitter4j.Status status : this.feed.getTweets ())
				{

					String temp2 = status.getText ();
					String[] feedData = temp2.split (",");

					// Check if in this session
					if ( !feedData[4].equals (sessionID))
					{
						continue;
					}

					Log.d ("Received", temp2);

					if (feedData[1].equals ("1"))
					{
						gv.addEmitterEdge (landmarks.get (Integer.parseInt (feedData[1]) - 1), Double.parseDouble (feedData[2]) * 10);
						isFound[0] = true;
					}
					else if (feedData[1].equals ("2"))
					{
						gv.addEmitterEdge (landmarks.get (Integer.parseInt (feedData[1]) - 1), Double.parseDouble (feedData[2]) * 10);
						isFound[1] = true;
					}
					else
					{
						gv.addEmitterEdge (landmarks.get (Integer.parseInt (feedData[1]) - 1), Double.parseDouble (feedData[2]) * 10);
						isFound[2] = true;
					}

					boolean allFound = true;
					for (int i = 0; i < isFound.length; i++)
					{
						if (isFound[i] == false)
						{
							allFound = false;
							break;
						}
					}
					
					if (allFound)
					{
						Toast.makeText (getApplicationContext(), "Found all data", Toast.LENGTH_SHORT).show();
					}
					gv.invalidate ();
				}

			}
		};

		receiveData.execute ();
	}


	private void generateTone()
	{
		Log.d ("asd", "Generating tone using freq: " + freq + "\tdur: " + duration);
		generatedSnd = null;
		generatedSnd = ToneGenerator.generateTone (freq, sampleRate, duration);
	}


	protected void onResume()
	{
		super.onResume ();
		mSensorManager.registerListener (this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}


	protected void onPause()
	{
		super.onPause ();
		mSensorManager.unregisterListener (this);
	}


	@ Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		// can be safely ignored for this demo
	}


	@ Override
	public void onSensorChanged(SensorEvent event)
	{
		TextView tvX = (TextView) findViewById (R.id.x_axis);
		TextView tvY = (TextView) findViewById (R.id.y_axis);
		TextView tvZ = (TextView) findViewById (R.id.z_axis);
		// ImageView iv = (ImageView) findViewById (R.id.image);
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		if ( !mInitialized)
		{
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			tvX.setText ("0.0");
			tvY.setText ("0.0");
			tvZ.setText ("0.0");
			mInitialized = true;
		}
		else
		{
			float deltaX = Math.abs (mLastX - x);
			float deltaY = Math.abs (mLastY - y);
			float deltaZ = Math.abs (mLastZ - z);
			if (deltaX < NOISE)
				deltaX = (float) 0.0;
			if (deltaY < NOISE)
				deltaY = (float) 0.0;
			if (deltaZ < NOISE)
				deltaZ = (float) 0.0;
			mLastX = x;
			mLastY = y;
			mLastZ = z;

			tvX.setText (Float.toString (deltaX));
			tvY.setText (Float.toString (deltaY));
			tvZ.setText (Float.toString (deltaZ));

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


	@ Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
	{
		listenerID = Integer.parseInt (parent.getItemAtPosition (pos).toString ());
		Toast.makeText (getApplicationContext (), "Selected ID: " + listenerID, Toast.LENGTH_SHORT).show ();
	}


	@ Override
	public void onNothingSelected(AdapterView<?> arg0)
	{}

}