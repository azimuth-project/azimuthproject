#include "utilities.h"
#include <stdlib.h>
#include <stdio.h>

void
debugAbort(const char* const msg)
{
    fprintf(stderr,msg);
    fflush(stderr);
    abort();
}

//find better implementation
int
log2(int i)
{
    switch(i) {
    case 4:
        return 2;
    case 8:
        return 3;
    case 16:
        return 4;
    case 32:
        return 5;
    case 64:
        return 6;
    case 128:
        return 7;
    case 256:
        return 8;
    }
}
