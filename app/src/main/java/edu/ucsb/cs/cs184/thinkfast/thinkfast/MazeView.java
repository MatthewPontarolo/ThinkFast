package edu.ucsb.cs.cs184.thinkfast.thinkfast;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * TODO: document your custom view class.
 */
public class MazeView extends View {
    private int maze_size;
    int cellSize;
    int paddingSize;
    Random random = new Random();
    Paint paint = new Paint();
    Bitmap bitmap;
    Canvas bmpCanvas;
    boolean showRed = false;
    boolean showGreen = false;


    public MazeView(Context context) {
        super(context);
    }

    public MazeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        maze_size = (int)Math.sqrt(((MainActivity)context).getScore())+3;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bmpCanvas = new Canvas(bitmap);
            int c = maze_size;
            int r = (height*c/width);
            cellSize = (int)(width/(1.5*c+0.5));
            paddingSize = cellSize/2;

            bmpCanvas.drawColor(Color.BLACK);
            paint.setColor(Color.WHITE);
            int[][] maze = new int[r][c];
            maze[0][0] = 1;
            ArrayList<Point> frontier = new ArrayList<>();
            HashSet<Point> frontierSet = new HashSet<>();
            frontier.add(new Point(0,1));
            frontier.add(new Point(1,0));
            frontierSet.add(new Point(0,1));
            frontierSet.add(new Point(1,0));
            generateMaze(maze, frontier, frontierSet);

            for(int i = 0; i < r; i++){
                for(int j = 0; j < c; j++){
                    bmpCanvas.drawRect(j*(cellSize+paddingSize)+paddingSize, i*(cellSize+paddingSize)+paddingSize, (j+1)*(cellSize+paddingSize), (i+1)*(cellSize+paddingSize), paint);
                }
            }

            paint.setStrokeWidth(0);
            paint.setColor(Color.BLUE);
            bmpCanvas.drawRect(paddingSize, paddingSize, paddingSize+cellSize, paddingSize+cellSize, paint);
            paint.setColor(Color.GREEN);
            bmpCanvas.drawRect((maze[0].length-1)*(cellSize+paddingSize)+paddingSize, (maze.length-1)*(cellSize+paddingSize)+paddingSize, maze[0].length*(cellSize+paddingSize), maze.length*(cellSize+paddingSize), paint);

        }
        if (showRed){
            canvas.drawColor(Color.RED);
        }
        else if (showGreen){
            canvas.drawColor(Color.GREEN);
        }
        else {
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(!showGreen) {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                if (bitmap.getPixel((int) event.getX(), (int) event.getY()) != Color.BLUE) {
                    showRed = true;
                    invalidate();
                }
            } else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
                showRed = true;
                invalidate();
            } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE && !showRed) {
                if (bitmap.getPixel((int) event.getX(), (int) event.getY()) == Color.GREEN) {
                    showGreen = true;
                    invalidate();
                    ((MainActivity) getContext()).CompleteMinigame();
                } else if (bitmap.getPixel((int) event.getX(), (int) event.getY()) == Color.BLACK) {
                    showRed = true;
                    invalidate();
                }
            } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                showRed = false;
                invalidate();
            }
        }
        return true;
    }

    private void generateMaze(int[][] maze, ArrayList<Point> frontier, HashSet<Point> frontierSet){
        while(frontier.size() > 0){
            Point current = frontier.remove(random.nextInt(frontier.size()));
            frontierSet.remove(current);
            int r = current.x;
            int c = current.y;
            maze[r][c] = 1;
            ArrayList<Point> visited = new ArrayList<>();
            if(r-1 >= 0){
                Point newPos = new Point(r-1, c);
                if(maze[r-1][c] == 0 && !frontierSet.contains(newPos)){
                    frontier.add(newPos);
                    frontierSet.add(newPos);
                }
                else if(maze[r-1][c] == 1){
                    visited.add(newPos);
                }
            }
            if(r+1 < maze.length){
                Point newPos = new Point(r+1, c);
                if(maze[r+1][c] == 0 && !frontierSet.contains(newPos)){
                    frontier.add(newPos);
                    frontierSet.add(newPos);
                }
                else if(maze[r+1][c] == 1){
                    visited.add(newPos);
                }
            }
            if(c-1 >=0){
                Point newPos = new Point(r, c-1);
                if(maze[r][c-1] == 0 && !frontierSet.contains(newPos)){
                    frontier.add(newPos);
                    frontierSet.add(newPos);
                }
                else if(maze[r][c-1] == 1){
                    visited.add(newPos);
                }
            }
            if(c+1 < maze[0].length){
                Point newPos = new Point(r, c+1);
                if(maze[r][c+1] == 0 && !frontierSet.contains(newPos)){
                    frontier.add(newPos);
                    frontierSet.add(newPos);
                }
                else if(maze[r][c+1] == 1){
                    visited.add(newPos);
                }
            }

            Point visitedCell = visited.get(random.nextInt(visited.size()));
            float left = Math.min(visitedCell.y, c);
            float top = Math.min(visitedCell.x, r);
            float right = Math.max(visitedCell.y, c);
            float bottom = Math.max(visitedCell.x, r);

            bmpCanvas.drawRect(left*(cellSize+paddingSize)+paddingSize, top*(cellSize+paddingSize)+paddingSize, (right+1)*(cellSize+paddingSize), (bottom+1)*(cellSize+paddingSize), paint);
        }
    }

}
