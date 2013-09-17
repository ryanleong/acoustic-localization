package com.comp90017.teamA.assignment.Emitter;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;


public class ToneGenerator
{
	public static byte[] generateTone(double frequency, int sampleRate, double duration)
	{
		int numSamples = (int) (duration * sampleRate);

		double[] sample = new double[numSamples];
		byte[] genSound = new byte[4 * numSamples];

		// fill out the array
		for (int i = 0; i < numSamples; ++i)
		{
			sample[i] = Math.sin (2.0 * Math.PI * frequency * i / sampleRate);
			// Math.sin (2.0 * Math.PI * i / (sampleRate / frequency));
		}

		// convert to 16 bit pcm sound array
		// assumes the sample buffer is normalised.
		int idx = 0;
		for (final double dVal : sample)
		{
			// scale to maximum amplitude
			final short val = (short) (dVal * 32767);
			// in 16 bit wav PCM, first byte is the low order byte
			genSound[idx++] = (byte) (val & 0x00ff);
			genSound[idx++] = (byte) ((val & 0xff00) >>> 8);
			genSound[idx++] = (byte) (val & 0x00ff);
			genSound[idx++] = (byte) ((val & 0xff00) >>> 8);
		}

		return genSound;
	}


	public static AudioTrack playSound(byte[] generatedSnd, int sampleRate)
	{
		// final AudioTrack audioTrack = new AudioTrack (AudioManager.STREAM_MUSIC, sampleRate,
		// AudioFormat.CHANNEL_OUT_STEREO,
		// AudioFormat.ENCODING_PCM_16BIT, AudioTrack.getMinBufferSize (sampleRate,
		// AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT), AudioTrack.MODE_STREAM);

		final AudioTrack audioTrack = new AudioTrack (AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_STEREO,
				AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length, AudioTrack.MODE_STREAM);
		audioTrack.write (generatedSnd, 0, generatedSnd.length);
		audioTrack.play ();

		return audioTrack;
	}
}