package com.azimuth.enso.delayedoscillator;

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

		try
		{

			String fileName = "delayedOscillator.dat";
			File file = new File(fileName);

			file.createNewFile();

			
			// open file, choose the file name and location here
			BufferedWriter out = new BufferedWriter(new FileWriter(file));

			IForce force = new Force();

			// for different noise strengths change the last parameter of the
			// constructor
			Stepper stepper = new Stepper(0.55, 0.1, 0.75, 10, force, 0.0);

			for (int index = 0; index < 1000; index++)
			{

				double temperature = stepper.getNextTemperature();
				double temperatureDerivative = stepper
						.getCurrentTemperatureDerivative();
				double time = stepper.getTime();

				out.write(time + " " + temperature + " "
						+ temperatureDerivative);
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
