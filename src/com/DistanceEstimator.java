package com;


import java.util.concurrent.ConcurrentHashMap;


/* Landmark localization using trilateration */
public class DistanceEstimator
{
	private int								numOfSensors;
	private double							numSamples;
	private double							frequency;
	private int								sampleRate;
	private double							duration;
	private double							timeFrame;
	private double							signalEnergy;
	private double							noiseEnergy;
	protected double						dl1l2;
	protected double						dl1l3;
	protected double						dl2l3;
	protected double						r1;
	protected double						r2;
	protected double						r3;
	private ConcurrentHashMap<String, Pair>	relativePos;


	public DistanceEstimator ()
	{
		// this.duration = duration;
		// this.sampleRate = sampleRate;
		// this.frequency = frequency;
		// this.numSamples = (int) (duration * sampleRate);
		// timeFrame = numSamples/frequency;
		// numOfSensors = 3;
	}


	public void initialization()
	{
		relativePos = new ConcurrentHashMap<String, Pair> ();
		// the relative position of the first landmark
		Pair land1 = new Pair (0.0, 0.0);
		relativePos.put ("land1", land1);
		Pair land2 = new Pair (land1.x + dl1l2, land1.y);
		relativePos.put ("land2", land2);

		double i = (2 * dl1l3 * dl1l3 - dl2l3 * dl2l3) / (2 * dl1l2);
		double j = Math.sqrt (dl1l3 * dl1l3 - i * i);
		Pair land3 = new Pair (i, j);
		relativePos.put ("land3", land3);
		trilateration ();
	}


	public void trilateration()
	{
		double x = (r1 * r1 - r2 * r2 + dl1l2 * dl1l2) / (2 * dl1l2);
		double i, j;
		i = relativePos.get ("land3").x;
		j = relativePos.get ("land3").y;
		double y = (r1 * r1 - r3 * r3 + i * i + j * j) / (2 * j) - (i / j) * x;
		Pair source = new Pair (x, y);
		relativePos.put ("source", source);
	}


	/** */
	public double[] lawOfCosine(double a, double b, double c)
	{
		double[] arcs = new double[3];
		double cosC = ( -Math.pow (c, 2) + Math.pow (a, 2) + Math.pow (b, 2)) / (2 * a * b);
		double C = Math.acos (cosC);
		// arc among side a and b
		arcs[2] = C;
		double cosB = ( -Math.pow (b, 2) + Math.pow (a, 2) + Math.pow (c, 2)) / (2 * a * c);
		double B = Math.acos (cosB);
		// arc among side a and c
		arcs[1] = B;
		double cosA = ( -Math.pow (a, 2) + Math.pow (b, 2) + Math.pow (c, 2)) / (2 * b * c);
		double A = Math.acos (cosA);
		// arc among side b and c
		arcs[0] = A;
		return arcs;

	}


	/**
	 * returns the estimated distance based on signal level in Db
	 * 
	 * @param signalLevelInDb
	 * @param freqInMHz
	 * @return
	 */
	public double calculateDistance(double signalLevelInDb, double freqInMHz)
	{
		double exp = (27.55 - (20 * Math.log10 (freqInMHz)) - signalLevelInDb) / 20.0;
		return Math.pow (10.0, exp);
	}


	public void setDl1l2(double sdb, double freq)
	{
		dl1l2 = calculateDistance (sdb, freq);
	}


	public void setDl2l3(double sdb, double freq)
	{
		dl2l3 = calculateDistance (sdb, freq);
	}


	public void setDl1l3(double sdb, double freq)
	{
		dl1l3 = calculateDistance (sdb, freq);
	}


	public void setR1(double sdb, double freq)
	{
		r1 = calculateDistance (sdb, freq);
	}


	public void setR2(double sdb, double freq)
	{
		r2 = calculateDistance (sdb, freq);
	}


	public void setR3(double sdb, double freq)
	{
		r3 = calculateDistance (sdb, freq);
	}


	public ConcurrentHashMap<String, Pair> getPositions()
	{
		return relativePos;
	}


	public static void main(String[] args)
	{
		DistanceEstimator de = new DistanceEstimator ();

		de.setDl1l2 (250.4, 1000);
		de.setDl1l3 (250.4, 1000);
		de.setDl2l3 (350.4, 1000);
		de.setR1 (100.5, 1000);
		de.setR2 (200.0, 1000);
		de.setR3 (200.3, 1000);

		de.initialization ();

		ConcurrentHashMap<String, Pair> pos = de.getPositions ();
		System.out.println (pos.get ("land1").x + " , " + pos.get ("land1").y);
		System.out.println (pos.get ("land2").x + " , " + pos.get ("land2").y);
		System.out.println (pos.get ("land3").x + " , " + pos.get ("land3").y);
		System.out.println (pos.get ("source").x + " , " + pos.get ("source").y);
		// double tri = de.lawOfCosine(4, 5, 3);
		// System.out.println(tri);

	}

	//
	// public void setSides(double a, double b, double c){
	// sides = new double[3];
	// // a is the side btw landmark 1 and 2
	// sides[0] = a;
	// // b is the side btw landmark 2 and 3
	// sides[1] = b;
	// // c is the side btw landmark 1 and 3
	// sides[2] = c;
	// }
	//
	// public void setSidesToSource(double d1, double d2, double d3){
	// // d1 is the side btw landmark 1 and source
	// sidesToSource[0] = d1;
	// // d2 is the side btw landmark 2 and source
	// sidesToSource[1] = d2;
	// // d3 is the side btw landmark 3 and source
	// sidesToSource[2] = d3;
	// }

	// public double aveEnergy(){
	// //1/(frequency*timeFrame)
	// return 0.0;
	// }

}
