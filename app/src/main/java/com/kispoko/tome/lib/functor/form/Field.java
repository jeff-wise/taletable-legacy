
package com.kispoko.tome.lib.functor.form;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.ImageViewBuilder;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;


/**
 * Functor Forms
 */
public class Field
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String       name;

    private LinearLayout formView;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    private Field(String name, LinearLayout formView)
    {
        this.name     = name;

        this.formView = formView;
    }


    /**
     * Create a text field.
     * @param name The field name.
     * @param value The field value as a string.
     * @param context The context.
     * @return The text field.
     */
    public static Field text(String name, String label, String value, Context context)
    {
        LinearLayout formView = textFieldView(label, value, context);

        return new Field(name, formView);
    }


    /**
     * Create a list field.
     * @param name The field name.
     * @param values The field value strings.
     * @param context The context.
     * @return The list field.
     */
    public static Field list(String name, String label, String values, Context context)
    {
        LinearLayout formView = listFieldView(label, values, context);

        return new Field(name, formView);
    }


    // API
    // -----------------------------------------------------------------------------------------

    // > Fields
    // -----------------------------------------------------------------------------------------

    /**
     * Text Field View.
     * @param name The field name.
     * @param value The field value string.
     * @param context The context.
     * @return The field Linear Layout.
     */
    private static LinearLayout textFieldView(String name,
                                             String value,
                                             Context context)
    {
        LinearLayout layout = viewLayout(context);

        // > Header
        layout.addView(fieldTypeView(R.drawable.ic_form_type_text, context));

        // > Data
        layout.addView(fieldDataView(name, value, context));

        return layout;
    }


    /**
     * List Field View.
     * @param name The field name.
     * @param context The context.
     * @return The field Linear Layout.
     */
    private static LinearLayout listFieldView(String name,
                                             String valuesString,
                                             Context context)
    {
        LinearLayout layout = viewLayout(context);

        // > Type
        layout.addView(fieldTypeView(R.drawable.ic_form_type_list, context));

        // > Data
        //String valuesString = Integer.toString(numberOfValues) + " values";
        layout.addView(fieldDataView(name, valuesString, context));

        return layout;
    }


    // API
    // -----------------------------------------------------------------------------------------

    /**
     * The field view.
     * @return The field Linear Layout.
     */
    public LinearLayout view()
    {
        return this.formView;
    }


    /**
     * The field name.
     * @return The name.
     */
    public String name()
    {
        return this.name;
    }


    // INTERNAL
    // -----------------------------------------------------------------------------------------

    // > Layout
    // -----------------------------------------------------------------------------------------

    private static LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.HORIZONTAL;

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity              = Gravity.CENTER_VERTICAL;

        layout.margin.topDp         = 15f;
        layout.margin.bottomDp      = 15f;

        return layout.linearLayout(context);
    }


    // > Type View
    // -----------------------------------------------------------------------------------------


    private static ImageView fieldTypeView(int iconId, Context context)
    {
        ImageViewBuilder icon = new ImageViewBuilder();

        icon.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image              = iconId;

        icon.margin.leftDp      = 15f;
        icon.margin.rightDp     = 15f;

        return icon.imageView(context);
    }




    // > Data View
    // -----------------------------------------------------------------------------------------

    private static LinearLayout fieldDataView(String descriptionString,
                                              String valueString,
                                              Context context)
    {
        LinearLayout layout = fieldDataViewLayout(context);

        // > Name
        layout.addView(fieldNameView(descriptionString, context));

        // > Value
        layout.addView(fieldValueView(valueString, context));

        return layout;
    }


    private static LinearLayout fieldDataViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    private static TextView fieldNameView(String nameString, Context context)
    {
        TextViewBuilder name = new TextViewBuilder();

        name.width               = LinearLayout.LayoutParams.WRAP_CONTENT;
        name.height              = LinearLayout.LayoutParams.WRAP_CONTENT;

        name.text                = nameString;

        name.font                = Font.serifFontRegular(context);
        name.color               = R.color.gold_light;
        name.sizeSp              = 16f;

        return name.textView(context);
    }


    private static View fieldValueView(String valueString, Context context)
    {
        if (valueString != null)
            return fieldValueTextView(valueString, context);
        else
            return fieldIncompleteValueView(context);
    }

    private static TextView fieldValueTextView(String valueString, Context context)
    {
        TextViewBuilder value = new TextViewBuilder();

        value.width         = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.height        = LinearLayout.LayoutParams.WRAP_CONTENT;

        value.text          = valueString;
        value.color         = R.color.dark_blue_hl_8;
        value.font          = Font.serifFontRegular(context);

        value.sizeSp        = 16f;

        value.margin.topDp  = 5f;

        return value.textView(context);
    }


    private static LinearLayout fieldIncompleteValueView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder     label  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.HORIZONTAL;

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity              = Gravity.CENTER_VERTICAL;

        layout.backgroundResource   = R.drawable.bg_edit_text;
        layout.backgroundColor      = R.color.dark_blue_9;

        layout.padding.leftDp       = 7f;
        layout.padding.rightDp      = 7f;
        layout.padding.topDp        = 8f;
        layout.padding.bottomDp     = 8f;

        layout.margin.topDp         = 12f;

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image              = R.drawable.ic_form_step_unfinished;
        icon.color              = R.color.red_light;

        icon.margin.rightDp     = 3f;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.textId            = R.string.incomplete;

        label.font              = Font.serifFontRegular(context);
        label.color             = R.color.red_light;
        label.sizeSp            = 16f;


        return layout.linearLayout(context);
    }


    /*
    // > Header View
    // -----------------------------------------------------------------------------------------

    private static LinearLayout fieldHeaderView(String fieldName,
                                                int iconId,
                                                boolean isEditMode,
                                                Context context)
    {
        LinearLayout layout = fieldHeaderViewLayout(context);

        layout.addView(fieldHeaderTitleView(fieldName, iconId, context));

        return layout;
    }


    private static LinearLayout fieldHeaderViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    // ** Header View Components
    // -----------------------------------------------------------------------------------------

    private static LinearLayout fieldHeaderTitleView(String fieldName,
                                                  int iconId,
                                                  Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder     label  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.HORIZONTAL;

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity              = Gravity.CENTER_VERTICAL;

        layout.margin.bottomDp      = 10f;
        layout.margin.leftDp        = 2f;

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image              = iconId;
        icon.color              = R.color.gold_medium_light;

        //icon.backgroundResource = R.drawable.bg_form_type;

        icon.margin.rightDp     = 5f;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.text              = fieldName;

        label.font              = Font.serifFontRegular(context);
        label.color             = R.color.gold_light;
        label.sizeSp            = 16f;


        return layout.linearLayout(context);
    }



    private static LinearLayout fieldInfoLastSavedView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder date   = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType       = LayoutType.RELATIVE;
        layout.width            = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT;

        layout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layout.addRule(RelativeLayout.CENTER_HORIZONTAL);

        layout.gravity          = Gravity.CENTER_VERTICAL;

        layout.margin.bottomDp  = 3.5f;

        layout.child(icon)
              .child(date);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image              = R.drawable.ic_form_field_last_saved;
        icon.color              = R.color.dark_blue_1;

        icon.margin.rightDp     = 3f;

        // [3 B] Date
        // -------------------------------------------------------------------------------------

        date.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        date.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        date.text               = "12/03/16";

        date.font               = Font.serifFontRegular(context);
        date.color              = R.color.dark_blue_1;
        date.sizeSp             = 11f;

        return layout.linearLayout(context);
    }

    */

}
