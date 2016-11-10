
package com.kispoko.tome.sheet.widget;


import android.content.Context;

import com.kispoko.tome.rules.Rules;



/**
 * Widget
 */
public interface Widget
{


   // abstract public View getDisplayView(Context context, Rules rules);
    //abstract public View getEditorView(Context context, Rules rules);

    void runAction(String actionName, Context context, Rules rules);
    String widgetName();

//    abstract public void save(UUID callerTrackerId);
//    abstract public void load(UUID callerTrackerId);

}
