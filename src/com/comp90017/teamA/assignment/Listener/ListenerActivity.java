package com.comp90017.teamA.assignment.Listener;


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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.AniFichadia.AniFichadiaToolkitAndroid.Timing.ScheduledTask;
import com.AniFichadia.AniFichadiaToolkitAndroid.Timing.TaskedTimer;
import com.comp90017.teamA.assignment.MyParcelable;
import com.comp90017.teamA.assignment.R;
import com.comp90017.teamA.assignment.Emitter.PulseTimeredTask;
import com.comp90017.teamA.assignment.Emitter.ToneGenerator;
import com.comp90017.teamA.assignment.Graph.GraphView;


public class ListenerActivity extends Activity implements SensorEventListener, OnItemSelectedListener
{
	private float			mLastX, mLastY, mLastZ;
	private boolean			mInitialized;
	private SensorManager	mSensorManager;
	private Sensor			mAccelerometer;
	private final float		NOISE		= 1f;

	private int				listenerID	= 1;

	private double			duration	= 1.0;		// seconds
	private final int		sampleRate	= 44100;
	private double			freq		= 1000;	// hz

	private byte			generatedSnd[];

	private GraphView		gv;


	/** Called when the activity is first created. */
	@ Override
	public void onCreate(Bundle savedInstanceState)
	{
		Intent intent = getIntent();
		MyParcelable data = intent.getParcelableExtra("DATA");
		
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_listener);
		mInitialized = false;
		mSensorManager = (SensorManager) getSystemService (Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor (Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener (this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

		Spinner listenerIDSpinner = (Spinner) findViewById (R.id.listener_id_spinner);
		listenerIDSpinner.setOnItemSelectedListener (this);

		gv = (GraphView) findViewById (R.id.testGraphView);
	}


	public void setupListeners()
	{
		// TODO Start listener thread or whatever to capture other listeners pulses
		generateTone ();
		long scheduledEmit = listenerID * 3 * 1000;
		final TaskedTimer t = new TaskedTimer (null);

		// TODO schedule task to stop listener before emitting?

		// Schedule sound to occur
		t.addTask (scheduledEmit, new PulseTimeredTask (generatedSnd, sampleRate));
		// Schedule Timer termination
		t.addTask (scheduledEmit, new ScheduledTask () {
			private static final long	serialVersionUID	= 1L;


			@ Override
			public void doTask(Object... params)
			{
				t.stopTimer ();
				// TODO: restart listener?
			}


			@ Override
			public void undoTask(Object... params)
			{}
		});
	}


	public void trackEmitter()
	{
		// start up listener thread.
		// when audio peaks levels hit the apex of its amplitude, save/store/relay amplitude and
		// calculate distance.
		// emit distance
		// Collect distances
		// build graph

		// gv.addEmitterEdge (GraphView.landmark1, myDistance);
		// gv.addEmitterEdge (GraphView.landmark2, otherDistance1);
		// gv.addEmitterEdge (GraphView.landmark2, otherDistance2);
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