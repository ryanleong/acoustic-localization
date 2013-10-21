package com.AniFichadia.Toolkit.Geometry;


import java.io.Serializable;


/**
 * Interface defining geometric objects that can be rotated around a 2-dimensional Euclidean plane
 * 
 * @author Aniruddh Fichadia (Ani.Fichadia@gmail.com)
 */
public interface Rotateable extends Serializable
{
	/**
	 * Rotate geometric object around the origin (0.0, 0.0)
	 * 
	 * Refer to rotate(double, double, double). Optimally, should use call: rotate(angleDegrees,
	 * 0.0, 0.0)
	 * 
	 * @param angleDegrees Angle (in degrees) to rotate the geometric object around the origin
	 */
	public void rotate(double angleDegrees);


	/**
	 * Rotate a geometric object around a specified set of points
	 * 
	 * @param angleDegrees Angle (in degrees) to rotate the geometric object
	 * @param rotatePointX x-coordinate to rotate geometric object around
	 * @param rotatePointY y-coordinate to rotate geometric object around
	 */
	public void rotate(double angleDegrees, double rotatePointX, double rotatePointY);
}