package edu.oswego.csc375;

public class GameState {
    private int[] tiles;
    private int moves;
    private int blankIndex;

    public void setTiles(int[] tiles){
        this.tiles = tiles;
        for (int i = 0; i < 16; ++i){
            if (tiles[i] == 16){
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
        for (int i = 0; i < 16; ++i){
            if (i + 1 != tiles[i]){
                score --;
            }
        }
        if (moves == 0){
            return score;
        } else {
            return score * moves;
        }
    }



    @Override
    public String toString(){
        String s = "";
        for (int i = 0; i < 16; ++i){
            String n = (tiles[i] == 16)? " " : tiles[i] + "";
            n = (n.length() > 1) ? n:" " + n;
            s += n + " " ;
            if (i % 4 == 3){
                s += '\n';
            }
        }
        s += "moves: " + moves;
        return s;
    }
}
