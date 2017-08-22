
package com.kispoko.tome.model.sheet.widget


import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.IconFormat
import com.kispoko.tome.model.sheet.style.TextStyle
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.rts.sheet.SheetUIContext
import effect.effApply
import effect.effError
import effect.effValue
import effect.split
import lulo.document.DocDict
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Mechanic Widget Format
 */
data class MechanicWidgetFormat(override val id : UUID,
                                val widgetFormat : Comp<WidgetFormat>,
                                val headerStyle : Comp<TextStyle>,
                                val mechanicHeaderStyle : Comp<TextStyle>,
                                val mechanicSummaryStyle : Comp<TextStyle>)
                                 : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetFormat.name          = "widget_format"
        this.headerStyle.name           = "header_style"
        this.mechanicHeaderStyle.name   = "mechanic_header_style"
        this.mechanicSummaryStyle.name  = "mechanic_summary_style"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                headerStyle : TextStyle,
                mechanicHeaderStyle : TextStyle,
                mechanicSummaryStyle : TextStyle)
        : this(UUID.randomUUID(),
               Comp(widgetFormat),
               Comp(headerStyle),
               Comp(mechanicHeaderStyle),
               Comp(mechanicSummaryStyle))


    companion object : Factory<MechanicWidgetFormat>
    {

        val defaultWidgetFormat         = WidgetFormat.default()
        val defaultHeaderStyle          = TextStyle.default()
        val defaultMechanicHeaderStyle  = TextStyle.default()
        val defaultMechanicSummaryStyle = TextStyle.default()


        override fun fromDocument(doc : SpecDoc) : ValueParser<MechanicWidgetFormat> = when (doc)
        {
            is DocDict -> effApply(::MechanicWidgetFormat,
                                   // Widget Format
                                   split(doc.maybeAt("widget_format"),
                                         effValue(defaultWidgetFormat),
                                         { WidgetFormat.fromDocument(it) }),
                                   // Header Style
                                   split(doc.maybeAt("header_style"),
                                         effValue(defaultHeaderStyle),
                                         { TextStyle.fromDocument(it) }),
                                   // Mechanic Header Style
                                   split(doc.maybeAt("mechanic_header_style"),
                                         effValue(defaultMechanicHeaderStyle),
                                         { TextStyle.fromDocument(it) }),
                                   // Mechanic Summary Style
                                   split(doc.maybeAt("mechanic_summary_style"),
                                         effValue(defaultMechanicSummaryStyle),
                                         { TextStyle.fromDocument(it) })
                            )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = MechanicWidgetFormat(defaultWidgetFormat,
                                             defaultHeaderStyle,
                                             defaultMechanicHeaderStyle,
                                             defaultMechanicSummaryStyle)

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat.value

    fun headerStyle() : TextStyle = this.headerStyle.value

    fun mechanicHeaderStyle() : TextStyle = this.mechanicHeaderStyle.value

    fun mechanicSummaryStyle() : TextStyle = this.mechanicSummaryStyle.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "mechanic_widget_format"

    override val modelObject = this

}



class MechanicWidgetViewBuilder(val mechanicWidget : MechanicWidget,
                                val sheetUIContext : SheetUIContext)
{


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = WidgetView.layout(this.mechanicWidget.widgetFormat(), sheetUIContext)

        layout.addView(this.mainView())

        return layout
    }


    private fun mainView() : LinearLayout
    {
        val layout              = this.mainViewLayout()

        // Header
        GameManager.engine(sheetUIContext.gameId) apDo {
            val category = it.mechanicCategoryWithId(mechanicWidget.categoryId())
            if (category != null) {
                val headerString = category.label() + " Mechanics"
                layout.addView(this.headerView(headerString))
            }
        }

        // Mechanic List
        GameManager.engine(sheetUIContext.gameId) apDo {
            val mechanics = it.mechanicsInCategory(mechanicWidget.categoryId())
            mechanics.forEach {
                layout.addView(this.mechanicView(it.label(), it.summary()))
            }
        }

        return layout
    }


    private fun mainViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation          = LinearLayout.VERTICAL

        return layout.linearLayout(sheetUIContext.context)
    }


    // Header
    // -----------------------------------------------------------------------------------------

    private fun headerView(headerString : String) : TextView
    {
        val header              = TextViewBuilder()

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        header.text             = headerString

        mechanicWidget.format().headerStyle().styleTextViewBuilder(header, sheetUIContext)

        return header.textView(sheetUIContext.context)
    }


    // Mechanic
    // -----------------------------------------------------------------------------------------

    private fun mechanicView(headerString : String, summaryString : String) : LinearLayout
    {
        val layout = this.mechanicViewLayout()

        // Header
        layout.addView(this.mechanicHeaderView(headerString))

        // Summary
        layout.addView(this.mechanicSummaryView(summaryString))

        return layout
    }


    private fun mechanicViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun mechanicHeaderView(headerString : String) : TextView
    {
        val header              = TextViewBuilder()

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        header.text             = headerString

        mechanicWidget.format().mechanicHeaderStyle().styleTextViewBuilder(header, sheetUIContext)

        return header.textView(sheetUIContext.context)
    }


    private fun mechanicSummaryView(summaryString : String) : TextView
    {
        val header              = TextViewBuilder()

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        header.text             = summaryString

        mechanicWidget.format().mechanicSummaryStyle().styleTextViewBuilder(header, sheetUIContext)

        return header.textView(sheetUIContext.context)
    }


}


