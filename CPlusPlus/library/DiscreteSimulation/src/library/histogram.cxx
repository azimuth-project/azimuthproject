// This program is free software; you can redistribute it and/or modify
// it under the terms of the Lesser GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or (at
// your option) any later version.
//  
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.

#include "histogram.h"
#include <string.h>
#include <algorithm>

namespace AzDiscreteSim {

void
HistogramU32::setup(int sLinearSz)
{
    machineFormat=true;
    linearSz=sLinearSz;
    int ok=posix_memalign((void **)(&bins),16,linearSz/4*sizeof(__m128i));
    reset();
}

HistogramU32::HistogramU32()
{
    //nothing
}


HistogramU32::HistogramU32(int sLinearSz)
{
    setup(sLinearSz);
}

HistogramU32::~HistogramU32()
{
    free(bins);
}

void
HistogramU32::reset()
{
    memset(bins,0,linearSz*sizeof(uint32_t));
}

void
HistogramU32::flipFormat(bool intoHighLevelFormat)
{
    int i,j;
    if(1 /*intoHighLevelFormat ^ (!machineFormat)*/){ //we need to do a flip
        const int quads=linearSz/SIMD_SZ-1;
        const int halfPt=linearSz/(2*SIMD_SZ);
        for(i=0;i<halfPt;++i){
            for(j=0;j<SIMD_SZ;++j){
                std::swap(bins[4*i+j],bins[4*(quads-i)+j]);
            }
        }
    }
}

void
HistogramU32::writeASCII(const char* const fileName,int fileIdx,int noRows,int noCols,uint32_t normaliser)
{
    //bins[5]=0;
    //bins[6]=normaliser;
    const char sep[4]={' ',' ',' ','\n'}; 
    static const char* const FMT="%16.8f%c";
    char fileNm[80];
    sprintf(fileNm,fileName,fileIdx);
    double normaliserD=double(normaliser);
    FILE *fp=fopen(fileNm,"w+");
    int i,j;
    for(i=0;i<linearSz;++i){
        fprintf(fp,FMT,double(bins[i])/normaliserD,((i+1)%noCols==0?'\n':' '));
    }
    fflush(fp);
    fclose(fp);
}

}//AzDiscreteSim
