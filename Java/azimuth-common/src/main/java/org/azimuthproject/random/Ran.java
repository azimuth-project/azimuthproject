// This program is free software; you can redistribute it and/or modify
// it under the terms of the Lesser GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or (at
// your option) any later version.
//  
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.

package org.azimuthproject.random;

/**
 * Based on Ran from ran.h of numerical recipes
 * 
 * @author Tim van Beek
 */
public class Ran implements IUniformRandom
{

	private long u;
	private long v;
	private long w;

	public Ran(final long pSeed)
	{
		v = 4101842887655102017L;
		w = 1;
		u = pSeed ^ v;
		generateRandomLong();
		v = u;
		generateRandomLong();
		w = v;
		generateRandomLong();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see numrec.random.IUniformRandom#generateRandomLong()
	 */
	@Override
	public long generateRandomLong()
	{
		u = u * 2862933555777941757L + 7046029254386353087L;
		v ^= v >> 17;
		v ^= v << 31;
		v ^= v >> 8;
		w = 4294957665L * (w & 0xffffffff) + (w >> 32);

		long x = u ^ (u << 21);
		x ^= x >> 35;
		x ^= x << 4;
		return (x + v) ^ w;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see numrec.random.IUniformRandom#generateUniformDouble()
	 */
	@Override
	public double generateUniformDouble()
	{
		return 5.42101086242752217E-20 * generateRandomLong() + 0.5;
	}
}
