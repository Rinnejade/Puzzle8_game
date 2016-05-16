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
    private int chunkWidth;

    PuzzleBoard(Bitmap bitmap, int parentWidth) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, parentWidth, parentWidth, true);
        chunkWidth= parentWidth/NUM_TILES;
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

    PuzzleBoard(PuzzleBoard otherBoard) {
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
        chunkWidth = otherBoard.chunkWidth;
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
        int i=0;
        int parentWidth = chunkWidth *NUM_TILES;
        for (PuzzleTile tile:tiles) {
            if(tile==null)break;
            i++;
        }
        int emptyTile = i;
        int x = (emptyTile % NUM_TILES)*chunkWidth;
        int y = (emptyTile / NUM_TILES)*chunkWidth;
        for (int row = 0; row < NEIGHBOUR_COORDS.length; row++) {
            int xCords = x + NEIGHBOUR_COORDS[row][0]*chunkWidth;
            int yCords = y + NEIGHBOUR_COORDS[row][1]*chunkWidth;
            if(xCords< parentWidth && yCords < parentWidth){
                int num = getNumber(xCords, yCords);
                ArrayList<PuzzleTile> originalList = (ArrayList<PuzzleTile>)this.tiles.clone();
                Collections.swap(tiles, emptyTile, num);
                PuzzleBoard copy = new PuzzleBoard(this);
                resultArrayList.add(copy);
                this.tiles = (ArrayList<PuzzleTile>) originalList.clone();
            }
        }
        return resultArrayList;
    }

    public int priority() {
        return 0;
    }

    public int getNumber(int xCords, int yCords){
        int i=0;
        int yCoord = 0;
        for(int x=0; x<NUM_TILES; x++){
            int xCoord = 0;
            for(int y=0; y<NUM_TILES; y++){
                if(xCords==xCoord && yCords==yCoord) return i;
                i++;
                xCoord += chunkWidth;
            }
            yCoord = yCoord+ chunkWidth;
        }
        return 0;
    }

}
