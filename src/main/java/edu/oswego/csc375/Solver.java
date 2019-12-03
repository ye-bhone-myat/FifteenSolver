package edu.oswego.csc375;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class Solver extends RecursiveTask<Solution> {

    private final GameState state;
    private int depth;

    public Solver(GameState state, int remainingDepth){
        this.state = state;
        this.depth = remainingDepth;
    }

    @Override
    protected Solution compute() {
        // state evaluates to 0, return
        if (state.evaluate() == 0){
            return new Solution(state);
        }
        // otherwise,
        // get available moves
        // get next states
        // divide
        // join
        else {
            ArrayList<Move> moves = getValidMoves();
            ArrayList<GameState> states = new ArrayList<>();
            for (Move m : moves){
                states.add(makeMove(m));
            }
            depth --;
            ArrayList<Solution> solutions = new ArrayList<>();
            for (GameState state : states){
                solutions.add(new Solution((new ArrayList<>(Arrays.asList(this.state, state)))));
            }

            if (depth == 0){
                solutions.sort(Comparator.naturalOrder());
                return solutions.get(0);
            } else {
                ArrayList<Solver> solvers = new ArrayList<>();
                for (GameState s : states){
                    solvers.add(new Solver(s,  depth));
                }

                Solution deeperSolution = RecursiveTask.invokeAll(solvers).stream()
                        .map(ForkJoinTask::join)
                        .sorted()
                        .collect(Collectors.toCollection(ArrayList::new)).get(0);

                int correctSolutionIndex = -1;
                for (int i = 0; i < solutions.size(); ++i){
                    if (solutions.get(i).getLastState().toString().equals(deeperSolution.getFirstState().toString())){
                        correctSolutionIndex = i;
                        break;
                    }
                }

                Solution correctSolution = solutions.get(correctSolutionIndex);
                deeperSolution.getSteps().remove(0);
                correctSolution.addStates(deeperSolution.getSteps());
                return correctSolution;

            }
        }
    }

    private GameState makeMove(Move m){
        GameState moved = new GameState();
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
        tiles[blankIndex] = tiles[blankIndex - 4];
        tiles[blankIndex - 4] = tmp;
        return tiles;
    }

    private int[] moveDown() {
        int[] tiles = state.getTiles().clone();
        int blankIndex = state.getBlankIndex();
        int tmp = tiles[blankIndex];
        tiles[blankIndex] = tiles[blankIndex + 4];
        tiles[blankIndex + 4] = tmp;
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

    private ArrayList<Move> getValidMoves(){
        ArrayList<Move> moves = new ArrayList<>();
        for (Move m : Move.values()){
            if (validMove(m)) moves.add(m);
        }
        return moves;
    }

    private boolean validMove(Move move){
        switch (move){
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
        return state.getBlankIndex() % 4 < 3;
    }

    private boolean canMoveLeft() {
        return state.getBlankIndex() % 4 > 0 ;
    }

    private boolean canMoveDown() {
        return state.getBlankIndex() < 12;
    }

    private boolean canMoveUp(){
        return state.getBlankIndex() > 3;
    }

}
