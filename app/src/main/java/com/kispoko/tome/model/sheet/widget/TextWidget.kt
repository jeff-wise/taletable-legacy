
package com.kispoko.tome.model.sheet.widget


import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.activity.sheet.dialog.openTextVariableEditorDialog
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.db.DB_WidgetTextFormat
import com.kispoko.tome.db.dbWidgetTextFormat
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.model.ProdType
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.Height
import com.kispoko.tome.model.sheet.style.TextFormat
import com.kispoko.tome.rts.sheet.*
import com.kispoko.tome.util.Util
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Text Widget Format
 */
data class TextWidgetFormat(override val id : UUID,
                            val widgetFormat : WidgetFormat,
                            val insideLabelFormat : TextFormat,
                            val outsideLabelFormat : TextFormat,
                            val valueFormat : TextFormat)
                             : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                insideLabelFormat : TextFormat,
                outsideLabelFormat : TextFormat,
                valueFormat : TextFormat)
        : this(UUID.randomUUID(),
               widgetFormat,
               insideLabelFormat,
               outsideLabelFormat,
               valueFormat)


    companion object : Factory<TextWidgetFormat>
    {

        private fun defaultWidgetFormat()       = WidgetFormat.default()
        private fun defaultInsideLabelFormat()  = TextFormat.default()
        private fun defaultOutsideLabelFormat() = TextFormat.default()
        private fun defaultValueFormat()        = TextFormat.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<TextWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::TextWidgetFormat,
                      // Widget Format
                      split(doc.maybeAt("widget_format"),
                            effValue(defaultWidgetFormat()),
                            { WidgetFormat.fromDocument(it) }),
                      // Inside Label Format
                      split(doc.maybeAt("inside_label_format"),
                            effValue(defaultInsideLabelFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Outside Label Format
                      split(doc.maybeAt("outside_label_format"),
                            effValue(defaultOutsideLabelFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Value Format
                      split(doc.maybeAt("value_format"),
                            effValue(defaultValueFormat()),
                            { TextFormat.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = TextWidgetFormat(defaultWidgetFormat(),
                                         defaultInsideLabelFormat(),
                                         defaultOutsideLabelFormat(),
                                         defaultValueFormat())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat().toDocument(),
        "inside_label_format" to this.insideLabelFormat().toDocument(),
        "outside_label_format" to this.outsideLabelFormat().toDocument(),
        "value_format" to this.valueFormat().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat


    fun insideLabelFormat() : TextFormat = this.insideLabelFormat


    fun outsideLabelFormat() : TextFormat = this.outsideLabelFormat


    fun valueFormat() : TextFormat = this.valueFormat


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun row() : DB_WidgetTextFormat = dbWidgetTextFormat(this.widgetFormat,
                                                                  this.insideLabelFormat,
                                                                  this.outsideLabelFormat,
                                                                  this.valueFormat)

}


object TextWidgetView
{

    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view(textWidget : TextWidget,
             format : TextWidgetFormat,
             sheetUIContext : SheetUIContext) : View
    {
        val layout = WidgetView.layout(format.widgetFormat(), sheetUIContext)

        layout.addView(this.mainView(textWidget, format, sheetUIContext))

        return layout
    }



    /**
     * The outermost view that holds the outside labels and the value view.
     *
     *                      top label
     *             --------------------------
     *             |                         |
     *  left label |        Value View       | right label
     *             |                         |
     *             ---------------------------
     *                    bottom label
     *
     */
    private fun mainView(textWidget : TextWidget,
                         format : TextWidgetFormat,
                         sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout = this.mainLayout(textWidget, sheetUIContext)

        // > Outside Top/Left Label View
//        if (format.outsideLabel() != null) {
//            if (format.outsideLabelFormat().position().isTop() ||
//                format.outsideLabelFormat().position().isLeft()) {
//                layout.addView(this.outsideLabelView(format, sheetUIContext))
//            }
//        }

        // > Value
        layout.addView(this.valueMainView(textWidget, format, sheetUIContext))

        // > Outside Bottom/Right Label View
//        if (format.outsideLabel() != null) {
//            if (format.outsideLabelFormat().position().isBottom() ||
//                format.outsideLabelFormat().position().isRight()) {
//                layout.addView(this.outsideLabelView(format, sheetUIContext))
//            }
//        }

        // On Click
        // -------------------------------------------------------------------------------------

        layout.setOnClickListener {
            val valueVar = textWidget.valueVariable(SheetContext(sheetUIContext))
            when (valueVar) {
                is effect.Val ->
                {
                    val textWidgetViewId = textWidget.viewId
                    if (textWidgetViewId != null)
                    {
                        val widgetReference = WidgetReference(textWidget.id, textWidgetViewId)
                        openTextVariableEditorDialog(valueVar.value,
                                                     UpdateTargetTextWidget(textWidget.id),
                                                     sheetUIContext)
                    }
                }
                is Err -> ApplicationLog.error(valueVar.error)
            }
        }


        return layout
    }


    private fun mainLayout(textWidget : TextWidget,
                           sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

//        layout.orientation          = textWidget.format().outsideLabelFormat()
//                                            .position().linearLayoutOrientation()

        layout.gravity              = textWidget.widgetFormat().elementFormat().alignment().gravityConstant()
        Log.d("***TEXTWIDGET", "${textWidget.widgetFormat().elementFormat().alignment()}" )

        layout.marginSpacing        = textWidget.widgetFormat().elementFormat().margins()


        return layout.linearLayout(sheetUIContext.context)
    }


    /**
     * The view that holds the value as well as the inside labels around the value.
     */
    private fun valueMainView(textWidget : TextWidget,
                              format : TextWidgetFormat,
                              sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout = this.valueMainViewLayout(format, sheetUIContext)

        // > Inside Top/Left Label View
//        if (format.insideLabel() != null && textWidget.description() == null) {
//            if (format.insideLabelFormat().position().isTop() ||
//                format.insideLabelFormat().position().isLeft()) {
//                layout.addView(this.insideLabelView(format, sheetUIContext))
//            }
//        }

        layout.addView(valueTextView(textWidget, format, sheetUIContext))

        // > Inside Bottom/Right Label View
//        if (format.insideLabel() != null && textWidget.description() == null) {
//            if (format.insideLabelFormat().position().isBottom() ||
//                format.insideLabelFormat().position().isRight()) {
//                layout.addView(this.insideLabelView(format, sheetUIContext))
//            }
//        }

        return layout
    }


    private fun valueMainViewLayout(format : TextWidgetFormat,
                                    sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

//        layout.orientation          = format.insideLabelFormat()
//                                            .position().linearLayoutOrientation()

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT

        val height = format.widgetFormat().elementFormat().height()
        when (height)
        {
            is Height.Wrap  -> layout.height   = LinearLayout.LayoutParams.WRAP_CONTENT
            is Height.Fixed -> layout.heightDp = height.value.toInt()
        }
        //layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

//        layout.backgroundColor      = SheetManager.color(
//                                                sheetUIContext.sheetId,
//                                                format.widgetFormat().backgroundColorTheme())

        layout.gravity              = format.valueFormat().elementFormat().alignment().gravityConstant() or
                                        Gravity.CENTER_VERTICAL

//        layout.backgroundResource   = format.valueFormat().height()
//                                            .resourceId(format.widgetFormat().corners())

        if (format.valueFormat().elementFormat().height().isWrap())
        {
            layout.padding.topDp    = format.valueFormat().elementFormat().padding().topDp()
            layout.padding.bottomDp = format.valueFormat().elementFormat().padding().bottomDp()
        }

//        if (format.widgetFormat.background() == BackgroundColor.EMPTY)
//            layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT

//        if (this.data().format().underlineThickness() > 0)
//        {
//            layout.backgroundColor    = this.data().format().underlineColor().resourceId();
//            layout.backgroundResource = R.drawable.bg_widget_bottom_border;
//        }

//        else if (this.data().format().background() != BackgroundColor.EMPTY &&
//                 this.data().format().background() != BackgroundColor.NONE)
//        {

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun valueTextView(textWidget : TextWidget,
                              format : TextWidgetFormat,
                              sheetUIContext: SheetUIContext) : TextView
    {
        val value = TextViewBuilder()

        textWidget.viewId   = Util.generateViewId()
        value.id            = textWidget.viewId

        value.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        value.layoutGravity = format.valueFormat().elementFormat().alignment().gravityConstant() or
                                Gravity.CENTER_VERTICAL
        value.gravity       = format.valueFormat().elementFormat().alignment().gravityConstant()

        value.text      = textWidget.valueString(SheetContext(sheetUIContext))

        format.valueFormat().styleTextViewBuilder(value, sheetUIContext)

        return value.textView(sheetUIContext.context)
    }


    private fun outsideLabelView(format : TextWidgetFormat,
                                 sheetUIContext: SheetUIContext) : TextView
    {
        val label = TextViewBuilder()

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.layoutGravity     = format.outsideLabelFormat().elementFormat().alignment().gravityConstant()

        //label.text              = format.outsideLabel()

        format.outsideLabelFormat().styleTextViewBuilder(label, sheetUIContext)

        label.marginSpacing     = format.outsideLabelFormat().elementFormat().margins()

        return label.textView(sheetUIContext.context)
    }


    private fun insideLabelView(format : TextWidgetFormat,
                                sheetUIContext: SheetUIContext) : TextView
    {
        val label               = TextViewBuilder()

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        //label.text              = format.insideLabel()

        format.insideLabelFormat().styleTextViewBuilder(label, sheetUIContext)

        label.marginSpacing     = format.insideLabelFormat().elementFormat().margins()

        return label.textView(sheetUIContext.context)
    }


}




//
//    // > Clicks
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * On a short click, open the value editor.
//     */
//    private void onTextWidgetShortClick(Context context)
//    {
//        SheetActivityOld sheetActivity = (SheetActivityOld) context;
//
//        switch (this.valueVariable().kind())
//        {
//
//            // OPEN the Quick Text Edit Dialog
//            case LITERAL:
//                // If the string is short, edit in DIALOG
//                if (this.value().length() < 145)
//                {
//                    TextEditorDialogFragment textDialog =
//                            TextEditorDialogFragment.forTextWidget(this);
//                    textDialog.show(sheetActivity.getSupportFragmentManager(), "");
//                }
//                // ...otherwise, edit in ACTIVITY
//                else
//                {
//                    Intent intent = new Intent(context, TextEditorActivity.class);
//                    intent.putExtra("text_widget", this);
//                    context.startActivity(intent);
//                }
//                break;
//
//            // OPEN the Choose Value Set Dialog
//            case VALUE:
//

//            case PROGRAM:
//                break;
//        }
//    }
//
//



//
//
/// ** Initialize
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Initialize the text widget.
//     */
//    @Override
//    public void initialize(GroupParent groupParent, Context context)
//    {
//        // [1] Initialize the value variable
//        // --------------------------------------------------------------------------------------
//
//        // > If the variable is non-null
//        if (!this.valueVariable.isNull())
//        {
//            this.valueVariable().initialize();
//
//            this.valueVariable().setOnUpdateListener(new Variable.OnUpdateListener() {
//                @Override
//                public void onUpdate() {
//                    onValueUpdate();
//                }
//            });
//
//            // > Add to the state
//            State.addVariable(this.valueVariable());
//        }
//
//        // [2] Initialize the helper variables
//        // -------------------------------------------------------------------------------------
//
//        for (VariableUnion variableUnion : this.variables()) {
//            State.addVariable(variableUnion);
//        }
//
//    }
//
//
//    // INTERNAL
//    // -----------------------------------------------------------------------------------------
//
//    // > Initialize
//    // -----------------------------------------------------------------------------------------
//
//    private void initializeTextWidget()
//    {
//        // [1] Apply default format values
//        // -------------------------------------------------------------------------------------
//
//        // ** Alignment
//        if (this.data().format().alignmentIsDefault())
//            this.data().format().setAlignment(Alignment.CENTER);
//
//        // ** Background
//        if (this.data().format().backgroundIsDefault())
//            this.data().format().setBackground(BackgroundColor.NONE);
//
//        // ** Corners
//        if (this.data().format().cornersIsDefault())
//            this.data().format().setCorners(Corners.SMALL);
//
//        // ** Underline Thickness
//        if (this.data().format().underlineThicknessIsDefault())
//            this.data().format().setUnderlineThickness(0);
//
//
//        this.valueViewId = null;
//    }
//
//
//    // > Value Update
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * When the text widget's valueVariable is updated.
//     */
//    private void onValueUpdate()
//    {
//        if (this.valueViewId != null && !this.valueVariable.isNull())
//        {
//            Activity activity = (Activity) SheetManagerOld.currentSheetContext();
//            TextView textView = (TextView) activity.findViewById(this.valueViewId);
//
//            String value = this.value();
//
//            if (value != null)
//                textView.setText(value);
//        }
//    }
//
//

//    // UPDATE EVENT
//    // -----------------------------------------------------------------------------------------
//
//    public static class UpdateLiteralEvent
//    {
//
//        // PROPERTIES
//        // -------------------------------------------------------------------------------------
//
//        private UUID   widgetId;
//        private String newValue;
//
//
//        // CONSTRUCTORS
//        // -------------------------------------------------------------------------------------
//
//        public UpdateLiteralEvent(UUID widgetId, String newValue)
//        {
//            this.widgetId   = widgetId;
//            this.newValue   = newValue;
//        }
//
//        // API
//        // -------------------------------------------------------------------------------------
//
//        public UUID widgetId()
//        {
//            return this.widgetId;
//        }

//        public String newValue()
//        {
//            return this.newValue;
//        }
//
//    }

