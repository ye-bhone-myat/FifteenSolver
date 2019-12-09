package edu.oswego.csc375;

public class GameState implements Comparable<GameState> {
    private int[] tiles;
    private int moves, blankIndex;
    private int sideCount;
    private int square;

    GameState(int sideCount){
        this.sideCount = sideCount;
        this.square = sideCount * sideCount;
    }

    public void setTiles(int[] tiles){
        if (tiles.length != square){
            throw new IllegalArgumentException();
        }
        this.tiles = tiles;
        for (int i = 0; i < square; ++i){
            if (tiles[i] == square){
                blankIndex = i;
                break;
            }
        }
    }

    public int getBlankIndex(){
        return blankIndex;
    }

    public void setMoves(int moves) {
        this.moves = moves;
    }

    public int[] getTiles() {
        return tiles;
    }

    public int getMoves() {
        return moves;
    }

    public int evaluate(){
        int score = 0;
        for (int i = 0; i < square; ++i){
            int expected = i + 1;
            int actual = tiles[i];
            int flatDistance = Math.abs(actual - expected);
            if (flatDistance != 0){
                int hDistance = flatDistance % sideCount;
                int vDistance = (flatDistance - hDistance) / sideCount;
                int tileScore = -1 * (hDistance + vDistance);
                score += tileScore;
            }
        }
        return score;
//        if (moves == 0){
//            return score;
//        } else {
//            return score * moves;
//        }
    }



    @Override
    public String toString(){
        String s = "";
        for (int i = 0; i < square; ++i){
            String n = (tiles[i] == square)? " " : tiles[i] + "";
            n = (n.length() > 1) ? n:" " + n;
            s += n + " " ;
            if (i % sideCount == (sideCount - 1)){
                s += '\n';
            }
        }
        s += "score: " + evaluate();
        return s;
    }


    @Override
    public int compareTo(GameState o) {
        return o.evaluate() - this.evaluate();
    }
}
