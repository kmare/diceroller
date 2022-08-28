package com.weirdloop.diceroller;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ioannis Panteleakis on 28/08/2022.
 * Relax Dice Simulation
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Relax Dice Calculator");

        // parse application arguments
        int gameType = -1;
        int numberOfGames = 1000;
        boolean toPlot = false;
        try {
            gameType = Integer.parseInt(args[0]);
            if (gameType != 0 && gameType != 1) {
                System.out.println("""
                        Please provide a game type:
                        0: game of 1 die with 4 rolls
                        1: game of 2 dice with 24 rolls""");
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println("""
                    No args provided!
                    0: game of 1 die with 4 rolls
                    1: game of 2 dice with 24 rolls""");
            System.exit(1);
        }

        if (args.length > 1) {
            numberOfGames = Integer.parseInt(args[1]);
        }

        if (args.length > 2) {
            toPlot = Boolean.parseBoolean(args[2]);
        }

        // run simulation
        Simulation simulation = new Simulation(numberOfGames, gameType);
        simulation.run();

        ArrayList<PayOut> payOuts = simulation.getPayOuts();
        simulation.printStats();

        // export the data to a json file
        simulation.saveToJson();

        // plot results
        if (toPlot) {
            List<Double> stdDeviation = payOuts.stream().map(PayOut::getStdDeviation).toList();
            Plotting plotting = new Plotting(stdDeviation);
            SwingUtilities.invokeLater(plotting::createAndShowGui);
        }

    }

}
