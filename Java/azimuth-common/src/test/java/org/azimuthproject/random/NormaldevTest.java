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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.junit.Test;

import org.azimuthproject.statistics.histogram.Interval;
import org.azimuthproject.statistics.histogram.SimpleHistogram;

public class NormaldevTest
{

	@Test
	public void testChiSquare()
	{
		try
		{
			double stepSize = 0.02;

			SimpleHistogram histogram = new SimpleHistogram(-10.0, stepSize, 1000);

			IUniformRandom uniformRandom = new Ran(1);
			Normaldev normdev = new Normaldev(0.0, 2.777, uniformRandom);

			int numberCount = 200000;
			
			double normalizationFactor = 1.0/( ((double)numberCount)*stepSize );
			
			for (int index = 0; index < numberCount; index++)
			{
				double number = normdev.generateNormalDev();
				histogram.add(number);
			}

			String fileName = "histogram.dat";
			File file = new File(fileName);

			file.createNewFile();

			// open file, choose the file name and location here
			BufferedWriter out = new BufferedWriter(new FileWriter(file));

			Interval[] intervalArray = histogram.getIntervals();
			double[] values = histogram.getValues();
			
			for(int index = 0; index < intervalArray.length; index++)
			{
				Interval interval = intervalArray[index];
                out.write(0.5*(interval.lowerBound + interval.upperBound) + " " + values[index] * normalizationFactor);
				
				out.write("\n");
			}
			out.close();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
