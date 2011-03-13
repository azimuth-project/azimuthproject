#ifndef __PARAMETER_BLOCK_H__
#define __PARAMETER_BLOCK_H__

// This program is free software; you can redistribute it and/or modify
// it under the terms of the Lesser GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or (at
// your option) any later version.
//  
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.

#include "simd.h"
#include "randomVariables.h"
#include "histogram.h"
#include "utilities.h"
#include <vector>
#include <stdint.h>

namespace AzDiscreteSim {

using namespace AzFundamentals;

/*Note all these things unfortunately NEED to be in the
 *header file so they can be aggressively inlined
 */

//forward declaration of system structure
class SysDescription;

/**
 *Combined symbolic and explicit representation of a linear spacing
 *(similar to but not same as MATLAB's linspace)
 */
struct LinSpace {
    LinSpace(float lo,float hi,int sNoDivs);
    ~LinSpace();
    float data[3];
    int noDivs;
    float *values;
};

LinSpace*
linspace(float lo,float hi,int sNoDivs);

/*FullTemporary, Parameter, Constant and RandomTemporary
 *provide the "Temporary" interface, namely a method
 *       LL_SIMD current() const
 *but since we're doing everything with templates the methods are inlined
 *at compile time, so there's no need for an actual inherited base clase
 */

/**
 *class that simply wraps a constant into a "Temporary" wrapper (storing
 *it as an LL_SIMD for minor efficiency simplification)
 */
class Constant { private: //prevent implicit usage
    Constant operator=(const Constant &);
public:
    Constant(float v) { value=_mm_set1_ps(v); }
    ~Constant() { /*do nothing*/ }
    LL_SIMD current() const { return value; }
    LL_SIMD value;
};

/**
 *class that simply wraps an updatable array into a "Temporary" wrapper
 */
class FullTemporary { private: //prevent implicit usage
    FullTemporary(FullTemporary &);
    FullTemporary operator=(const FullTemporary &);
public:
    FullTemporary();
    FullTemporary(int noElts);
    FullTemporary(SIMDArray *sArr);
    FullTemporary(SysDescription &sysDescription,float initialValue);
    ~FullTemporary();
    operator LL_SIMD() const { return current(); }
    void setStorage(int noElts);
    void initialiseConstant(float v);
    void resetCurrent() { currentPtr=arr->elts; }
    void advanceCurrent() { ++currentPtr; }
    LL_SIMD current() const { return currentPtr->simd; }
    void store(SIMD v) { *currentPtr=v; }
    void set(int i,SIMD v) { arr->elts[i]=v.simd; }
    //data members
    SIMDArray *arr;
    SIMD *currentPtr;//nasty pointer that gives instant access
};

/**
 *A FullTemporary with some metadata the system controller can manipulate to
 *"march through" a search hyper-rectangle
 */
class Parameter : public FullTemporary { private: //prevent implicit usage
    Parameter();
    Parameter operator=(const Parameter &);
public:
    Parameter(float lo,float hi,int sNoDivs) : metadata(lo,hi,sNoDivs) {/*nothing*/  }
    ~Parameter() { /*do nothing*/ }
    LinSpace metadata; //describes the set of values for this parameter
    int shift,mask;
};

/**
 *Wrapper around a random stream providing a "random variable", optionally
 *scaled by either a Constant or a Parameter
 */
class RandomTemporary { private: //prevent implicit usage
    RandomTemporary();
    RandomTemporary operator=(const RandomTemporary &);
public:
    void basicSetup(const char* const sName,SysDescription &sys,RVSIMDStreamBase *sStream);
    RandomTemporary(const char* const sName,SysDescription &sys,RVSIMDStreamBase *sStream);
    RandomTemporary(const char* const sName,SysDescription &sys,RVSIMDStreamBase *sStream,
                    Constant &m);
    RandomTemporary(const char* const sName,SysDescription &sys,RVSIMDStreamBase *sStream,
                    Constant &m,Constant &a);
    RandomTemporary(const char* const sName,SysDescription &sys,RVSIMDStreamBase *sStream,
                    Parameter &m);
    RandomTemporary(const char* const sName,SysDescription &sys,RVSIMDStreamBase *sStream,
                    Parameter &m,Parameter &a);
    ~RandomTemporary();
    void reset() { stream->reset(); }
    void activeRewind();
    LL_SIMD current() const { return stream->current().simd; }
    RVSIMDStreamBase *stream;
    Parameter *multiplier,*addend;
    char* name;
};

/*The RNG object isn't part of this because the complete program may want
 *random variables at other points as well
 */
/**
 *NOTE: deliberately DOESN'T contain references to the random streams
 *used so that they can be manipulated independently if desired
 */
class SysDescription { public:
    struct InitRec {
        FullTemporary* array;
        float value;
        InitRec(FullTemporary* sArray,float sValue) {
            array=sArray;
            value=sValue;
        }
    };
    void cartProdParameters(int sNoParameters,...);
    void recordVariable(FullTemporary* var);
    void recordConstInitialisedVariable(FullTemporary* var,float initialValue);
    void recordRandomVariate(RandomTemporary *var);
    void randVarsActiveRewind();
    void initialiseVarsForRun();
    void resetWork();
    bool stillWork();
    void incorporateIntoHistogram(HistogramU32 &histo,LL_SIMD v) {
        incorporateBooleanSIMDIntoHistogram(histo,SIMD_SZ*workUnits,v);
    }
    int simdSteps,workUnits;
    int noParameters;
    int linearDimension;
    std::vector<RandomTemporary*> randomVars;
    std::vector<FullTemporary*> varsNeedingAdvancer;
    std::vector<InitRec> varsWithInitialisations;
};

//---------------- nasty macros to avoid creating many repetitive function overloads ----------
#define MAKE_OPERATOR(opName,op) \
template<class T,class U> inline LL_SIMD operator opName(const T &a,const U &b) { return LL_SIMD(op(a.current(),b.current())); } \
template<class T>  inline LL_SIMD operator opName(const T &a,LL_SIMD b) { return LL_SIMD(op(a.current(),b)); } \
template<class T>  inline LL_SIMD operator opName(LL_SIMD a,const T &b) { return LL_SIMD(op(a,b.current())); }
//inline LL_SIMD operator opName(LL_SIMD a,LL_SIMD b) { return LL_SIMD(op(a.simd,b.simd)); }


#define MAKE_FN(fnName,op) \
template<class T,class U>  inline LL_SIMD fnName(const T &a,const U &b) { return LL_SIMD(op(a.current(),b.current())); } \
template<class T>  inline LL_SIMD fnName(const T &a,LL_SIMD b) { return LL_SIMD(op(a.current(),b)); } \
template<class T>  inline LL_SIMD fnName(LL_SIMD a,const T &b) { return LL_SIMD(op(a,b.current())); } \
inline LL_SIMD fnName(LL_SIMD a,LL_SIMD b) { return LL_SIMD(op(a,b)); }

//--------------------- provide overloads for common SIMD operators ------------------------------
MAKE_OPERATOR(+,_mm_add_ps)
MAKE_OPERATOR(-,_mm_sub_ps)
MAKE_OPERATOR(*,_mm_mul_ps)
MAKE_OPERATOR(/,_mm_div_ps)
MAKE_OPERATOR(<,_mm_cmplt_ps)
MAKE_OPERATOR(>,_mm_cmpgt_ps)
MAKE_FN(add,_mm_add_ps)
MAKE_FN(subtract,_mm_sub_ps)
MAKE_FN(multiply,_mm_mul_ps)
MAKE_FN(divide,_mm_div_ps)
MAKE_FN(less,_mm_cmplt_ps)
MAKE_FN(greater,_mm_cmpgt_ps)
MAKE_FN(min,_mm_min_ps)
MAKE_FN(max,_mm_max_ps)
MAKE_FN(clampAbove,_mm_min_ps)
MAKE_FN(clampBelow,_mm_max_ps)
MAKE_FN(onlyIf,_mm_and_ps)
MAKE_FN(andV,_mm_and_ps)
MAKE_FN(andNotV,_mm_andnot_ps)
MAKE_FN(orV,_mm_or_ps)

//--------------------------------- useful multi-step operators ----------------------------------------

template<typename T,typename U>
inline
LL_SIMD
bernoulli(const T &rv,const U &threshs)
{
    return onlyIf(less(rv,threshs),SIMD(1).simd);
}

template<typename T,typename U,typename V>
inline
LL_SIMD
ifelse(const T &pred,const U &trueV,const V &falseV)
{
    return orV(andV(trueV,pred),andNotV(falseV,pred));
}

}//AzDiscreteSim

#endif /*__PARAMETER_BLOCK_H__*/
