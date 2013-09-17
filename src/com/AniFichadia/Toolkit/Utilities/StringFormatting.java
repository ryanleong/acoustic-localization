package com.AniFichadia.Toolkit.Utilities;


/**
 * A collection of various string formatting utility methods
 * 
 * @author Aniruddh Fichadia (Ani.Fichadia@gmail.com)
 */
public class StringFormatting
{
	/**
	 * Formats an long integer time value in format hh:mm:ss
	 * 
	 * @param time Long value to convert into a time string
	 * 
	 * @return A time string in the format hh:mm:ss
	 */
	public static String formatTimeIntAsTime(long time)
	{
		boolean isPositive = (time >= 0);
		if ( !isPositive)
			time *= -1;

		long milliSeconds = time / 1;
		long seconds = time / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		milliSeconds = milliSeconds % 1000;
		seconds = seconds % 60;
		minutes = minutes % 60;
		hours = hours % 24;

		String msTrim = (milliSeconds + "").charAt (0) + "";

		return ((isPositive) ? "" : "-") + hours + ":" + ((minutes < 10) ? "0" + minutes : minutes) + ":"
				+ ((seconds < 10) ? "0" + seconds : seconds) + "." + msTrim;
	}


	/**
	 * Refer to formatTimeIntAsTime(long)
	 * 
	 * Used for integer values instead of longs.
	 */
	public static String formatTimeIntAsTime(int time)
	{
		return formatTimeIntAsTime ((long) time);
	}


	/**
	 * Truncates a double to a specified number of significant places.
	 * 
	 * @param d double to truncate
	 * @param sigPlaces Number of significant places
	 * @return Truncated double, with sigPlaces significant places
	 */
	public static String doubleTruncate(double d, int sigPlaces)
	{
		String s = d + "";

		int trimIndex = s.indexOf (".") + sigPlaces + 1;
		trimIndex = (trimIndex > s.length ()) ? s.length () : trimIndex;

		return s.substring (0, trimIndex);
	}


	/**
	 * Truncates a double to 3 significant places. Refer to doubleTruncate(double, int)
	 * 
	 * @param d double to truncate
	 * @return Truncated double, with 3 sig places
	 */
	public static String doubleTruncate(double d)
	{
		return doubleTruncate (d, 3);
	}

}