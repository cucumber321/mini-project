// P3Q2.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "omp.h"
#define NUM_THREADS 2
static long num_steps = 100000;
double step;

int main()
{
     int i, nthreads ;
     double pi, sum[NUM_THREADS];
     step = 1.0 / (double)num_steps;
     omp_set_num_threads(NUM_THREADS);
     double start_time = omp_get_wtime();
     #pragma omp parallel
     {
          int i, id, nthrds;
          double x;
          id = omp_get_thread_num();
          nthrds = omp_get_num_threads();
          if (id == 0) nthreads = nthrds;

          for (i = id, sum[id] = 0.0; i < num_steps; i=i+nthrds)
          {
               x = (i + 0.5)*step;
               sum[id] +=4.0 / (1.0 + x * x);
          }
     }
     double end_time = omp_get_wtime();
     for (i = 0, pi = 0.0; i < nthreads; i++) {
          pi += sum[i] * step;
     }
     printf("%f\n", pi);
     
     printf("Work took %f seconds\n", end_time-start_time);
     return 0;
}

