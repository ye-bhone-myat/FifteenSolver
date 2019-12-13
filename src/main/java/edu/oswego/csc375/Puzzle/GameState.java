package edu.oswego.csc375.Puzzle;

public class GameState implements Comparable<GameState> {
    private int[] tiles;
    private int moves, blankIndex;
    private int sideCount;
    private int square;
    // TODO: make method that evaluates if state is solvable

    GameState(int sideCount) {
        this.sideCount = sideCount;
        this.square = sideCount * sideCount;
    }

    GameState(int[] tiles) {
        this.tiles = tiles;
        this.sideCount = (int) Math.sqrt(tiles.length);
        this.square = sideCount * sideCount;
        this.moves = 0;
        for (int i = 0; i < square; ++i){
            if (tiles[i] == square){
                blankIndex = i;
            }
        }
    }

    void setTiles(int[] tiles) {
        if (tiles.length != square) {
            throw new IllegalArgumentException();
        }
        this.tiles = tiles;
        for (int i = 0; i < square; ++i) {
            if (tiles[i] == square) {
                blankIndex = i;
                break;
            }
        }
    }

    int getBlankIndex() {
        return blankIndex;
    }

    void setMoves(int moves) {
        this.moves = moves;
    }

    public int[] getTiles() {
        return tiles;
    }

    public int getMoveCount() {
        return moves;
    }

    int evaluate() {
        int score = 0;
        for (int i = 0; i < square; ++i) {
            int incidentValue = tiles[i];
            if (incidentValue != i + 1) {
                int flatDistance = Math.abs(incidentValue - (i + 1));
                int hDistance = flatDistance % sideCount;
                int vDistance = (flatDistance - hDistance) / sideCount;
                int manhattanDistance = hDistance + vDistance;
                score -= manhattanDistance;
            }
        }

        return score;
    }

    boolean solvable() {

        int parity = 0;
        int row = 0; // the current row we are on
        int blankRow = 0; // the row with the blank tile

        for (int i = 0; i < square; i++)
        {
            if (i % sideCount == 0) { // advance to next row
                row++;
            }
            if (tiles[i] == square) { // the blank tile
                blankRow = row; // save the row on which encountered
                continue;
            }
            for (int j = i + 1; j < square; j++)
            {
                if (tiles[i] > tiles[j] && tiles[j] != square)
                {
                    parity++;
                }
            }
        }

        if (sideCount % 2 == 0) { // even grid
            if (blankRow % 2 == 0) { // blank on odd row; counting from bottom
                return parity % 2 == 0;
            } else { // blank on even row; counting from bottom
                return parity % 2 != 0;
            }
        } else { // odd grid
            return parity % 2 == 0;
        }

    }

    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < square; ++i) {
            String n = (tiles[i] == square) ? " " : tiles[i] + "";
            n = (n.length() > 1) ? n : " " + n;
            s += n + " ";
            if (i % sideCount == (sideCount - 1)) {
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
