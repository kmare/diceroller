package com.weirdloop.diceroller;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;

/**
 * Created by Ioannis Panteleakis on 28/08/2022.
 * Relax Dice Simulation
 */
public class Simulation {

    private ArrayList<PayOut> payOuts;

    private int totalWins = 0;
    private final Random rnd;
    private final int numberOfGames;
    private final int gameType;

    private Instant runStartTime;
    private Instant runEndTime;

    public Simulation(int numberOfGames, int gameType) {
        this.numberOfGames = numberOfGames;
        this.gameType = gameType;

        this.rnd = new Random();
        this.payOuts = new ArrayList<>();
    }

    static class PreviousGame {
        static double meanSum = 0;
        static int numberOfGamesSoFar = 0;
    }

    public void setPreviousGame(double sum, int currentGameNumber) {
        PreviousGame.meanSum = sum;
        PreviousGame.numberOfGamesSoFar = currentGameNumber;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public int getNumberOfGames() {
        return numberOfGames;
    }

    public ArrayList<PayOut> getPayOuts() {
        return payOuts;
    }

    public void setPayOuts(ArrayList<PayOut> payOuts) {
        this.payOuts = payOuts;
    }


    public void run() {
        runStartTime = Instant.now();
        for (int game = 0; game < numberOfGames; game++) {
            boolean isWin = gameRolls();
            if (isWin)
                totalWins++;

            PayOut payOut = new PayOut(isWin, 0, 0, 0);
            calculateCurrentStats(payOut);

            payOuts.add(payOut);
        }
        runEndTime = Instant.now();
    }

    private boolean gameRolls() {
        int rollsPerGame = gameType == 0 ? 4 : 24;

        for (int roll = 0; roll < rollsPerGame; roll++) {
            int dice = gameType == 0 ? 1 : 2;
            boolean victory = true;
            while (dice != 0) {
                int rr = rnd.nextInt(6) + 1;
                boolean res = rr == 6;
                victory &= res;
                dice--;
            }
            if (victory)
                return true;
        }
        return false;
    }

    protected double calculateMean(PayOut currentPayout) {
        PreviousGame.meanSum += currentPayout.isResult() ? 1 : 0;
        PreviousGame.numberOfGamesSoFar++;

        return PreviousGame.meanSum / (double) (PreviousGame.numberOfGamesSoFar);
    }

    protected double calculateVariance(double mean) {
        double sum = 0;
        for (PayOut game : payOuts) {
            int res = game.isResult() ? 1 : 0;
            sum += Math.pow(res - mean, 2);
        }
        // we care only about calculating the variance after a win, add the last one to the variance formula
        sum += Math.pow(1 - mean, 2);

        return sum / (payOuts.size() + 1);
    }

    protected static double calculateStandardDeviation(double data) {
        return Math.sqrt(data);
    }

    private void calculateCurrentStats(PayOut payOut) {
        // "mean" can be cached, so save the value anyway (win/loss)
        double mean = calculateMean(payOut);
        payOut.setMean(mean);

        // store "variance" and "standard deviation" only for wins
        if (payOut.isResult()) {
            double variance = calculateVariance(mean);
            double stdDeviation = calculateStandardDeviation(variance);
            payOut.setVariance(variance);
            payOut.setStdDeviation(stdDeviation);
        }
    }

    public void printStats() {
        System.out.println("Simulation time: " + this.getElapsedTime() + ".");
        System.out.println("Latest payout stats:");
        System.out.println(payOuts.get(payOuts.size() - 1));
        System.out.println("Player wins: " + getTotalWins() +
                "\nPlayer losses: " + (getNumberOfGames() - getTotalWins()));

        /*for (PayOut p : payOuts) {
            System.out.printf((p.isResult() ? 1 : 0) + " ");
        }*/
    }

    public void saveToJson() {
        final int TAB = 4;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String filePath = "./data-" + timestamp.getTime() + ".json";

        JSONArray jArray = new JSONArray(payOuts.toArray());
        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(jArray.toString(TAB));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getElapsedTime() {
        return Duration.between(runStartTime, runEndTime).toSeconds() > 0 ?
                Duration.between(runStartTime, runEndTime).toSeconds() + " seconds":
                Duration.between(runStartTime, runEndTime).toMillis() + " milliseconds";
    }

}
