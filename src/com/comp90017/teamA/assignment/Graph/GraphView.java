package com.comp90017.teamA.assignment.Graph;


import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedMultigraph;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


import com.AniFichadia.Toolkit.Geometry.Coordinate;


public class GraphView extends View
{
	protected DirectedMultigraph<String, DefaultWeightedEdge>	g			= new DirectedMultigraph<String, DefaultWeightedEdge> (
																					DefaultWeightedEdge.class);

	protected Paint												vertexColor	= new Paint ();
	protected Paint												edgeColor	= new Paint ();

	public static final String									v1			= "v1";
	public static final String									v2			= "v2";
	public static final String									v3			= "v3";

	public static final String									t1			= "t1";


	public GraphView (Context context, AttributeSet attr)
	{
		super (context, attr);

		// Setup default graph
		// add the vertices
		g.addVertex (v1);
		g.addVertex (v2);
		g.addVertex (v3);

		g.addVertex (t1);

		// add edges to create a circuit
		DefaultWeightedEdge e1 = new DefaultWeightedEdge ();
		e1.setWeight (10);

		DefaultWeightedEdge e2 = new DefaultWeightedEdge ();
		e2.setWeight (10);

		DefaultWeightedEdge e3 = new DefaultWeightedEdge ();
		e3.setWeight (10);

		g.addEdge (v1, v2, e1);
		g.addEdge (v2, v3, e2);
		g.addEdge (v3, v1, e3);

		DefaultWeightedEdge t1E1 = new DefaultWeightedEdge ();
		t1E1.setWeight (5.682);

		DefaultWeightedEdge t1E2 = new DefaultWeightedEdge ();
		t1E2.setWeight (5.682);

		DefaultWeightedEdge t1E3 = new DefaultWeightedEdge ();
		t1E3.setWeight (5.682);

		g.addEdge (t1, v1, t1E1);
		g.addEdge (t1, v2, t1E2);
		g.addEdge (t1, v3, t1E3);

		vertexColor.setColor (Color.RED);
		edgeColor.setColor (Color.BLUE);
	}


	public void setEdgeWeight(String vertex1, String vertex2, double weight)
	{
		g.getEdge (vertex1, vertex2).setWeight (weight);
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

		// Initial coordinate for V1
		Coordinate v1C = new Coordinate (0, 0);

		// Initial coordinate for V2
		Coordinate v2C = new Coordinate (v1C.getX (), v1C.getY () + g.getEdge (v1, v2).getWeight ());

		DefaultWeightedEdge e1 = g.getEdge (v3, v1);
		DefaultWeightedEdge e2 = g.getEdge (v2, v3);

		// Calculate coordinate for V3
		Coordinate[] cP3 = getP3 (v1C, e1.getWeight (), v2C, e2.getWeight ());
		Coordinate v3C = cP3[0];

		DefaultWeightedEdge t1E1 = g.getEdge (t1, v1);
		DefaultWeightedEdge t1E2 = g.getEdge (t1, v2);
		DefaultWeightedEdge t1E3 = g.getEdge (t1, v3);

		int numEdges = countNulls (t1E1, t1E2, t1E3);

		Coordinate[] t1P3 = getP3 (v1C, t1E1.getWeight (), v2C, t1E2.getWeight ());
		Coordinate t1C;
		
		//if (numEdges == 3)
		//{
			t1C = t1P3[0];
		//}
		//else
		//{
		//	t1C = t1P3[1];
		//}

		canvas.drawLine ((float) v1C.getX () * scale + offsetX, (float) v1C.getY () * scale + offsetY, (float) v2C.getX () * scale + offsetX,
				(float) v2C.getY () * scale + offsetY, edgeColor);
		canvas.drawLine ((float) v1C.getX () * scale + offsetX, (float) v1C.getY () * scale + offsetY, (float) v3C.getX () * scale + offsetX,
				(float) v3C.getY () * scale + offsetY, edgeColor);
		canvas.drawLine ((float) v3C.getX () * scale + offsetX, (float) v3C.getY () * scale + offsetY, (float) v2C.getX () * scale + offsetX,
				(float) v2C.getY () * scale + offsetY, edgeColor);

		canvas.drawCircle ((int) v1C.getX () * scale + offsetX, (int) v1C.getY () * scale + offsetY, vertexSize, vertexColor);
		canvas.drawCircle ((int) v2C.getX () * scale + offsetX, (int) v2C.getY () * scale + offsetY, vertexSize, vertexColor);
		canvas.drawCircle ((int) v3C.getX () * scale + offsetX, (int) v3C.getY () * scale + offsetY, vertexSize, vertexColor);

		canvas.drawCircle ((int) t1C.getX () * scale + offsetX, (int) t1C.getY () * scale + offsetY, vertexSize, vertexColor);
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

		return 0;
	}
}