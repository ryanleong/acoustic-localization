package com.comp90017.teamA.assignment.Graph;


import android.app.Activity;
import android.os.Bundle;


import com.comp90017.teamA.assignment.R;


public class GraphActivity extends Activity
{

	@ Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_graph);

		GraphView gv = (GraphView) findViewById (R.id.graphView);

		// gv.resetGraph ();
		// gv.addLandmarkEdge (GraphView.landmark1, GraphView.landmark2, 20);
		// gv.addLandmarkEdge (GraphView.landmark2, GraphView.landmark3, 20);
		// gv.addLandmarkEdge (GraphView.landmark3, GraphView.landmark1, 20);
		//
		// gv.addEmitterEdge (GraphView.landmark1, 5.682);
		// gv.addEmitterEdge (GraphView.landmark2, 5.682);
		// gv.addEmitterEdge (GraphView.landmark3, 5.682);
		//
		// gv.invalidate ();
	}
}
