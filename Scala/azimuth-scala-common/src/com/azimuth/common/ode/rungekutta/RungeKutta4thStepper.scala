/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.azimuth.common.ode.rungekutta

import com.azimuth.common.function.TwoDimFunction;

/**
 * Implementation of a simple 4th order Runge-Kutta method
 */
class RungeKutta4thStepper(function : TwoDimFunction, startValue : Double) {

  def step(xValue : Double, yValue : Double, stepSize : Double) : Double = {

    val k1 = stepSize * function.calculate(xValue, yValue);

    val k2 = stepSize * function.calculate(xValue + 0.5 * stepSize, yValue + 0.5 * k1);

    val k3 = stepSize * function.calculate(xValue + 0.5 * stepSize, yValue + 0.5 * k2);

    val k4 = stepSize * function.calculate(xValue + stepSize, yValue + k3);

    yValue + 1/6 * k1 + 1/3 * k2 + 1/3 * k3 + 1/6 * k4;
  }

}
