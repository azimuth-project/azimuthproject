package com.azimuth.random;

/**
 * Interface that describes a random number generator for uniform distributed
 * random numbers
 * 
 * @author Tim van Beek
 */
public interface IUniformRandom
{

	/**
	 * a random long
	 * 
	 * @return long a random long
	 */
	public abstract long generateRandomLong();

	/**
	 * a random double from the interval [0, 1], unifom distribution
	 * 
	 * @return double a random double
	 */
	public abstract double generateUniformDouble();

}