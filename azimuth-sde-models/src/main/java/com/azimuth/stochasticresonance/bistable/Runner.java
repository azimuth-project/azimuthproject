package com.azimuth.stochasticresonance.bistable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class executes a simulation and stores the result in a file.
 * 
 * @author Tim van Beek
 * 
 */
public class Runner
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

		
			// open file, choose the file name and location here
			BufferedWriter out = new BufferedWriter(new FileWriter(
					"bistable.txt"));

			IForce force = new Force(0.05);

			// for different noise strengths change the last parameter of the
			// constructor
			Stepper stepper = new Stepper(1.0, 0.001, force, 500.0);

			for (int index = 0; index < 50000; index++)
			{

				double xPosition = stepper.getNextPosition();
				double time = stepper.getTime();

				out.write(time + " " + xPosition);
				out.write("\n");
			}

			out.close();
		

	}

}
