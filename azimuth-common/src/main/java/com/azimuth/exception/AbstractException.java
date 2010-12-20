// This program is free software; you can redistribute it and/or modify
// it under the terms of the Lesser GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or (at
// your option) any later version.
//  
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.


package com.azimuth.exception;

/**
 * Abstract basis class for all checked exceptions of the Azimuth project
 * @author Tim van Beek
 *
 */
public abstract class AbstractException extends Exception
{

	/**
	 * a serial version uid 
	 */
	private static final long serialVersionUID = 1L;

	public AbstractException(String pMessage)
	{
		super(pMessage);
	}

}
