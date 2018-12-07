package edu.ucsb.cs.cs184.thinkfast.thinkfast;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

/**
 * TODO: document your custom view class.
 */
public class MazeView extends View {
    int h;
    Paint paint = new Paint();
    Bitmap bitmap;
    Canvas bmpCanvas;
    boolean playing = false;
    boolean won = false;

    public MazeView(Context context) {
        super(context);
    }

    public MazeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bmpCanvas = new Canvas(bitmap);
            h = new Random().nextInt(height-100);
        }
        if (playing){
            bmpCanvas.drawColor(Color.RED);
            canvas.drawBitmap(bitmap, 0,0,null);
        }
        else if (won){
            bmpCanvas.drawColor(Color.GREEN);
            canvas.drawBitmap(bitmap, 0,0,null);
        }
        else {
            bmpCanvas.drawColor(Color.BLACK);
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(0);
            bmpCanvas.drawRect(0, h, width, h+100, paint);
            paint.setColor(Color.BLUE);
            bmpCanvas.drawRect(0, h, 100, h+100, paint);
            paint.setColor(Color.GREEN);
            bmpCanvas.drawRect(width - 100, h, width, h+100, paint);
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(!won) {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                playing = true;
                if (bitmap.getPixel((int) event.getX(), (int) event.getY()) != Color.BLUE) {
                    invalidate();
                }
            } else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
                invalidate();
            } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE && playing) {
                if (bitmap.getPixel((int) event.getX(), (int) event.getY()) == Color.GREEN) {
                    playing = false;
                    won = true;
                    invalidate();
                    ((MainActivity) getContext()).CompleteMinigame();
                } else if (bitmap.getPixel((int) event.getX(), (int) event.getY()) == Color.BLACK) {
                    invalidate();
                }
            } else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP || event.getActionMasked() == MotionEvent.ACTION_UP) {
                playing = false;
                invalidate();
            }
        }
        return true;
    }

}
