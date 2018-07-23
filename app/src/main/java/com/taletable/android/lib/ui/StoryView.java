
package com.taletable.android.lib.ui;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatTextView;

import com.taletable.android.model.sheet.widget.StoryPart;
import com.taletable.android.model.sheet.widget.StoryPartSpan;

import java.util.List;



/**
 * Story View
 */
public class StoryView extends AppCompatTextView
{


    List<StoryPart> parts;


    public StoryView(List<StoryPart> parts, Context context)
    {
        super(context);

        this.parts = parts;
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        Paint paint = getPaint();

        paint.setTextSize(20f);

        for (StoryPart part : this.parts)
        {
            if (part instanceof StoryPartSpan)
            {
                String s = ((StoryPartSpan) part).textString();
                canvas.drawText(s, 0, 30, paint);
            }
        }



    }


//    @Override
//    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
//
//        setMeasuredDimension(200, 200);
//    }


}
