package org.azimuthproject.vizualization.stochasticresonance.bistable

import org.azimuthproject.stochasticresonance.bistable._

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
  def toJsArray(xs: Iterable[Double], precision: Int): String = xs.map(toJsDouble(_, precision)).mkString("[",",","]")

  // setup the params
  val N = 500 // iterations of stepper for each force/diffusion
  val tStep = 0.1 // time per iteration
  val initX = 1.0 // initial position of particle
  val (aMin, aMax, aStep) = (0.0, 2.0, 0.1)
  val (dMin, dMax, dStep) = (0.0, 4.0, 0.1)

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
