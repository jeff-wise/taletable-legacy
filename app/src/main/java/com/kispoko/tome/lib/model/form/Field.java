
package com.kispoko.tome.lib.model.form;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.form.TextFieldDialogFragment;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.ImageViewBuilder;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;

import java.io.Serializable;
import java.util.UUID;



/**
 * Functor Forms
 */
public class Field implements Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private UUID            modelId;

    private String          name;
    private String          label;
    private String          description;
    private String          value;

    private Type            type;
    private Mode            mode;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    private Field(UUID modelId,
                  String fieldName,
                  String fieldLabel,
                  String fieldDescription,
                  String fieldValue,
                  Type type,
                  Mode mode)
    {
        this.modelId    = modelId;

        this.name       = fieldName;
        this.label      = fieldLabel;
        this.description    = fieldDescription;
        this.value      = fieldValue;

        this.type       = type;
        this.mode       = mode;
    }


    /**
     * Create a text field.
     * @param fieldName The field name.
     * @param fieldLabel The field label.
     * @param fieldValue The field value as a string.
     * @return The text field.
     */
    public static Field text(UUID modelId,
                             String fieldName,
                             String fieldLabel,
                             String fieldValue)
    {
        return new Field(modelId, fieldName, fieldLabel, null, fieldValue, Type.TEXT, Mode.EDIT);
    }


    public static Field textNew(UUID modelId,
                                String fieldName,
                                String fieldLabel,
                                String fieldDescription)
    {
        return new Field(modelId, fieldName, fieldLabel, fieldDescription,
                         null, Type.TEXT, Mode.NEW);
    }


    /**
     * Create a list field.
     * @param values The field value strings.
     * @return The list field.
     */
    public static Field list(UUID modelId,
                             String fieldName,
                             String fieldLabel,
                             String values)
    {
        return new Field(modelId, fieldName, fieldLabel, null, values, Type.LIST, Mode.EDIT);
    }


    // API
    // -----------------------------------------------------------------------------------------

    /**
     * The field view.
     * @return The field Linear Layout.
     */
    public LinearLayout view(AppCompatActivity context)
    {
        switch (this.type)
        {
            case TEXT:
                switch (this.mode)
                {
                    case EDIT:
                        return this.editTextFieldView(context);
                    case NEW:
                        return this.newTextFieldView(context);
                }
            case LIST:
                return this.editListFieldView(context);
            default:
                return this.editTextFieldView(context);
        }
    }


    /**
     * The field name.
     * @return The name.
     */
    public String name()
    {
        return this.name;
    }


    /**
     * The field label.
     * @return The label.
     */
    public String label()
    {
        return this.label;
    }


    /**
     * The field description.
     * @return The description.
     */
    public String description()
    {
        return this.description;
    }


    // ** Value
    // -----------------------------------------------------------------------------------------

    /**
     * The field value string.
     * @return The value.
     */
    public String value()
    {
        return this.value;
    }


    /**
     * Update the field value.
     * @param value The new value
     */
    public void setValue(String value, LinearLayout fieldView)
    {
        this.value = value;

        TextView valueView = (TextView) fieldView.findViewById(R.id.field_value);
        valueView.setText(value);
    }


    // INTERNAL
    // -----------------------------------------------------------------------------------------

    // > New View
    // -----------------------------------------------------------------------------------------

    // ** Fields
    // -----------------------------------------------------------------------------------------

    private LinearLayout newTextFieldView(Context context)
    {
        LinearLayout layout = this.newViewLayout(context);

        // > Header
        layout.addView(this.newTextFieldHeaderView(context));

        // > Description
        layout.addView(this.newTextFieldDescriptionView(context));

        return layout;
    }


    // ** Layout
    // -----------------------------------------------------------------------------------------

    private LinearLayout newViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.margin.topDp         = 15f;
        layout.margin.bottomDp      = 15f;
        layout.margin.leftDp        = 10f;
        layout.margin.rightDp       = 10f;

        //layout.backgroundColor      = R.color.dark_blue_7;

        return layout.linearLayout(context);
    }


    // ** Header
    // -----------------------------------------------------------------------------------------

    private LinearLayout newTextFieldHeaderView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder     status = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.HORIZONTAL;

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity              = Gravity.CENTER_VERTICAL;

        layout.child(icon)
              .child(status);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.margin.rightDp         = 10f;

        // [3 B] Name
        // -------------------------------------------------------------------------------------

        status.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        status.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        status.text                 = this.label();

        status.font                 = Font.serifFontRegular(context);
        status.color                = R.color.dark_blue_hl_1;
        status.sizeSp               = 24f;


        return layout.linearLayout(context);
    }


    private TextView newTextFieldDescriptionView(Context context)
    {
        TextViewBuilder name = new TextViewBuilder();

        name.width          = LinearLayout.LayoutParams.MATCH_PARENT;
        name.height         = LinearLayout.LayoutParams.WRAP_CONTENT;

        name.text           = this.description();

        name.font           = Font.serifFontRegular(context);
        name.color          = R.color.dark_blue_hl_5;
        name.sizeSp         = 22f;

        return name.textView(context);
    }


    private TextView newTextFieldValueView(Context context)
    {
        TextViewBuilder value = new TextViewBuilder();

        value.width                 = LinearLayout.LayoutParams.MATCH_PARENT;
        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        value.text                  = this.value();

        value.font                  = Font.serifFontRegular(context);
        value.color                 = R.color.dark_blue_hl_5;
        value.sizeSp                = 18f;

        return value.textView(context);
    }


    // > Edit View
    // -----------------------------------------------------------------------------------------

    // ** Fields
    // -----------------------------------------------------------------------------------------

    /**
     * Text Field View.
     * @param context The context.
     * @return The field Linear Layout.
     */
    private LinearLayout editTextFieldView(final AppCompatActivity context)
    {
        LinearLayout layout = this.editViewLayout(context);

        // > Header
        layout.addView(editFieldTypeView(R.drawable.ic_form_type_text, context));

        // > Data
        layout.addView(editFieldDataView(context));


        final Field thisField = this;
        layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                TextFieldDialogFragment dialog =
                        TextFieldDialogFragment.newInstance(modelId, thisField);
                dialog.show(context.getSupportFragmentManager(), "");
            }
        });


        return layout;
    }


    /**
     * List Field View.
     * @param context The context.
     * @return The field Linear Layout.
     */
    private LinearLayout editListFieldView(final AppCompatActivity context)
    {
        LinearLayout layout = editViewLayout(context);

        // > Type
        layout.addView(editFieldTypeView(R.drawable.ic_form_type_list, context));

        // > Data
        layout.addView(editFieldDataView(context));

        return layout;
    }


    // ** Layout
    // -----------------------------------------------------------------------------------------

    private LinearLayout editViewLayout(Context context)
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


    private ImageView editFieldTypeView(int iconId, Context context)
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

    private LinearLayout editFieldDataView(Context context)
    {
        LinearLayout layout = editFieldDataViewLayout(context);

        // > Name
        layout.addView(this.editFieldNameView(context));

        // > Value
        layout.addView(this.editFieldValueTextView(context));

        return layout;
    }


    private LinearLayout editFieldDataViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    private TextView editFieldNameView(Context context)
    {
        TextViewBuilder name = new TextViewBuilder();

        name.width               = LinearLayout.LayoutParams.WRAP_CONTENT;
        name.height              = LinearLayout.LayoutParams.WRAP_CONTENT;

        name.text                = this.label();

        name.font                = Font.serifFontRegular(context);
        name.color               = R.color.gold_light;
        name.sizeSp              = 16f;

        return name.textView(context);
    }


    private TextView editFieldValueTextView(Context context)
    {
        TextViewBuilder value = new TextViewBuilder();

        value.id            = R.id.field_value;

        value.width         = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.height        = LinearLayout.LayoutParams.WRAP_CONTENT;

        value.text          = this.value;
        value.color         = R.color.dark_blue_hl_8;
        value.font          = Font.serifFontRegular(context);

        value.sizeSp        = 16f;

        value.margin.topDp  = 5f;

        return value.textView(context);
    }


    // TYPE
    // -----------------------------------------------------------------------------------------

    public enum Type
    {
        TEXT,
        LIST
    }


    // MODE
    // -----------------------------------------------------------------------------------------

    public enum Mode
    {
        EDIT,
        NEW
    }


    // UPDATE EVENTS
    // -----------------------------------------------------------------------------------------

    public static class TextUpdateEvent
    {

        // PROPERTIES
        // -------------------------------------------------------------------------------------

        private UUID    modelId;
        private String  fieldName;

        private String  text;


        // CONSTRUCTORS
        // -------------------------------------------------------------------------------------

        public TextUpdateEvent(UUID modelId, String fieldName, String text)
        {
            this.modelId    = modelId;
            this.fieldName  = fieldName;
            this.text       = text;
        }

        // API
        // -------------------------------------------------------------------------------------

        public UUID modelId()
        {
            return this.modelId;
        }

        public String fieldName()
        {
            return this.fieldName;
        }

        public String text()
        {
            return this.text;
        }

    }

}
