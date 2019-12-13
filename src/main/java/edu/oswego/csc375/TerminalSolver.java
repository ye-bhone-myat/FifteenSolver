package edu.oswego.csc375;


import edu.oswego.csc375.Puzzle.*;

import java.util.concurrent.*;


public class TerminalSolver {


    public static void main(String[] args) {

        if (args.length < 3) {
            System.out.println("Give depth, number of cores, and puzzle size as first, second, and third arguments respectively");
            System.exit(0);
        }

        int depth = Integer.parseInt(args[0]);
        int nCores = Integer.parseInt(args[1]);
        int sideCount = Integer.parseInt(args[2]);
        System.out.println("Using " + nCores + " cores at depth " + depth);
        Puzzle puzzle16 = new Puzzle(depth, nCores, sideCount);
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Future<Solution> futureSolution = exec.submit(puzzle16::solve);
        int i = 0;
        while (!futureSolution.isDone()) {
            String bar = "";
            switch (i) {
                case 0:
                    bar = "-";
                    ++i;
                    break;
                case 1:
                    bar = "\\";
                    ++i;
                    break;
                case 2:
                    bar = "|";
                    ++i;
                    break;
                case 3:
                    bar = "/";
                    i = 0;
                    break;
            }
            try {
                System.out.print("Solving puzzle..." + bar + "\r");
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Solution actual = null;
        try {
            actual = futureSolution.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("Total moves: " + actual.getMoves());
        int moves = 0;
        for (String step : actual.getSteps()) {
            System.out.println(step + " moves: " + moves + "\n");
            ++moves;
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Total time taken: " + actual.getTimeTaken() + " seconds");

    }
}
