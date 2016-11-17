
package com.kispoko.tome.sheet.widget;


import android.content.Context;

import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.model.Model;



/**
 * Widget
 */
public interface Widget extends Model
{


   // abstract public View getDisplayView(Context context, Rules rules);
    //abstract public View getEditorView(Context context, Rules rules);

    void runAction(String actionName, Context context, Rules rules);
    String name();

    WidgetData data();

//    abstract public void save(UUID callerTrackerId);
//    abstract public void load(UUID callerTrackerId);


}
