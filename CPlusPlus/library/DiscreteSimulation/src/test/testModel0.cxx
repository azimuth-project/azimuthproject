#include "simd.h"
#include "histogram.h"
#include "simulationElts.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <iostream>

#define PARAMETER_RF 0

using namespace AzFundamentals;
using namespace AzDiscreteSim;

void
simulateSystem(int noRuns,int noTimesteps,float rfLo,float rfHi,float ffLo,float ffHi,int paramStepsF,
               int paramStepsR,float initialFoxes,float rabbitsToSustainFox,float maxSupportableRabbits)
{
    int noRows=1,noCols=paramStepsF*paramStepsR;
    SysDescription sysDescription;
    Parameter FF(ffLo,ffHi,paramStepsF);
#if PARAMETER_RF
    Parameter RF(rfLo,rfHi,paramStepsR);
    sysDescription.cartProdParameters(2,&FF,&RF);
    RF.arr->writeASCII("RF_%u",0,noRows,noCols);
#else
    Constant RF(2.5*rabbitsToSustainFox);
    sysDescription.cartProdParameters(1,&FF);
#endif
    FF.arr->writeASCII("FF_%u",0,noRows,noCols);
    FullTemporary F0(sysDescription,initialFoxes);
    FullTemporary F1(sysDescription,0);
    FullTemporary R0(sysDescription,2*initialFoxes*rabbitsToSustainFox);
    FullTemporary R1(sysDescription,0);
    Constant c(rabbitsToSustainFox);
    Constant capR(maxSupportableRabbits);
    Constant zero(0);
    Constant two(2);

    UniformSIMDStream ustream1(randBufferUnifromU32,noTimesteps);
    UniformSIMDStream ustream2(randBufferUnifromU32,noTimesteps);

    RandomTemporary rabbitReprodU("rabbitReprodU",sysDescription,&ustream1,RF);
    RandomTemporary foxReprodU("foxReprodU",sysDescription,&ustream2,FF);
    HistogramU32 *histograms=new HistogramU32[noTimesteps];
    int i;
    for(i=0;i<noTimesteps;++i){
        histograms[i].setup(noCols);
    }
    int run;
    for(run=1;run<=noRuns;++run){
        //fprintf(stdout,"RUN %u\n",run);fflush(stdout);
        sysDescription.initialiseVarsForRun();
        do{
            //fprintf(stdout,"TILE\n");fflush(stdout);
            LL_SIMD f0=F0,f1=F1,r0=R0,r1=R1;
            int step=0;
            do{
                //fprintf(stdout,"BEGSTEP %u\n",step);fflush(stdout);
                LL_SIMD e=clampAbove(c*(f0+f1),r0+r1);//rabbits eaten this year. note e<=r0+r1
                //young rabbits/foxes that survive become mature rabbits/foxes
                r1=r0-clampBelow(e-r1,zero);
                f1=clampAbove(e/c,f0);
                //ensure once the population drops below 2.0 it definitely gets zeroed
                //r1=onlyIf(r1>two*/,r1);
                f1=onlyIf(f1>two,f1);
                //now any surviving mature rabbits/foxes breed
                r0=clampAbove(r1*rabbitReprodU,capR-r1);
                r0=onlyIf(r0>two,r0);
                f0=f1*foxReprodU;
                //fprintf(stdout,"ESTEP %u\n",step);fflush(stdout);
                sysDescription.incorporateIntoHistogram(histograms[step],f0>two);
            }while(_mm_movemask_ps(f0>two) > 0 && ++step<noTimesteps);
            F0.store(f0);
            F1.store(f1);
            R0.store(r0);
            R1.store(r1);
        }while(sysDescription.stillWork());
#if 0
        F0.arr->writeASCII("F0_%u",run,noRows,noCols);
        F1.arr->writeASCII("F1_%u",run,noRows,noCols);
        R0.arr->writeASCII("R0_%u",run,noRows,noCols);
        R1.arr->writeASCII("R1_%u",run,noRows,noCols);
#endif
        //fprintf(stdout,"ERUN %u\n",run);fflush(stdout);
        //TODO: do incorporate array of final array of values into accumulator
        if(0 && run%(1<<18)==0){
            sleep(120);
        }
    }
    //TODO: do some analysis
    for(i=0;i<noTimesteps;++i){
        histograms[i].flipFormat(true);
        histograms[i].writeASCII("H_%.2u",i,paramStepsR,paramStepsF/*noRows,noCols*/,noRuns);
    }
    delete[] histograms;
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
    int paramStepsF=16;
    int paramStepsR=4;
    float initialFoxes=100;
    float rabbitsToSustainFox=200;
    float maxSupportableRabbits=initialFoxes*rabbitsToSustainFox*5;
    float rfLo=0;//.125*rabbitsToSustainFox;
    float rfHi=10.0*rabbitsToSustainFox;
    float ffLo=1.75;
    float ffHi=2.75;

    time_t start=time(0);
    simulateSystem(noRuns,noTimesteps,rfLo,rfHi,ffLo,ffHi,paramStepsF,
                   paramStepsR,initialFoxes,rabbitsToSustainFox, maxSupportableRabbits);
    time_t end=time(0);

    fprintf(stdout,"TOOK %llu seconds\n",(long long)(end-start));fflush(stdout);
    delete randBufferUnifromU32;
    return 0;
}
