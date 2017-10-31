
package com.kispoko.tome.activity.sheet


import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent



class CustomViewPager(context : Context, attrs : AttributeSet) : ViewPager(context, attrs)
{

    override fun onInterceptTouchEvent(ev : MotionEvent?) : Boolean
    {
        if (ev != null)
        {
            Log.d("***VIEW PAGER", ev.action.toString())
            when (ev.action)
            {
                MotionEvent.ACTION_UP ->
                {
                    Log.d("***VIEW PAGER", "view pager action up")
                    SheetActivityGlobal.cancelLongPressRunnable()
                }
                MotionEvent.ACTION_OUTSIDE ->
                {
                    SheetActivityGlobal.cancelLongPressRunnable()
                }
                MotionEvent.ACTION_SCROLL ->
                {
                    SheetActivityGlobal.cancelLongPressRunnable()
                }
                MotionEvent.ACTION_CANCEL ->
                {
                    SheetActivityGlobal.cancelLongPressRunnable()
                }
            }
        }
        return false
    }


}