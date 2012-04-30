package org.azimuthproject.hopfbifurcation.euler.cartesian;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.azimuthproject.geometry.Point2D;
import org.azimuthproject.statistics.histogram.Interval;
import org.azimuthproject.statistics.histogram.SimpleHistogram;

/**
 * This class executes a simulation and stores the result in a file.
 * 
 * @author Tim van Beek
 * 
 */
public class HopfRunnerTest
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
			
			String fileName = "hopfEulerCartesianRadiusHistogram.dat";
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

			double diffusionConstant = 0.1;

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
			
			double histogramStepSize = 0.01;
			SimpleHistogram histogram = new SimpleHistogram(0.0, 1.0, histogramStepSize); 
			
			
			Point2D startingPoint = new Point2D(xStart, yStart);

			Stepper stepper = new Stepper(force, startingPoint, stepSize,
					diffusionConstant, 1);
			
			double h = 0.0;
			
			int numbercount = 0;
			
			for (int index = 0; index < 100000000; index++)
			{

				Point2D currentPosition = stepper.performStep();
				
				h += stepSize;
				
				if(h > 2.0)
				{
					h = 0.0;
					
					double radius = Math.sqrt(currentPosition.x * currentPosition.x + currentPosition.y * currentPosition.y);
					
					histogram.add(radius);
					
					numbercount++;
				}
			}
		    
			double normalizationFactor = 1.0/( (numbercount)*histogramStepSize );
			
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
