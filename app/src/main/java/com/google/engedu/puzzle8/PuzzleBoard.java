package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PuzzleBoard {

    private static final int NUM_TILES = 3;
    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };
    private ArrayList<PuzzleTile> tiles;

    private int steps=0;
    private PuzzleBoard previousBoard = null;

    PuzzleBoard(Bitmap bitmap, int parentWidth) {
        if(bitmap!=null && parentWidth>0){
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, parentWidth, parentWidth, true);
            int chunkWidth= parentWidth/NUM_TILES;
            int yCoord = 0;
            int i = 0;
            tiles= new ArrayList<PuzzleTile>();
            for(int x=0; x<NUM_TILES; x++){
                int xCoord = 0;
                for(int y=0; y<NUM_TILES; y++){
                    if(i== NUM_TILES*NUM_TILES-1){
                        tiles.add(null);
                        break;
                    }
                    Bitmap splitBitmap = Bitmap.createBitmap(scaledBitmap, xCoord, yCoord, chunkWidth, chunkWidth);
                    tiles.add(new PuzzleTile(splitBitmap, i++));
                    xCoord += chunkWidth;
                }
                yCoord += chunkWidth;
            }
        }
    }

    PuzzleBoard(PuzzleBoard otherBoard) {
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
        steps = otherBoard.steps++;
        previousBoard = otherBoard;
    }

    public PuzzleBoard getpreviousBoard(){
        return previousBoard;
    }

    public void setValues(PuzzleBoard p, int steps){
        this.steps = steps;
        this.previousBoard = p;
    }
    public boolean isEqual(PuzzleBoard b){
        if(this ==null || b == null ) return false;
        for (int i = 0; i < NUM_TILES*NUM_TILES; i++) {
            if(tiles.get(i)!=null && b.tiles.get(i)!=null && tiles.get(i).getNumber() != b.tiles.get(i).getNumber())
                    return false;
        }
        return true;
    }
    public void reset() {
        // Nothing for now but you may have things to reset once you implement the solver.
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public ArrayList<PuzzleBoard> neighbours() {
        ArrayList<PuzzleBoard> resultArrayList = new ArrayList<PuzzleBoard>();
        int i = 0;
        for (PuzzleTile tile:tiles) {
            if(tile==null)break;
            i++;
        }
        int tileX = i % NUM_TILES, tileY = i / NUM_TILES;
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES
                    && tiles.get(XYtoIndex(nullX, nullY)) !=null ) {
                PuzzleBoard copy = new PuzzleBoard(this);
                Collections.swap(copy.tiles,XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                resultArrayList.add(copy);
            }
        }
        return resultArrayList;
    }

    public int priority() {
        int priority=0;
        for (int i = 0; i < NUM_TILES*NUM_TILES; i++) {
            if(tiles.get(i)!=null) {
                int correctPos = tiles.get(i).getNumber();
//                Log.i("asdf correct pos: ",""+correctPos);
//                Log.i("asdf now pos: ",""+i);
                int x0 = correctPos % NUM_TILES;
                int y0 = correctPos / NUM_TILES;
                int x1 = i % NUM_TILES;
                int y1 = i / NUM_TILES;
                priority += Math.abs(x1 - x0) + Math.abs(y1 - y0);
            }
        }
//        isSolution
        if(priority==0) return -1;
        return priority+this.steps;
    }
}
