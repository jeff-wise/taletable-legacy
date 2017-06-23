
package com.kispoko.tome.model.sheet.widget


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.sheet.style.Spacing
import com.kispoko.tome.model.sheet.style.TextStyle
import effect.Err
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Expander Widget Label
 */
data class ExpanderLabel(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ExpanderLabel>
    {
        override fun fromDocument(doc: SpecDoc)
                      : ValueParser<ExpanderLabel> = when (doc)
        {
            is DocText -> effValue(ExpanderLabel(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}



/**
 * Button Widget Format
 */
data class ExpanderWidgetFormat(override val id : UUID,
                                val widgetFormat : Comp<WidgetFormat>,
                                val nameStyleClosed : Func<TextStyle>,
                                val nameStyleOpen : Func<TextStyle>,
                                val headerPadding : Func<Spacing>) : Model
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetFormat.name      = "widget_format"
        this.nameStyleClosed.name   = "name_style_closed"
        this.nameStyleOpen.name     = "name_style_open"
        this.headerPadding.name     = "header_padding"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ExpanderWidgetFormat>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<ExpanderWidgetFormat> = when (doc)
        {
            is DocDict -> effApply(::ExpanderWidgetFormat,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Widget Format
                                   doc.at("widget_format") ap {
                                       effApply(::Comp, WidgetFormat.fromDocument(it))
                                   },
                                   // Name Style Closed
                                   doc.at("name_style_closed") ap {
                                       effApply(::Comp, TextStyle.fromDocument(it))
                                   },
                                   // Name Style Open
                                   doc.at("name_style_open") ap {
                                       effApply(::Comp, TextStyle.fromDocument(it))
                                   },
                                   // Header Padding
                                   doc.at("header_padding") ap {
                                       effApply(::Comp, Spacing.fromDocument(it))
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

    override val name : String = "expander_widget_format"

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
//    private void initializeExpanderWidget()
//    {
//        // [1] Apply default formats
//        // -------------------------------------------------------------------------------------
//
//        if (this.data().format().alignmentIsDefault())
//            this.data().format().setAlignment(Alignment.CENTER);
//
//        if (this.data().format().backgroundIsDefault())
//            this.data().format().setBackground(BackgroundColor.NONE);
//
//        if (this.data().format().cornersIsDefault())
//            this.data().format().setCorners(Corners.NONE);
//    }
//
//
//    // > Views
//    // -----------------------------------------------------------------------------------------
//
//    private LinearLayout widgetView(boolean rowHasLabel, Context context)
//    {
//        LinearLayout layout = this.layout(rowHasLabel, context);
//
//        layout.addView(mainView(context));
//
//        return layout;
//    }
//
//
//    private LinearLayout mainView(Context context)
//    {
//        LinearLayout layout = mainViewLayout(context);
//
//        // > Header View
//        // -----------------------------------------------------------
//        layout.addView(headerView(context));
//
//        return layout;
//    }
//
//
//    private LinearLayout mainViewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation          = LinearLayout.VERTICAL;
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.backgroundColor      = this.data().format().background().colorId();
//        layout.backgroundResource   = this.data().format().corners().resourceId();
//
//
//        Spacing margins = this.data().format().margins();
//
//        if (this.data().format().elevation() != null)
//        {
//            layout.elevation        = this.data().format().elevation().floatValue();
//
//            if (margins.bottom() < 3)
//                margins.setBottom(3);
//
//            if (margins.left() < 3)
//                margins.setLeft(3);
//
//            if (margins.right() < 3)
//                margins.setRight(3);
//        }
//
//        layout.marginSpacing        = margins;
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout headerView(Context context)
//    {
//        LinearLayout layout = headerViewLayout(context);
//
//        this.onExpanderClick(layout);
//
//        // > Name View
//        layout.addView(nameView(context));
//
//
//        return layout;
//    }
//
//
//    private LinearLayout headerViewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation      = LinearLayout.VERTICAL;
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.paddingSpacing   = this.format().headerPadding();
//
//        return layout.linearLayout(context);
//    }
//
//    private RelativeLayout nameView(Context context)
//    {
//        // [1] Declarations
//        // -------------------------------------------------------------------------------------
//
//        RelativeLayoutBuilder layout  = new RelativeLayoutBuilder();
//
//        TextViewBuilder       name    = new TextViewBuilder();
//        ImageViewBuilder      icon    = new ImageViewBuilder();
//
//        // [2] Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.gravity              = Gravity.CENTER_VERTICAL;
//
//        layout.addRule(RelativeLayout.CENTER_VERTICAL);
//
//        layout.child(name)
//              .child(icon);
//
//        // [3 A] Name
//        // -------------------------------------------------------------------------------------
//
//        name.layoutType         = LayoutType.RELATIVE;
//        name.width              = RelativeLayout.LayoutParams.WRAP_CONTENT;
//        name.height             = RelativeLayout.LayoutParams.WRAP_CONTENT;
//
//        name.text               = this.name();
//
//        name.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        name.addRule(RelativeLayout.CENTER_VERTICAL);
//
//        this.format().nameStyleClosed().styleTextViewBuilder(name, context);
//
//        // [3 B] Icon
//        // -------------------------------------------------------------------------------------
//
//        icon.layoutType         = LayoutType.RELATIVE;
//        icon.width              = RelativeLayout.LayoutParams.WRAP_CONTENT;
//        icon.height             = RelativeLayout.LayoutParams.WRAP_CONTENT;
//
//        icon.image              = R.drawable.ic_expander_more;
//
//        //icon.color              = this.format().nameStyleClosed().color().resourceId();
//        icon.color              = R.color.dark_blue_hl_8;
//
//        icon.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        icon.addRule(RelativeLayout.CENTER_VERTICAL);
//
//
//        return layout.relativeLayout(context);
//    }

