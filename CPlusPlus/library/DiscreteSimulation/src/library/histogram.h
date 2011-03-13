#ifndef __HISTOGRAM_H__
#define __HISTOGRAM_H__

#include <stdint.h>
#include "simd.h"

namespace AzDiscreteSim {

using namespace AzFundamentals;

/**
 *A simple histogram that just has a linear linear array of bins
 *capable of holding up to 2^32-1. Because it can be loaded
 *from a LL_SIMD which is "reversed" it can be filled in
 *machine oriented format, then flipped into standard format
 *before being analysed.
 */
class HistogramU32 { public:
    void setup(int sLinearSz);
    HistogramU32();
    HistogramU32(int sLinearSz);
    ~HistogramU32();
    void reset();
    void increment(int binNo) { bins[binNo]+=1; }
    void addToBin(int binNo,uint32_t value){ bins[binNo]+=value; }
    void writeASCII(const char* const fileName,int fileIdx,int noRows,int noCols,uint32_t normaliser);
    void flipFormat(bool intoHighLevelFormat);
    uint32_t *bins;
    int linearSz;
    bool machineFormat;
};

//void
//incorporateBooleanSIMDIntoHistogram(HistogramU32 &histo,int basePos,LL_SIMD v);

inline
void
incorporateBooleanSIMDIntoHistogram(HistogramU32 &histo,int basePos,LL_SIMD v)
{
    __m128i* b=(__m128i*)histo.bins;
    b[basePos/4]=_mm_add_epi32(b[basePos/4],_mm_cvtps_epi32(_mm_and_ps(v,_mm_set1_ps(1.0f))));
}

}//AzDiscreteSim

#endif /*__HISTOGRAM_H__*/
