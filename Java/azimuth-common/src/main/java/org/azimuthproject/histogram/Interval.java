// This program is free software; you can redistribute it and/or modify
// it under the terms of the Lesser GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or (at
// your option) any later version.
//  
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.

package org.azimuthproject.statistics.histogram;

public class Interval
{
	public double lowerBound;
	
	public double upperBound;
	
	public Interval(double pLowerBound, double pUpperBound)
	{
		lowerBound = pLowerBound;
		upperBound = pUpperBound;
	}

	public int compare(double x)
	{
		if (x < lowerBound)
		{
			return 1;
		}
		if(x >= upperBound)
		{
			return -1;
		}
		return 0;
	}
}
