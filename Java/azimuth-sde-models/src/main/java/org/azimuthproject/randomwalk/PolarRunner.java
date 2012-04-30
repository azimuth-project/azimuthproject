package org.azimuthproject.randomwalk;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class executes a simulation and stores the result in a file.
 * 
 * @author Tim van Beek
 * 
 */
public class PolarRunner
{

	/**
	 * the main method.
	 * 
	 * @param args
	 *            are ignored
	 * @throws IOException
	 */
	public static final void main(String[] args) throws IOException
	{

		try
		{

			String fileName = "randomWalkPolar.dat";
			File file = new File(fileName);

			file.createNewFile();

			
			// open file, choose the file name and location here
			BufferedWriter out = new BufferedWriter(new FileWriter(file));

			// for different noise strengths change the last parameter of the
			// constructor
			PolarRandomWalkStepper stepper = new PolarRandomWalkStepper(0.001, 0.0, 1.0, 0.1);

			double angleFactor = 360.0/(2.0 * Math.PI);
			
			for (int index = 0; index < 100000; index++)
			{
				out.write(stepper.getRadius() * Math.cos(stepper.getPhi()) + " " + stepper.getRadius() * Math.sin(stepper.getPhi()));
				out.write("\n");
				stepper.performNextStep();
			}

			out.close();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
