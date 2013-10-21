package com.comp90017.teamA.assignment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.AniFichadia.Toolkit.Utilities.ReceiveData;
import com.AniFichadia.Toolkit.Utilities.SendData;
import com.AniFichadia.Toolkit.Utilities.SoundMeter;
import com.comp90017.teamA.assignment.Emitter.EmitterActivity;
import com.comp90017.teamA.assignment.Graph.GraphActivity;
import com.comp90017.teamA.assignment.Listener.ListenerActivity;


public class LaunchActivity extends Activity implements View.OnClickListener
{
	@ Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_launch);
		Button emitterButton = (Button) findViewById (R.id.emitterButton);
		Button listenerButton = (Button) findViewById (R.id.listenerButton);
		Button settingsButton = (Button) findViewById (R.id.settingsButton);

		emitterButton.setOnClickListener (this);
		listenerButton.setOnClickListener (this);
		settingsButton.setOnClickListener (this);
	}


	@ Override
	public void onClick(View v)
	{
		@ SuppressWarnings ("rawtypes")
		Class c = null;
		switch (v.getId ())
		{
			case R.id.emitterButton :
				// TEMPORARY SEND DATA THROUGH TWITTER
				//new SendData ().execute ("data to send");

				// TEMPORARY RECEIVE DATA THROUGH TWITTER
				//new ReceiveData ().execute ();

				// TEMPORARY: GET AUDIO AMPLITTUDE
//				SoundMeter xMeter = new SoundMeter();
//				xMeter.execute();
//				
//				try {
//					Thread.sleep(2000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				xMeter.stop();
//				
				c = EmitterActivity.class;
				break;
			case R.id.listenerButton :
				c = ListenerActivity.class;
				break;
			case R.id.settingsButton :
				c = GraphActivity.class;
				break;
			default :
				break;
		}

		if (c != null)
			startActivity (new Intent (getApplicationContext (), c));
		else
			Toast.makeText (getApplicationContext (), "Feature not available", Toast.LENGTH_LONG).show ();
	}


	@ Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater ().inflate (R.menu.launch, menu);
		return true;
	}
}