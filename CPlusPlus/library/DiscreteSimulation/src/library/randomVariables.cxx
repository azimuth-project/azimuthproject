// This program is free software; you can redistribute it and/or modify
// it under the terms of the Lesser GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or (at
// your option) any later version.
//  
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.

#include "randomVariables.h"
#include "utilities.h"
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <stdlib.h>

namespace AzDiscreteSim {

//------------------- glue code interacting with SFMT ------------------------
#define DHAVE_SSE2 1
#define MEXP 19937
extern "C" {
#include "SFMT.c"
}

RandBufferUniformU32::~RandBufferUniformU32()
{
    free(buffer);
}

/**
 *initialise both buffer and the SFMT code.
 *Calling with seed=0 requests initialisation from system random number pool
 */
void
RandBufferUniformU32::setup(int sCapacity,uint32_t seed)
{
    if(seed==0){//signifies getting entropy from system pool
        FILE* urandom=fopen("/dev/urandom","rb");
        if(urandom) {
            uint32_t seedArray[4];
            size_t success;
            success=(fread(&seedArray,sizeof(uint32_t),4,urandom)==1);
            init_by_array(seedArray,4);
        }else{
            debugAbort("Couldn't open /dev/urandom\n");
        }
        fclose(urandom);
    }else{
        init_gen_rand(seed);
    }
    capacity=sCapacity;
    bool ok=posix_memalign((void**)&buffer,16,capacity*sizeof(LL_SIMD));
    pos=capacity;//set to trigger fillBuffer() on first use
}

void
RandBufferUniformU32::fillBuffer()
{
    gen_rand_array((w128_t *)buffer,capacity);
    pos=0;
}

//Global object interfacing to SFMT code
RandBufferUniformU32 *randBufferUnifromU32=0;

//---------------------------- random streams code -----------------------------------

RVSIMDStreamBase::RVSIMDStreamBase(RandBufferUniformU32 *sUniformGenerator,
                                   int sCapacity) : stream(SIMD_SZ*(sCapacity+SIMD_SZ))
{
    uniformGenerator=sUniformGenerator;
    //we need to add 1 SIMD-word's worth of padding because using simd we might generate
    //up to SIMD_SZ-1 elements more than we want
    capacity=sCapacity+SIMD_SZ;
    type[0]=NONE;
    type[1]=NONE;
    int i;
    for(i=0;i<2;++i){//TODO: lookup NAN
        multiplier[i]=_mm_set1_ps(-2121231231.0);
        addend[i]=_mm_set1_ps(215241235.0);
    }
    //unless we're doing type[1] postprocessing stream2 just points at stream
    stream2=&stream;
}

RVSIMDStreamBase::~RVSIMDStreamBase()
{
    if(stream2!=&stream){
        delete stream2;
    }
}

void
RVSIMDStreamBase::setPostprocessTypes(RVSIMDStreamBase::Type sType0,RVSIMDStreamBase::Type sType1)
{
    type[0]=sType0;
    type[1]=sType1;
    if(type[1]!=NONE){ // we'll need buffer to post-process values into
        stream2=new SIMDArray(SIMD_SZ*capacity);
    }
}

/**
 *Move back to the beginning of this random stream. If this is a post-processed
 *stream also post-process it here (all at once).
 */
void
RVSIMDStreamBase::activeRewind()
{
    LL_SIMD m=multiplier[1],a=addend[1];
    int i=0;
    switch (type[1]){
    case NONE:
        //nothing to do
        break;
    case MULTIPLY:
        do{
            stream2->elts[i].simd=stream.elts[i].simd*m;
        }while(++i<capacity);
        break;
    case AFFINE:
        do{
            stream2->elts[i].simd=stream.elts[i].simd*m+a;
        }while(++i<capacity);
        break;
    }
}

template<class Transform>
RVSIMDStream<Transform>::RVSIMDStream(RandBufferUniformU32 *sUniformGenerator,
                                      int sCapacity) : RVSIMDStreamBase(sUniformGenerator,sCapacity)
{
    //nothing
}

template<class Transform>
RVSIMDStream<Transform>::~RVSIMDStream()
{
    //nothing
}

template<class Transform>
void
RVSIMDStream<Transform>::reset()
{
    LL_SIMD m=multiplier[0],a=addend[0];
    int i=0;
    switch(type[0]){
    case NONE:
        do{
            SIMD buffer=Transform::transform(uniformGenerator);
            for(int j=0;j<SIMD_SZ;++j){
                stream.elts[i].simd=_mm_set1_ps(buffer.scalars[j]);
                ++i;
            }
        }while(i<capacity-SIMD_SZ);
        break;
    case MULTIPLY:
        do{
            SIMD buffer=Transform::transform(uniformGenerator)*m;
            for(int j=0;j<SIMD_SZ;++j){
                stream.elts[i].simd=_mm_set1_ps(buffer.scalars[j]);
                ++i;
            }
        }while(i<capacity-SIMD_SZ);
        break;
    case AFFINE:
        do{
            SIMD buffer=Transform::transform(uniformGenerator)*m+a;
            for(int j=0;j<SIMD_SZ;++j){
                stream.elts[i].simd=_mm_set1_ps(buffer.scalars[j]);
                ++i;
            }
        }while(i<capacity-SIMD_SZ);
        break;
    }
#if 0
    for(i=0;i<capacity;++i){
        fprintf(stderr,"%u: %g %g %g %g\n",i,stream.elts[i].scalars[0],
                stream.elts[i].scalars[1],stream.elts[i].scalars[2],
                stream.elts[i].scalars[3]);
    }
#endif
    idx=0;
}

// request instantiations of classes
template class RVSIMDStream<UniformRVCreator>;
template class RVSIMDStream<NormalRVCreator>;
template class RVSIMDStream<CauchyRVCreator>;

}//AzDiscreteSim
