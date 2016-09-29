
package com.kispoko.tome.component;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Component Interface
 */
public interface ComponentI
{
    // View getView(Context context);
    //String getLabel();

    String getName();

    View getView(Context context);
}
