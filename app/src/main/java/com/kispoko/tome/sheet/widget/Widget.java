
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.activity.sheet.ActionDialogFragment;
import com.kispoko.tome.error.UnknownVariantError;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.engine.RulesEngine;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.LinearLayoutOrientation;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;

import java.io.Serializable;

import static android.R.attr.label;


/**
 * Widget
 */
public abstract class Widget implements Model, Serializable
{

    // INTERFACE
    // ------------------------------------------------------------------------------------------

    abstract public View view();
    abstract public View getEditorView(Context context, RulesEngine rulesEngine);

    abstract public void runAction(String actionName, Context context, RulesEngine rulesEngine);
    abstract public String name();

    abstract public WidgetData data();


    // TYPE
    // ------------------------------------------------------------------------------------------

    public enum Type
    {
        TEXT,
        NUMBER,
        BOOLEAN,
        IMAGE,
        TABLE;


        public static Type fromString(String typeString)
                      throws InvalidDataException
        {
            return EnumUtils.fromString(Type.class, typeString);
        }


        public static Type fromYaml(Yaml yaml)
                      throws YamlException
        {
            String typeString = yaml.getString();
            try {
                return Type.fromString(typeString);
            } catch (InvalidDataException e) {
                throw YamlException.invalidEnum(new InvalidEnumError(typeString));
            }
        }

    }


    // METHODS
    // ------------------------------------------------------------------------------------------

    // > Views
    // ------------------------------------------------------------------------------------------

    /**
     * Widget layout.
     *
     * @return A LinearLayout that represents the outer-most container of a component view.
     */
    public LinearLayout linearLayout()
    {
        // [1] Variables
        // --------------------------------------------------------------------------------------

        final Context context = SheetManager.currentSheetContext();
        final Widget  widget  = this;

        // [2] Layout
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation         = LinearLayoutOrientation.VERTICAL;
        layout.backgroundResource  = R.drawable.bg_widget;
        layout.margin.left         = R.dimen.widget_layout_margins_horz;
        layout.margin.right        = R.dimen.widget_layout_margins_horz;

        layout.onClick             = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AppCompatActivity activity = (AppCompatActivity) context;
                ActionDialogFragment actionDialogFragment =
                        ActionDialogFragment.newInstance(widget);
                actionDialogFragment.show(activity.getSupportFragmentManager(),
                        actionDialogFragment.getTag());
            }
        };


        // [3] Content
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder contentLayout = new LinearLayoutBuilder();

        contentLayout.width               = LinearLayout.LayoutParams.WRAP_CONTENT;
        contentLayout.orientation         = LinearLayoutOrientation.VERTICAL;
        contentLayout.id                  = R.id.widget_content_layout;
        contentLayout.layoutGravity       = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        contentLayout.padding.top         = R.dimen.widget_content_padding_vert;
        contentLayout.padding.bottom      = R.dimen.widget_content_padding_vert;


        // [4] Label
        // --------------------------------------------------------------------------------------

        String  label     = widget.data().getFormat().getLabel().toUpperCase();
        Boolean showLabel = widget.data().getFormat().getShowLabel();

        TextViewBuilder labelView = new TextViewBuilder();
        labelView.gravity         = Gravity.CENTER_HORIZONTAL;
        labelView.text            = label;
        labelView.size            = R.dimen.widget_label_text_size;
        labelView.color           = R.color.dark_grey_1;
        labelView.font            = Font.sansSerifFontRegular(context);
        labelView.backgroundColor = R.color.dark_grey_6;
        labelView.padding.bottom  = R.dimen.widget_label_padding_vert;
        labelView.padding.top     = R.dimen.widget_label_padding_vert;


        // [5] Structure layout
        // --------------------------------------------------------------------------------------

        layout.child(contentLayout.linearLayout(context));

        if (showLabel)
            layout.child(labelView.textView(context));


        return layout.linearLayout(context);
    }


    // STATIC METHODS
    // ------------------------------------------------------------------------------------------

    public static Widget fromYaml(Yaml yaml)
                  throws YamlException
    {
        Type widgetType = Type.fromYaml(yaml.atKey("type"));

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
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(Type.class.getName())));
        }

        return null;
    }


}
