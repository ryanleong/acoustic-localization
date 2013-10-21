package com.AniFichadia.Toolkit.Geometry;


import java.util.Collection;
import java.util.LinkedList;


import com.AniFichadia.Toolkit.BaseInterfaces.Copyable;


/**
 * Represents a polygon with n-vertices. Polygon is represented by coordinates that form the shape
 * in sequence. The last coordinate will link to the first.
 * For example, a 4-vertex polygon:
 * c1 --> c2
 * ^ |
 * | V
 * c4 <-- c3
 * 
 * @author Aniruddh Fichadia (Ani.Fichadia@gmail.com)
 */
public class Polygon extends Shape implements Copyable
{
	// ============================= Attributes ==============================
	private static final long			serialVersionUID	= 1L;

	/** List of coordinates in polygon. */
	protected LinkedList<Coordinate>	coordinates;

	// The bounds of the polygon
	/** Min x-value of polygon */
	protected double					minX				= 0d;
	/** Max x-value of polygon */
	protected double					maxX				= 0d;
	/** Min y-value of polygon */
	protected double					minY				= 0d;
	/** Max y-value of polygon */
	protected double					maxY				= 0d;


	// ============================ Constructors =============================
	public Polygon ()
	{
		this.coordinates = new LinkedList<Coordinate> ();
	}


	public Polygon (Collection<Coordinate> coordinates)
	{
		this.coordinates = new LinkedList<Coordinate> (coordinates);
	}


	// =============================== Methods ===============================
	/**
	 * Adds a coordinate to the polygon if it isn't already present
	 * 
	 * @param c Coordinate to add
	 * 
	 * @return Success of adding the coordinate
	 */
	public boolean addCoordinate(Coordinate c)
	{
		if (coordinates.contains (c))
			return false;
		else
		{
			coordinates.add (c);
			updateBounds (c);
			return true;
		}
	}


	/**
	 * Removes a coordinate from the polygon
	 * 
	 * @param removeC Coordinate to remove
	 * 
	 * @return Success of removing the coordinate
	 */
	public boolean removeCoordinate(Coordinate removeC)
	{
		if (coordinates.remove (removeC))
		{
			updateBounds ();
			return true;
		}
		else
			return false;
	}


	/**
	 * Gets the n-th coordinate
	 * 
	 * @param n Index of coordinate to get
	 * 
	 * @return Coordinate at index n
	 */
	public Coordinate get(int n)
	{
		return coordinates.get (n);
	}


	/**
	 * Gets the number of vertices in the polygon
	 * 
	 * @return The number of vertices in the polygon
	 */
	public int size()
	{
		return coordinates.size ();
	}


	/**
	 * Resets the bound values to 0
	 */
	protected void resetBounds()
	{
		this.minX = 0d;
		this.maxX = 0d;
		this.minY = 0d;
		this.maxY = 0d;
	}


	/**
	 * Updates the bounds for all coordinates in the polygon
	 */
	protected void updateBounds()
	{
		for (Coordinate c : coordinates)
		{
			updateBounds (c);
		}
	}


	/**
	 * Updates the bounds using the specified coordinate
	 * 
	 * @param c Coordinate to update the bounds with
	 */
	protected void updateBounds(Coordinate c)
	{
		if (c == null)
			return;

		if (c.getX () > this.maxX)
			this.maxX = c.getX ();

		if (c.getX () < this.minX)
			this.minX = c.getX ();

		if (c.getY () > this.maxY)
			this.maxY = c.getY ();

		if (c.getY () < this.minY)
			this.minY = c.getY ();
	}


	// ============================= Implemented =============================
	@ Override
	public void rotate(double angleDegrees)
	{
		rotate (angleDegrees, 0d, 0d);
	}


	@ Override
	public void rotate(double angleDegrees, double rotatePointX, double rotatePointY)
	{
		for (Coordinate c : coordinates)
		{
			c.rotate (angleDegrees, rotatePointX, rotatePointY);
		}

		resetBounds ();
		updateBounds ();
	}


	@ Override
	public Polygon getCopy()
	{
		Polygon copy = new Polygon ();

		for (Coordinate c : coordinates)
		{
			copy.addCoordinate (c.getCopy ());
		}

		return copy;
	}
}