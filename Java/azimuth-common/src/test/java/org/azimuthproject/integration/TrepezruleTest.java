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

import org.junit.Test;

import org.azimuthproject.function.IFunctionOneDimReal;

public class TrepezruleTest
{
	@Test
	public void testTrapezrule()
	{
		IFunctionOneDimReal function = new RadialDistributionOfHopfBifurcationExample(-0.25, 0.3);
		
		Trapezrule trapezrule = new Trapezrule(0.0, 1.0, function); 
		
		for(int index = 0; index <20; index++)
		{
			System.out.println(trapezrule.getNextRefinedValue());
		}
		
       function = new RadialDistributionOfHopfBifurcationExample(-0.25, 0.1);
		
		 trapezrule = new Trapezrule(0.0, 1.0, function); 
		
		for(int index = 0; index <20; index++)
		{
			System.out.println(trapezrule.getNextRefinedValue());
		}
	}

}
