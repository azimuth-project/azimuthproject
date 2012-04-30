package org.azimuthproject.randomwalk;

import org.azimuthproject.random.INormalDevRandom;
import org.azimuthproject.random.IUniformRandom;
import org.azimuthproject.random.Normaldev;
import org.azimuthproject.random.Ran;

/**
 * 
 * @author Tim van Beek
 * 
 */
public class PolarRandomWalkStepper
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

	private double phi;
	
	private double radius;
		
	
	public PolarRandomWalkStepper(final double pStepsize, final double pPhi, final double pRadius,
			final double pLambda)
	{
		stepsize = pStepsize;
		
		phi = pPhi;
		radius = pRadius;
		
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
		
		radius = radius + stepsize*lambda*lambda/(2.0*radius)  + lambda * Math.cos(phi) * firstRandom + lambda * Math.sin(phi) * secondRandom;
		
		phi = phi + (-Math.sin(phi)/radius) * lambda * firstRandom + (Math.cos(phi)/radius) * lambda * secondRandom; 
		
		if(phi < 0)
		{
			phi += 2.0*Math.PI;
		}
		if(phi > 2.0*Math.PI)
		{
			phi -= 2.0*Math.PI;
		}
	}

	public double getPhi()
	{
		return phi;
	}
	
	public double getRadius()
	{
		return radius;
	}
}
