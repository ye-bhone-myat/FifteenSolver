package edu.oswego.csc375;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;


public class SolverTask extends RecursiveTask<Solution> {

    private final GameState state;
    private int depth;
    private static boolean cancelled;
    private final ConcurrentMap<String, GameState> searchedConfigs;
    private int sideCount, square;


    public SolverTask(GameState state, int depth, ConcurrentMap<String, GameState> searchedConfigs, int sideCount) {
        this.state = state;
        this.depth = depth;
        this.searchedConfigs = searchedConfigs;
        cancelled = false;
        this.sideCount = sideCount;
        this.square = sideCount * sideCount;
    }

    @Override
    protected Solution compute() {
        // try to add current to searched
        // if current is searched or cancelled, null

        // if depth is 0 or if this is end, this
        // remove this from searched

        // get next states
        // remove all that are searched
        // fork
        // if there is some, sort and pick best
        // if there are none, null

        if (searchedConfigs.putIfAbsent(state.toString(), state) != null || cancelled){
            return null;
        }
        if (depth == 0 || state.evaluate() == 0){
            searchedConfigs.remove(state.toString(), state);
            return new Solution(state);
        }
        // perform next moves
        ArrayList<GameState> states = new ArrayList<>();
//        if (getValidMoves().size() == 2){
//            for (Move m : getValidMoves()){
//                GameState s = makeMove(m);
//                searchedConfigs.remove(s.toString(), s);
//            }
//        }
        for (Move m : getValidMoves()){
            GameState s = makeMove(m);
            // only put unexplored states in list of next possible states
            if (!searchedConfigs.containsKey(s.toString())) {
                states.add(s);
            }
        }
        -- depth;
        // if no available next states, null
        if (states.size() == 0){
            return null;
        }
        // check for cancellation again
        if (cancelled) {
            return null;
        }
        // fork next states
        ArrayList<SolverTask> solvers = new ArrayList<>();
        for (GameState s : states) {
            // only create new solvers for unexplored configs
            solvers.add(new SolverTask(s, depth, searchedConfigs, sideCount));
        }
        ArrayList<Solution> deeperSolutions = RecursiveTask.invokeAll(solvers).stream()
                .map(ForkJoinTask::join)
                .filter(Objects::nonNull)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toCollection(ArrayList::new));
        // if next states return null, null
        if (deeperSolutions.isEmpty()) {
            return null;
        }
        // add next moves to current move, return
        Solution deeperSolution = deeperSolutions.remove(0);
        Solution solution = new Solution(state);
        solution.addStates(deeperSolution.getSteps());
        return solution;






//
//        // if config is searched, or if task is cancelled
//        if (searchedConfigs.putIfAbsent(state.toString(), state) != null || cancelled) {
//            return null;
//        }
//        // if current config is end state
//        if (state.evaluate() == 0) {
//            cancelled = true;
//            searchedConfigs.put(state.toString(), state);
//            return new Solution(state);
//        }
//        ArrayList<Move> possibleMoves = getValidMoves();
//        ArrayList<GameState> states = new ArrayList<>();
//        for (Move m : possibleMoves) {
//            GameState s = makeMove(m);
//            // only put unexplored states in list of next possible states
//            if (!searchedConfigs.containsKey(s.toString())) {
//                states.add(s);
//            }
//        }
//        if (states.size() == 0){
//            return null;
//        }
//        --depth;
//        states.sort(Comparator.naturalOrder());
//        if (depth == 0) {
//            // maximum depth reached; return a new Solution with this state and the best of the next states
//            GameState s = states.get(0);
//            searchedConfigs.put(s.toString(), s);
//            return new Solution(new ArrayList<>(Arrays.asList(state, states.get(0))));
//        }
//        if (cancelled) {
//            return null;
//        }
//        ArrayList<SolverTask> solvers = new ArrayList<>();
//        for (GameState s : states) {
//            // only create new solvers for unexplored configs
//            solvers.add(new SolverTask(s, depth, searchedConfigs));
//        }
//
//
//        ArrayList<Solution> deeperSolutions = RecursiveTask.invokeAll(solvers).stream()
//                .map(ForkJoinTask::join)
//                .filter(Objects::nonNull)
//                .sorted(Comparator.naturalOrder())
//                .collect(Collectors.toCollection(ArrayList::new));
//        if (deeperSolutions.isEmpty()) {
//            return null;
//        }
//        Solution deeperSolution = deeperSolutions.remove(0);
//        for (Solution s : deeperSolutions) {
//            searchedConfigs.remove(s.toString(), s);
//        }
//        Solution solution = new Solution(state);
//        solution.addStates(deeperSolution.getSteps());
//        return solution;

    }


    private GameState makeMove(Move m) {
        GameState moved = new GameState(sideCount);
        moved.setMoves(state.getMoves() + 1);

        switch (m) {
            case UP:
                moved.setTiles(moveUp());
                break;
            case DOWN:
                moved.setTiles(moveDown());
                break;
            case LEFT:
                moved.setTiles(moveLeft());
                break;
            case RIGHT:
                moved.setTiles(moveRight());
                break;
        }
        return moved;
    }

    private int[] moveUp() {
        int[] tiles = state.getTiles().clone();
        int blankIndex = state.getBlankIndex();
        int tmp = tiles[blankIndex];
        tiles[blankIndex] = tiles[blankIndex - sideCount];
        tiles[blankIndex - sideCount] = tmp;
        return tiles;
    }

    private int[] moveDown() {
        int[] tiles = state.getTiles().clone();
        int blankIndex = state.getBlankIndex();
        int tmp = tiles[blankIndex];
        tiles[blankIndex] = tiles[blankIndex + sideCount];
        tiles[blankIndex + sideCount] = tmp;
        return tiles;
    }

    private int[] moveLeft() {
        int[] tiles = state.getTiles().clone();
        int blankIndex = state.getBlankIndex();
        int tmp = tiles[blankIndex];
        tiles[blankIndex] = tiles[blankIndex - 1];
        tiles[blankIndex - 1] = tmp;
        return tiles;
    }

    private int[] moveRight() {
        int[] tiles = state.getTiles().clone();
        int blankIndex = state.getBlankIndex();
        int tmp = tiles[blankIndex];
        tiles[blankIndex] = tiles[blankIndex + 1];
        tiles[blankIndex + 1] = tmp;
        return tiles;
    }

    private ArrayList<Move> getValidMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        for (Move m : Move.values()) {
            if (validMove(m)) moves.add(m);
        }
        return moves;
    }

    private boolean validMove(Move move) {
        switch (move) {
            case UP:
                return canMoveUp();
            case DOWN:
                return canMoveDown();
            case LEFT:
                return canMoveLeft();
            case RIGHT:
                return canMoveRight();
        }
        return false;
    }

    private boolean canMoveRight() {
        return state.getBlankIndex() % sideCount < (sideCount - 1);
    }

    private boolean canMoveLeft() {
        return state.getBlankIndex() % sideCount > 0;
    }

    private boolean canMoveDown() {
        return state.getBlankIndex() < (sideCount * (sideCount - 1));
    }

    private boolean canMoveUp() {
        return state.getBlankIndex() > (sideCount - 1);
    }

}
