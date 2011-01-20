// This program is free software; you can redistribute it and/or modify
// it under the terms of the Lesser GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or (at
// your option) any later version.
//  
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.

package com.azimuth.integration;

import com.azimuth.function.IFunctionOneDimReal;

public class RadialDistributionOfHopfBifurcationExample implements IFunctionOneDimReal
{

	private double beta;
	
	private double lambda;
	
	public RadialDistributionOfHopfBifurcationExample(double pBeta, double pLambda)
	{
		beta = pBeta;
		lambda = pLambda;
	}
	
	
	@Override
	public double getValue(double x)
	{
		double lambdaSquare = lambda*lambda;
		
		double xSquare = x*x;
		
		double result = x * Math.exp( - 1/(2.0 * lambdaSquare)* xSquare * xSquare + (beta/lambdaSquare) *xSquare ); 
		return result;
	}

}
