package com.comp90017.teamA.assignment.Emitter;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


import com.AniFichadia.AniFichadiaToolkitAndroid.Timing.ScheduledTask;
import com.AniFichadia.AniFichadiaToolkitAndroid.Timing.TaskedTimer;
import com.comp90017.teamA.assignment.R;


public class EmitterActivity extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener
{
	// originally from http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.html
	// and modified by Steve Pomeroy <steve@staticfree.info>
	private double				duration			= 1.0;				// seconds
	private final int			sampleRate			= 44100;
	private double				freq				= 1000;			// hz

	private byte				generatedSnd[];

	private Handler				handler				= new Handler ();

	private final static int	FREQ_SEEK_BAR_SCALE	= 100;
	private final static double	DUR_SEEK_BAR_SCALE	= 0.5;

	private TextView			freqTV;
	private SeekBar				freqSeekBar;

	private TextView			durTV;
	private SeekBar				durSeekBar;

	private TextView			timerTV;

	private Button				playButton;
	private Button				playPulseButton;


	@ Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_emitter);

		freqTV = (TextView) findViewById (R.id.freqTV);
		freqSeekBar = (SeekBar) findViewById (R.id.freqSeekBar);
		Button freqLessButton = (Button) findViewById (R.id.freqLessButton);
		Button freqMoreButton = (Button) findViewById (R.id.freqMoreButton);

		durTV = (TextView) findViewById (R.id.durTV);
		durSeekBar = (SeekBar) findViewById (R.id.durSeekBar);
		Button durLessButton = (Button) findViewById (R.id.durLessButton);
		Button durMoreButton = (Button) findViewById (R.id.durMoreButton);

		playButton = (Button) findViewById (R.id.playButton);
		playPulseButton = (Button) findViewById (R.id.playPulseButton);

		timerTV = (TextView) findViewById (R.id.timerTV);

		freq = freqSeekBar.getProgress () * FREQ_SEEK_BAR_SCALE;
		freqTV.setText (freq + "");

		duration = durSeekBar.getProgress () * DUR_SEEK_BAR_SCALE;
		durTV.setText (duration + "");

		freqSeekBar.setOnSeekBarChangeListener (this);
		freqLessButton.setOnClickListener (this);
		freqMoreButton.setOnClickListener (this);

		durSeekBar.setOnSeekBarChangeListener (this);
		durLessButton.setOnClickListener (this);
		durMoreButton.setOnClickListener (this);

		playButton.setOnClickListener (this);
		playPulseButton.setOnClickListener (this);

		timerTV.setText ("0.0");
	}


	@ Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater ().inflate (R.menu.main, menu);
		return true;
	}


	@ Override
	public void onClick(View v)
	{
		switch (v.getId ())
		{
			case R.id.playButton :
				play ();
				break;
			case R.id.freqLessButton :
				freq -= FREQ_SEEK_BAR_SCALE;

				if (freq < 0)
					freq = 0;

				freqTV.setText (freq + "");
				freqSeekBar.setProgress ((int) (freq / FREQ_SEEK_BAR_SCALE));
				break;
			case R.id.freqMoreButton :
				freq += FREQ_SEEK_BAR_SCALE;

				if (freq > freqSeekBar.getMax () * FREQ_SEEK_BAR_SCALE)
					freq = freqSeekBar.getMax () * FREQ_SEEK_BAR_SCALE;

				freqTV.setText (freq + "");
				freqSeekBar.setProgress ((int) (freq / FREQ_SEEK_BAR_SCALE));
				break;
			case R.id.durLessButton :
				duration -= DUR_SEEK_BAR_SCALE;

				if (duration < 0)
					duration = 0;

				durTV.setText (duration + "");
				durSeekBar.setProgress ((int) (duration / DUR_SEEK_BAR_SCALE));
				break;
			case R.id.durMoreButton :
				duration += DUR_SEEK_BAR_SCALE;

				if (duration > processDurSeekBar (durSeekBar.getMax ()))
					duration = processDurSeekBar (durSeekBar.getMax ());

				durTV.setText (duration + "");
				durSeekBar.setProgress ((int) (duration / DUR_SEEK_BAR_SCALE));
				break;
			case R.id.playPulseButton :
				playPulseButton.setEnabled (false);
				generateTone ();
				final TaskedTimer t = new TaskedTimer (timerTV);

				t.addTask (0, new PulseTimeredTask (generatedSnd, sampleRate));
				t.addPeriodicTask ((long) (duration * 2 * 1000), new PulseTimeredTask (generatedSnd, sampleRate));
				t.addTask (1000 * 5, new ScheduledTask () {
					private static final long	serialVersionUID	= 1L;


					@ Override
					public void doTask(Object... params)
					{
						t.stopTimer ();
						playPulseButton.setEnabled (true);
					}


					@ Override
					public void undoTask(Object... params)
					{}
				});

				t.startTimer ();

				break;
		}
	}


	@ Override
	public void onProgressChanged(SeekBar view, int progress, boolean arg2)
	{
		switch (view.getId ())
		{
			case R.id.freqSeekBar :
				freq = progress * FREQ_SEEK_BAR_SCALE;
				freqTV.setText (freq + "");
				break;
			case R.id.durSeekBar :
				duration = progress * DUR_SEEK_BAR_SCALE;
				durTV.setText (duration + "");
				break;
		}

	}


	@ Override
	public void onStartTrackingTouch(SeekBar arg0)
	{}


	@ Override
	public void onStopTrackingTouch(SeekBar arg0)
	{}


	private static double processDurSeekBar(int progress)
	{
		return progress * DUR_SEEK_BAR_SCALE;
	}


	private void play()
	{
		// Use a new tread as this can take a while
		final Thread thread = new Thread (new Runnable () {
			public void run()
			{
				generateTone ();
				handler.post (new Runnable () {
					public void run()
					{
						playSound ();
					}
				});
			}
		});
		thread.start ();
	}


	private void generateTone()
	{
		Log.d ("asd", "Generating tone using freq: " + freq + "\tdur: " + duration);
		generatedSnd = null;
		generatedSnd = ToneGenerator.generateTone (freq, sampleRate, duration);
	}


	private void playSound()
	{
		ToneGenerator.playSound (generatedSnd, sampleRate);
	}
}