package com.google.engedu.puzzle8;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

public class PuzzleBoardView extends View {
    public static final int NUM_SHUFFLE_STEPS = 40;
    private Activity activity;
    private PuzzleBoard puzzleBoard;
    private ArrayList<PuzzleBoard> animation;
    private Random random = new Random();
    private int NUM_TILES;

    public PuzzleBoardView(Context context) {
        super(context);
        activity = (Activity) context;
        animation = null;
    }

    public void initialize(Bitmap imageBitmap, View parent, int numOfTiles) {
        int width = imageBitmap.getWidth();
        NUM_TILES = numOfTiles;
        puzzleBoard = new PuzzleBoard(imageBitmap, width, NUM_TILES);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (puzzleBoard != null) {
            if (animation != null && animation.size() > 0) {
                puzzleBoard = animation.remove(0);
                puzzleBoard.draw(canvas);
                if (animation.size() == 0) {
                    animation = null;
                    puzzleBoard.reset();
                    Toast toast = Toast.makeText(activity, "Solved! ", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    this.postInvalidateDelayed(500);
                }
            } else {
                puzzleBoard.draw(canvas);
            }
        }
    }

    public void shuffle() {
        if (animation == null && puzzleBoard != null) {
            // Do something.
            for (int i = 0; i < NUM_SHUFFLE_STEPS ; i++) {
                ArrayList<PuzzleBoard> resultArrayList = puzzleBoard.neighbours();
                puzzleBoard = resultArrayList.get(random.nextInt(resultArrayList.size()));
            }
        }
//        Log.i("asdf steps : ",""+puzzleBoard.steps);
//        Log.i("asdf manhattan dist",""+puzzleBoard.priority());
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animation == null && puzzleBoard != null) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (puzzleBoard.click(event.getX(), event.getY())) {
                        invalidate();
                        if (puzzleBoard.resolved()) {
                            Toast toast = Toast.makeText(activity, "Congratulations!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        return true;
                    }
            }
        }
        return super.onTouchEvent(event);
    }

    public void solve() {
//        Log.i("asdf manhattan dist",""+puzzleBoard.priority());
        PriorityQueue<PuzzleBoard> pq = new PriorityQueue<PuzzleBoard>(5000  , new Comparator<PuzzleBoard>() {
            @Override
            public int compare(PuzzleBoard puzzleBoard1, PuzzleBoard puzzleBoard2) {
                return Integer.valueOf(puzzleBoard1.priority()).compareTo(puzzleBoard2.priority());
            }
        });
        if(puzzleBoard!=null){
            puzzleBoard.setValues(null, 0);
            pq.add(puzzleBoard);
        }

        int i =0;
        while(true){
            PuzzleBoard currentBoard = pq.poll();
            if(currentBoard == null ) break;
//            is solution
            if(currentBoard.priority()==-1){
                animation = new ArrayList<PuzzleBoard>();
                while(currentBoard!=null) {
                    animation.add(currentBoard);
                    currentBoard = currentBoard.getpreviousBoard();
                }
                Collections.reverse(animation);
                invalidate();
                break;
            }
//            not solution
            else{
                ArrayList<PuzzleBoard> resultArrayList = currentBoard.neighbours();
                for (PuzzleBoard board:resultArrayList)
                    if(!(board.isEqual(currentBoard.getpreviousBoard())))
                        pq.add(board);
            }
        }

    }
}
