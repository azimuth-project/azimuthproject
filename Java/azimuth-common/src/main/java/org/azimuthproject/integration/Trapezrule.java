// This program is free software; you can redistribute it and/or modify
// it under the terms of the Lesser GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or (at
// your option) any later version.
//  
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.

package org.azimuthproject.integration;

import org.azimuthproject.function.IFunctionOneDimReal;

/**
 * This is a simple implementation of the trapez rule for an definite integral of 
 * a one dimensional real function. The implementation is similar to the algorithm
 * of the struct Trapzd in the file quadrature.h of numerical recipes, 3rd edition.
 *   
 * @author Tim van Beek
 *
 */
public class Trapezrule
{
	/**
	 * the lower boundary of the integral
	 */
	private double lowerLimit;

	/**
	 * the upper boundary of the integral
	 */
	private double upperLimit;
	
	/**
	 * the last calculated integral value
	 */
	private double currentIntegralValue;
	
	/**
	 * a counter that counts how many times the method {@link #getNextRefinedValue()} has been called,
	 * the first call counts as {@link refinementLevel} = 1.
	 */
	private int refinementLevel = 0;
	
	/**
	 * This interface represents the one dimensional real function that this class will integrate.
	 */
	private IFunctionOneDimReal function;
	
	public Trapezrule(double pLowerLimit, double pUpperLimit, IFunctionOneDimReal pFunction)
	{
		lowerLimit = pLowerLimit;
		upperLimit = pUpperLimit;
		function = pFunction;
	}
	
	public double getNextRefinedValue()
	{
		refinementLevel++;

		if(refinementLevel == 1)
		{
			currentIntegralValue = 0.5*(upperLimit-lowerLimit)*(function.getValue(upperLimit) + function.getValue(lowerLimit));
			return currentIntegralValue;
		}
		
		int it = 1;
		for (int j =1; j<refinementLevel-1; j++)
		{
			it <<= 1;
		}
		double tnm = it;
		double del = (upperLimit - lowerLimit)/tnm;
		double x = lowerLimit + 0.5 *del;
		
		double sum = 0.0;
		
		for (int j =0; j < it; j++, x+=del)
		{
			sum += function.getValue(x);
		}
		currentIntegralValue =0.5*(currentIntegralValue + (upperLimit - lowerLimit)*sum/tnm);

		return currentIntegralValue;
		
	}
}
