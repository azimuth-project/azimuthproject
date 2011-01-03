package com.azimuth.randomwalk;

import com.azimuth.random.INormalDevRandom;
import com.azimuth.random.IUniformRandom;
import com.azimuth.random.Normaldev;
import com.azimuth.random.Ran;

/**
 * 
 * @author Tim van Beek
 * 
 */
public class CartesianRandomWalkStepper
{
	
	/**
	 * the step size, each time set will increase the system time by this
	 * amount: {@link #time} = {@link #time} + {@link #stepsize}
	 */
	private double stepsize;

	/**
	 * 
	 */
	private double lambda;

	/**
	 * a random generator for Gaussian random numbers
	 */
	private INormalDevRandom normdev;

	private double x;
	
	private double y;
		
	
	public CartesianRandomWalkStepper(final double pStepsize, final double pX, final double pY,
			final double pLambda)
	{
		stepsize = pStepsize;
		
		x = pX;
		y = pY;
		
		lambda = pLambda;
		
		IUniformRandom uniformRandom = new Ran(1);
		normdev = new Normaldev(0.0, Math.sqrt(pStepsize), uniformRandom);
	}

	/**
	 * performs a time step and returns the x position after the step, as a side
	 * effect {@link #time} and {@link #currentTemperature} will be updated.
	 * 
	 * @return double the x position after the performed time step
	 */
	public void performNextStep()
	{
		// we implement the Euler scheme
		double firstRandom = normdev.generateNormalDev();
		double secondRandom = normdev.generateNormalDev();
		
		x = x + lambda * firstRandom;
		
		y = y + lambda * secondRandom; 
	}

	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}
}
