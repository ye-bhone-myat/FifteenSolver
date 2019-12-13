package edu.oswego.csc375.Puzzle;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Solution implements Comparable<Solution>{
    private ArrayList<GameState> steps;
    private int score;
    private double timeTaken;


    public Solution(GameState state){
        steps = new ArrayList<>();
        addState(state);
    }

    void setTimeTaken(double timeTaken){
        this.timeTaken = timeTaken;
    }

    public double getTimeTaken() {
        return timeTaken;
    }

    public Solution(ArrayList<GameState> steps){
        this.steps = steps;
        this.score = steps.get(steps.size() - 1).evaluate();
    }

    void addState(GameState state){
        steps.add(state);
        score = state.evaluate();
    }

    GameState getFirstState(){
        return steps.get(0);
    }

    public GameState getLastState(){
        return steps.get(steps.size() - 1);
    }

    void addStates(ArrayList<GameState> states){
        steps.addAll(states);
        score = steps.get(steps.size() - 1).evaluate();
    }

    public ArrayList<GameState> getStates(){
        return steps;
    }

    public ArrayList<String> getSteps() {
        return steps.stream().map(GameState::toString).collect(Collectors.toCollection(ArrayList::new));
    }

    public int getMoves(){
        return steps.size() - 1;
    }

    int getScore() {
        return score;
    }



    @Override
    public int compareTo(Solution other) {
            return other.score - this.score;
    }

    @Override
    public String toString(){
        return "[Moves: " + steps.size()+ ", Score: " + score + "]";
    }
}
