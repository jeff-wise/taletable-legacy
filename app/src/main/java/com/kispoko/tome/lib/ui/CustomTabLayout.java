
package com.kispoko.tome.lib.ui;


import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kispoko.tome.model.sheet.style.TextFont;
import com.kispoko.tome.model.sheet.style.TextFontStyle;



public class CustomTabLayout extends TabLayout
{
    private Typeface mTypeface;

    private TextFont textFont = TextFont.Cabin.INSTANCE;
    private TextFontStyle textFontStyle = TextFontStyle.Medium.INSTANCE;

    public CustomTabLayout(Context context)
    {
        super(context);
        init(context);
    }

    public CustomTabLayout(Context context, TextFont textFont, TextFontStyle textFontStyle)
    {
        super(context);

        this.textFont = textFont;
        this.textFontStyle = textFontStyle;

        init(context);
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        mTypeface = Font.INSTANCE.typeface(this.textFont,
                                           this.textFontStyle,
                                           context);
    }

    @Override
    public void addTab(Tab tab, boolean setSelected)
    {
        super.addTab(tab, setSelected);

        ViewGroup mainView = (ViewGroup) getChildAt(0);
        ViewGroup tabView = (ViewGroup) mainView.getChildAt(tab.getPosition());

        int tabChildCount = tabView.getChildCount();
        for (int i = 0; i < tabChildCount; i++) {
            View tabViewChild = tabView.getChildAt(i);
            if (tabViewChild instanceof TextView) {
                TextView tabTextView = (TextView) tabViewChild;
                tabTextView.setTypeface(mTypeface, Typeface.NORMAL);
//                tabTextView.setText(tabTextView.getText().toString().toLowerCase());
            }
        }
    }

}
