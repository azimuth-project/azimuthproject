package org.azimuthproject.vizualization.stochasticresonance.bistable

import org.azimuthproject.stochasticresonance.bistable._

/** Generates data from the [[org.azimuthproject.stochasticresonance.bistable.Stepper]] object used to build
  * stochastic resonance simulations.
  *
  * For the particular (currently hardcoded) parameter values, the output is a JSON object containing:
  *  - for each amplitude, eg 0.1 an object 0.1: containing
  *    - for each diffusion, eg 0.2 an object 0.2: containing
  *     - an array times:
  *     - an array positions:
  *
  * Eg:
  * {{{
  * {
	*   '0.0': {
  *	    '0.0': {
  *	      times: [0.1,0.2,0.3, ...],
  *		    positions: [1.000,1.000,1.000, ...]
  *	    },
  *	    '0.1': {
  *		    times: [0.1,0.2,0.3, ...],
  *       positions: [1.009,0.975,1.000, ...]
  *     },
  *     ...
  *   },
  *   '0.1': {
  *     ...
  *   },
  *   ...
  * }
  * }}}
  *
  * This format is suitable for assigning the X and Y axes of a
  * [[http://jsxgraph.uni-bayreuth.de/ JSXGraph]] chart with no further parsing required.
  *
  * It may be necessary to change the output format depending on which charting library is being used to draw the
  * data, for example [[http://code.shutterstock.com/rickshaw Rickshaw]] would be better served with a single array
  * of `{x: , y: }` points.
  *
  * (The idea is to avoid unnecessary parsing or object construction in Javascript, which may be very slow in some
  * browsers, even for reasonably sized data)
  */
object GenerateData extends App {
  /** Runs a new [[org.azimuthproject.stochasticresonance.bistable.Stepper]] in a continuous stream */
  def runStepper(initX: Double, stepSize: Double, force: IForce, diffusion: Double): Iterable[(Double, Double)] = {
    val stepper = new Stepper(initX, stepSize, force, diffusion)
    Stream.continually {
      val position = stepper.getNextPosition
      val time = stepper.getTime
      (time, position)
    }
  }
  def toJsDouble(x: Double, precision: Int = 16): String = {
    require(0 <= precision && precision <= 16)
    if (x.isInfinite) "Infinity"
    else if (x.isNaN) "NaN"
    else {
      val fmt = "%."+precision+"f"
      fmt.format(x)
    }
  }
  def toJsArray(xs: Iterable[Double], precision: Int): String = xs.map(toJsDouble(_, precision)).mkString("[", ",", "]")

  // setup the params
  val N = 500 // iterations of stepper for each force/diffusion
  val tStep = 0.1 // time per iteration
  val initX = 1.0 // initial position of particle
  val (aMin, aMax, aStep) = (0.0, 2.0, 0.1) // range and step size of amplitudes for forcing function
  val (dMin, dMax, dStep) = (0.0, 4.0, 0.1) // range and step size of diffusion constants

  // print out the JSON object
  printf("{\n")
  for (a <- aMin until aMax by aStep) {
    printf("\t'%.1f': {\n", a)                      // ..indexed first by forcing amplitudes a
    val force = new Force(a)
    for (d <- dMin until dMax by dStep) {
      printf("\t\t'%.1f': {\n", d)                  // ..then by diffusion rates d
      val (times, positions) = runStepper(initX, tStep, force, d).take(N).unzip
      printf("\t\t\ttimes: %s\n,", toJsArray(times, 1))
      printf("\t\t\tpositions: %s\n", toJsArray(positions, 3))
      printf("\t\t},\n")
    }
    printf("\t},\n")
  }
  printf("}\n")
}
