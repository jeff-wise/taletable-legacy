
package com.taletable.android.model.sheet.widget


import android.content.Context
import com.google.android.material.tabs.TabLayout
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.sql.*
import com.taletable.android.lib.ui.CustomTabLayout
import com.taletable.android.lib.ui.Font
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.sheet.group.Group
import com.taletable.android.model.sheet.group.GroupReference
import com.taletable.android.model.sheet.style.BorderEdge
import com.taletable.android.model.sheet.style.ElementFormat
import com.taletable.android.model.sheet.style.TextFormat
import com.taletable.android.model.theme.ColorTheme
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack
import com.taletable.android.rts.entity.groups
import com.taletable.android.rts.entity.sheetOrError
import com.taletable.android.util.Util
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable



/**
 * Tab
 */
data class Tab(val tabName : TabName,
               val groupReferences : List<GroupReference>)
                : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var groupsCache : Maybe<List<Group>> = Nothing()


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<Tab>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<Tab> = when (doc)
        {
            is DocDict ->
            {
                apply(::Tab,
                      // Tab Name
                      doc.at("name") ap { TabName.fromDocument(it) },
                      // Group References
                      doc.list("group_references") ap { docList ->
                          docList.map { GroupReference.fromDocument(it) }
                      })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "name" to this.tabName().toDocument(),
        "group_references" to DocList(this.groupReferences.map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun tabName() : TabName = this.tabName


    fun groupReferences() : List<GroupReference> = this.groupReferences


    // -----------------------------------------------------------------------------------------
    // GROUPS
    // -----------------------------------------------------------------------------------------

    fun groups(entityId : EntityId) : List<Group>
    {
        val groupsCache = this.groupsCache
        return when (groupsCache) {
            is Just    -> groupsCache.value
            is Nothing -> {
                val _groups = groups(this.groupReferences, entityId)
                _groups.map { it.group }.forEach {
                    it.rows().forEach {
                        it.widgets().forEach { widget ->
                            sheetOrError(entityId) apDo { it.indexWidget(widget)  }
                        }
                    }
                }
                _groups.map { it.group }
            }
        }
    }

}


/**
 * Tab Name
 */
data class TabName(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TabName>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TabName> = when (doc)
        {
            is DocText -> effValue(TabName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Default Tab Index
 */
data class DefaultTabIndex(val value : Int) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DefaultTabIndex>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<DefaultTabIndex> = when (doc)
        {
            is DocNumber -> effValue(DefaultTabIndex(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value.toDouble())


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = this.value.asSQLValue()

}



/**
 * Tab Widget Format
*/
data class TabWidgetFormat(val widgetFormat : WidgetFormat,
                           val viewType : TabWidgetViewType,
                           val tabBarFormat : ElementFormat,
                           val contentFormat : ElementFormat,
                           val selectedTabFormat : TextFormat,
                           val unselectedTabFormat : TextFormat,
                           val tabFormat : TabFormat)
                           : ToDocument, Serializable
{

    companion object : Factory<TabWidgetFormat>
    {

        private fun defaultWidgetFormat()        = WidgetFormat.default()
        private fun defaultViewType()            = TabWidgetViewType.Underline
        private fun defaultTabBarFormat()        = ElementFormat.default()
        private fun defaultContentFormat()       = ElementFormat.default()
        private fun defaultSelectedTabFormat()   = TextFormat.default()
        private fun defaultUnselectedTabFormat() = TextFormat.default()
        private fun defaultTabFormat()           = TabFormat.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<TabWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::TabWidgetFormat,
                      // Widget Format
                      split(doc.maybeAt("widget_format"),
                            effValue(defaultWidgetFormat()),
                            { WidgetFormat.fromDocument(it) }),
                      // View Type
                      split(doc.maybeAt("view_type"),
                            effValue<ValueError,TabWidgetViewType>(defaultViewType()),
                            { TabWidgetViewType.fromDocument(it) }),
                      // Tab Bar Format
                      split(doc.maybeAt("tab_bar_format"),
                            effValue(defaultTabBarFormat()),
                            { ElementFormat.fromDocument(it) }),
                      // Content Format
                      split(doc.maybeAt("content_format"),
                            effValue(defaultContentFormat()),
                            { ElementFormat.fromDocument(it) }),
                      // Selected Tab Format
                      split(doc.maybeAt("selected_tab_format"),
                            effValue(defaultSelectedTabFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Unselected Tab Format
                      split(doc.maybeAt("unselected_tab_format"),
                            effValue(defaultUnselectedTabFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Tab Format
                      split(doc.maybeAt("tab_format"),
                            effValue(defaultTabFormat()),
                            { TabFormat.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = TabWidgetFormat(defaultWidgetFormat(),
                                         defaultViewType(),
                                         defaultTabBarFormat(),
                                         defaultContentFormat(),
                                         defaultSelectedTabFormat(),
                                         defaultUnselectedTabFormat(),
                                         defaultTabFormat())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat().toDocument(),
        "view_type" to this.viewType.toDocument(),
        "tab_bar_format" to this.tabBarFormat.toDocument(),
        "content_format" to this.contentFormat.toDocument(),
        "selected_tab_format" to this.selectedTabFormat.toDocument(),
        "unselected_tab_format" to this.unselectedTabFormat.toDocument(),
        "tab_format" to this.tabFormat.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat


    fun viewType() : TabWidgetViewType = this.viewType


    fun tabBarFormat() : ElementFormat = this.tabBarFormat


    fun contentFormat() : ElementFormat = this.contentFormat


    fun selectedTabFormat() : TextFormat = this.selectedTabFormat


    fun unselectedTabFormat() : TextFormat = this.unselectedTabFormat


    fun tabFormat() : TabFormat = this.tabFormat

}


/**
 * Tab Widget View Type
 */
sealed class TabWidgetViewType : ToDocument, SQLSerializable, Serializable
{

    object Basic : TabWidgetViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "basic" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("basic")

    }


    object Underline : TabWidgetViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "underline" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("underline")

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<TabWidgetViewType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "basic"     -> effValue<ValueError,TabWidgetViewType>(
                                    TabWidgetViewType.Basic)
                "underline" -> effValue<ValueError,TabWidgetViewType>(
                                        TabWidgetViewType.Underline)
                else     -> effError<ValueError,TabWidgetViewType>(
                                    UnexpectedValue("TabWidgetViewType", doc.text, doc.path))
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}


/**
 * Tab Format
*/
data class TabFormat(val underlineThickness : TabUnderlineThickness,
                     val underlineColorTheme : ColorTheme)
                      : ToDocument, Serializable
{

    companion object : Factory<TabFormat>
    {

        private fun defaultUnderlineThickness()  = TabUnderlineThickness(3)
        private fun defaultUnderlineColorTheme() = ColorTheme.black


        override fun fromDocument(doc : SchemaDoc) : ValueParser<TabFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::TabFormat,
                      // Underline Thickness
                      split(doc.maybeAt("underline_thickness"),
                            effValue(defaultUnderlineThickness()),
                            { TabUnderlineThickness.fromDocument(it) }),
                      // Underline Color Theme
                      split(doc.maybeAt("underline_color_theme"),
                            effValue(defaultUnderlineColorTheme()),
                            { ColorTheme.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = TabFormat(defaultUnderlineThickness(),
                                  defaultUnderlineColorTheme())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "underline_thickness" to this.underlineThickness.toDocument(),
        "underline_color_theme" to this.underlineColorTheme.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun underlineThickness() : TabUnderlineThickness = this.underlineThickness


    fun underlineColorTheme() : ColorTheme = this.underlineColorTheme

}


/**
 * Tab Underline Thickness
 */
data class TabUnderlineThickness(val value : Int) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TabUnderlineThickness>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TabUnderlineThickness> = when (doc)
        {
            is DocNumber -> effValue(TabUnderlineThickness(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }

    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value.toDouble())


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = this.value.asSQLValue()

}


class TabWidgetUI(val tabWidget : WidgetTab,
                  val entityId : EntityId,
                  val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var contentViewLayout : LinearLayout? = null
    private var tabBarViewLayout : LinearLayout? = null

    private var currentTabIndex : Int = 0

    private val tabViewMap : MutableMap<Int,List<View>> = mutableMapOf()


    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------

    fun showTab(index : Int)
    {
        if (index >= 0 && index < tabWidget.tabs().size)
        {

            this.tabViewMap[currentTabIndex]?.let { views ->
                views.forEach { it.visibility = View.GONE }
            }

            if (this.tabViewMap.containsKey(index))
            {
                this.tabViewMap[index]!!.forEach { it.visibility = View.VISIBLE }
            }
            else
            {
                val views : MutableList<View> = mutableListOf()

                val groups = tabWidget.tabs()[index].groups(entityId)
                groups.forEach {
                    val view = it.view(entityId, context, tabWidget.groupContext)
                    views.add(view)
                }

                views.forEach {
                    contentViewLayout?.addView(it)
                }

                this.tabViewMap[index] = views
            }


            this.currentTabIndex = index

            when (tabWidget.format().viewType()) {
                is TabWidgetViewType.Basic -> {
                    this.tabBarViewLayout?.removeAllViews()
                    this.tabBarViewLayout?.addView(this.tabBarView())
                }
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        // Tab Bar
        val tabBarLayout = this.tabBarViewLayout()
        this.tabBarViewLayout = tabBarLayout
        tabBarLayout.addView(this.tabBarView())
        layout.addView(tabBarLayout)

        // Content
        val contentViewLayout = this.contentViewLayout()
        this.contentViewLayout = contentViewLayout
        layout.addView(contentViewLayout)

//        tabWidget.tabAtIndex(0).doMaybe {
//            it.groups(entityId).forEach {
//                contentViewLayout.addView(it.view(entityId, context))
//            }
//        }

        this.showTab(0)

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation  = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    // VIEWS > Content View
    // -----------------------------------------------------------------------------------------

    private fun contentViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        val format              = tabWidget.format().contentFormat()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.marginSpacing    = format.margins()
        layout.paddingSpacing   = format.padding()

        layout.backgroundColor  = colorOrBlack(format.backgroundColorTheme(), entityId)

        return layout.linearLayout(context)
    }


    private fun borderView(format : BorderEdge) : LinearLayout
    {
        val border                  = LinearLayoutBuilder()

        border.width               = LinearLayout.LayoutParams.MATCH_PARENT
        border.heightDp            = format.thickness().value

        border.backgroundColor     = colorOrBlack(format.colorTheme(), entityId)

        return border.linearLayout(context)
    }



    // VIEWS > Tab Bar
    // -----------------------------------------------------------------------------------------

    private fun tabBarView() : View = when (tabWidget.format().viewType())
    {
        is TabWidgetViewType.Basic -> tabBarBasicView()
        is TabWidgetViewType.Underline -> {
            tabBarUnderlineView()
        }

    }


    private fun tabBarViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        return layout.linearLayout(context)
    }


    // VIEWS > Tab Bar > Underline
    // -----------------------------------------------------------------------------------------

    private fun tabBarUnderlineView() : LinearLayout
    {
        val layout = this.tabBarUnderlineViewLayout()

        tabWidget.format().tabBarFormat().border().top().doMaybe {
            layout.addView(this.borderView(it))
        }

        layout.addView(this.tabBarUnderlineTabsView())

        tabWidget.format().tabBarFormat().border().bottom().doMaybe {
            layout.addView(this.borderView(it))
        }

        return layout
    }


    private fun tabBarUnderlineViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun tabBarUnderlineTabsView() : View
    {
        val selectedTextFormat = tabWidget.format().selectedTabFormat()
        val unselectedTextFormat = tabWidget.format().unselectedTabFormat()
        val tabFormat = tabWidget.format().tabFormat()

        val tabLayout = CustomTabLayout(context,
                                        selectedTextFormat.font(),
                                        selectedTextFormat.fontStyle())

        tabWidget.tabs().forEachIndexed { index, tab ->
            val newTab = tabLayout.newTab()

            if (index == 0)
                newTab.customView = tabTextView(tab.tabName().value, true)
            else
                newTab.customView = tabTextView(tab.tabName().value, false)

            tabLayout.addTab(newTab)
        }

        tabLayout.tabMode

        tabLayout.setTabTextColors(
                colorOrBlack(unselectedTextFormat.colorTheme(), entityId),
                colorOrBlack(selectedTextFormat.colorTheme(), entityId)
        )

        tabLayout.setSelectedTabIndicatorHeight(Util.dpToPixel(tabFormat.underlineThickness.value.toFloat()))

        tabLayout.setSelectedTabIndicatorColor(
                colorOrBlack(tabFormat.underlineColorTheme(), entityId))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabReselected(tab: TabLayout.Tab?) { }

            override fun onTabSelected(tab : TabLayout.Tab?) {


                if (tab != null) {
                    val pos = tab.position
                    if (pos >= 0 && pos < tabWidget.tabs().size)
                    {
                        showTab(pos)

                        val tabTextView = tab?.customView as TextView?
                        tabTextView?.setTextColor(colorOrBlack(selectedTextFormat.colorTheme(), entityId))
                    }
                }
                else {
                    showTab(0)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?)
            {
                val tabTextView = tab?.customView as TextView?
                tabTextView?.setTextColor(colorOrBlack(unselectedTextFormat.colorTheme(), entityId))
            }
        })

        return tabLayout
    }


    // VIEWS > Tab Bar > Basic
    // -----------------------------------------------------------------------------------------

    private fun tabBarBasicView() : LinearLayout
    {
        val layout = this.tabBarBasicViewLayout()

        tabWidget.tabs().forEachIndexed { index, tab ->
            val isSelected = index == currentTabIndex
            layout.addView(this.tabView(tab.tabName.value, index, isSelected))
        }

        return layout
    }


    private fun tabBarBasicViewLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation  = LinearLayout.HORIZONTAL


        return layout.linearLayout(context)
    }



    private fun tabView(labelString : String, index : Int, isSelected : Boolean) : LinearLayout
    {
        val format =    if (isSelected)
            tabWidget.format().selectedTabFormat()
        else
            tabWidget.format().unselectedTabFormat()

        val layout = this.tabViewLayout(format, index)

        layout.addView(tabTextView(labelString, isSelected))

        format.elementFormat().border().bottom().doMaybe { bottomBorder ->
            layout.addView(tabBottomBorderView(format, bottomBorder))
        }

        return layout
    }


    private fun tabViewLayout(format : TextFormat, index : Int) : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.width        = 0
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.weight       = 1f

        layout.orientation  = LinearLayout.VERTICAL

        layout.backgroundColor   = colorOrBlack(format.elementFormat().backgroundColorTheme(), entityId)

        layout.onClick      = View.OnClickListener {
            showTab(index)
        }

//        layout.gravity      = Gravity.CENTER

        return layout.linearLayout(context)
    }


    private fun tabTextView(labelString : String, isSelected : Boolean) : TextView
    {
        val label               = TextViewBuilder()

        val format =    if (isSelected)
                            tabWidget.format().selectedTabFormat()
                        else
                            tabWidget.format().unselectedTabFormat()


        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.layoutGravity     = format.elementFormat().alignment().gravityConstant()

//        label.backgroundColor   = colorOrBlack(format.elementFormat().backgroundColorTheme(), entityId)

        label.text              = labelString

        label.color             = colorOrBlack(format.colorTheme(), entityId)

        label.sizeSp            = format.sizeSp()

        label.font              = Font.typeface(format.font(),
                                                format.fontStyle(),
                                                context)

        label.paddingSpacing    = format.elementFormat().padding()
        label.marginSpacing     = format.elementFormat().margins()

        return label.textView(context)
    }


    private fun tabBottomBorderView(format : TextFormat, borderEdge : BorderEdge) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = borderEdge.thickness().value

        layout.backgroundColor  = colorOrBlack(borderEdge.colorTheme(), entityId)

        return layout.linearLayout(context)
    }

//
//    private fun customTabTextView(labelString : String) : TextView
//    {
//        val label               = TextViewBuilder()
//
//        val format =    if (isSelected)
//                            tabWidget.format().selectedTabFormat()
//                        else
//                            tabWidget.format().unselectedTabFormat()
//
//
//        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
//        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        label.text              = labelString
//
//        label.color             = colorOrBlack(format.colorTheme(), entityId)
//
//        label.sizeSp            = format.sizeSp()
//
//        label.font              = Font.typeface(format.font(),
//                                                format.fontStyle(),
//                                                context)
//
//        label.paddingSpacing    = format.elementFormat().padding()
//        label.marginSpacing     = format.elementFormat().margins()
//
//        return label.textView(context)
//    }


}
