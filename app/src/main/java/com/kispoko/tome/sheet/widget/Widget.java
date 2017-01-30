
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
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.sheet.widget.util.WidgetBackground;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;

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

    abstract public void initialize();


    // TYPE
    // ------------------------------------------------------------------------------------------

    public enum Type
    {
        TEXT,
        NUMBER,
        BOOLEAN,
        IMAGE,
        TABLE,
        ACTION;


        public static Type fromString(String typeString)
                      throws InvalidDataException
        {
            return EnumUtils.fromString(Type.class, typeString);
        }


        public static Type fromYaml(YamlParser yaml)
                      throws YamlParseException
        {
            String typeString = yaml.getString();
            try {
                return Type.fromString(typeString);
            } catch (InvalidDataException e) {
                throw YamlParseException.invalidEnum(new InvalidEnumError(typeString));
            }
        }


        public int stringLabelResourceId()
        {
            switch (this)
            {
                case TEXT:
                    return R.string.widget_text;
                case NUMBER:
                    return R.string.widget_number;
                case BOOLEAN:
                    return R.string.widget_boolean;
                case TABLE:
                    return R.string.widget_table;
                case IMAGE:
                    return R.string.widget_image;
                case ACTION:
                    return R.string.widget_action;
            }

            return 0;
        }

    }


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
    public LinearLayout layout(boolean readStyle, final Context context)
    {
        // [1 A] Declarations
        // --------------------------------------------------------------------------------------

        final Widget widget  = this;

        String label = widget.data().format().label();
        if (label != null)
            label = label.toUpperCase();

        // [1 B] Views
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        LinearLayoutBuilder contentLayout = new LinearLayoutBuilder();
        TextViewBuilder labelView = new TextViewBuilder();

        // [2 A] Layout
        // --------------------------------------------------------------------------------------

        layout.orientation         = LinearLayout.VERTICAL;

        setWidgetBackgroundResource(layout);

        layout.onClick             = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                widget.runPrimaryAction();
            }
        };

        layout.onLongClick         = new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                AppCompatActivity activity = (AppCompatActivity) context;
                ActionDialogFragment actionDialogFragment =
                        ActionDialogFragment.newInstance(widget);
                actionDialogFragment.show(activity.getSupportFragmentManager(),
                        actionDialogFragment.getTag());
                return true;
            }
        };

        layout.child(contentLayout);

        if (label != null)
            layout.child(labelView);

        // [2 B] Content
        // --------------------------------------------------------------------------------------

        contentLayout.orientation         = LinearLayout.VERTICAL;
        contentLayout.id                  = R.id.widget_content_layout;

        if (readStyle)
        {
            contentLayout.layoutGravity       = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
            contentLayout.padding.top         = R.dimen.widget_content_read_padding_vert;
            contentLayout.padding.bottom      = R.dimen.widget_content_read_padding_vert;
            contentLayout.width               = LinearLayout.LayoutParams.WRAP_CONTENT;
        }
        else
        {
            contentLayout.width               = LinearLayout.LayoutParams.MATCH_PARENT;
        }

        // [2 C] Label
        // --------------------------------------------------------------------------------------

        labelView.gravity         = Gravity.CENTER_HORIZONTAL;
        labelView.text            = label;
        labelView.size            = R.dimen.widget_label_text_size;
        labelView.color           = R.color.dark_blue_hl_9;
        labelView.font            = Font.sansSerifFontRegular(context);
        labelView.backgroundColor = R.color.dark_blue_4;
        labelView.padding.bottom  = R.dimen.widget_label_padding_vert;
        labelView.padding.top     = R.dimen.widget_label_padding_vert;

        return layout.linearLayout(context);
    }


    private void setWidgetBackgroundResource(LinearLayoutBuilder layout)
    {
        WidgetBackground background = this.data().format().background();

        switch (background)
        {
            case NONE:
                // DO NOTHING
                break;
            case LIGHT:
                layout.backgroundResource = R.drawable.bg_widget_light;
                break;
            case MEDIUM:
                layout.backgroundResource = R.drawable.bg_widget_medium;
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
            case ACTION:
                return ActionWidget.fromYaml(yaml);
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(Type.class.getName())));
        }

        return null;
    }


}
