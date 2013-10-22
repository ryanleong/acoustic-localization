package com.AniFichadia.Toolkit.Utilities;

/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.IOException;

import android.media.MediaRecorder;
import android.os.AsyncTask;

import com.comp90017.teamA.assignment.Listener.ListenerActivity;


public class SoundMeter extends AsyncTask<Integer, Void, String> {
        static final private double EMA_FILTER = 0.6;

        private MediaRecorder mRecorder = null;
        private double mEMA = 0.0;
        private Boolean keepRunning = true;
        
        private int duration = 1000;

        public void start() {
                if (mRecorder == null) {

                    try {
                        mRecorder = new MediaRecorder();
                        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
	                    mRecorder.setOutputFile("/dev/null"); 
						mRecorder.prepare();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    mRecorder.start();
                    mEMA = 0.0;
                }
        }
        
        public void stop() {
                if (mRecorder != null) {
                        mRecorder.stop();       
                        mRecorder.release();
                        mRecorder = null;
                        keepRunning = false;
                }
        }
        
        public double getAmplitude() {
                if (mRecorder != null) {
                        return  (mRecorder.getMaxAmplitude());
                }
                else
                        return 0;

        }

        public double getAmplitudeEMA() {
                double amp = getAmplitude();
                mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
                return mEMA;
        }

		@Override
		protected String doInBackground(Integer... params) {
			
			if (params.length > 0) {
				duration = params[0];
			}
			
			start();
			long startTime = System.currentTimeMillis();
			
			while(keepRunning) {
				double amp = getAmplitude();
				
				
				if (amp > 0.0) {
					
					double db = 20 * Math.log10(amp/32767.0);
					
					if(ListenerActivity.maxDB < db) {
						ListenerActivity.maxDB = db;
					}
				}
				
				if ((System.currentTimeMillis() - duration > startTime) ) {
					System.out.println(ListenerActivity.maxDB);
					stop();
				}
			}
			return null;
		}
}


