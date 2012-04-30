package org.azimuthproject.stochasticresonance.bistable;

/**
 * This class represents the deterministic force, in one spatial dimension.
 * 
 * @author Tim van Beek
 * 
 */
public class Force implements IForce
{

	/**
	 * this is the amplitude of the time dependent sin function of the force
	 */
	private double amplitude;

	/**
	 * Constructor. This simple force accepts only one parameter, see
	 * {@link #amplitude}
	 * 
	 * @param pAmplitude
	 */
	public Force(final double pAmplitude)
	{
		amplitude = pAmplitude;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.azimuthproject.stochasticresonance.bistable.IForce#getForce(double,
	 * double)
	 */
	public double getForce(final double pXPosition, final double pTime)
	{
		return pXPosition - pXPosition * pXPosition * pXPosition + amplitude
				* Math.sin(pTime);
	}
}
