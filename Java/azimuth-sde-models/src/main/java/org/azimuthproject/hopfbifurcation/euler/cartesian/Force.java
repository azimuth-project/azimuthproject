// This program is free software; you can redistribute it and/or modify
// it under the terms of the Lesser GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or (at
// your option) any later version.
//  
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.

package org.azimuthproject.hopfbifurcation.euler.cartesian;

import org.azimuthproject.geometry.Point2D;

public class Force implements IForce2DCartesian
{
	double omega;
	double beta;
	
	
	public Force(double pOmega, double pBeta, double pBetaConst)
	{
	  omega = pOmega;
	  beta = pBeta - pBetaConst;
	}
	
	/* (non-Javadoc)
	 * @see org.azimuthproject.hopfbifurcation.euler.cartesian.IForce2DCartesian#getXForce(org.azimuthproject.geometry.Point2D)
	 */
	public double getXForce(final Point2D position)
	{
		return -omega * position.y + beta * position.x - position.x*(position.x*position.x + position.y*position.y);
	}

	/* (non-Javadoc)
	 * @see org.azimuthproject.hopfbifurcation.euler.cartesian.IForce2DCartesian#getYForce(org.azimuthproject.geometry.Point2D)
	 */
	public double getYForce(final Point2D position)
	{
		return omega * position.x + beta * position.y - position.y*(position.x*position.x + position.y*position.y);
	}
}
