#include "simd.h"

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
