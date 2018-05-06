package com.company;

import java.util.ArrayList;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
	    new Main();
    }

    final double startCapital = 1000;
    final double percPerBet   = 0.0001;
    final int nofRounds       = 500;
    final double winChance    = 0.51;
    final int nofExperiments  = 10000;

    final long seed = 121137190807L;
    Random rand;

    double capital;
    double max;
    double maxRelativeDrawdown;


    Main() {
        rand = new Random();

        rand.setSeed(seed);
        montanteHollandaise();

        rand.setSeed(seed);
        montanteAmericaine();
    }

    void Init() {
        capital = startCapital;
        max = capital;
        maxRelativeDrawdown = 0;
    }

    boolean Bet(double risk) {
        if (risk*percPerBet > 0.85) {
            capital = 0;
            return false;
        }

        if (rand.nextDouble() <= winChance) {
            capital *= 1.0 + risk*percPerBet;
            return true;
        }
        else {
            capital *= 1.0 - risk*percPerBet;
            return false;
        }
    }

    // todo: this doesn't work
    // Montante Américaine
    void montanteAmericaine() {
        double maxDD = 0;
        double avgDD = 0;
        double avgEnd = 0;

        ArrayList<Integer> mh = new ArrayList<>();
        mh.add(1); mh.add(2); mh.add(3); mh.add(4);

        for (int i = 0; i < nofExperiments; i++) {
            Init();

            for (int b = 0; b < nofRounds; b++) {
                int risk = 0;
                if (mh.size() == 1) risk = mh.get(0);
                else                risk = mh.get(0) + mh.get(mh.size() - 1);

                if (Bet(risk)) {
                        mh.remove(mh.size()-1);
                        if (mh.size() > 0) mh.remove(0);
                        if (mh.size() == 0) {
                            mh.add(1); mh.add(2); mh.add(3); mh.add(4);
                        }
                }
                else {
                    mh.add(risk);
                }

                if (capital <= 0) {
                    maxDD = 1.0;
                    capital = 0.0;
                    break;
                }

                max = Math.max(capital, max);
                maxRelativeDrawdown = Math.max(1.0 - capital/max, maxRelativeDrawdown);
            }

            maxDD   = Math.max(maxRelativeDrawdown, maxDD);
            avgDD  += maxRelativeDrawdown;
            avgEnd += capital;
        }

        avgDD  /= nofExperiments;
        avgEnd /= nofExperiments;

        System.out.println("Montante Americaine");
        System.out.println("Max. DD: " + maxDD * 100.0 + "%");
        System.out.println("Avg. DD: " + avgDD*100.0 + "%");
        System.out.println("Avg. capital: " + avgEnd);
        System.out.println("Avg. % return / 1% risk: " + (avgEnd - startCapital) / (avgDD * 100.0) / startCapital * 100.0);
    }

    // Montante Hollandaise
    void montanteHollandaise() {
        double maxDD = 0;
        double avgDD = 0;
        double avgEnd = 0;

        ArrayList<Integer> mh = new ArrayList<>();

        for (int i = 0; i < nofExperiments; i++) {
            Init();

            for (int b = 0; b < nofRounds; b++) {
                int risk = 0;
                if (mh.size() == 0) risk = 1;
                else risk = mh.get(0) + 1;

                if (Bet(risk)) {
                    if (mh.size() > 0) {
                        mh.remove(0);
                    }
                }
                else {
                    mh.add(risk);
                }

                if (capital <= 0) {
                    maxDD = 1.0;
                    capital = 0.0;
                    break;
                }

                max = Math.max(capital, max);
                maxRelativeDrawdown = Math.max(1.0 - capital/max, maxRelativeDrawdown);
            }

            maxDD   = Math.max(maxRelativeDrawdown, maxDD);
            avgDD  += maxRelativeDrawdown;
            avgEnd += capital;
        }

        avgDD  /= nofExperiments;
        avgEnd /= nofExperiments;

        System.out.println("Montante Hollandaise");
        System.out.println("Max. DD: " + maxDD * 100.0 + "%");
        System.out.println("Avg. DD: " + avgDD*100.0 + "%");
        System.out.println("Avg. capital: " + avgEnd);
        System.out.println("Avg. % return / 1% risk: " + (avgEnd - startCapital) / (avgDD * 100.0) / startCapital * 100.0);
    }

}
