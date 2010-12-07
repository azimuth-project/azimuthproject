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