//    // > Views
//    // -----------------------------------------------------------------------------------------
//
//    private View widgetView(Context context)
//    {
//        LinearLayout layout = widgetViewLayout(context);
//
//        Set<Mechanic> mechanics = SheetManagerOld.currentSheet().engine().mechanicIndex()
//                                              .mechanicsInCategory(this.mechanicCategory(), true);
//
//        for (Mechanic mechanic : mechanics) {
//            layout.addView(mechanicView(mechanic.label(), mechanic.summary(), context));
//        }
//
//        return layout;
//    }
//
//
//    private LinearLayout widgetViewLayout(Context context)
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
//    private LinearLayout mechanicView(String nameText, String descriptionText, Context context)
//    {
//        // [1] Declarations
//        // -------------------------------------------------------------------------------------
//
//        LinearLayoutBuilder layout      = new LinearLayoutBuilder();
//        TextViewBuilder     name        = new TextViewBuilder();
//        TextViewBuilder     description = new TextViewBuilder();
//        LinearLayoutBuilder divider     = new LinearLayoutBuilder();
//
//        // [2] Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.orientation          = LinearLayout.VERTICAL;
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.margin.bottom        = R.dimen.widget_mechanic_item_margin_bottom;
//
//        layout.child(name)
//              .child(description)
//              .child(divider);
//
//        // [3 A] Name
//        // -------------------------------------------------------------------------------------
//
//        name.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
//        name.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        name.text                   = nameText;
//        name.font                   = Font.serifFontRegular(context);
//        name.color                  = R.color.dark_blue_hl_1;
//        name.size                   = R.dimen.widget_mechanic_name_text_size;
//
//        name.padding.left           = R.dimen.widget_mechanic_item_padding_horz;
//        name.padding.right          = R.dimen.widget_mechanic_item_padding_horz;
//
//        name.margin.bottom          = R.dimen.widget_mechanic_item_name_margin_bottom;
//
//        // [3 B] Description
//        // -------------------------------------------------------------------------------------
//
//        description.width           = LinearLayout.LayoutParams.WRAP_CONTENT;
//        description.height          = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        description.text            = descriptionText;
//        description.font            = Font.serifFontRegular(context);
//        description.size            = R.dimen.widget_mechanic_description_text_size;
//        description.color           = R.color.dark_blue_hl_8;
//
//        description.padding.left    = R.dimen.widget_mechanic_item_padding_horz;
//        description.padding.right   = R.dimen.widget_mechanic_item_padding_horz;
//
//        description.margin.bottom   = R.dimen.widget_mechanic_item_description_margin_bottom;
//
//        // [3 C] Divider
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
//}
