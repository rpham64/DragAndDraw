package com.bignerdranch.android.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Draws boxes in response to the user touching the screen
 * and dragging
 *
 * Created by Rudolf on 3/22/2016.
 */
public class BoxDrawingView extends View {

    private static final String TAG = "BoxDrawingView";

    public static final String KEY_SUPERSTATE = "superState";
    public static final String KEY_BOXES = "boxes";

    private Box mCurrentBox;
    private List<Box> mBoxes = new ArrayList<>();

    private Paint mBoxPaint;
    private Paint mBackgroundPaint;

    /**
     * Used when creating the view in CODE
     *
     * @param context
     */
    public BoxDrawingView(Context context) {
        this(context, null);
    }

    /**
     * Used when inflating the view from XML
     *
     * @param context
     * @param attrs
     */
    public BoxDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Paint the boxes a nice semitransparent red (ARGB)
        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);

        // Paint the background off-white
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);

    }

    @Override
    protected Parcelable onSaveInstanceState() {

        Log.i(TAG, "State saved");

        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_SUPERSTATE, super.onSaveInstanceState());
        bundle.putParcelableArrayList(KEY_BOXES, (ArrayList<? extends Parcelable>) mBoxes);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        Log.i(TAG, "State restored");

        Bundle bundle = (Bundle) state;
        Parcelable savedInstanceState = bundle.getParcelable(KEY_SUPERSTATE);
        mBoxes = bundle.getParcelableArrayList(KEY_BOXES);

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // Fill background
        canvas.drawPaint(mBackgroundPaint);

        // Color each box
        for (Box box : mBoxes) {

            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);

            canvas.drawRect(left, top, right, bottom, mBoxPaint);

        }

    }

    /**
     * Logs message for any touch events
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        PointF current = new PointF(event.getX(), event.getY());
        String action = "";

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                action = "ACTION_DOWN";

                // Reset drawing state
                // Start drawing box
                mCurrentBox = new Box(current);
                mBoxes.add(mCurrentBox);

                break;

            case MotionEvent.ACTION_MOVE:

                action = "ACTION_MOVE";

                if (mCurrentBox != null) {
                    mCurrentBox.setCurrent(current);
                    invalidate();   // Forces BoxDrawingView to draw itself
                }

                break;

            case MotionEvent.ACTION_UP:

                action = "ACTION_UP";

                // Finish drawing box
                mCurrentBox = null;

                break;

            case MotionEvent.ACTION_CANCEL:

                action = "ACTION_CANCEL";

                // Finish drawing box
                mCurrentBox = null;

                break;

        }

        Log.i(TAG, action + " at x=" + current.x + ", y=" + current.y);

        return true;
    }
}
