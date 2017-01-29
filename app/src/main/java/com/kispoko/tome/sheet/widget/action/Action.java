
package com.kispoko.tome.sheet.widget.action;


import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.Widget;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.ImageViewBuilder;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;



/**
 * Action Type
 */
public enum Action implements ToYaml
{

    // VALUES
    // ------------------------------------------------------------------------------------------

    EDIT,
    CHOOSE,
    CHOOSE_IMAGE,
    SHARE,
    GENERATE;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static Action fromString(String typeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(Action.class, typeString);
    }


    /**
     * Creates an Action Type from its Yaml representation.
     * @param yaml The Yaml parser.
     * @return A new Action Type.
     * @throws YamlParseException
     */
    public static Action fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return null;

        String typeString = yaml.getString();
        try {
            return Action.fromString(typeString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(typeString));
        }
    }


    public static Action fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            Action action = Action.fromString(enumString);
            return action;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
        }
    }


    // TO YAML
    // ------------------------------------------------------------------------------------------

    /**
     * The Action's yaml string representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.string(this.name().toLowerCase());
    }


    // VIEW
    // ------------------------------------------------------------------------------------------

    /**
     * The Action view. The view is a row in the action diagog fragment which explains an action
     * the user can do on a widget. Clicking the action view executes that action.
     * @param action The action type.
     * @param widget The widget the action is for.
     * @param dialog The diagog fragment that is the parent of the action.
     * @return The action Linear Layout view.
     */
    public static LinearLayout view(Action action, Widget widget, DialogFragment dialog)
    {
        // [1] Setup / Declarations
        // --------------------------------------------------------------------------------------

        Context context = SheetManager.currentSheetContext();

        // [2] Structure
        // --------------------------------------------------------------------------------------

        LinearLayout layout = actionLayout(action, widget, dialog, context);

        ImageView actionIconView = iconView(action, context);
        TextView actionTextView = actionTextView(action, context);

        layout.addView(actionIconView);
        layout.addView(actionTextView);

        return layout;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    // > Label
    // ------------------------------------------------------------------------------------------

    private static String label(Action action)
    {
        switch (action)
        {
            case EDIT:
                return "Edit";
            case GENERATE:
                return "Generate";
        }
        return "";
    }


    // > Views
    // ------------------------------------------------------------------------------------------

    private static LinearLayout actionLayout(final Action action,
                                             final Widget widget,
                                             final DialogFragment dialog,
                                             Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.height          = R.dimen.action_height;
        layout.width           = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.orientation     = LinearLayout.HORIZONTAL;
        layout.padding.left    = R.dimen.action_padding_left;
        layout.padding.top     = R.dimen.action_padding_vert;
        layout.padding.bottom  = R.dimen.action_padding_vert;
        layout.gravity         = Gravity.CENTER_VERTICAL;

        layout.onClick         = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //widget.runAction(action);
                //dialog.dismiss();
            }
        };

        return layout.linearLayout(context);
    }


    /**
     * The action text viewa.
     * @param context The context.
     * @return The action TextView.
     */
    private static TextView actionTextView(Action action, Context context)
    {
        TextViewBuilder view = new TextViewBuilder();

        view.height  = LinearLayout.LayoutParams.WRAP_CONTENT;
        view.width   = LinearLayout.LayoutParams.WRAP_CONTENT;
        view.text    = label(action);
        view.color   = R.color.light_grey_8;
        view.font    = Font.serifFontBold(context);
        view.size    = R.dimen.action_text_size;

        return view.textView(context);
    }


    public static ImageView iconView(Action action, Context context)
    {
        ImageViewBuilder view = new ImageViewBuilder();

        view.height       = LinearLayout.LayoutParams.WRAP_CONTENT;
        view.width        = R.dimen.action_icon_width;
        view.margin.right = R.dimen.action_icon_margin_right;

        // Set drawable for action
        switch (action)
        {
            case EDIT:
                view.image = R.drawable.ic_action_edit;
                break;
            case GENERATE:
                view.image = R.drawable.ic_action_generate;
                break;
        }

        return view.imageView(context);
    }



}
