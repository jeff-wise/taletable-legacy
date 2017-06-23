
package com.kispoko.tome.model.sheet.widget


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.style.TextStyle
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.DocDict
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.util.*



/**
 * List Widget Format
 */
data class ListWidgetFormat(override val id : UUID,
                            val widgetFormat : Comp<WidgetFormat>,
                            val listStyle : Func<TextStyle>,
                            val annotationStyle : Func<TextStyle>) : Model
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetFormat.name      = "widget_format"
        this.listStyle.name         = "list_style"
        this.annotationStyle.name   = "annotation_style"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ListWidgetFormat>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<ListWidgetFormat> = when (doc)
        {
            is DocDict -> effApply(::ListWidgetFormat,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Widget Format
                                   doc.at("widget_format") ap {
                                       effApply(::Comp, WidgetFormat.fromDocument(it))
                                   },
                                   // List Style
                                   doc.at("list_style") ap {
                                       effApply(::Comp, TextStyle.fromDocument(it))
                                   },
                                   // Annotation Style
                                   doc.at("annotation_style") ap {
                                       effApply(::Comp, TextStyle.fromDocument(it))
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "list_widget_format"

    override val modelObject = this

}



//
//
//    // INTERNAL
//    // -----------------------------------------------------------------------------------------
//
//    // > Initialize
//    // -----------------------------------------------------------------------------------------
//
//    private void initializeListWidget()
//    {
//        // [1] Set default format values
//        // -------------------------------------------------------------------------------------
//
//        // ** Background
//        if (this.data().format().backgroundIsDefault())
//            this.data().format().setBackground(BackgroundColor.NONE);
//    }
//
//
//    // > Views
//    // -----------------------------------------------------------------------------------------
//
//    private LinearLayout viewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation          = LinearLayout.VERTICAL;
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout listView(Context context)
//    {
//        LinearLayout layout = listViewLayout(context);
//
//        int itemIndex = 0;
//        for (VariableUnion variableUnion : this.values())
//        {
//            String itemValue = null;
//
//            try {
//                itemValue = variableUnion.variable().valueString();
//            }
//            catch (NullVariableException exception) {
//                ApplicationFailure.nullVariable(exception);
//            }
//
//            // > Label
//            String itemLabel = null;
//            if (variableUnion.type() == VariableType.TEXT)
//            {
//                TextVariable textVariable = variableUnion.textVariable();
//                if (textVariable.kind() == TextVariable.Kind.VALUE) {
//                    Dictionary dictionary = SheetManagerOld.currentSheet().engine().dictionary();
//                    Value value = dictionary.value(textVariable.valueReference());
//                    if (value != null)
//                        itemLabel = value.description();
//                }
//            }
//
//            if (itemValue != null)
//            {
//                itemIndex += 1;
//                layout.addView(listItemView(itemValue, itemLabel, itemIndex, context));
//            }
//        }
//
//        return layout;
//    }
//
//
//    private LinearLayout listViewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation          = LinearLayout.VERTICAL;
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout listItemView(String itemValue,
//                                      String itemLabel,
//                                      final int itemIndex,
//                                      final Context context)
//    {
//        // [1] Declarations
//        // -------------------------------------------------------------------------------------
//
//        LinearLayoutBuilder layout      = new LinearLayoutBuilder();
//
//        LinearLayoutBuilder valueLayout = new LinearLayoutBuilder();
//
//        TextViewBuilder     item        = new TextViewBuilder();
//        TextViewBuilder     annotation  = new TextViewBuilder();
//
//        LinearLayoutBuilder divider     = new LinearLayoutBuilder();
//
//        // [2] Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.orientation          = LinearLayout.VERTICAL;
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.onClick              = new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                onListWidgetShortClick(itemIndex, context);
//            }
//        };
//
//        layout.child(valueLayout)
//              .child(divider);
//
//        // [3] Value Layout
//        // -------------------------------------------------------------------------------------
//
//        valueLayout.orientation     = LinearLayout.HORIZONTAL;
//        valueLayout.width           = LinearLayout.LayoutParams.MATCH_PARENT;
//        valueLayout.height          = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        valueLayout.child(item)
//                   .child(annotation);
//
//        // [4 A] Item
//        // -------------------------------------------------------------------------------------
//
//        item.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
//        item.height             = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        item.text               = itemValue;
//
//        this.format().itemStyle().styleTextViewBuilder(item, context);
//
//        item.padding.left       = R.dimen.widget_list_item_value_padding_left;
//        item.padding.top        = R.dimen.widget_list_item_padding_vert;
//        item.padding.bottom     = R.dimen.widget_list_item_padding_vert;
//
//        // [4 B] Inline Label
//        // -------------------------------------------------------------------------------------
//
//        annotation.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
//        annotation.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        this.format().annotationStyle().styleTextViewBuilder(annotation, context);
//
//        annotation.text             = itemLabel;
//
//        annotation.margin.left      = R.dimen.widget_list_inline_label_margin_left;
//
//        // [5] Divider
//        // -------------------------------------------------------------------------------------
//
//        divider.orientation          = LinearLayout.HORIZONTAL;
//        divider.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        divider.height               = R.dimen.one_dp;
//
//        divider.backgroundColor      = R.color.dark_blue_4;
//
//
//        return layout.linearLayout(context);
//    }
//
//
//    // > Clicks
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * On a short click, open the value editor.
//     */
//    private void onListWidgetShortClick(Integer itemClicked, Context context)
//    {
//        SheetActivityOld sheetActivity = (SheetActivityOld) context;
//
//        ListWidgetDialogFragment dialog = ListWidgetDialogFragment.newInstance(this, itemClicked);
//        dialog.show(sheetActivity.getSupportFragmentManager(), "");
//    }
//

