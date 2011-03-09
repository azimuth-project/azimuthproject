

package com.azimuth.common.algebra

/**
 * This is an implementation for complex numbers.
 */
class Complex(val re: Double, val im: Double)
{

  // first we'll define the arithmetic operations
  // of the field C
  def + (x: Complex) =
    new Complex(re + x.re, im + x.im)

  def - (x: Complex) =
    new Complex(re - x.re, im - x.im)

  // additive inverse
  def unary_- = new Complex(-re, -im)

  def * (x: Complex) =
    new Complex(re * x.re - im * x.im,
                re * x.im + im * x.re)

  def / (y: Complex) =
  {
    val denom = y.re * y.re + y.im * y.im
    new Complex((re * y.re + im * y.im) / denom,
                (im * y.re - re * y.im) / denom)
  }

  // now some fancier stuff, nth power,
  // a simple example of a recursive function
  def ^ (exponent: Int): Complex =
  {
    if(exponent == 0) Complex(1)(0)
    else if(exponent == 1) this
    else this * (this^(exponent-1))
  }

  def toPolar = (radius, theta)

  private def radius = scala.Math.sqrt(re*re + im*im)
  private def theta = scala.Math.atan2(re, im)

  override def toString =
  {
    if (re==0&&im==0) "0"
    else if ( im == 0 ) re.toString
    else if ( re ==0)im + " i"
    else re + (if (im < 0) "" else "+") + im + " i"
  }
}

object Complex
{
  def apply(re: Double)(im: Double) = new Complex(re, im)
}

// a global constant i representing the complex number (0, 1)
object i extends Complex(0.0, 1.0)

