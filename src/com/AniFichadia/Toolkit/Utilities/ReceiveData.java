package com.AniFichadia.Toolkit.Utilities;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.os.AsyncTask;

public class ReceiveData extends AsyncTask<String, Void, String> {

	@Override
	protected String doInBackground(String... params) {

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("YjQjJlJZ3Chc6CaDgiR8AA")
		  .setOAuthConsumerSecret("o9q0EiLcyUYl0QO73EleO4yXnUWbsrkWd6cpYv1k")
		  .setOAuthAccessToken("1966090495-rgYWat4q6FFPaJr09Fseku5FhDOZSKKtLDyL0DX")
		  .setOAuthAccessTokenSecret("1Gfxwq18bC69fobmP1okSQsPEx8Z9PZmponw7TgKeE");
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		
		String feed = "";
		
		try {

		    
		    Query query = new Query("from:AcousticLocal");
		    QueryResult result = twitter.search(query);
		    
		    for (twitter4j.Status status : result.getTweets()) {

		    	feed += "@" + status.getUser().getScreenName() + ": " + status.getText() + "\n";
		    }
		    
		}
		catch (TwitterException e) {
		  e.printStackTrace();
		}
		System.out.println("Feed: " + feed);
		return feed;
	}
	
    @Override
    protected void onPostExecute(String result) {
    	System.out.println("Received Data: " + result);
    	//MainActivity.twitterFeed.setText(result);
    }
}