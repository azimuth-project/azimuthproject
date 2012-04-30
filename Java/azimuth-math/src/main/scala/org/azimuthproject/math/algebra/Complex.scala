// This program is free software; you can redistribute it and/or modify
// it under the terms of the Lesser GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or (at
// your option) any later version.
//
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.

package org.azimuthproject.math.algebra

/**
 * This is an implementation of complex numbers, since neither Java nor Scala come with
 * one that is prepackaged.
 */
class Complex(val re: Double, val im: Double) {

  // first we'll define the arithmetic operations
  // of the field C
  def +(x: Complex) =
    new Complex(re + x.re, im + x.im)

  def -(x: Complex) =
    new Complex(re - x.re, im - x.im)

  // additive inverse
  def unary_- = new Complex(-re, -im)

  def *(x: Complex) =
    new Complex(re * x.re - im * x.im,
      re * x.im + im * x.re)

  def /(y: Complex) = {
    val denom = y.re * y.re + y.im * y.im
    new Complex((re * y.re + im * y.im) / denom,
      (im * y.re - re * y.im) / denom)
  }

  // now some fancier stuff, nth power,
  // a simple example of a recursive function
  def ^(exponent: Int): Complex = {
    if (exponent == 0) Complex(1)(0)
    else if (exponent == 1) this
    else this * (this ^ (exponent - 1))
  }

  def toPolar = (radius, theta)


  private def radius = {
    // the problem with the naive implementation
    // scala.Math.sqrt(re*re + im*im)
    // is that it will lead to an unnecessary overflow
    // if either re or im is bigger than the square root of the
    // maximum double number.
    // This can be avoided by a slight reformulation.
    val reAbs = Math.abs(re);
    val imAbs = Math.abs(im);

    if (reAbs > imAbs) {
      reAbs * Math.sqrt(1 + (im / re) * (im / re))
    }
    else {
      imAbs * Math.sqrt(1 + (re / im) * (re / im))
    }
  }


  private def theta = scala.Math.atan2(re, im)

  override def toString = {
    if (re == 0 && im == 0) "0"
    else if (im == 0) re.toString
    else if (re == 0) im + " i"
    else re + (if (im < 0) "" else "+") + im + " i"
  }


}

object Complex
{
  def apply(re: Double)(im: Double) = new Complex(re, im)
}

// a global constant i representing the complex number (0, 1)
object i extends Complex(0.0, 1.0)

