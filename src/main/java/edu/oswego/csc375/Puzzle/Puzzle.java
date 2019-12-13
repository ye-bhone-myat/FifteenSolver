package edu.oswego.csc375.Puzzle;


import java.util.concurrent.*;

public class Puzzle {
    private final int sidecount;
    private final int processorCount;
    private final int depth;
    private GameStateFactory gsf;
    private GameState startingState;

    public Puzzle(int depth, int processorCount, int sidecount) {
        this.depth = depth;
        this.processorCount = processorCount;
        this.sidecount = sidecount;
        gsf = new GameStateFactory(sidecount);
        startingState = gsf.getRandomState();
    }

    public Puzzle(int depth, int processorCount, int sidecount, int[] tiles) {
        this.depth = depth;
        this.processorCount = processorCount;
        this.sidecount = sidecount;
        gsf = new GameStateFactory(sidecount);
        startingState = gsf.getState(tiles);
    }

    public Puzzle(int depth, int processorCount, int sidecount, GameState startingState) {
        this.depth = depth;
        this.processorCount = processorCount;
        this.sidecount = sidecount;
        gsf = new GameStateFactory(sidecount);
        if (!startingState.solvable()){
            throw new IllegalArgumentException("Given state is not solvable");
        }
        this.startingState = startingState;
    }


    public Solution solve() {
        ConcurrentHashMap<String, GameState> searchedConfigs = new ConcurrentHashMap<>();
        Solution actual = new Solution(startingState);
        Solution forked;
        ForkJoinPool fjp = new ForkJoinPool(processorCount);
        long start = System.nanoTime();
        try {
            forked = fjp.submit(new SolverTask(startingState, depth, searchedConfigs, sidecount)).get();
            do {
////                 if forked on a dead-end, take a step back and fork again
                if (forked == null){
//                        || forked.getLastState().toString().equals(actual.getLastState().toString())) {
                    actual.getStates().remove(actual.getLastState());
                } else {
                    forked.getStates().remove(forked.getFirstState());
                    actual.addStates(forked.getStates());
//                    for (GameState s : forked.getStates()){
//                        System.out.println(s.toString());
//                    }
                }

                searchedConfigs.remove(actual.getLastState().toString(), actual.getLastState());
                forked = fjp.submit(new SolverTask(actual.getLastState(), depth, searchedConfigs, sidecount)).get();

            } while (actual.getScore() < 0);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        long duration = System.nanoTime() - start;
        double totalTime = duration / 1000000000.0;
        actual.setTimeTaken(totalTime);
        return actual;
    }

}
