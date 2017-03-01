
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.error.UnknownVariantError;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.sheet.group.GroupParent;
import com.kispoko.tome.sheet.BackgroundColor;
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



    // METHODS
    // ------------------------------------------------------------------------------------------

    public void runPrimaryAction()
    {
//        Action primaryAction = this.data().primaryAction();
//        this.runAction(primaryAction);
    }


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
        layout.weight           = this.data().format().width().floatValue();

//        if (this.data().format().label() == null && rowHasLabel) {
//            layout.padding.top  = R.dimen.widget_label_fill_padding;
//        }

        layout.margin.left      = R.dimen.widget_margin_horz;
        layout.margin.right     = R.dimen.widget_margin_horz;

        return layout.linearLayout(context);
    }


    private void setWidgetBackgroundResource(LinearLayoutBuilder layout)
    {
        BackgroundColor background = this.data().format().background();

        switch (background)
        {
            case NONE:
                // DO NOTHING
                break;
            case LIGHT:
                layout.backgroundResource = R.drawable.bg_widget_light;
                break;
            case DARK:
                layout.backgroundResource = R.drawable.bg_widget_dark_large_corners;
                break;
        }

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
