// This program is free software; you can redistribute it and/or modify
// it under the terms of the Lesser GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or (at
// your option) any later version.
//  
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.


package org.azimuthproject.random;

/**
 * Interface that describes a random number generator for uniform distributed
 * random numbers
 * 
 * @author Tim van Beek
 */
public interface IUniformRandom
{

	/**
	 * a random long
	 * 
	 * @return long a random long
	 */
	public abstract long generateRandomLong();

	/**
	 * a random double from the interval [0, 1], unifom distribution
	 * 
	 * @return double a random double
	 */
	public abstract double generateUniformDouble();

}