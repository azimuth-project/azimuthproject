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
#include "simulationElts.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <xmmintrin.h>
#include <iostream>
#include <stdarg.h>

namespace AzDiscreteSim {

//---------------------------- RandomTemporary -----------------------
void
RandomTemporary::basicSetup(const char* const sName,SysDescription &sys,RVSIMDStreamBase *sStream)
{
    stream=sStream;
    multiplier=0;
    addend=0;
    name=strdup(sName);
    sys.recordRandomVariate(this);
}

RandomTemporary::RandomTemporary(const char* const sName,SysDescription &sys,RVSIMDStreamBase *sStream)
{
    basicSetup(sName,sys,sStream);
    stream->setPostprocessTypes(RVSIMDStreamBase::NONE,RVSIMDStreamBase::NONE);

}

RandomTemporary::RandomTemporary(const char* const sName,SysDescription &sys,RVSIMDStreamBase *sStream,
                Constant &m)
{
    basicSetup(sName,sys,sStream);
    stream->setPostprocessTypes(RVSIMDStreamBase::MULTIPLY,RVSIMDStreamBase::NONE);
    stream->setPostprocessParams(0,m.current(),SIMD(0).simd);
}

RandomTemporary::RandomTemporary(const char* const sName,SysDescription &sys,RVSIMDStreamBase *sStream,
                    Constant &m,Constant &a)
{
    basicSetup(sName,sys,sStream);
    stream->setPostprocessTypes(RVSIMDStreamBase::MULTIPLY,RVSIMDStreamBase::NONE);
    stream->setPostprocessParams(0,m.current(),a.current());
}

RandomTemporary::RandomTemporary(const char* const sName,SysDescription &sys,RVSIMDStreamBase *sStream,
                Parameter &m)
{
    basicSetup(sName,sys,sStream);
    stream->setPostprocessTypes(RVSIMDStreamBase::NONE,RVSIMDStreamBase::MULTIPLY);
    multiplier=&m;
}

RandomTemporary::RandomTemporary(const char* const sName,SysDescription &sys,RVSIMDStreamBase *sStream,
                    Parameter &m,Parameter &a)
{
    basicSetup(sName,sys,sStream);
    stream->setPostprocessTypes(RVSIMDStreamBase::NONE,RVSIMDStreamBase::MULTIPLY);
    multiplier=&m;
    addend=&a;
}

