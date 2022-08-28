package com.weirdloop.diceroller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Ioannis Panteleakis on 28/08/2022.
 * Relax Dice Simulation
 */
class SimulationTest {

    /*
    Initial game results (rounded):

    game 1 = win	    mean=1.00	 variance=0.00	 stdDeviation=0.00
    game 2 = win	    mean=1.00	 variance=0.00	 stdDeviation=0.00
    game 3 = loss	    mean=0.67	 variance=0.00	 stdDeviation=0.00
    game 4 = win	    mean=0.75	 variance=0.19	 stdDeviation=0.43
    game 5 = loss	    mean=0.60	 variance=0.00	 stdDeviation=0.00

    Current game expected results (rounded):

    game 6 = win	    mean=0.67	 variance=0.22	 stdDeviation=0.47
    ------------------------------------------------------------------
    Total player wins / losses: 4 / 2

     */

    Simulation simulation = new Simulation(6, 0);
    PayOut currentPayOut = new PayOut(true, 0, 0, 0);

    @BeforeEach
    void setUp() {
        simulation.setPayOuts(new ArrayList<>() {{
            add(new PayOut(true, 1.00f, 0.0f, 0.0f));
            add(new PayOut(true, 1.00f, 0.0f, 0.0f));
            add(new PayOut(false, 0.67f, 0.0f, 0.0f));
            add(new PayOut(true, 0.75f, 0.19f, 0.43f));
            add(new PayOut(false, 0.60f, 0.0f, 0.0f));
            add(currentPayOut);
        }});
        simulation.setPreviousGame(3, 5);
    }

    @Test
    void calculateMean() {
        double mean = simulation.calculateMean(currentPayOut);
        assertEquals(0.66666666667, mean, 0.01);
    }

    @Test
    void calculateVariance() {
        double mean = simulation.calculateMean(currentPayOut);
        double variance = simulation.calculateVariance(mean);
        assertEquals(0.22222222222, variance, 0.02);
    }

    @Test
    void calculateStandardDeviation() {
        double mean = simulation.calculateMean(currentPayOut);
        double variance = simulation.calculateVariance(mean);
        double res = Simulation.calculateStandardDeviation(variance);
        assertEquals(0.47140452079, res, 0.02);
    }
}