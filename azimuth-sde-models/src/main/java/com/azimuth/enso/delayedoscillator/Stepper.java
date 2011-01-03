package com.azimuth.enso.delayedoscillator;

import com.azimuth.random.INormalDevRandom;
import com.azimuth.random.IUniformRandom;
import com.azimuth.random.Normaldev;
import com.azimuth.random.Ran;

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
	 * the current X-position of the test particle, is set in the constructor
	 */
	private double currentTemperature;
	
	/**
	 * the current X-position of the test particle, is set in the constructor
	 */
	private double currentTemperatureDerivative = 0;

	/**
	 * the step size, each time set will increase the system time by this
	 * amount: {@link #time} = {@link #time} + {@link #step}
	 */
	private double step;

	/**
	 * the diffusion constant,
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

	private double alpha;
	
	/**
	 * this specifies the delay offset delta as a multiple of the step size.
	 */
	private int timeDelayFactor;
	
	private int currentStepNumber = 0;
	
	private double[] temperature;
	
	double time = 0;
	
	/**
	 * The constructor.
	 * 
	 * @param pInitialTemperature
	 *            the initial x position
	 * @param pStepsize
	 *            the step size of the time steps
	 * @param pForce
	 *            the force exerted by the time dependent potential
	 * @param pDiffusionConstant
	 *            the diffusion constant,
	 *            <em>this is not D, it is the value of sqrt(2*D)!</em>
	 */
	public Stepper(final double pInitialTemperature, final double pStepsize, final double pAlpha, final int pTimeDelayFactor,
			final IForce pForce, final double pDiffusionConstant)
	{
		currentTemperature = pInitialTemperature;
		step = pStepsize;
		alpha = pAlpha;
		timeDelayFactor = pTimeDelayFactor;
		temperature = new double[timeDelayFactor];
		force = pForce;
		diffusionConstant = pDiffusionConstant;
		
		IUniformRandom uniformRandom = new Ran(1);
		normdev = new Normaldev(0.0, Math.sqrt(pStepsize), uniformRandom);
	}

	/**
	 * performs a time step and returns the x position after the step, as a side
	 * effect {@link #time} and {@link #currentTemperature} will be updated.
	 * 
	 * @return double the x position after the performed time step
	 */
	public double getNextTemperature()
	{
		// we implement the Euler scheme
		currentStepNumber++;
		int index = (currentStepNumber % timeDelayFactor);
		
		double delayedTemperature = temperature[index];
		
		currentTemperatureDerivative = (force.getForce(currentTemperature) + alpha * delayedTemperature);
		
		currentTemperature = currentTemperature + step
				* currentTemperatureDerivative + diffusionConstant
				* normdev.generateNormalDev();

		temperature[index] = currentTemperature;
		
		time += step;
		
		return currentTemperature;
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
	
	public double getCurrentTemperatureDerivative()
	{
		return currentTemperatureDerivative;
	}
}
