package com.comp90017.teamA.assignment.Graph;


import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedMultigraph;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import com.AniFichadia.Toolkit.Geometry.Coordinate;


public class GraphView extends View
{
	protected DirectedMultigraph<String, DefaultWeightedEdge>	g;

	protected Paint												vertexColor			= new Paint ();
	protected Paint												thisVertexColor		= new Paint ();
	protected Paint												trackingVertexColor	= new Paint ();
	protected Paint												edgeColor			= new Paint ();
	protected Paint												blackColor			= new Paint ();

	public static final String									landmark1			= "l1";
	public static final String									landmark2			= "l2";
	public static final String									landmark3			= "l3";

	public static final String									emitter1			= "e1";

	public boolean												defaultGraph		= true;

	public boolean												emitterSet			= false;


	public GraphView (Context context, AttributeSet attr)
	{
		super (context, attr);

		g = new DirectedMultigraph<String, DefaultWeightedEdge> (DefaultWeightedEdge.class);
		// Setup default graph
		// add the vertices
		g.addVertex (landmark1);
		g.addVertex (landmark2);
		g.addVertex (landmark3);

		g.addVertex (emitter1);

		// add edges to create a circuit
		g.addEdge (landmark1, landmark2, new DefaultWeightedEdge (10));
		g.addEdge (landmark2, landmark3, new DefaultWeightedEdge (10));
		g.addEdge (landmark3, landmark1, new DefaultWeightedEdge (10));

		g.addEdge (emitter1, landmark1, new DefaultWeightedEdge (5.682));
		g.addEdge (emitter1, landmark2, new DefaultWeightedEdge (5.682));
		g.addEdge (emitter1, landmark3, new DefaultWeightedEdge (5.682));
		emitterSet = true;

		vertexColor.setColor (Color.RED);
		thisVertexColor.setColor (Color.GRAY);
		trackingVertexColor.setColor (Color.GREEN);
		edgeColor.setColor (Color.BLUE);
		blackColor.setColor (Color.BLACK);
	}


	@ SuppressLint ("DrawAllocation")
	@ Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw (canvas);

		int offsetX = 100;
		int offsetY = 100;
		int scale = 20;
		int vertexSize = 5;

