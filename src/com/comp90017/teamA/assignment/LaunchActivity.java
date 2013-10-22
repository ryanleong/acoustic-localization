package com.comp90017.teamA.assignment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.comp90017.teamA.assignment.Emitter.EmitterActivity;
import com.comp90017.teamA.assignment.Graph.GraphActivity;
import com.comp90017.teamA.assignment.Listener.ListenerActivity;


public class LaunchActivity extends Activity implements View.OnClickListener
{
	protected MyParcelable	data;


	@ Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate (savedInstanceState);

		data = new MyParcelable (null);

		setContentView (R.layout.activity_launch);
		Button emitterButton = (Button) findViewById (R.id.emitterButton);
		Button listenerButton = (Button) findViewById (R.id.listenerButton);
		Button graphTestButton = (Button) findViewById (R.id.graphTestButton);

		emitterButton.setOnClickListener (this);
		listenerButton.setOnClickListener (this);
		graphTestButton.setOnClickListener (this);
	}


	@ Override
	public void onClick(View v)
	{
		Intent i = null;
		@ SuppressWarnings ("rawtypes")
		Class c = null;
		switch (v.getId ())
		{
			case R.id.emitterButton :
				c = EmitterActivity.class;
				break;
			case R.id.listenerButton :
				c = ListenerActivity.class;
				break;
			case R.id.graphTestButton :
				c = GraphActivity.class;
				break;
			default :
				break;
		}

		if (c != null)
		{
			i = new Intent (getApplicationContext (), c);
			i.putExtra ("DATA", data);
			startActivityForResult (i, 1);
			// startActivity (new Intent (getApplicationContext (), c));
		}
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