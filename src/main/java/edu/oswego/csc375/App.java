package edu.oswego.csc375;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

/**
 * Hello world!
 *
 */
public class App
{

    private static final int SIDECOUNT = 4;
    public static void main( String[] args ) {
        if (args.length < 2){
            System.out.println("Give depth and number of cores as first and second arguments respectively");
            System.exit(0);
        }
        int depth = Integer.parseInt(args[0]);
        int nCores = Integer.parseInt(args[1]);
        System.out.println("Using " + nCores + " cores at depth " + depth);
        Random r = new Random();
//        int[] tiles = new int[]{6, 4, 7, 8, 1, 5, 9, 2, 3};
        int[] tiles = new int[]{7, 4, 13, 10, 8, 1, 14, 16, 11, 3, 9, 2, 6, 15, 5, 12};
        GameState state = new GameState(SIDECOUNT);
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        for(int i = 0; i < (SIDECOUNT * SIDECOUNT); ++i){
            arrayList.add(i + 1);
        }



        for(int i = 0; i < SIDECOUNT * SIDECOUNT; ++i){
            tiles[i] = arrayList.remove(r.nextInt(arrayList.size()));
        }

        Scanner sc = new Scanner(System.in);
//        GameState state2 = new GameState();
//        state2.setTiles(tiles2);
//        state2.setMoves(0);
        state.setTiles(tiles);
        state.setMoves(0);



        ForkJoinPool fjp = new ForkJoinPool(nCores);
        long start = System.nanoTime();
        Solution forked;
        Solution actual;



        ConcurrentHashMap<String, GameState> searchedConfigs = new ConcurrentHashMap<>();
//        ConcurrentHashMap<String, GameState> usedConfigs = new ConcurrentHashMap<>();
        try {
            forked = fjp.submit(new SolverTask(state, depth, searchedConfigs, SIDECOUNT)).get();

            actual = new Solution();
            do{

                if (forked == null){
                    // don't know why this works
                    // removing last state, removing new last state from searchedConfigs
                    //
                    actual.getSteps().remove(actual.getLastState());
                } else {
                    forked.getSteps().remove(forked.getFirstState());
//                    usedConfigs.put(forked.getLastState().toString(), forked.getLastState());
                    actual.addStates(forked.getSteps());
                    for (GameState gameState : forked.getSteps()) {
                        System.out.println(gameState + " Moves: " + gameState.getMoves() + "\n");
                    }
                }
                searchedConfigs.remove(actual.getLastState().toString(), actual.getLastState());
//                sc.nextLine();
                forked = fjp.submit(new SolverTask(actual.getLastState(), depth, searchedConfigs, SIDECOUNT)).get();

            } while (actual.getScore() < 0);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
//        while (!fjp.isTerminated()){
//        }
        long duration = System.nanoTime() - start;
        double durationSeconds = duration / 1000000000.0;



//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        System.out.println("Duration: " + durationSeconds + " seconds");
    }
}
