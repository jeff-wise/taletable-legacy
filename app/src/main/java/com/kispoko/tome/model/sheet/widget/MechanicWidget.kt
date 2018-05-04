
package com.kispoko.tome.model.sheet.widget


import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.entity.engine.mechanic.MechanicOptionDialog
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue8
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.ui.ImageViewBuilder
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.engine.mechanic.Mechanic
import com.kispoko.tome.model.engine.mechanic.MechanicCategoryId
import com.kispoko.tome.model.engine.mechanic.MechanicType
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.activeMechanicsInCategory
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.rts.entity.mechanicCategory
import com.kispoko.tome.util.Util
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Just
import java.io.Serializable
import java.util.*



/**
 * Mechanic Widget Format
 */
data class MechanicWidgetFormat(override val id : UUID,
                                val widgetFormat : WidgetFormat,
                                val viewType : MechanicWidgetViewType,
                                val mechanicFormat : ElementFormat,
                                val headerFormat : TextFormat,
                                val mechanicHeaderFormat : TextFormat,
                                val mechanicSummaryFormat : TextFormat,
                                val annotationFormat : TextFormat,
                                val optionElementFormat : ElementFormat,
                                val optionLabelFormat : TextFormat)
                                 : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                viewType : MechanicWidgetViewType,
                mechanicFormat : ElementFormat,
                headerFormat : TextFormat,
                mechanicHeaderFormat : TextFormat,
                mechanicSummaryFormat : TextFormat,
                annotationFormat : TextFormat,
                optionElementFormat : ElementFormat,
                optionLabelFormat : TextFormat)
        : this(UUID.randomUUID(),
               widgetFormat,
               viewType,
               mechanicFormat,
               headerFormat,
               mechanicHeaderFormat,
               mechanicSummaryFormat,
               annotationFormat,
               optionElementFormat,
               optionLabelFormat)


    companion object : Factory<MechanicWidgetFormat>
    {

        private fun defaultWidgetFormat()           = WidgetFormat.default()
        private fun defaultViewType()               = MechanicWidgetViewType.Boxes
        private fun defaultMechanicFormat()         = ElementFormat.default()
        private fun defaultHeaderFormat()           = TextFormat.default()
        private fun defaultMechanicHeaderFormat()   = TextFormat.default()
        private fun defaultMechanicSummaryFormat()  = TextFormat.default()
        private fun defaultAnnotationFormat()       = TextFormat.default()
        private fun defaultOptionElementFormat()    = ElementFormat.default()
        private fun defaultOptionLabelFormat()      = TextFormat.default()


        override fun fromDocument(doc: SchemaDoc): ValueParser<MechanicWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::MechanicWidgetFormat,
                      // Widget Format
                      split(doc.maybeAt("widget_format"),
                            effValue(defaultWidgetFormat()),
                            { WidgetFormat.fromDocument(it) }),
                      // View Type
                      split(doc.maybeAt("view_type"),
                            effValue<ValueError,MechanicWidgetViewType>(defaultViewType()),
                            { MechanicWidgetViewType.fromDocument(it) }),
                      // Mechanic Format
                      split(doc.maybeAt("mechanic_format"),
                            effValue(defaultMechanicFormat()),
                            { ElementFormat.fromDocument(it) }),
                      // Header Format
                      split(doc.maybeAt("header_format"),
                            effValue(defaultHeaderFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Mechanic Header Format
                      split(doc.maybeAt("mechanic_header_format"),
                            effValue(defaultMechanicHeaderFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Mechanic Summary Format
                      split(doc.maybeAt("mechanic_summary_format"),
                            effValue(defaultMechanicSummaryFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Annotation Format
                      split(doc.maybeAt("annotation_format"),
                            effValue(defaultAnnotationFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Option Element Format
                      split(doc.maybeAt("option_element_format"),
                            effValue(defaultOptionElementFormat()),
                            { ElementFormat.fromDocument(it) }),
                      // Option Label Format
                      split(doc.maybeAt("option_label_format"),
                            effValue(defaultOptionLabelFormat()),
                            { TextFormat.fromDocument(it) })
                )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = MechanicWidgetFormat(defaultWidgetFormat(),
                                             defaultViewType(),
                                             defaultMechanicFormat(),
                                             defaultHeaderFormat(),
                                             defaultMechanicHeaderFormat(),
                                             defaultMechanicSummaryFormat(),
                                             defaultAnnotationFormat(),
                                             defaultOptionElementFormat(),
                                             defaultOptionLabelFormat())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
            "widget_format" to this.widgetFormat.toDocument(),
            "view_type" to this.viewType.toDocument(),
            "mechanic_format" to this.mechanicFormat.toDocument(),
            "header_format" to this.headerFormat.toDocument(),
            "mechanic_header_format" to this.mechanicHeaderFormat.toDocument(),
            "mechanic_summary_format" to this.mechanicSummaryFormat.toDocument(),
            "option_element_format" to this.optionElementFormat().toDocument(),
            "option_label_format" to this.optionLabelFormat().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat


    fun viewType() : MechanicWidgetViewType = this.viewType


    fun mechanicFormat() : ElementFormat = this.mechanicFormat


    fun headerFormat() : TextFormat = this.headerFormat


    fun mechanicHeaderFormat() : TextFormat = this.mechanicHeaderFormat


    fun mechanicSummaryFormat() : TextFormat = this.mechanicSummaryFormat


    fun annotationFormat() : TextFormat = this.annotationFormat


    fun optionElementFormat() : ElementFormat = this.optionElementFormat


    fun optionLabelFormat() : TextFormat = this.optionLabelFormat


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetMechanicFormatValue =
        RowValue8(widgetMechanicFormatTable,
                  ProdValue(this.widgetFormat),
                  PrimValue(this.viewType),
                  ProdValue(this.mechanicFormat),
                  ProdValue(this.headerFormat),
                  ProdValue(this.mechanicHeaderFormat),
                  ProdValue(this.mechanicSummaryFormat),
                  ProdValue(this.optionElementFormat),
                  ProdValue(this.optionLabelFormat))

}


/**
 * View Type
 */
sealed class MechanicWidgetViewType : ToDocument, SQLSerializable, Serializable
{

    object Boxes : MechanicWidgetViewType()
    {
        override fun asSQLValue() : SQLValue = SQLText({"boxes"})

        override fun toDocument() = DocText("boxes")
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
                                val entityId : EntityId,
                                val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val mechanicsByCategoryId : MutableMap<MechanicCategoryId,MutableSet<Mechanic>> = mutableMapOf()

    init {
        activeMechanicsInCategory(mechanicWidget.categoryReference(), entityId) apDo { mechanics ->
            mechanics.forEach { mechanic ->
                val categoryId = mechanic.categoryId

                if (!mechanicsByCategoryId.containsKey(categoryId))
                    mechanicsByCategoryId.put(categoryId, mutableSetOf())

                val mechanicsSet = mechanicsByCategoryId[categoryId]!!
                mechanicsSet.add(mechanic)
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = WidgetView.layout(this.mechanicWidget.widgetFormat(), entityId, context)

        val viewId = Util.generateViewId()
        layout.id = viewId
        mechanicWidget.viewId = viewId

        this.updateView(layout)

        return layout
    }


    fun updateView(layout : LinearLayout)
    {
        val contentLayout = layout.findViewById<LinearLayout>(R.id.widget_content_layout)
        contentLayout.removeAllViews()
        contentLayout.addView(this.categoryListView())
    }


    private fun categoryListView() : LinearLayout
    {
        val layout = this.categoryListViewLayout()

        this.mechanicsByCategoryId.keys.forEach { categoryId ->
            layout.addView(this.categoryView(categoryId))
        }

        return layout
    }


    private fun categoryListViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation          = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun categoryView(categoryId : MechanicCategoryId) : LinearLayout
    {
        val layout              = this.categoryViewLayout()

        // Header
        mechanicCategory(categoryId, entityId) apDo { category ->
            val headerString = category.labelString() + " Mechanics"
            layout.addView(this.headerView(headerString))
        }

        // Mechanic List
        this.mechanicsByCategoryId[categoryId]?.forEach {
            when (it.mechanicType()) {
                is MechanicType.Auto -> {
                    layout.addView(this.mechanicView(it))
                }
                is MechanicType.OptionSelected -> {
//                    layout.addView(this.mechanicView(it))
                }
                is MechanicType.Option -> {
//                    layout.addView(this.optionMechanicView(it))
                }
            }
        }
//        GameManager.engine(sheetUIContext.gameId) apDo {
//            val mechanics = it.acti(mechanicWidget.categoryId())
//            mechanics.forEach {
//                layout.addView(this.mechanicView(it.labelString(), it.summaryString()))
//            }
//        }

        return layout
    }


    private fun categoryViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation          = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    // Header
    // -----------------------------------------------------------------------------------------

    private fun headerView(headerString : String) : LinearLayout
    {
        val layout = this.headerViewLayout()

        layout.addView(this.headerTextView(headerString))

        val bottomBorder = mechanicWidget.format().headerFormat().elementFormat().border().bottom()
        when (bottomBorder) {
            is Just -> {
                layout.addView(this.borderView(bottomBorder.value))
            }
        }

        return layout
    }


    private fun headerTextView(headerString : String) : TextView
    {
        val header              = TextViewBuilder()

        val format = mechanicWidget.format().headerFormat()

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        header.text             = headerString

        mechanicWidget.format().headerFormat()
                      .styleTextViewBuilder(header, entityId, context)

        header.marginSpacing    = format.elementFormat().margins()
        header.paddingSpacing   = format.elementFormat().padding()

        return header.textView(context)
    }


    private fun headerViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL


        return layout.linearLayout(context)
    }




    // MECHANIC AUTO
    // -----------------------------------------------------------------------------------------

    private fun mechanicView(mechanic : Mechanic) : LinearLayout
    {
        val layout = this.mechanicViewLayout()

        val innerLayout = this.mechanicViewInnerLayout()
        layout.addView(innerLayout)

        // Header
        innerLayout.addView(this.mechanicHeaderView(mechanic))

        // Summary
        innerLayout.addView(this.mechanicSummaryView(mechanic.summaryString()))

        val border = mechanicWidget.format().mechanicFormat().border()

        val bottomBorder = border.bottom()
        when (bottomBorder) {
            is Just -> {
                layout.addView(this.borderView(bottomBorder.value))
            }
        }

        return layout
    }


    private fun mechanicViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        val format              = mechanicWidget.format().mechanicFormat()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.backgroundColor  = colorOrBlack(format.backgroundColorTheme(), entityId)

        layout.corners          = format.corners()

        layout.marginSpacing    = format.margins()

        return layout.linearLayout(context)
    }


    private fun mechanicViewInnerLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        val format              = mechanicWidget.format().mechanicFormat()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.paddingSpacing   = format.padding()

        return layout.linearLayout(context)
    }


    private fun mechanicAnnotationView(annotationString : String) : TextView
    {
        val annotation          = TextViewBuilder()
        val format              = mechanicWidget.format().annotationFormat()

        annotation.width        = LinearLayout.LayoutParams.WRAP_CONTENT
        annotation.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        annotation.text         = annotationString

        format.styleTextViewBuilder(annotation, entityId, context)

        return annotation.textView(context)
    }


    private fun mechanicHeaderView(mechanic : Mechanic) : LinearLayout
    {
        val layout      = this.mechanicHeaderLayout()

        layout.addView(this.mechanicHeaderTextView(mechanic.labelString()))

        when (mechanic.mechanicType()) {
            is MechanicType.OptionSelected -> {
                layout.addView(this.mechanicOptionSelectedView())
            }
        }

        return layout
    }


    private fun mechanicHeaderLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.CENTER_VERTICAL

        return layout.linearLayout(context)
    }


    private fun mechanicHeaderTextView(headerString : String) : TextView
    {
        val header              = TextViewBuilder()

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        header.text             = headerString

        mechanicWidget.format().mechanicHeaderFormat()
                      .styleTextViewBuilder(header, entityId, context)

        return header.textView(context)
    }


    private fun mechanicOptionSelectedView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val icon            = ImageViewBuilder()

        val elementFormat = mechanicWidget.format().optionElementFormat()
        val textFormat = mechanicWidget.format().optionLabelFormat()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width        = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity      = Gravity.CENTER

        layout.backgroundColor  = colorOrBlack(elementFormat.backgroundColorTheme(), entityId)

        layout.padding.topDp     = 3f
        layout.padding.bottomDp     = 3f
        layout.padding.leftDp     = 6f
        layout.padding.rightDp     = 6f

        layout.corners      = Corners(2.0, 2.0, 2.0, 2.0)

        layout.margin.leftDp        = 7f

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp        = 17
        icon.heightDp       = 17

        icon.image          = R.drawable.icon_replace

        icon.color          = colorOrBlack(textFormat.colorTheme(), entityId)

        return layout.linearLayout(context)
    }


    private fun mechanicSummaryView(summaryString : String) : TextView
    {
        val header              = TextViewBuilder()

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        header.text             = summaryString

        mechanicWidget.format().mechanicSummaryFormat()
                      .styleTextViewBuilder(header, entityId, context)

        return header.textView(context)
    }


    // MECHANIC OPTION
    // -----------------------------------------------------------------------------------------

    private fun optionMechanicView(mechanic : Mechanic) : LinearLayout
    {
        val layout = this.optionMechanicViewLayout()

        // Header
        layout.addView(this.optionMechanicLabelView(mechanic.summaryString()))

        layout.setOnClickListener {
            val activity = context as AppCompatActivity
            val dialog  = MechanicOptionDialog.newInstance(mechanic.mechanicId(),
                                                           entityId)
            dialog.show(activity.supportFragmentManager, "")

        }

        return layout
    }


    private fun optionMechanicViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        val format              = mechanicWidget.format().optionElementFormat()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.backgroundColor  = colorOrBlack(format.backgroundColorTheme(), entityId)

        layout.corners          = format.corners()

        layout.marginSpacing    = format.margins()
        layout.paddingSpacing   = format.padding()

        return layout.linearLayout(context)
    }


    private fun optionMechanicLabelView(headerString : String) : TextView
    {
        val header              = TextViewBuilder()

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        header.text             = headerString

        mechanicWidget.format().optionLabelFormat()
                      .styleTextViewBuilder(header, entityId, context)

        return header.textView(context)
    }


    private fun borderView(edge : BorderEdge) : LinearLayout
    {
        val border = LinearLayoutBuilder()

        border.width               = LinearLayout.LayoutParams.MATCH_PARENT
        border.heightDp            = edge.thickness().value

        border.backgroundColor     = colorOrBlack(edge.colorTheme(), entityId)

        return border.linearLayout(context)
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
