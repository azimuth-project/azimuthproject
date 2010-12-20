// This program is free software; you can redistribute it and/or modify
// it under the terms of the Lesser GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or (at
// your option) any later version.
//  
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.

package com.azimuth.geometry;

/**
 * simple implementation of a point in two dimensions
 * 
 * @author Tim van Beek
 * 
 */
public class Point2D
{
	public double x;

	public double y;

	public Point2D(double pX, double pY)
	{
		x = pX;
		y = pY;
	}

	public double getRadius()
	{
		return Math.sqrt(x * x + y * y);
	}

	public double getAngle()
	{
		return Math.atan2(y, x);
	}
}
