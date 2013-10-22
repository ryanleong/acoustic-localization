package com.AniFichadia.Toolkit.Utilities;


import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.os.AsyncTask;


public class SendData extends AsyncTask<String, Void, String>
{

	@ Override
	protected String doInBackground(String... params)
	{
		ConfigurationBuilder cb = new ConfigurationBuilder ();
		cb.setDebugEnabled (true).setOAuthConsumerKey ("YjQjJlJZ3Chc6CaDgiR8AA").setOAuthConsumerSecret ("o9q0EiLcyUYl0QO73EleO4yXnUWbsrkWd6cpYv1k")
				.setOAuthAccessToken ("1966090495-rgYWat4q6FFPaJr09Fseku5FhDOZSKKtLDyL0DX")
				.setOAuthAccessTokenSecret ("1Gfxwq18bC69fobmP1okSQsPEx8Z9PZmponw7TgKeE");
		TwitterFactory tf = new TwitterFactory (cb.build ());
		Twitter twitter = tf.getInstance ();

		try
		{

			// Data format
			// ID,Data,Time

			twitter4j.Status status = twitter.updateStatus (params[0]);

			System.out.println ("Successfully updated the status to [" + status.getText () + "].");
		}
		catch (TwitterException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace ();
		}

		return "";
	}
	
	@ Override
	protected void onPostExecute(String result) {
		
	}
}
