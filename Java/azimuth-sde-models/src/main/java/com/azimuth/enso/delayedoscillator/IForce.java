package com.azimuth.enso.delayedoscillator;

/**
 * Interface for the force exerted by a one dimensional, time dependent
 * potential
 * 
 * @author Tim van Beek
 */
public interface IForce
{

	/**
	 * The force at a x position and at a certain time
	 * 
	 * @param pXPosition
	 *            the spatial position
	 * @param pTime
	 *            the time
	 * @return double the amount of the force
	 */
	public abstract double getForce(final double pXPosition);
}