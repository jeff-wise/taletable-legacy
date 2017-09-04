
package com.kispoko.tome.model.sheet.widget


import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.rts.sheet.SheetUIContext
import effect.effApply
import effect.effError
import effect.effValue
import effect.split
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Mechanic Widget Format
 */
data class MechanicWidgetFormat(override val id : UUID,
                                val widgetFormat : Comp<WidgetFormat>,
                                val viewType : Prim<MechanicWidgetViewType>,
                                val headerFormat : Comp<TextFormat>,
                                val mechanicHeaderFormat : Comp<TextFormat>,
                                val mechanicSummaryFormat : Comp<TextFormat>,
                                val mechanicFormat : Comp<ElementFormat>)
                                 : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetFormat.name          = "widget_format"
        this.viewType.name              = "view_tyep"
        this.headerFormat.name          = "header_style"
        this.mechanicHeaderFormat.name  = "mechanic_header_format"
        this.mechanicSummaryFormat.name = "mechanic_summary_format"
        this.mechanicFormat.name        = "mechanic_format"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                viewType : MechanicWidgetViewType,
                headerFormat : TextFormat,
                mechanicHeaderFormat : TextFormat,
                mechanicSummaryFormat : TextFormat,
                mechanicFormat : ElementFormat)
        : this(UUID.randomUUID(),
               Comp(widgetFormat),
               Prim(viewType),
               Comp(headerFormat),
               Comp(mechanicHeaderFormat),
               Comp(mechanicSummaryFormat),
               Comp(mechanicFormat))


    companion object : Factory<MechanicWidgetFormat>
    {

        val defaultWidgetFormat             = WidgetFormat.default()
        val defaultViewType                 = MechanicWidgetViewType.Boxes
        val defaultHeaderFormat             = TextFormat.default()
        val defaultMechanicHeaderFormat     = TextFormat.default()
        val defaultMechanicSummaryFormat    = TextFormat.default()
        val defaultMechanicFormat           = ElementFormat.default()


        override fun fromDocument(doc: SchemaDoc): ValueParser<MechanicWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply(::MechanicWidgetFormat,
                         // Widget Format
                         split(doc.maybeAt("widget_format"),
                               effValue(defaultWidgetFormat),
                               { WidgetFormat.fromDocument(it) }),
                         // View Type
                         split(doc.maybeAt("view_type"),
                               effValue<ValueError,MechanicWidgetViewType>(defaultViewType),
                               { MechanicWidgetViewType.fromDocument(it) }),
                         // Header Format
                         split(doc.maybeAt("header_format"),
                               effValue(defaultHeaderFormat),
                               { TextFormat.fromDocument(it) }),
                         // Mechanic Header Style
                         split(doc.maybeAt("mechanic_header_format"),
                               effValue(defaultMechanicHeaderFormat),
                               { TextFormat.fromDocument(it) }),
                         // Mechanic Summary Style
                         split(doc.maybeAt("mechanic_summary_format"),
                               effValue(defaultMechanicSummaryFormat),
                               { TextFormat.fromDocument(it) }),
                         // Mechanic Format
                         split(doc.maybeAt("mechanic_format"),
                               effValue(defaultMechanicFormat),
                                { ElementFormat.fromDocument(it) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = MechanicWidgetFormat(defaultWidgetFormat,
                                             defaultViewType,
                                             defaultHeaderFormat,
                                             defaultMechanicHeaderFormat,
                                             defaultMechanicSummaryFormat,
                                             defaultMechanicFormat)

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat.value

    fun headerFormat() : TextFormat = this.headerFormat.value

    fun mechanicHeaderFormat() : TextFormat = this.mechanicHeaderFormat.value

    fun mechanicSummaryFormat() : TextFormat = this.mechanicSummaryFormat.value

    fun mechanicFormat() : ElementFormat = this.mechanicFormat.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "mechanic_widget_format"

    override val modelObject = this

}


/**
 * View Type
 */
sealed class MechanicWidgetViewType : SQLSerializable, Serializable
{

    object Boxes : MechanicWidgetViewType()
    {
        override fun asSQLValue() : SQLValue = SQLText({"left"})
    }


    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<MechanicWidgetViewType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "boxes" -> effValue<ValueError,MechanicWidgetViewType>(
                                    MechanicWidgetViewType.Boxes)
                else    -> effError<ValueError,MechanicWidgetViewType>(
                                    UnexpectedValue("MechanicWidgetViewType", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

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

        mechanicWidget.format().headerFormat().style()
                      .styleTextViewBuilder(header, sheetUIContext)

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

        val format              = mechanicWidget.format().mechanicFormat()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId,
                                                     format.backgroundColorTheme())

        layout.corners          = format.corners()

        layout.marginSpacing    = format.margins()
        layout.paddingSpacing   = format.padding()

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun mechanicHeaderView(headerString : String) : TextView
    {
        val header              = TextViewBuilder()

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        header.text             = headerString

        mechanicWidget.format().mechanicHeaderFormat().style()
                      .styleTextViewBuilder(header, sheetUIContext)

        return header.textView(sheetUIContext.context)
    }


    private fun mechanicSummaryView(summaryString : String) : TextView
    {
        val header              = TextViewBuilder()

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        header.text             = summaryString

        mechanicWidget.format().mechanicSummaryFormat().style()
                      .styleTextViewBuilder(header, sheetUIContext)

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
