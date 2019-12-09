package edu.oswego.csc375;

import java.util.ArrayList;

public class Solution implements Comparable<Solution>{
    private ArrayList<GameState> steps;
    private int score;

    public Solution(){
        steps = new ArrayList<>();
        score = 1;
    }

    public Solution(GameState state){
        steps = new ArrayList<>();
        addState(state);
    }

    public Solution(ArrayList<GameState> steps){
        this.steps = steps;
        this.score = steps.get(steps.size() - 1).evaluate();
    }

    public void addState(GameState state){
        steps.add(state);
        score = state.evaluate();
    }

    public GameState getFirstState(){
        return steps.get(0);
    }

    public GameState getLastState(){
        return steps.get(steps.size() - 1);
    }

    public void addStates(ArrayList<GameState> states){
        steps.addAll(states);
        score = steps.get(steps.size() - 1).evaluate();
    }

    public ArrayList<GameState> getSteps() {
        return steps;
    }

    public int getScore() {
        return score;
    }

    public void setSteps(ArrayList<GameState> steps) {
        this.steps = steps;
        this.score = steps.get(steps.size() - 1).evaluate();
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
