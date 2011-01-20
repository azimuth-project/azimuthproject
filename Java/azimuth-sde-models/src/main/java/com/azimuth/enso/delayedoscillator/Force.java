package com.azimuth.enso.delayedoscillator;

/**
 * This class represents the deterministic force, in one spatial dimension.
 * 
 * @author Tim van Beek
 * 
 */
public class Force implements IForce
{


	/**
	 * Constructor. This simple force accepts only one parameter, see
	 * {@link #amplitude}
	 * 
	 * @param pAmplitude
	 */
	public Force()
	{
		// nothing to do here
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.azimuth.stochasticresonance.bistable.IForce#getForce(double,
	 * double)
	 */
	public double getForce(final double pXPosition)
	{
		return pXPosition - pXPosition * pXPosition * pXPosition;
	}
}
