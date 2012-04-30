package org.azimuthproject.hopfbifurcation.euler.cartesian;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.azimuthproject.geometry.Point2D;

/**
 * This class executes a simulation and stores the result in a file.
 * 
 * @author Tim van Beek
 * 
 */
public class HopfRunner
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
			Date date = new Date();
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
			
			String dateString = dateFormat.format(date);
			
			//String fileName = "hopfEulerCartesian_" + dateString + ".txt";
			String fileName = "hopfEulerCartesian.dat";
			File file = new File(fileName);

			file.createNewFile();

			// open file, choose the file name and location here
			BufferedWriter out = new BufferedWriter(new FileWriter(file));

			// model parameters:
			double omega = 1.0;
			double beta = -0.25;
			double betaConst = 0;
			double xStart = 0.0;
			double yStart = 2.0;

			double stepSize = 0.001;

			double diffusionConstant = 0.0;

			String headline = "# omega: " + omega + "\n";
			headline = headline + "# beta: " + beta + "\n";
			headline = headline + "# betaConst: " + betaConst + "\n";
			headline = headline + "# diffusionConstant: " + diffusionConstant
					+ "\n";
			headline = headline + "# yStart: " + yStart + "\n";
			headline = headline + "# xStart: " + xStart + "\n";
			headline = headline + "# stepSize: " + stepSize + "\n";

			out.write(headline);

			IForce2DCartesian force = new Force(omega, beta, betaConst);

			// for different noise strengths change the last parameter of the
			// constructor
			
			
			
			Point2D startingPoint = new Point2D(xStart, yStart);

			Stepper stepper = new Stepper(force, startingPoint, stepSize,
					diffusionConstant, 1);

			
			double h = 0.0;
			
			for (int index = 0; index < 40000; index++)
			{

				Point2D currentPosition = stepper.performStep();

				out.write(h + " " + currentPosition.x + " " + currentPosition.y);
				
				//out.write(currentPosition.x + " " + currentPosition.y);
				
				out.write("\n");
				
				h += stepSize;
			}			

			out.close();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
