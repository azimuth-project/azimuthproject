#ifndef __RANDOM_VARIABLES_H__
#define __RANDOM_VARIABLES_H__

#include "simd.h"
#include <stdint.h>

namespace AzDiscreteSim {

    using namespace AzFundamentals;

/**
 *Singleton object class just for interacting with 3-rd party SFMT
 *random number generator code
 */
    class RandBufferUniformU32 { private:
        RandBufferUniformU32();
    public:
        RandBufferUniformU32(int sCapacity,uint32_t seed) { setup(sCapacity,seed); } 
        ~RandBufferUniformU32();
        void setup(int sCapacity,uint32_t seed);
        void fillBuffer();
        LL_SIMD getRandomVectorAsLL_SIMD() {
            static const __m128i U32_MSB_WIPE=_mm_set1_epi32(0x7FFFFFFF);
            if(pos==capacity){
                fillBuffer();
            }
            return _mm_cvtepi32_ps(_mm_and_si128(buffer[pos++],U32_MSB_WIPE));
        }
        static const uint32_t MAX_RAND=0x80000000UL;
        __m128i *buffer;
        int capacity;
        int pos;
    };

/**
 *A global instance of RandBufferUniformU32 providing global access
 */
    extern RandBufferUniformU32 *randBufferUnifromU32;

/**
 *Transformation method for U[0,1) random variates
 */
    struct UniformRVCreator {
        static LL_SIMD transform(RandBufferUniformU32* uniformGenerator) {
            LL_SIMD s=(uniformGenerator->getRandomVectorAsLL_SIMD()
                       /_mm_set1_ps(RandBufferUniformU32::MAX_RAND));
            return s;
        }
    };

//TODO AMAZINGLY URGENT: this requires all 4 random variates to fall within unit circle, otherwise
//completely retries. need to figure out a relatively simple way to do rejection on a point by point
//basis
/**
 *Common code for methods using a pair of variates uniformly of unit disc
 */
    struct PolarRVCreator {
        static void transform(RandBufferUniformU32* uniformGenerator,LL_SIMD &U,LL_SIMD &V,LL_SIMD &R_SQR) {
            static const LL_SIMD one=_mm_set1_ps(1.0f);
            static const LL_SIMD scaling=_mm_set1_ps(2.0f*float(RandBufferUniformU32::MAX_RAND));
            do{
                LL_SIMD u=uniformGenerator->getRandomVectorAsLL_SIMD();
                LL_SIMD v=uniformGenerator->getRandomVectorAsLL_SIMD();        
                U=u/scaling;
                V=v/scaling;
                R_SQR=U*U+V*V;
            }while(_mm_movemask_ps(_mm_cmpgt_ps(R_SQR,one))!=0);//repeat if any outside circle
        }
    };

//TODO: complete code
/**
 *Transformation method for N[0,1) random variates
 */
    struct NormalRVCreator : public PolarRVCreator {
        static LL_SIMD transform(RandBufferUniformU32* uniformGenerator) {
            return _mm_set1_ps(0);
        }
    };

/**
 *Transformation method for Cauchy (gamma=1) random variates
 */
    struct CauchyRVCreator : public PolarRVCreator {
        static LL_SIMD transform(RandBufferUniformU32* uniformGenerator) {
            static const LL_SIMD one=_mm_set1_ps(1.0f);
            LL_SIMD U,V,W;
            PolarRVCreator::transform(uniformGenerator,U,V,W);
            LL_SIMD W_SQ=W*W;
            LL_SIMD finalRV=U*_mm_sqrt_ps((one-W_SQ)/(W*W_SQ));
            return finalRV;
        }
    };

/*NOTE: no bounds error checking
 *
 */
/*NOTE:
 *Three levels: 1. basic uniform variate generator
 *2. conversion to variate from other distribution (eg, normal) -- may be identity
 *3. affine scaling of this variate -- various parts may be identity
 */
/**
 *Functionality of random streams indpendent of distribution type of variate.
 */
    class RVSIMDStreamBase { public:
        enum Type { NONE, MULTIPLY, AFFINE };
        RandBufferUniformU32 *uniformGenerator;
        SIMDArray stream;
        SIMDArray *stream2;
        int capacity,idx;
        LL_SIMD multiplier[2],addend[2];
        //type[0] is preprocessing type for reset()
        //type[1] is preprocessing type for beginAgainOnly()
        Type type[2];
        RVSIMDStreamBase(RandBufferUniformU32 *sUniformGenerator,int sCapacity);
        virtual ~RVSIMDStreamBase();
        SIMD current() { return stream2->elts[++idx]; }
        void setPostprocessTypes(RVSIMDStreamBase::Type sType0,RVSIMDStreamBase::Type sType1);
        void setPostprocessParams(int stage,LL_SIMD sMultiplier,LL_SIMD sAddend) {
            multiplier[stage]=sMultiplier;
            addend[stage]=sAddend;
        }
        virtual void reset()=0;
        void simpleRewind() { idx=0; }
        void activeRewind();
    };

/*Template class which implements "transform variates to distribution" method.
 *Note the code from the Transform class should be inlined at compile time.
 */

    template<typename Transform>
        class RVSIMDStream : public RVSIMDStreamBase {
    public:
        RVSIMDStream(RandBufferUniformU32 *sUniformGenerator,int sCapacity);
        ~RVSIMDStream();
        void reset();
    };

    typedef RVSIMDStream<UniformRVCreator> UniformSIMDStream;
    typedef RVSIMDStream<NormalRVCreator> NormalSIMDStream;
    typedef RVSIMDStream<CauchyRVCreator> CauchySIMDStream;

}//AzDiscreteSim

#endif /*__RANDOM_VARIABLES_H__*/
