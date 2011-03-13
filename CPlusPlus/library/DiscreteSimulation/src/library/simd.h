#ifndef __SIMD_H__
#define __SIMD_H__

// This program is free software; you can redistribute it and/or modify
// it under the terms of the Lesser GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or (at
// your option) any later version.
//  
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.

#include <xmmintrin.h>
#include <stdio.h>

namespace AzFundamentals {

//currently just types that apply on Intel x86/x86-64
/**
 *LL_SIMD is a typedef for machine's lowest level float SIMD type
 */
typedef __m128 LL_SIMD;
static const int SIMD_SZ=4;

/**
 *Anonymous union wrapping LL_SIMD (primarily due to new aliasing rules).
 */
struct SIMD {
    union {
        LL_SIMD simd;
        float scalars[SIMD_SZ];
    };
    SIMD() { }
    SIMD(float v) { simd=_mm_set1_ps(v); }
    SIMD(LL_SIMD sSimd) { simd=sSimd; }
};

/**
 *An array of floats primarily intended to be accessed in a SIMD fashion.
 */
class SIMDArray { private: //prevent implicit usage
    SIMDArray();
    SIMDArray operator=(SIMDArray &);
public:
    SIMD *elts;
    int linearSIMDSteps;
    SIMDArray(int sLinearSIMDSteps);
    ~SIMDArray();
    void setScalar(int i,float v) { elts[i/SIMD_SZ].scalars[i%SIMD_SZ]=v; }
    void setSIMD(int i,SIMD s) { elts[i]=s; }
    LL_SIMD get(int i) const { return elts[i].simd; }
    void writeASCII(const char* const fileName,int fileIdx,int noRows,int noCols);
};

}//AzFundamentals

#endif /*__SIMD_H__*/