		try
		{
			if (defaultGraph)
			{
				canvas.drawText ("Default graph, not actual results", 0, 0, blackColor);
			}

			DefaultWeightedEdge l1L2Edge = g.getEdge (landmark1, landmark2);
			l1L2Edge = (l1L2Edge != null ? l1L2Edge : g.getEdge (landmark2, landmark1));

			DefaultWeightedEdge l3L1Edge = g.getEdge (landmark3, landmark1);
			l3L1Edge = (l3L1Edge != null ? l3L1Edge : g.getEdge (landmark1, landmark3));

			DefaultWeightedEdge l2L3Edge = g.getEdge (landmark2, landmark3);
			l2L3Edge = (l2L3Edge != null ? l3L1Edge : g.getEdge (landmark3, landmark2));

			DefaultWeightedEdge emitterLandmark1Edge = g.getEdge (emitter1, landmark1);
			DefaultWeightedEdge emitterLandmark2Edge = g.getEdge (emitter1, landmark2);
			DefaultWeightedEdge emitterLandmark3Edge = g.getEdge (emitter1, landmark3);

			// Initial coordinate for V1. Assume at 0, 0
			Coordinate v1C = new Coordinate (0, 0);

			// Initial coordinate for V2. Assume X-coordinate is the same as v1C and Y-Coordinate is
			// v1.getY() + weight
			Coordinate v2C = new Coordinate (v1C.getX (), v1C.getY () + l1L2Edge.getWeight ());

			// Calculate coordinate for V3
			Coordinate[] cP3 = getP3 (v1C, l3L1Edge.getWeight (), v2C, l2L3Edge.getWeight ());
			Coordinate v3C = cP3[0];

			int numEdges = countNonNulls (emitterLandmark1Edge, emitterLandmark2Edge, emitterLandmark3Edge);

			Coordinate[] emitterP3 = null;

			boolean draw = true;
			boolean enoughInfoForTrack = false;

			if (emitterSet)
			{
				if (numEdges == 3)
				{
					emitterP3 = getP3 (v1C, emitterLandmark1Edge.getWeight (), v2C, emitterLandmark2Edge.getWeight ());
					enoughInfoForTrack = true;
				}
				else if (numEdges == 2)
				{
					if (emitterLandmark1Edge == null)
					{
						emitterP3 = getP3 (v3C, emitterLandmark3Edge.getWeight (), v2C, emitterLandmark2Edge.getWeight ());
					}
					else if (emitterLandmark2Edge == null)
					{
						emitterP3 = getP3 (v1C, emitterLandmark1Edge.getWeight (), v3C, emitterLandmark3Edge.getWeight ());
					}
					else if (emitterLandmark3Edge == null)
					{
						emitterP3 = getP3 (v1C, emitterLandmark1Edge.getWeight (), v2C, emitterLandmark2Edge.getWeight ());
					}
				}
				else
				{
					draw = false;
				}
			}

			if (draw)
			{
				if (l1L2Edge != null)
					canvas.drawLine ((float) v1C.getX () * scale + offsetX, (float) v1C.getY () * scale + offsetY, (float) v2C.getX () * scale
							+ offsetX, (float) v2C.getY () * scale + offsetY, edgeColor);

				if (l3L1Edge != null)
					canvas.drawLine ((float) v1C.getX () * scale + offsetX, (float) v1C.getY () * scale + offsetY, (float) v3C.getX () * scale
							+ offsetX, (float) v3C.getY () * scale + offsetY, edgeColor);

				if (l2L3Edge != null)
					canvas.drawLine ((float) v3C.getX () * scale + offsetX, (float) v3C.getY () * scale + offsetY, (float) v2C.getX () * scale
							+ offsetX, (float) v2C.getY () * scale + offsetY, edgeColor);

				canvas.drawCircle ((int) v1C.getX () * scale + offsetX, (int) v1C.getY () * scale + offsetY, vertexSize, thisVertexColor);
				canvas.drawCircle ((int) v2C.getX () * scale + offsetX, (int) v2C.getY () * scale + offsetY, vertexSize, vertexColor);
				canvas.drawCircle ((int) v3C.getX () * scale + offsetX, (int) v3C.getY () * scale + offsetY, vertexSize, vertexColor);

				if (emitterSet)
				{
					canvas.drawCircle ((int) emitterP3[0].getX () * scale + offsetX, (int) emitterP3[0].getY () * scale + offsetY, vertexSize,
							trackingVertexColor);

					if ( !enoughInfoForTrack)
					{
						canvas.drawCircle ((int) emitterP3[1].getX () * scale + offsetX, (int) emitterP3[1].getY () * scale + offsetY, vertexSize,
								trackingVertexColor);
						canvas.drawText ("Not enough data to draw, multiple possible locations", 0f, 0f, blackColor);
					}

				}
			}
			else
			{
				canvas.drawText ("Not enough data to draw", 0f, 0f, blackColor);
			}
		}
		catch (Exception e)
		{
			canvas.drawText ("Error building graph", 0f, 0f, blackColor);
		}
	}


	@ Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// emitterSet = true;
		// invalidate ();
		return true;
	}


	public void setEdgeWeight(String vertex1, String vertex2, double weight)
	{
		DefaultWeightedEdge edge = g.getEdge (vertex1, vertex2);
		if (edge != null)
		{
			edge.setWeight (weight);
			this.invalidate ();
		}
	}


	public void addLandmarkEdge(String v1, String v2, double weight)
	{
		defaultGraph = false;
		g.addEdge (v1, v2, new DefaultWeightedEdge (weight));
	}


	public void addEmitterEdge(String v1, double weight)
	{
		defaultGraph = false;
		emitterSet = true;
		g.addEdge (emitter1, v1, new DefaultWeightedEdge (weight));
	}


	public void resetGraph()
	{
		defaultGraph = false;
		g = new DirectedMultigraph<String, DefaultWeightedEdge> (DefaultWeightedEdge.class);

		g.addVertex (landmark1);
		g.addVertex (landmark2);
		g.addVertex (landmark3);

		g.addVertex (emitter1);
	}


	/**
	 * http://stackoverflow.com/questions/3665842/as3-java-find-point-of-triangle-by-knowing-other-
	 * two-points-and-segment-leng
	 * 
	 * @param p1
	 * @param distanceFromP1
	 * @param p2
	 * @param distanceFromP2
	 * @return
	 */
	public static Coordinate[] getP3(Coordinate p1, double distanceFromP1, Coordinate p2, double distanceFromP2)
	{
		double d = Coordinate.distanceBetween (p1, p2);

		if (d > (distanceFromP1 + distanceFromP2) || p1.equals (p2) || d < Math.abs (distanceFromP1 - distanceFromP2))
		{
			// there does not exist a 3rd point, or there are an infinite amount of them
			return new Coordinate[] {};
		}

		double a = (distanceFromP1 * distanceFromP1 - distanceFromP2 * distanceFromP2 + d * d) / (2 * d);
		double h = Math.sqrt (distanceFromP1 * distanceFromP1 - a * a);

		Coordinate temp = new Coordinate (p1.getX () + a * (p2.getX () - p1.getX ()) / d, p1.getY () + a * (p2.getY () - p1.getY ()) / d);

		return new Coordinate[] {
				new Coordinate (temp.getX () + h * (p2.getY () - p1.getY ()) / d, temp.getY () - h * (p2.getX () - p1.getX ()) / d),
				new Coordinate (temp.getX () - h * (p2.getY () - p1.getY ()) / d, temp.getY () + h * (p2.getX () - p1.getX ()) / d)};
	}


	public static <T> int countNulls(T... vals)
	{
		int nulls = 0;

		for (T val : vals)
		{
			if (val == null)
				nulls++;
		}

		return nulls;
	}


	public static <T> int countNonNulls(T... vals)
	{
		int nonNulls = vals.length;

		for (T val : vals)
		{
			if (val == null)
				nonNulls--;
		}

		return nonNulls;
	}
}