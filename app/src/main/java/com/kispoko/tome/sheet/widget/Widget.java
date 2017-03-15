
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.error.UnknownVariantError;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.sheet.group.GroupParent;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;



/**
 * Widget
 */
public abstract class Widget implements Model, ToYaml, Serializable
{

    // INTERFACE
    // ------------------------------------------------------------------------------------------

    abstract public View view(boolean rowhasLabel, Context context);

    abstract public WidgetData data();

    abstract public void initialize(GroupParent groupParent);


    // > State
    // ------------------------------------------------------------------------------------------


    // > Views
    // ------------------------------------------------------------------------------------------

    /**
     * Widget layout.
     *
     * @return A LinearLayout that represents the outer-most container of a component view.
     */
    public LinearLayout layout(boolean rowHasLabel, final Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;
        layout.width            = 0;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.weight       = this.data().format().width().floatValue();

//        layout.margin.left      = R.dimen.widget_margin_horz;
//        layout.margin.right     = R.dimen.widget_margin_horz;

        return layout.linearLayout(context);
    }


    // STATIC METHODS
    // ------------------------------------------------------------------------------------------

    public static Widget fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        WidgetType widgetType = WidgetType.fromYaml(yaml.atKey("type"));

        switch (widgetType) {
            case TEXT:
                return TextWidget.fromYaml(yaml);
            case NUMBER:
                return NumberWidget.fromYaml(yaml);
            case BOOLEAN:
                return BooleanWidget.fromYaml(yaml);
            case IMAGE:
                return ImageWidget.fromYaml(yaml);
            case TABLE:
                return TableWidget.fromYaml(yaml);
            case ACTION:
                return ActionWidget.fromYaml(yaml);
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(WidgetType.class.getName())));
        }

        return null;
    }


}
