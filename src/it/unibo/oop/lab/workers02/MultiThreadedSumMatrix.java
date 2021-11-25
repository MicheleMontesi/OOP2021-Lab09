package it.unibo.oop.lab.workers02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix{
    
    private final int nThreads;

    public MultiThreadedSumMatrix(final int nThreads) {
        super();
        this.nThreads = nThreads;
    }
    
    private static class Worker extends Thread {
        private final double[][] matrix;
        private final int startPos;
        private final int nElem;
        private double res;
        
        Worker(final double[][] matrix, final int startPos, final int nElem) {
            super();
            this.matrix = Arrays.copyOf(matrix, matrix.length);
            this.startPos = startPos;
            this.nElem = nElem;
        }
        
        public void run() {
            System.out.println("Working from position " + this.startPos + " to position " +
                                (this.startPos + this.nElem - 1));
            for (int i = startPos; i < matrix.length && i < (startPos + nElem); i++) {
                for (final double d : this.matrix[i]) {
                    res += d;
                }
            }
        }
    }

    @Override
    public double sum(final double[][] matrix) {
        final int size = matrix.length % nThreads + matrix.length / nThreads;
        final List<Worker> workers= new ArrayList<>(nThreads);
        /*
         * create the workers
         */
        for (int start = 0; start < matrix.length; start += size) {
            workers.add(new Worker(matrix, start, size));
        }
        /*
         * start them
         */
        for (final Worker w : workers) {
            w.start();
        }
        /*
         * wait for every thread to finish
         */
        double sum = 0;
        for (final Worker w : workers) {
            try {
                w.join();
                sum += w.res;
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            
        }
        return sum;
    }
    
    

}
