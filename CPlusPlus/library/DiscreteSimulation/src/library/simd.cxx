#include "simd.h"

// This program is free software; you can redistribute it and/or modify
// it under the terms of the Lesser GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or (at
// your option) any later version.
//  
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.

namespace AzFundamentals {

SIMDArray::SIMDArray(int noElts)
{
    linearSIMDSteps=noElts/SIMD_SZ;
    void *t=0;
    int ok=posix_memalign((void **)(&t),16,linearSIMDSteps*sizeof(SIMD));
    elts=(SIMD*)t;
    int i,j;
#if 1
    for(i=0;i<linearSIMDSteps;++i){
        SIMD s;
        for(j=0;j<SIMD_SZ;++j){
            s.scalars[j]=float(rand());
        }
        elts[i]=s.simd;
    }
#endif
}

SIMDArray::~SIMDArray()
{
    free(elts);
}

void
SIMDArray::writeASCII(const char* const fileName,int fileIdx,int noRows,int noCols)
{
    static const char* const FMT="%16.1f%c";
    char fileNm[80];
    sprintf(fileNm,fileName,fileIdx);
    FILE *fp=fopen(fileNm,"w+");
    int i,j,idx=1;
    for(i=0;i<linearSIMDSteps;++i){
        for(j=0;j<SIMD_SZ;++j){
            fprintf(fp,FMT,double(elts[i].scalars[j]),(idx%noCols==0?'\n':' '));
            ++idx;
        }
    }
    fflush(fp);
    fclose(fp);
}

}//AzFundamentals
