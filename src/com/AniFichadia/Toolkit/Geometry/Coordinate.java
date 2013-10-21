package com.AniFichadia.Toolkit.Geometry;


import com.AniFichadia.Toolkit.BaseInterfaces.Copyable;


/**
 * Represents an x, y coordinate pair. Coordinates stored using double
 * 
 * @author Aniruddh Fichadia (Ani.Fichadia@gmail.com)
 */
public class Coordinate extends Shape implements Copyable
{
	// ============================= Attributes ==============================
	private static final long	serialVersionUID		= 1L;
	private double				x						= 0d;
	private double				y						= 0d;

	public final static String	STRING_REPRESENTATION	= "%xCoord%, %yCoord%";


	// ============================ Constructors =============================
	public Coordinate ()
	{}


	public Coordinate (double x, double y)
	{
		this.x = x;
		this.y = y;
	}


	// =============================== Methods ===============================
	/**
	 * Calculates the Euclidean distance between two coordinates
	 * 
	 * @param c1 First coordinate
	 * @param c2 Second coordinate
	 * 
	 * @return Euclidean distance between the coordinates
	 */
	public static double distanceBetween(Coordinate c1, Coordinate c2)
	{
		return Math.sqrt (Math.pow (c2.getX () - c1.getX (), 2) + Math.pow (c2.getY () - c1.getY (), 2));
	}


	// ============================= Implemented =============================
	@ Override
	public void rotate(double angle)
	{
		rotate (angle, 0d, 0d);
	}


	@ Override
	public void rotate(double angle, double rotatePointX, double rotatePointY)
	{
		if (this.equals (new Coordinate (rotatePointX, rotatePointY)))
			return;

		double theta = Math.toRadians (angle);
		double cos = Math.cos (theta);
		double sin = Math.sin (theta);

		double newX = rotatePointX + (getX () - rotatePointX) * cos - (getY () - rotatePointY) * sin;
		double newY = rotatePointY + (getX () - rotatePointX) * sin + (getY () - rotatePointY) * cos;

		this.setCoordinates (newX, newY);
	}


	@ Override
	public Coordinate getCopy()
	{
		return new Coordinate (this.x, this.y);
	}


	// ========================== Getters & Setters ==========================
	/**
	 * Adds specified values to the coordinates
	 * 
	 * @param addX Value to add to the x-coordinate
	 * @param addY Value to add to the y-coordinate
	 */
	public void addToCoordinates(double addX, double addY)
	{
		this.x += addX;
		this.y += addY;
	}


	/**
	 * Set both x- and y- coordinates
	 * 
	 * @param x New x-coordinate
	 * @param y New y-coordinate
	 */
	public void setCoordinates(double x, double y)
	{
		this.setX (x);
		this.setY (y);
	}


	public double getX()
	{
		return x;
	}


	public void setX(double x)
	{
		this.x = x;
	}


	public double getY()
	{
		return y;
	}


	public void setY(double y)
	{
		this.y = y;
	}


	// ============================== Inherited ==============================
	public boolean equals(Coordinate another)
	{
		if (this.x == another.x && this.y == another.y)
			return true;
		else
			return false;
	}


	// ================================ String ===============================
	/**
	 * Generates a string in format: x,y
	 */
	@ Override
	public String toString()
	{
		return this.x + "," + this.y;
	}


	/**
	 * Creates a coordinate from a string. Note, must be in the same format as toString()
	 * 
	 * @param fStr String to extract coordinate from.
	 * 
	 * @return Coordinate from string, or null on exception
	 */
	public static Coordinate fromString(String fStr)
	{
		String[] fStrSplit = fStr.split (",");
		double cX = 0;
		double cY = 0;

		try
		{
			cX = Double.parseDouble (fStrSplit[0]);
			cY = Double.parseDouble (fStrSplit[1]);
		}
		catch (NumberFormatException e)
		{
			return null;
		}

		return new Coordinate (cX, cY);
	}
}