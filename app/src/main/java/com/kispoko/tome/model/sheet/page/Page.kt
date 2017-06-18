
package com.kispoko.tome.model.sheet.page


import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.model.sheet.group.Group
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.rts.sheet.SheetComponent
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.util.Util
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Page
 */
data class Page(override val id : UUID,
                val name : Prim<PageName>,
                val format : Comp<PageFormat>,
                val index : Prim<Int>,
                val groups : CollS<Group>) : Model, SheetComponent, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    val pageViewId = Util.generateViewId()


    companion object
    {
        fun fromDocument(doc : SpecDoc, index : Int) : ValueParser<Page> = when (doc)
        {
            is DocDict -> effApply(::Page,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Name
                                   doc.at("name") ap {
                                       effApply(::Prim, PageName.fromDocument(it))
                                   },
                                   // Format
                                   split(doc.maybeAt("format"),
                                         effValue(Comp(PageFormat.default())),
                                         { effApply(::Comp, PageFormat.fromDocument(it)) }),
                                   // Index
                                   effValue(Prim(index)),
                                   // Groups
                                   doc.list("groups") ap { docList ->
                                       effApply(::CollS, docList.mapIndexed {
                                           doc, index -> Group.fromDocument(doc,index) })
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext : SheetContext)
    {
        this.groups.list.forEach { it.onSheetComponentActive(sheetContext) }
    }


    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view(sheetContext : SheetContext) : View
    {
        val layout = this.viewLayout(sheetContext.context)

        this.groups.list.forEach { layout.addView(it.view(sheetContext)) }

        return layout
    }


    private fun viewLayout(context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.id               = this.pageViewId

        layout.orientation      = LinearLayout.VERTICAL
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        return layout.linearLayout(context)
    }

}


/**
 * Page Name
 */
data class PageName(val name : String) : Serializable
{

    companion object : Factory<PageName>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<PageName> = when (doc)
        {
            is DocText -> effValue(PageName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}


/**
 * Page Format
 */
data class PageFormat(override val id : UUID,
                      val backgroundColor : Prim<ColorTheme>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(colorTheme : ColorTheme) : this(UUID.randomUUID(), Prim(colorTheme))


    companion object : Factory<PageFormat>
    {

        override fun fromDocument(doc : SpecDoc) : ValueParser<PageFormat> = when (doc)
        {
            is DocDict -> effApply(::PageFormat,
                                   // Model Id
                                    effValue(UUID.randomUUID()),
                                   // Background Color
                                   doc.at("background_color_theme") ap {
                                       effApply(::Prim, ColorTheme.fromDocument(it))
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() : PageFormat = PageFormat(ColorTheme.transparent)

    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

}
