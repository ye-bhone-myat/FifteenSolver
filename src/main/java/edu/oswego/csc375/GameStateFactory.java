package edu.oswego.csc375;

public class GameStateFactory {
    private static int SIDECOUNT;

    public GameStateFactory(int sideCount){
        this.SIDECOUNT = sideCount;
    }

    public GameState getNewState(){
        return new GameState(SIDECOUNT);
    }

}
