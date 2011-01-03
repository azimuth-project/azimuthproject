package com.azimuth.randomwalk;

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
public class CartesianRunner
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

			String fileName = "randomWalkCartesian.dat";
			File file = new File(fileName);

			file.createNewFile();

			
			// open file, choose the file name and location here
			BufferedWriter out = new BufferedWriter(new FileWriter(file));

			// for different noise strengths change the last parameter of the
			// constructor
			CartesianRandomWalkStepper stepper = new CartesianRandomWalkStepper(0.001, 1.0, 0.0, 0.1);

			for (int index = 0; index < 100000; index++)
			{
				out.write(stepper.getX() + " " + stepper.getY());
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
