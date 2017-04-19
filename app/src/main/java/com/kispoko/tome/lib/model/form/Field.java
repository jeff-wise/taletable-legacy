
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


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    private Field(UUID modelId,
                  String fieldName,
                  String fieldLabel,
                  String fieldDescription,
                  String fieldValue,
                  Type type)
    {
        this.modelId        = modelId;

        this.name           = fieldName;
        this.label          = fieldLabel;
        this.description    = fieldDescription;
        this.value          = fieldValue;

        this.type           = type;
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
        return new Field(modelId, fieldName, fieldLabel, null, fieldValue, Type.TEXT);
    }


    public static Field model(UUID modelId,
                              String fieldName,
                              String fieldLabel,
                              String fieldDescription)
    {
        return new Field(modelId, fieldName, fieldLabel, fieldDescription, null, Type.MODEL);
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
        return new Field(modelId, fieldName, fieldLabel, null, values, Type.LIST);
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
                return this.textFieldView(context);
            case MODEL:
                return this.modelFieldview(context);
            case LIST:
                return this.listFieldView(context);
            default:
                return this.textFieldView(context);
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

    // ** Fields
    // -----------------------------------------------------------------------------------------

    /**
     * Text Field View.
     * @param context The context.
     * @return The field Linear Layout.
     */
    private LinearLayout textFieldView(final AppCompatActivity context)
    {
        LinearLayout layout = this.viewLayout(context);

        // > Header
        layout.addView(fieldTypeView(R.drawable.ic_form_type_text, context));

        // > Data
        layout.addView(fieldDataView(context));


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
     * Model Field View
     * @param context
     * @return
     */
    private LinearLayout modelFieldview(final AppCompatActivity context)
    {
        LinearLayout layout = this.viewLayout(context);

        // > Type
        layout.addView(this.fieldTypeView(R.drawable.ic_form_type_model, context));

        // > Data
        layout.addView(this.fieldDataView(context));

        return layout;
    }


    /**
     * List Field View.
     * @param context The context.
     * @return The field Linear Layout.
     */
    private LinearLayout listFieldView(final AppCompatActivity context)
    {
        LinearLayout layout = viewLayout(context);

        // > Type
        layout.addView(fieldTypeView(R.drawable.ic_form_type_list, context));

        // > Data
        layout.addView(fieldDataView(context));

        return layout;
    }


    // ** Layout
    // -----------------------------------------------------------------------------------------

    private LinearLayout viewLayout(Context context)
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


    private ImageView fieldTypeView(int iconId, Context context)
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

    private LinearLayout fieldDataView(Context context)
    {
        LinearLayout layout = fieldDataViewLayout(context);

        // > Name
        layout.addView(this.fieldNameView(context));

        // > Value / Description
        layout.addView(this.fieldValueTextView(context));

        return layout;
    }


    private LinearLayout fieldDataViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.margin.rightDp       = 10f;

        return layout.linearLayout(context);
    }


    private TextView fieldNameView(Context context)
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


    private TextView fieldValueTextView(Context context)
    {
        TextViewBuilder value = new TextViewBuilder();

        value.id            = R.id.field_value;

        value.width         = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.height        = LinearLayout.LayoutParams.WRAP_CONTENT;

        if (this.value() != null)
            value.text      = this.value;
        else
            value.text      = this.description;

        value.color         = R.color.dark_blue_hl_8;
        value.font          = Font.serifFontRegular(context);

        value.sizeSp        = 14f;

        value.margin.topDp  = 5f;

        return value.textView(context);
    }


    // TYPE
    // -----------------------------------------------------------------------------------------

    public enum Type
    {
        TEXT,
        MODEL,
        LIST
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
