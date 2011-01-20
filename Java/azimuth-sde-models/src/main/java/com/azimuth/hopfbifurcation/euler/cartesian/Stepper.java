// This program is free software; you can redistribute it and/or modify
// it under the terms of the Lesser GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or (at
// your option) any later version.
//  
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.

package com.azimuth.hopfbifurcation.euler.cartesian;

import com.azimuth.geometry.Point2D;
import com.azimuth.random.INormalDevRandom;
import com.azimuth.random.IUniformRandom;
import com.azimuth.random.Normaldev;
import com.azimuth.random.Ran;

public class Stepper
{
	private Point2D currentPosition;
	
	private IForce2DCartesian force;
	
	/**
	 * the step size, each time set will increase the system time by this
	 * amount: {@link #time} = {@link #time} + {@link #step}
	 */
	private double step;

	/**
     * the factor of the additive noise
	 */
	private double diffusionConstant;

	/**
	 * a random generator for Gaussian random numbers
	 */
	private INormalDevRandom normdev;
	
	public Stepper(IForce2DCartesian pForce, Point2D pStartingPosition, double pStepSize, double pDiffusionConstant, int pSeed)
	{
		currentPosition = pStartingPosition;
		force = pForce;
		step = pStepSize;
		diffusionConstant = pDiffusionConstant;
		
		IUniformRandom uniformRandom = new Ran(pSeed);
		normdev = new Normaldev(0.0, Math.sqrt(pStepSize), uniformRandom);
	}
	
	public Point2D performStep()
	{
		double newX = currentPosition.x + step * force.getXForce(currentPosition) + diffusionConstant * normdev.generateNormalDev();
		double newY = currentPosition.y + step * force.getYForce(currentPosition) + diffusionConstant * normdev.generateNormalDev();
		
		currentPosition.x = newX;
		currentPosition.y = newY;
		
		return currentPosition;
	}

}
