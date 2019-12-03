package edu.oswego.csc375;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Random r = new Random();
        int[] tiles = new int[]{1, 2, 3, 16, 5, 6, 7, 4, 9, 10, 11, 8, 13, 14, 15, 12};
        GameState state = new GameState();
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        for(int i = 0; i < 16; ++i){
            arrayList.add(i + 1);
        }


//        for(int i = 0; i < 16; ++i){
//            tiles[i] = arrayList.remove(r.nextInt(arrayList.size()));
//        }

        state.setTiles(tiles);
        state.setMoves(0);
        ForkJoinPool fjp = new ForkJoinPool(2);
        long start = System.currentTimeMillis();
        Solution forked = null;
        try {
            forked = fjp.submit(new Solver(state, 15)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        while (!fjp.isTerminated()){
        }
        long duration = System.currentTimeMillis() - start;
        double durationSeconds = duration / 1000;


        for (GameState gameState : forked.getSteps()){
            System.out.println(gameState + "\n");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.printf("Duration: %.3f\n", durationSeconds);
    }
}
