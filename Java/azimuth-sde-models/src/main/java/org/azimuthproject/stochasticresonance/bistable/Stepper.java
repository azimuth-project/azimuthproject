package org.azimuthproject.stochasticresonance.bistable;

import org.azimuthproject.random.INormalDevRandom;
import org.azimuthproject.random.IUniformRandom;
import org.azimuthproject.random.Normaldev;
import org.azimuthproject.random.Ran;

/**
 * This class holds the information about the current state of the system and is
 * able to perform a time step.
 * 
 * @author Tim van Beek
 * 
 */
public class Stepper
{

	/**
	 * the current time, is set in the constructor
	 */
	private double time;

	/**
	 * the current X-position of the test particle, is set in the constructor
	 */
	private double currentXposition;

	/**
	 * the step size, each time set will increase the system time by this
	 * amount: {@link #time} = {@link #time} + {@link #step}
	 */
	private double step;

	/**
	 * the diffusion constant,
	 * <em>this is not D, it is the value of sqrt(2*D)!</em>
	 */
	private double diffusionConstant;

	/**
	 * the force exerted by the effective, time dependent potential
	 */
	private IForce force;

	/**
	 * a random generator for Gaussian random numbers
	 */
	private INormalDevRandom normdev;

	/**
	 * The constructor.
	 * 
	 * @param pInitialXPosition
	 *            the initial x position
	 * @param pStepsize
	 *            the step size of the time steps
	 * @param pForce
	 *            the force exerted by the time dependent potential
	 * @param pDiffusionConstant
	 *            the diffusion constant,
	 *            <em>this is not D, it is the value of sqrt(2*D)!</em>
	 */
	public Stepper(final double pInitialXPosition, final double pStepsize,
			final IForce pForce, final double pDiffusionConstant)
	{
		currentXposition = pInitialXPosition;
		step = pStepsize;
		force = pForce;
		diffusionConstant = pDiffusionConstant;

		IUniformRandom uniformRandom = new Ran(1);
		normdev = new Normaldev(0.0, Math.sqrt(pStepsize), uniformRandom);
	}

	/**
	 * performs a time step and returns the x position after the step, as a side
	 * effect {@link #time} and {@link #currentXposition} will be updated.
	 * 
	 * @return double the x position after the performed time step
	 */
	public double getNextPosition()
	{
		// we implement the Euler scheme
		currentXposition = currentXposition + step
				* force.getForce(currentXposition, time) + diffusionConstant
				* normdev.generateNormalDev();
		time += step;
		return currentXposition;
	}

	/**
	 * returns the current time
	 * 
	 * @return double the current time
	 */
	public double getTime()
	{
		return time;
	}
}
