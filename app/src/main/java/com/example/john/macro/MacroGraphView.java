package com.example.john.macro;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by John on 12/27/2014.
 */
public class MacroGraphView extends View
{
    private static final String LOG_TAG = MacroGraphView.class.getSimpleName();
    private static double mFat = 0;
    private static double mProtein = 0;
    private static double mCarb = 0;

    public MacroGraphView(Context context)
    {
        super(context);
    }

    public MacroGraphView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MacroGraphView(Context context, AttributeSet attrs, int defaultStyle)
    {
        super(context, attrs, defaultStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int myHeight = hSpecSize;

        if(hSpecMode == MeasureSpec.EXACTLY) {
            myHeight = hSpecSize;
        }
        else if(hSpecMode == MeasureSpec.AT_MOST) {
            // Wrap Content
        }

        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int myWidth = wSpecSize;
        if(wSpecMode == MeasureSpec.EXACTLY) {
            myWidth = wSpecSize;
        }
        else if(wSpecMode == MeasureSpec.AT_MOST) {
            // Wrap Content
        }

        setMeasuredDimension(myWidth, myHeight);
    }

    public void setMacros(double fat, double protein, double carb)
    {
        mFat = fat;
        mProtein = protein;
        mCarb = carb;
        super.invalidate();
    }

    public void setFat(double fat)
    {
        mFat = fat;
        super.invalidate();
    }

    public void setProtein(double protein)
    {
        mProtein = protein;
        super.invalidate();
    }

    public void setCarb(double carb)
    {
        mCarb = carb;
        super.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        Log.v(MacroGraphView.class.getSimpleName(), "onDraw()");

        // Sets up the axis parameters for the graph.
        Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisPaint.setColor(Color.BLACK);
        axisPaint.setStrokeWidth(4);

        int axisXEnd = (int)(getMeasuredWidth() * .9);
        int axisXStart = getMeasuredWidth() - (int)(getMeasuredWidth() * .90);

        int axisYEnd = (int)(getMeasuredHeight() * .9) - 1;
        int axisYStart = getMeasuredHeight() - (int)(getMeasuredHeight() * .95);

        // Sets up the grid line parameters for the graph.
        Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(getResources().getColor(R.color.mintCream));
        gridPaint.setAlpha(200);
        gridPaint.setStrokeWidth(2);

        int axisHeight = axisYEnd - axisYStart;
        int gridOne = (int)(axisHeight * .25) + axisYStart;
        int gridTwo = (int)(axisHeight * .5) + axisYStart;
        int gridThree = (int)(axisHeight * .75) + axisYStart;

        // Sets up the bar parameters for the graph.
        Paint fatPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fatPaint.setColor(Color.RED);
        Paint proteinPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        proteinPaint.setColor(Color.BLUE);
        Paint carbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        carbPaint.setColor(Color.GREEN);

        int axisWidth = axisXEnd - axisXStart;
        int barDivider = (int)(axisWidth / 10);
        int barWidth = barDivider * 2;
        int fatStart = axisXStart + barDivider;
        int fatEnd = fatStart + barWidth;
        int proteinStart = fatEnd + barDivider;
        int proteinEnd = proteinStart + barWidth;
        int carbStart = proteinEnd + barDivider;
        int carbEnd = carbStart + barWidth;
        double total = mFat + mProtein + mCarb;
        int fatTop = axisYEnd;
        int proteinTop = axisYEnd;
        int carbTop = axisYEnd;

        if(total != 0)
        {
            fatTop = axisYEnd - (int) ((mFat / total) * (axisHeight));
            proteinTop = axisYEnd - (int) ((mProtein / total) * (axisHeight));
            carbTop = axisYEnd - (int) ((mCarb / total) * (axisHeight));
        }

        // Sets up the parameters for the labels.
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(getResources().getColor(R.color.mintCream));
        textPaint.setTextSize((int)((getHeight() * .1)));

        int textY = getHeight() - 5;

        // Draws the axes for the graph.
        canvas.drawLine(axisXStart, axisYStart, axisXStart, axisYEnd, axisPaint);
        canvas.drawLine(axisXStart, axisYEnd, axisXEnd, axisYEnd, axisPaint);


        // Draws the grid lines for the graph.
        canvas.drawLine(axisXStart, gridOne, axisXEnd, gridOne, gridPaint);
        canvas.drawLine(axisXStart, gridTwo, axisXEnd, gridTwo, gridPaint);
        canvas.drawLine(axisXStart, gridThree, axisXEnd, gridThree, gridPaint);

        // Draws the bars for the graph.
        canvas.drawRect(fatStart, fatTop - 2, fatEnd, axisYEnd - 2, fatPaint);
        canvas.drawRect(proteinStart, proteinTop - 2, proteinEnd, axisYEnd - 2, proteinPaint);
        canvas.drawRect(carbStart, carbTop - 2, carbEnd, axisYEnd - 2, carbPaint);

        // Draws the labels for the graph.
        canvas.drawText("Fat", fatStart, textY, textPaint);
        canvas.drawText("Protein", proteinStart, textY, textPaint);
        canvas.drawText("Carbs", carbStart, textY, textPaint);
    }
}
