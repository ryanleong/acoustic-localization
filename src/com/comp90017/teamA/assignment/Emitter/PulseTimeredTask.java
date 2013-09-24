package com.comp90017.teamA.assignment.Emitter;


import com.AniFichadia.AniFichadiaToolkitAndroid.Timing.ScheduledTask;


public class PulseTimeredTask extends ScheduledTask
{
	private static final long	serialVersionUID	= 1L;

	private byte				generatedSnd[];
	private int					sampleRate;


	public PulseTimeredTask (byte[] generatedSnd, int sampleRate)
	{
		super ();
		this.generatedSnd = generatedSnd;
		this.sampleRate = sampleRate;
	}


	@ Override
	public void doTask(Object... params)
	{
		// Catch exceptions just in case. IllegalStateException occurs sometimes
		try
		{
			ToneGenerator.playSound (generatedSnd, sampleRate);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	@ Override
	public void undoTask(Object... params)
	{
		throw new UnsupportedOperationException ();
	}
}