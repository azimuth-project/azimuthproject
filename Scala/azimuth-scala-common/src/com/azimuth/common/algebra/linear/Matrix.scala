

package com.azimuth.common.algebra.linear

/**
 * basic trait for a 2 dimensional algebra of doubles
 */
trait Matrix {

  /**
   * Gets a matrix element
   * @param rowIndex the row index
   * @param columnIndex the column index
   * @return Double the value at the specified indices
   */
  def get(rowIndex : Int, columnIndex : Int) : Double;

  /**
   * Sets a matrix element
   * @param rowIndex the row index
   * @param columnIndex the column index
   * @param value the value to be set
   */
  def set(rowIndex : Int, columnIndex : Int, value : Double) : Unit;

  def getRowDim() : Int;

  def getColumDim() : Int;

}


