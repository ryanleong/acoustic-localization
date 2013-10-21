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

	public static int											numLandmarks		= 3;

	public static final String									emitter1			= "e1";

	public boolean												defaultGraph		= true;

	public boolean												emitterSet			= false;

	public static double										estimationNoise		= 1;


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

		int offsetX = 125;
		int offsetY = 125;
		int scale = 20;
		int vertexSize = 5;

		try
		{
			if (defaultGraph)
			{
				canvas.drawText ("Default graph, not actual results", 10f, 10f, blackColor);
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
			Coordinate landmark1Coord = new Coordinate (0, 0);

			// Initial coordinate for V2. Assume X-coordinate is the same as v1C and Y-Coordinate is
			// v1.getY() + weight
			Coordinate landmark2Coord = new Coordinate (landmark1Coord.getX (), landmark1Coord.getY () + l1L2Edge.getWeight ());

			// Calculate coordinate for V3
			Coordinate[] landmark3PossibleCoord = getP3 (landmark1Coord, l3L1Edge.getWeight (), landmark2Coord, l2L3Edge.getWeight ());
			Coordinate landmark3Coord = landmark3PossibleCoord[0];

			int numEmitterEdges = countNonNulls (emitterLandmark1Edge, emitterLandmark2Edge, emitterLandmark3Edge);

			Coordinate[] emitterPossibles = null;

			boolean emitterValid = true;

			if (emitterSet)
			{
				if (numEmitterEdges == numLandmarks)
				{
					emitterPossibles = getP3 (landmark1Coord, emitterLandmark1Edge.getWeight (), landmark2Coord, emitterLandmark2Edge.getWeight ());
				}
				else if (numEmitterEdges == numLandmarks - 1)
				{
					DefaultWeightedEdge usedELEdge1 = null;
					Coordinate usedELCoord1 = null;

					DefaultWeightedEdge usedELEdge2 = null;
					Coordinate usedELCoord2 = null;

					// DefaultWeightedEdge unusedELEdge = null;
					// Coordinate unusedELCoord = null;

					if (emitterLandmark1Edge == null)
					{
						usedELEdge1 = emitterLandmark3Edge;
						usedELCoord1 = landmark3Coord;

						usedELEdge2 = emitterLandmark2Edge;
						usedELCoord2 = landmark2Coord;

						// unusedELEdge = emitterLandmark1Edge;
						// unusedELCoord = landmark1Coord;
					}
					else if (emitterLandmark2Edge == null)
					{
						usedELEdge1 = emitterLandmark1Edge;
						usedELCoord1 = landmark1Coord;

						usedELEdge2 = emitterLandmark3Edge;
						usedELCoord2 = landmark3Coord;

						// unusedELEdge = emitterLandmark2Edge;
						// unusedELCoord = landmark2Coord;
					}
					else if (emitterLandmark3Edge == null)
					{
						usedELEdge1 = emitterLandmark1Edge;
						usedELCoord1 = landmark1Coord;

						usedELEdge2 = emitterLandmark2Edge;
						usedELCoord2 = landmark2Coord;

						// unusedELEdge = emitterLandmark3Edge;
						// unusedELCoord = landmark3Coord;
					}

					emitterPossibles = getP3 (usedELCoord1, usedELEdge1.getWeight (), usedELCoord2, usedELEdge2.getWeight ());
				}
				else
				{
					emitterValid = false;
				}
			}

			if (l1L2Edge != null)
				canvas.drawLine ((float) landmark1Coord.getX () * scale + offsetX, (float) landmark1Coord.getY () * scale + offsetY,
						(float) landmark2Coord.getX () * scale + offsetX, (float) landmark2Coord.getY () * scale + offsetY, edgeColor);

			if (l3L1Edge != null)
				canvas.drawLine ((float) landmark1Coord.getX () * scale + offsetX, (float) landmark1Coord.getY () * scale + offsetY,
						(float) landmark3Coord.getX () * scale + offsetX, (float) landmark3Coord.getY () * scale + offsetY, edgeColor);

			if (l2L3Edge != null)
				canvas.drawLine ((float) landmark3Coord.getX () * scale + offsetX, (float) landmark3Coord.getY () * scale + offsetY,
						(float) landmark2Coord.getX () * scale + offsetX, (float) landmark2Coord.getY () * scale + offsetY, edgeColor);

			canvas.drawCircle ((int) landmark1Coord.getX () * scale + offsetX, (int) landmark1Coord.getY () * scale + offsetY, vertexSize,
					thisVertexColor);
			canvas.drawCircle ((int) landmark2Coord.getX () * scale + offsetX, (int) landmark2Coord.getY () * scale + offsetY, vertexSize,
					vertexColor);
			canvas.drawCircle ((int) landmark3Coord.getX () * scale + offsetX, (int) landmark3Coord.getY () * scale + offsetY, vertexSize,
					vertexColor);

			if (emitterSet && emitterValid)
			{
				// If there's enough data, there can still be 2 possible locations since emitter
				// coordinate is based on landmark1 and landmark2. Check that the coordinates
				// are roughly equal to ensure location accuracy
				if (numEmitterEdges == numLandmarks)
				{
					double actualEmitterL3Dist = Math.abs (Coordinate.distanceBetween (landmark3Coord, emitterPossibles[0]));
					double estimateLimitEmitterL3 = emitterLandmark3Edge.getWeight () + estimationNoise;

					if (actualEmitterL3Dist <= estimateLimitEmitterL3) // Coordinate is within
																		// triangle
					{
						canvas.drawCircle ((int) emitterPossibles[0].getX () * scale + offsetX, (int) emitterPossibles[0].getY () * scale + offsetY,
								vertexSize, trackingVertexColor);
					}
					else
					// Coordinate is outside triangle
					{
						canvas.drawCircle ((int) emitterPossibles[1].getX () * scale + offsetX, (int) emitterPossibles[1].getY () * scale + offsetY,
								vertexSize, trackingVertexColor);
					}
				}
				else
				{
					// Draw all (both) possible points
					for (Coordinate c : emitterPossibles)
					{
						canvas.drawCircle ((int) c.getX () * scale + offsetX, (int) c.getY () * scale + offsetY, vertexSize, trackingVertexColor);
					}

					canvas.drawText ("Not enough data to draw, multiple possible locations", 10f, 10f, blackColor);
				}
			}
			else
			{
				canvas.drawText ("Cannot draw emitter. Emitter is not set or has an infinite number of locations.", 10f, 10f, blackColor);
			}
		}
		catch (Exception e)
		{
			canvas.drawText ("Error building graph", 10f, 10f, blackColor);
			e.printStackTrace ();
		}
	}


	@ Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// Just in case the view doesn't get updated for some reason, invalidate (redraw) the view
		// on touches to the screen
		invalidate ();
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
		// Just recreate the graph with the points
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