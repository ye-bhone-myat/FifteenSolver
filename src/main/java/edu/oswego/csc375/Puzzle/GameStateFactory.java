package edu.oswego.csc375.Puzzle;

import java.util.Random;

public class GameStateFactory {
    private static int SIDECOUNT;
    private static int SQUARE;

    public GameStateFactory(int sideCount){
        SIDECOUNT = sideCount;
        SQUARE = SIDECOUNT * SIDECOUNT;
    }

    public GameState getBlankState(){
        return new GameState(SIDECOUNT);
    }

    public GameState getState(int[] tiles){
        GameState s = new GameState(tiles);
        if (!s.solvable()){
            throw new IllegalArgumentException("Configuration given is unsolvable");
        }
        return new GameState(tiles);
    }

    public GameState getRandomState(){
        GameState s = new GameState(generateRandomConfig());
        while (!s.solvable()){
            s = new GameState(generateRandomConfig());
        }
        return s;
    }

    private int[] generateRandomConfig(){
        int[] tiles = new int[SQUARE];
        for (int i = 0; i < SQUARE; ++i){
            tiles[i] = i + 1;
        }
        Random r = new Random();
        int blankIndex = SQUARE - 1;
        int max = 40;
        int[] moves = new int[]{-1, 1, -4, 4};
        for (int i = 0; i < max; ++i){
            int move = moves[r.nextInt(4)];
            int newIndex;
            if ((newIndex = blankIndex + move) > -1 && newIndex < SQUARE){
                int temp = tiles[newIndex];
                tiles[newIndex] = SQUARE;
                tiles[blankIndex] = temp;
                blankIndex = newIndex;
            }
        }
        return tiles;
    }



}
