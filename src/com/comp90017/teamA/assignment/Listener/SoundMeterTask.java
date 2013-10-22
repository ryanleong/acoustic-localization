package com.comp90017.teamA.assignment.Listener;

import com.AniFichadia.AniFichadiaToolkitAndroid.Timing.ScheduledTask;
import com.AniFichadia.Toolkit.Utilities.SoundMeter;

public class SoundMeterTask extends ScheduledTask {
	
	private int duration = 1000;
	
	public SoundMeterTask(int duration) {
		
		this.duration = duration;
	}
	
	@Override
	public void doTask(Object... arg0) {
		// TODO Auto-generated method stub
		
		SoundMeter soundMeter = new SoundMeter();
		
		soundMeter.execute(duration);
	}
	
	@Override
	public void undoTask(Object... params) {
		// TODO Auto-generated method stub
		
	}
}
