// This program is free software; you can redistribute it and/or modify
// it under the terms of the Lesser GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or (at
// your option) any later version.
//  
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.

package com.azimuth.random;


/**
 * See Normaldev in deviates.h of numerical recipes
 * @author Tim van Beek
 *
 */
public class Normaldev implements INormalDevRandom {

	/**
	 * a generator of uniform random variables
	 */
	private IUniformRandom uniformRandom;
	
	/**
	 * parameter mu, the expectation value
	 */
	private double mu;
	
	/**
	 * parameter sigma, the square root of the variance
	 */
	private double sig;

	/**
	 * Constructor
	 * @param pMu mean
	 * @param pSigma square root of the variance
	 * @param pUniformRandom generator of uniform random variables
	 */
	public Normaldev(final double pMu, final double pSigma, final IUniformRandom pUniformRandom) {
		mu = pMu;
		sig = pSigma;
		uniformRandom = pUniformRandom;
	}

	/* (non-Javadoc)
	 * @see com.azimuth.random.INormalDevRandom#generateNormalDev()
	 */
	@Override
	public final double generateNormalDev()
	    {
	        double u, v, x, y, q;
	        
	        do
	        {
	            u = uniformRandom.generateUniformDouble();
	            v = 1.7156 * (uniformRandom.generateUniformDouble() - 0.5);
	            x = u - 0.449871;
	            y = Math.abs(v) + 0.386595;
	            q = x*x + y * (0.19600 * y - 0.25472 * x);
	        }
	        while (q > 0.27597
	                && (q > 0.27846 || v*v > -4.
	                        * Math.log(u) * u*u));

	        return mu + sig * v / u;
	    }

}
