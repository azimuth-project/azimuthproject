CFLAGS= -msse -msse2 -O3 -g
#CFLAGS= -msse -msse2 -g
CC=g++

testModel0:testModel0.o simulationElts.o histogram.o randomVariables.o simd.o utilities.o simulationElts.h histogram.h randomVariables.h simd.h utilities.h
	${CC} ${CFLAGS} -o testModel0 testModel0.o simulationElts.o histogram.o randomVariables.o simd.o utilities.o

testModel1:testModel1.o simulationElts.o histogram.o randomVariables.o simd.o utilities.o simulationElts.h histogram.h randomVariables.h simd.h utilities.h
	${CC} ${CFLAGS} -o testModel1 testModel1.o simulationElts.o histogram.o randomVariables.o simd.o utilities.o

#clean:

testModel0.o:testModel0.cxx simulationElts.h histogram.h randomVariables.h simd.h utilities.h
	${CC} ${CFLAGS} -o testModel0.o -c testModel0.cxx

testModel1.o:testModel1.cxx simulationElts.h histogram.h randomVariables.h simd.h utilities.h
	${CC} ${CFLAGS} -o testModel1.o -c testModel1.cxx

simd.o:simd.cxx simulationElts.h histogram.h randomVariables.h simd.h utilities.h
	${CC} ${CFLAGS} -o simd.o -c simd.cxx

utilities.o:utilities.cxx simulationElts.h histogram.h randomVariables.h simd.h utilities.h
	${CC} ${CFLAGS} -o utilities.o -c utilities.cxx

histogram.o:histogram.cxx simulationElts.h histogram.h randomVariables.h simd.h utilities.h
	${CC} ${CFLAGS} -o histogram.o -c histogram.cxx

randomVariables.o:randomVariables.cxx simulationElts.h histogram.h randomVariables.h simd.h utilities.h
	${CC} ${CFLAGS} -o randomVariables.o -c randomVariables.cxx

simulationElts.o:simulationElts.cxx simulationElts.h histogram.h randomVariables.h simd.h utilities.h
	${CC} ${CFLAGS} -o simulationElts.o -c simulationElts.cxx
