#include "simd.h"
#include "histogram.h"
#include "simulationElts.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <iostream>

using namespace AzFundamentals;
using namespace AzDiscreteSim;

void
simulateSystem(int noRuns,int noTimesteps,float rfLo,float rfHi,float ffLo,float ffHi,int paramStepsF,
               int paramStepsRA,float initialFoxes,float rabbitsToSustainFox,float maxSupportableRabbits)
{
    int noRows=1,noCols=paramStepsF*paramStepsF;
    SysDescription sysDescription;
    Parameter FF(ffLo,ffHi,paramStepsF);
    Parameter WF(ffLo,ffHi,paramStepsF);
    Constant RF(2.5*rabbitsToSustainFox);
    sysDescription.cartProdParameters(2,&FF,&WF);
    FF.arr->writeASCII("FF_%u",0,paramStepsF,paramStepsF);
    WF.arr->writeASCII("WF_%u",0,paramStepsF,paramStepsF);
    FullTemporary F0(sysDescription,initialFoxes/2);
    FullTemporary F1(sysDescription,0);
    FullTemporary W0(sysDescription,initialFoxes/2);
    FullTemporary W1(sysDescription,0);
    FullTemporary R0(sysDescription,3*initialFoxes*rabbitsToSustainFox);
    FullTemporary R1(sysDescription,0);
    Constant c(rabbitsToSustainFox);
    Constant capR(maxSupportableRabbits);
    Constant zero(0);
    Constant one(1);
    Constant two(2);

    UniformSIMDStream ustream1(randBufferUnifromU32,noTimesteps);
    UniformSIMDStream ustream2(randBufferUnifromU32,noTimesteps);
    UniformSIMDStream ustream3(randBufferUnifromU32,noTimesteps);
    UniformSIMDStream ustream4(randBufferUnifromU32,noTimesteps);

    RandomTemporary rabbitReprodU("rabbitReprodU",sysDescription,&ustream1,RF);
    RandomTemporary foxReprodU("foxReprodU",sysDescription,&ustream2,FF);
    RandomTemporary wolfReprodU("wolfReprodU",sysDescription,&ustream3,WF);
    RandomTemporary eatenFracU("eatenFracU",sysDescription,&ustream4);
    HistogramU32 *histograms[3];
    int j,i;
    for(j=0;j<3;++j){
        histograms[j]=new HistogramU32[noTimesteps];
        for(i=0;i<noTimesteps;++i){
            histograms[j][i].setup(noCols);
        }
    }
    int run;
    for(run=1;run<=noRuns;++run){
        //fprintf(stdout,"RUN %u\n",run);fflush(stdout);
        sysDescription.initialiseVarsForRun();
        if(run==1){
            F0.arr->writeASCII("F0_%u",0,noRows,noCols);
            F1.arr->writeASCII("F1_%u",0,noRows,noCols);
            W0.arr->writeASCII("W0_%u",0,noRows,noCols);
            W1.arr->writeASCII("W1_%u",0,noRows,noCols);
            R0.arr->writeASCII("R0_%u",0,noRows,noCols);
            R1.arr->writeASCII("R1_%u",0,noRows,noCols);
        }
        do{
            //fprintf(stdout,"TILE\n");fflush(stdout);
            LL_SIMD f0=F0,f1=F1,r0=R0,r1=R1,w0=W0,w1=W1;
            int step=0;
            do{
                //fprintf(stdout,"BEGSTEP %u\n",step);fflush(stdout);
                LL_SIMD totalF=f0+f1,totalW=w0+w1;
                LL_SIMD totalPredators=totalF+totalW;
                LL_SIMD e=clampAbove(c*totalPredators,r0+r1);//rabbits eaten this year. note e<=r0+r1
                //young rabbits/foxes that survive become mature rabbits/foxes
                r1=r0-clampBelow(e-r1,zero);
                LL_SIMD eOverCP=e/(c*totalPredators);
                f1=clampAbove(eOverCP*totalF,f0);
                w1=clampAbove(eOverCP*totalW,w0);
                //ensure once the population drops below 2.0 it definitely gets zeroed
                //r1=onlyIf(r1>two*/,r1);
                f1=onlyIf(f1>two,f1);
                w1=onlyIf(w1>two,w1);
                //now any surviving mature rabbits/foxes breed
                r0=clampAbove(r1/*rabbitReprodU*/,capR-r1);
                r0=onlyIf(r0>two,r0);
                f0=f1*foxReprodU.current();
                w0=w1*wolfReprodU.current();
                //fprintf(stdout,"ESTEP %u\n",step);fflush(stdout);
                LL_SIMD fOK=f0>zero,wOK=w0>zero;
                sysDescription.incorporateIntoHistogram(histograms[0][step],fOK/*two*/);
                sysDescription.incorporateIntoHistogram(histograms[1][step],wOK/*two*/);
                sysDescription.incorporateIntoHistogram(histograms[2][step],_mm_and_ps(fOK,wOK)/*two*/);
            }while(/*_mm_movemask_ps(f0>two) > 0 &&*/ ++step<noTimesteps);
#if 0
            F0.store(f0);
            F1.store(f1);
            R0.store(r0);
            R1.store(r1);
            W0.store(w0);
            W1.store(w1);
#endif
        }while(sysDescription.stillWork());
#if 0
        F0.arr->writeASCII("F0_%u",run,paramStepsF,paramStepsF);
        F1.arr->writeASCII("F1_%u",run,paramStepsF,paramStepsF);
        W0.arr->writeASCII("W0_%u",run,paramStepsF,paramStepsF);
        W1.arr->writeASCII("W1_%u",run,paramStepsF,paramStepsF);
        R0.arr->writeASCII("R0_%u",run,paramStepsF,paramStepsF);
        R1.arr->writeASCII("R1_%u",run,paramStepsF,paramStepsF);
#endif
        //fprintf(stdout,"ERUN %u\n",run);fflush(stdout);
        //TODO: do incorporate array of final array of values into accumulator
        if(0 && run%(1<<18)==0){
            sleep(120);
        }
    }
    //TODO: do some analysis
    for(i=0;i<noTimesteps;++i){
        for(j=0;j<3;++j){
            histograms[j][i].flipFormat(true);
        }
        histograms[0][i].writeASCII("H_%.2u",i,paramStepsF,paramStepsF/*noRows,noCols*/,noRuns);
        histograms[1][i].writeASCII("J_%.2u",i,paramStepsF,paramStepsF/*noRows,noCols*/,noRuns);
        histograms[2][i].writeASCII("K_%.2u",i,paramStepsF,paramStepsF/*noRows,noCols*/,noRuns);
    }
    for(j=0;j<2;++j){
        delete[] histograms[j];
    }
}

int
main(int argc,char* argv[])
{
    //srand(time(NULL));
    randBufferUnifromU32=new RandBufferUniformU32(512,0);
    int noRuns=1;
    int noTimesteps=1;//20;
    sscanf(argv[1],"%u",&noRuns);
    noRuns=1<<noRuns;
    sscanf(argv[2],"%u",&noTimesteps);
    int paramStepsF=32;
    int paramStepsR=4;
    float initialFoxes=100;
    float rabbitsToSustainFox=200;
    float maxSupportableRabbits=initialFoxes*rabbitsToSustainFox*5;
    float rfLo=0;//.125*rabbitsToSustainFox;
    float rfHi=10.0*rabbitsToSustainFox;
    float ffLo=1.7;
    float ffHi=2.8;

    time_t start=time(0);
    simulateSystem(noRuns,noTimesteps,rfLo,rfHi,ffLo,ffHi,paramStepsF,
                   paramStepsR,initialFoxes,rabbitsToSustainFox, maxSupportableRabbits);
    time_t end=time(0);

    fprintf(stdout,"TOOK %llu seconds\n",(long long)(end-start));fflush(stdout);
    delete randBufferUnifromU32;
    return 0;
}
