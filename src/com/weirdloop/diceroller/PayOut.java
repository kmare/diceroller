package com.weirdloop.diceroller;

/**
 * Created by Ioannis Panteleakis on 28/08/2022.
 * Relax Dice Simulation
 */
public class PayOut {
    private final boolean result;
    private double mean;
    private double variance;
    private double stdDeviation;

    public PayOut(boolean result, double mean, double variance, double stdDeviation) {
        this.result = result;
        this.mean = mean;
        this.variance = variance;
        this.stdDeviation = stdDeviation;
    }

    public boolean isResult() {
        return result;
    }

    public double getVariance() {
        return variance;
    }

    public double getStdDeviation() {
        return stdDeviation;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public void setVariance(double variance) {
        this.variance = variance;
    }

    public void setStdDeviation(double stdDeviation) {
        this.stdDeviation = stdDeviation;
    }

    @Override
    public String toString() {
        return "PayOut{" +
                "result=" + result +
                "\t mean=" + String.format("%.2f", mean) +
                "\t variance=" + String.format("%.2f", variance) +
                "\t stdDeviation=" + String.format("%.2f", stdDeviation) +
                '}' + '\n';
    }
}
