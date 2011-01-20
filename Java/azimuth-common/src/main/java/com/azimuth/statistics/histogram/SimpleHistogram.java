// This program is free software; you can redistribute it and/or modify
// it under the terms of the Lesser GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or (at
// your option) any later version.
//  
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.

package com.azimuth.statistics.histogram;

import java.util.Collections;

public class SimpleHistogram
{
	private Interval[] intervalArray;

	private double[] values;
	
	public SimpleHistogram(double lowerBound, double upperBound, double stepsize)
	{
		this(lowerBound, stepsize, (int) Math.ceil((upperBound - lowerBound)/stepsize));
	}
	
	public SimpleHistogram(double lowerBound, double stepsize, int steps)
	{
		intervalArray = new Interval[steps];
		values = new double[steps];
			
		double currentLowerBound = lowerBound;
		
		for(int index = 0; index < steps; index++)
		{
			Interval interval = new Interval(currentLowerBound, currentLowerBound + stepsize);
			intervalArray[index] = interval;
			currentLowerBound += stepsize;
		}
	}
	
	public boolean add(double pDouble)
	{
		int low = 0;
		int high = intervalArray.length - 1;

		while (low <= high) {
		    int mid = (low + high) >>> 1;
		    Interval midVal = intervalArray[mid];
		    int cmp = midVal.compare(pDouble);

		    if (cmp < 0)
		    {
			low = mid + 1;
		    }
		    else if (cmp > 0)
		    {
			high = mid - 1;
		    }
		    else
		    {
		    	// matching interval found
		    	values[mid]++;
		    	return true;
		    }
		}
		// no matching interval found
		return false;
	}
	
	public Interval[] getIntervals()
	{
		return intervalArray;
	}
	
	public double[] getValues()
	{
		return values;
	}
}