    RandomTemporary::~RandomTemporary()
    {
        free(name);
    }

//TODO URGENT: figure way to make this compile time
void
RandomTemporary::activeRewind()
{
    //fprintf(stderr,"%s AC_REW this %p %p %p\n",name,this,multiplier,addend);fflush(stdout);
    if(multiplier!=0){
        LL_SIMD m=*multiplier;
        LL_SIMD a=m;//dummy initialisation
        LL_SIMD cmp=_mm_cmpeq_ps(m,stream->multiplier[1]);
        SIMD M(m);
        //fprintf(stderr,"%s AC_REW values %g %g %g %g\n",name,M.scalars[0],M.scalars[1],M.scalars[2],M.scalars[3]);fflush(stdout);
        if(addend!=0){
            a=*addend;
            cmp=_mm_and_ps(cmp,_mm_cmpeq_ps(a,stream->addend[1]));
        }
        if(_mm_movemask_ps(cmp)!=15){//something's changed
            stream->setPostprocessParams(1,m,a);
            stream->activeRewind();
        }
    }
    stream->simpleRewind();
    //fprintf(stderr,"%s R %g %g %g %g\n",name,stream->stream2->elts[0].scalars[0],stream->stream2->elts[0].scalars[1],
    //        stream->stream2->elts[0].scalars[2],stream->stream2->elts[0].scalars[3]);
}

//-------------------------- LinSpace ----------------------------

LinSpace::LinSpace(float lo,float hi,int sNoDivs)
{
    data[0]=lo;
    data[1]=hi;
    noDivs=sNoDivs;
    if(noDivs%SIMD_SZ!=0){
        debugAbort("No divisions must be divisible by 4");
    }
    data[2]=(hi-lo)/(noDivs-1);
    values=new float[noDivs];
    int i;
    for(i=0;i<noDivs;++i){
        values[i]=data[0]+i*data[2];
    }
}

LinSpace::~LinSpace()
{
    delete[] values;
}

LinSpace*
linspace(float lo,float hi,int sNoDivs)
{
    return new LinSpace(lo,hi,sNoDivs);
}

//-------------------------------- FullTemporary -------------------------------

FullTemporary::FullTemporary()
{
    arr=0;
}

FullTemporary::FullTemporary(int noElts) {
    setStorage(noElts);
}

FullTemporary::FullTemporary(SIMDArray *sArr) {
    arr=sArr;
    resetCurrent();
}

FullTemporary::FullTemporary(SysDescription &sysDescription,float initialValue)
{
    setStorage(sysDescription.linearDimension);
    sysDescription.recordConstInitialisedVariable(this,initialValue);
}

FullTemporary::~FullTemporary()
{
    if(arr!=0){
        delete arr;
    }
}

void
FullTemporary::setStorage(int linearSize)
{
    arr=new SIMDArray(linearSize);
    resetCurrent();
}

void
FullTemporary::initialiseConstant(float v)
{
    SIMD vSimd(_mm_set1_ps(v));
    int i=0;
    do{
        arr->elts[i]=vSimd;
    }while(++i<arr->linearSIMDSteps);
}

//------------------------ SysDescription --------------------------

void
SysDescription::cartProdParameters(int sNoParameters,...)
{
    varsNeedingAdvancer.clear();
    noParameters=sNoParameters;
    linearDimension=1;
    va_list ap;
    va_start(ap,sNoParameters);
    int i;
    for(i=0;i<noParameters;++i){
        Parameter* param=va_arg(ap,Parameter*);
        recordVariable((FullTemporary*)param);
        linearDimension*=param->metadata.noDivs;
    }
    va_end(ap);
    simdSteps=linearDimension/SIMD_SZ;
    //fprintf(stdout,"SSTEMPS %u %u\n",linearDimension,simdSteps);fflush(stdout);
    // allocate space and fill-in parameters
    int shiftSoFar=0;
    for(i=0;i<noParameters;++i){
        // allocate space
        varsNeedingAdvancer[i]->setStorage(linearDimension);
        Parameter* paramPtr=(Parameter*)varsNeedingAdvancer[i];
        LinSpace &meta=paramPtr->metadata;
        SIMDArray *arr=varsNeedingAdvancer[i]->arr;
        // create explicit index slicing representation
        int size=meta.noDivs;
        paramPtr->mask=size-1;
        paramPtr->shift=shiftSoFar;
        shiftSoFar+=log2(size);
        // fill-in parameters at appropriate positions
        int j;
        for(j=0;j<linearDimension;++j){
            int idx=(j>>paramPtr->shift) & paramPtr->mask;
            arr->setScalar(j,meta.values[idx]);
        }
    }
}

void
SysDescription::randVarsActiveRewind()
{
    for(std::vector<RandomTemporary*>::iterator it=randomVars.begin();
        it!=randomVars.end();++it){
        (*it)->activeRewind();
    }
}

void
SysDescription::resetWork()
{
    workUnits=simdSteps-1;
    for(std::vector<FullTemporary*>::iterator it=varsNeedingAdvancer.begin();
        it!=varsNeedingAdvancer.end();++it){
        (*it)->resetCurrent();
    }
    randVarsActiveRewind();
}

bool
SysDescription::stillWork()
{
    --workUnits;
    if(workUnits<0){
        resetWork();
        return false;
    }else{
        for(std::vector<FullTemporary*>::iterator it=varsNeedingAdvancer.begin();
            it!=varsNeedingAdvancer.end();++it){
            (*it)->advanceCurrent();
        }
        randVarsActiveRewind();
        return true;
    }
}

void
SysDescription::recordRandomVariate(RandomTemporary *var)
{
    randomVars.push_back(var);
}

void
SysDescription::recordVariable(FullTemporary* var)
{
    varsNeedingAdvancer.push_back(var);
}

void
SysDescription::recordConstInitialisedVariable(FullTemporary* var,float initialValue)
{
    varsNeedingAdvancer.push_back(var);
    varsWithInitialisations.push_back(InitRec(var,initialValue));
}

void
SysDescription::initialiseVarsForRun()
{
    for(std::vector<InitRec>::iterator it=varsWithInitialisations.begin();
        it!=varsWithInitialisations.end();++it){
        it->array->initialiseConstant(it->value);
    }
    for(std::vector<RandomTemporary*>::iterator it1=randomVars.begin();
        it1!=randomVars.end();++it1){
        //fprintf(stdout,"Resetting %p\n",*it1);fflush(stdout);
        (*it1)->reset();
    }
    resetWork();
}

}//AzDiscreteSim
