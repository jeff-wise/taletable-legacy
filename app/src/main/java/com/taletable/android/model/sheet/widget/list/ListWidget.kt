
package com.taletable.android.model.sheet.widget.list


import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.activity.sheet.dialog.ValueChooserDialogFragment
import com.taletable.android.activity.sheet.dialog.openVariableEditorDialog
import com.taletable.android.app.ApplicationLog
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.lib.ui.*
import com.taletable.android.model.engine.value.ValueId
import com.taletable.android.model.sheet.group.GroupContext
import com.taletable.android.model.sheet.group.RowLayoutType
import com.taletable.android.model.sheet.widget.ListWidget
import com.taletable.android.model.sheet.widget.WidgetView
import com.taletable.android.model.theme.Theme
import com.taletable.android.rts.entity.*
import com.taletable.android.rts.entity.sheet.*
import com.taletable.android.util.Util
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable



// -----------------------------------------------------------------------------------------
// | VIEW DATA
// -----------------------------------------------------------------------------------------

data class ListWidgetViewData(
        val values : List<String>,
        val label : Maybe<String>
)



/**
 * List Widget Description
 */
data class ListWidgetDescription(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ListWidgetDescription>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ListWidgetDescription> = when (doc)
        {
            is DocText -> effValue(ListWidgetDescription(doc.text))
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

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


sealed class ListWidgetEditType
{
    object Simple : ListWidgetEditType()
    object Advanced : ListWidgetEditType()
}




class ListWidgetUI(val listWidget : ListWidget,
                   val entityId : EntityId,
                   val context : Context,
                   val groupContext : Maybe<GroupContext> = Nothing())
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val activity = context as AppCompatActivity

    private var editMode : Boolean = false

    private var editButtonTextView : TextView? = null
    private var rowViews : MutableList<LinearLayout> = mutableListOf()


    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------

    private fun openSubsetEditDialog()
    {
        val textListVariable =  listWidget.variable(entityId)
        when (textListVariable) {
            is Val -> {
                openVariableEditorDialog(textListVariable.value,
                                         null,
                                         UpdateTargetListWidget(listWidget.widgetId()),
                                         entityId,
                                         context)
            }
        }
    }


    private fun openListEditDialog(valueString : String? = null)
    {
//        val valueIdsEff = listWidget.variable(entityId)
//                                 .apply { it.setv(entityId) }
//                                 .apply { effValue<AppError,List<ValueId>>(it.map { ValueId(it) }) }

//

        listWidget.variable(entityId) apDo { variable ->

            val setVariable = variable.setVariable(entityId)
            when (setVariable)
            {
                is Just ->
                {
                    val values = setVariable.value.values(entityId)
                    variable.valueSetId.doMaybe { valueSetId ->
                        val chooseItemDialog = ValueChooserDialogFragment.newInstance(
                                                                valueSetId,
                                                                values.map { it.valueId() },
                                                                valueString?.let { ValueId(it) },
                                                                UpdateTargetListWidget(listWidget.widgetId()),
                                                                entityId)
                        chooseItemDialog.show(activity.supportFragmentManager, "")
                    }
                }
                is Nothing ->
                {
                    variable.valueSetId().doMaybe { valueSetId ->
                        val chooseItemDialog = ValueChooserDialogFragment.newInstance(
                                                                valueSetId,
                                                                listOf(),
                                                                valueString?.let { ValueId(it) },
                                                                UpdateTargetListWidget(listWidget.widgetId()),
                                                                entityId)
                        chooseItemDialog.show(activity.supportFragmentManager, "")
                    }

                }
            }


        }



    }



    private fun toggleEditMode()
    {
        editMode = !editMode

        this.toggleTableRowEditButtons()
        this.toggleEditButton()
    }


    private fun toggleEditButton()
    {
        if (editMode)
            this.editButtonTextView?.text = context.getString(R.string.view_only)
        else
            this.editButtonTextView?.text = context.getString(R.string.edit)
    }


    private fun toggleTableRowEditButtons()
    {
        if (editMode)
        {
            this.rowViews.forEach { tableRowView ->

                val editButtonView = tableRowView.findViewById<LinearLayout>(R.id.list_item_edit_button)
                if (editButtonView != null)
                    editButtonView.visibility = View.VISIBLE

            }
//            val headerRowEditButtonView = headerRowView?.findViewById<LinearLayout>(R.id.table_row_edit_button)
//            if (headerRowEditButtonView != null)
//                headerRowEditButtonView.visibility = View.VISIBLE
        }
        else
        {
            this.rowViews.forEach { tableRowView ->

                val editButtonView = tableRowView.findViewById<LinearLayout>(R.id.list_item_edit_button)
                if (editButtonView != null)
                    editButtonView.visibility = View.GONE
            }

//            val headerRowEditButtonView = headerRowView?.findViewById<LinearLayout>(R.id.table_row_edit_button)
//            if (headerRowEditButtonView != null)
//                headerRowEditButtonView.visibility = View.GONE
        }
    }



    private fun itemStrings() : List<String>
    {
        val idStrings = listWidget.valueIdStrings(entityId)
        return when (idStrings)
        {
            is Val -> {
                idStrings.value.sorted()
            }
            is Err -> {
                ApplicationLog.error(idStrings.error)
                listOf()
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = WidgetView.layout(listWidget.widgetFormat(), entityId, context)

        val layoutId = Util.generateViewId()
        layout.id = layoutId
        listWidget.layoutViewId = layoutId

        //Log.d("***LIST WIDGET", "group context is : $groupContext")

//        this.updateView(layout)

        return layout
    }


//    fun listView() : View = when (listWidget.format.viewType)
//    {
//        is ListViewType.ParagraphCommas -> inlineView()
//        is ListViewType.Rows -> rowsView(ListWidgetEditType.Advanced)
//        is ListViewType.RowsSimpleEditor -> rowsView(ListWidgetEditType.Simple)
//        is ListViewType.Pool -> poolView()
//    }
//
//
//    fun updateView(layout : LinearLayout)
//    {
//        val contentLayout = layout.findViewById<LinearLayout>(R.id.widget_content_layout)
//
//        contentLayout?.removeAllViews()
//
//        contentLayout?.addView(this.listView())
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // VIEW > Inline
//    // -----------------------------------------------------------------------------------------
//

    // -----------------------------------------------------------------------------------------
    // VIEW > Rows
    // -----------------------------------------------------------------------------------------

//    private fun rowsView(editType : ListWidgetEditType) : LinearLayout
//    {
//        val layout = this.rowsViewLayout()
//
//        // skip this for now
//        //layout.addView(this.titleBarView(editType))
//
//        listWidget.variable(entityId, groupContext) apDo { variable ->
//            variable.constraint().doMaybe { constraint ->
//            constraint.constraintOfType(ConstraintTypeTextListMaxSize).doMaybe {
//                if (it is TextListConstraintMaxSize)
//                {
//                    SheetData.number(it.maxSize, entityId).apDo {
//                        it.doMaybe {
//                            val currentSizeString = this.itemStrings().size.toString()
//                            val maxSizeString = Util.doubleString(it)
//                            val counterString = "$currentSizeString\u2008/\u2008$maxSizeString"
//                            listWidget.format().constraintFormat.message().doMaybe {
//                                val constraintString = it.templateString(listOf(counterString))
//                                layout.addView(this.rowsConstraintView(constraintString))
//                            }
//                        }
//                    }
//                }
//            } }
//        }
//
//        layout.addView(this.itemsView())
//
//        return layout
//    }


    private fun rowsViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


//    private fun itemsView() : LinearLayout
//    {
//        val layout = this.itemsViewLayout()
//
//        Log.d("***LIST WIDGET", "group context is : $groupContext")
//        val itemStrings = listWidget.valueIdStrings(entityId, groupContext)
//        when (itemStrings)
//        {
//            is Val -> {
//
//                // add option to sort
//                //itemStrings.value.sorted().forEach {
//                itemStrings.value.forEach {
//                    val rowView = this.rowView(it)
//                    this.rowViews.add(rowView)
//                    Log.d("***LIST WIDGET", "adding item: $it")
//                    layout.addView(rowView)
//                }
//            }
//            is Err -> {
//                ApplicationLog.error(itemStrings.error)
//            }
//        }
//
//        return layout
//    }
//
//
//    private fun itemsViewLayout() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation      = LinearLayout.VERTICAL
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun rowView(itemString : String) : LinearLayout
//    {
//        val layout = this.rowViewLayout()
//        val format = listWidget.format().itemFormat
//
//        format.elementFormat().border().top().doMaybe {
//            layout.addView(this.rowBorderView(it))
//        }
//
//        layout.addView(this.rowItemContentView(itemString))
//
//        format.elementFormat().border().bottom().doMaybe {
//            layout.addView(this.rowBorderView(it))
//        }
//
//        return layout
//    }
//
//
//    private fun rowViewLayout() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//        val format              = listWidget.format().itemFormat
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation      = LinearLayout.VERTICAL
//
//        layout.backgroundColor  = colorOrBlack(format.elementFormat().backgroundColorTheme(), entityId)
//
//        layout.corners          = format.elementFormat().corners()
//
//        layout.marginSpacing    = format.elementFormat().margins()
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun rowItemContentView(itemString : String) : LinearLayout
//    {
//        val layout = this.rowItemContentViewLayout()
//
//        layout.addView(this.rowItemIconView())
//
//        layout.addView(this.rowItemTextView(itemString))
//
//        return layout
//    }
//
//
//    private fun rowItemContentViewLayout() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//        val format              = listWidget.format().itemFormat
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation      = LinearLayout.HORIZONTAL
//
//        layout.gravity          = Gravity.CENTER_VERTICAL
//
//        layout.paddingSpacing   = format.elementFormat().padding()
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun rowItemIconView() : LinearLayout
//    {
//        // (1) Declarations
//        // -------------------------------------------------------------------------------------
//
//        val layout              = LinearLayoutBuilder()
//        val icon                = ImageViewBuilder()
//
//        val format              = listWidget.format().itemFormat
//
//        // (2) Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.id               = R.id.list_item_edit_button
//
//        layout.widthDp          = LinearLayout.LayoutParams.WRAP_CONTENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.visibility       = View.GONE
//
//        layout.gravity          = Gravity.CENTER_VERTICAL
//        layout.layoutGravity    = Gravity.CENTER_VERTICAL
//
//        layout.margin.rightDp   = format.elementFormat().padding().leftDp()
//
//        layout.child(icon)
//
//        // (3) Icon
//        // -------------------------------------------------------------------------------------
//
//        icon.widthDp            = 18
//        icon.heightDp           = 18
//
//        icon.image              = R.drawable.icon_vertical_ellipsis
//
//        icon.layoutGravity      = Gravity.CENTER
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
//        icon.color              = colorOrBlack(colorTheme, entityId)
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun rowItemTextView(itemString : String) : TextView
//    {
//        val item                = TextViewBuilder()
//        val format              = listWidget.format().itemFormat
//
//        item.width              = LinearLayout.LayoutParams.WRAP_CONTENT
//        item.height             = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        // what is this for???
////        listWidget.title(entityId).doMaybe { titleString ->
////            item.text           = titleString
////        }
//
//        item.text               = itemString
//
//        item.color              = colorOrBlack(format.colorTheme(), entityId)
//
//        item.sizeSp             = format.sizeSp()
//
//        item.font               = Font.typeface(format.font(),
//                                                format.fontStyle(),
//                                                context)
//
//
//        return item.textView(context)
//    }
//
//
//    private fun rowBorderView(format : BorderEdge) : LinearLayout
//    {
//        val border                  = LinearLayoutBuilder()
//
//        border.width                = LinearLayout.LayoutParams.MATCH_PARENT
//        border.heightDp             = format.thickness().value
//
//        border.backgroundColor      = colorOrBlack(format.colorTheme(), entityId)
//
//        return border.linearLayout(context)
//    }


    // VIEWS > Rows > Title Bar
    // -----------------------------------------------------------------------------------------

//    private fun titleBarView(editType : ListWidgetEditType) : ViewGroup
//    {
//        val layout = this.titleBarViewLayout()
//
//        val titleTextView = this.titleTextView()
//        layout.addView(titleTextView)
//
//        layout.addView(this.editButtonView(editType))
//
//        layout.setOnLongClickListener {
//            listWidget.bookReference().doMaybe {
//                val intent = Intent(activity, BookActivity::class.java)
//                intent.putExtra("book_reference", it)
//                activity.startActivity(intent)
//            }
//
//            true
//        }
//
//        return layout
//    }
//
//
//    private fun titleBarViewLayout() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//        val format              = listWidget.format().titleBarFormat
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation      = LinearLayout.HORIZONTAL
//
//        layout.marginSpacing    = format.margins()
//        layout.paddingSpacing   = format.padding()
//
//        layout.backgroundColor  = colorOrBlack(format.backgroundColorTheme(), entityId)
//
//        layout.gravity          = Gravity.CENTER_VERTICAL
//
//        layout.corners          = format.corners()
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun titleTextView() : TextView
//    {
//        val title               = TextViewBuilder()
//        val format              = listWidget.format().titleFormat
//
////        title.layoutType        = LayoutType.RELATIVE
////        title.width             = RelativeLayout.LayoutParams.WRAP_CONTENT
////        title.height            = RelativeLayout.LayoutParams.WRAP_CONTENT
//        title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
//        title.height            = LinearLayout.LayoutParams.WRAP_CONTENT
//        title.weight            = 1f
//
//        title.addRule(RelativeLayout.ALIGN_PARENT_START)
//        title.addRule(RelativeLayout.CENTER_VERTICAL)
//
//        listWidget.title(entityId).doMaybe { titleString ->
//            title.text          = titleString
//        }
//
//        title.color             = colorOrBlack(format.colorTheme(), entityId)
//
//        title.sizeSp            = format.sizeSp()
//
//        title.font              = Font.typeface(format.font(),
//                                                format.fontStyle(),
//                                                context)
//
//        title.paddingSpacing    = format.elementFormat().padding()
//        title.marginSpacing     = format.elementFormat().margins()
//
//        return title.textView(context)
//    }
//
//
//    private fun editButtonView(editType : ListWidgetEditType) : LinearLayout
//    {
//        val layout      = this.editButtonViewLayout(editType)
//
//        val v = this.editButtonTextView()
//        layout.addView(v)
//        this.editButtonTextView = v
//
//        return layout
//    }
//
//
//    private fun editButtonViewLayout(editType : ListWidgetEditType) : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
////        layout.layoutType       = LayoutType.RELATIVE
////        layout.width            = RelativeLayout.LayoutParams.WRAP_CONTENT
////        layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT
//        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.padding.leftDp   = 10f
//        layout.padding.rightDp  = 5f
//
//        layout.addRule(RelativeLayout.CENTER_VERTICAL)
//        layout.addRule(RelativeLayout.ALIGN_PARENT_END)
//
//        layout.onClick          = View.OnClickListener {
//            when (editType) {
//                is ListWidgetEditType.Simple -> this.openSubsetEditDialog()
//                is ListWidgetEditType.Advanced -> this.toggleEditMode()
//            }
//
//        }
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun editButtonTextView() : TextView
//    {
//        val label               = TextViewBuilder()
//        val format              = listWidget.format().editButtonFormat
//
//        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
//        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        label.textId            = R.string.edit
//
//        format.styleTextViewBuilder(label, entityId, context)
//
//        return label.textView(context)
//    }
//
//
//    // VIEWS > Rows > Constraint
//    // -----------------------------------------------------------------------------------------
//
//    private fun rowsConstraintView(constraintString : String) : LinearLayout
//    {
//        val layout = this.rowsConstraintViewLayout()
//
//        layout.addView(this.rowConstraintTextView(constraintString))
//
//        return layout
//    }
//
//
//    private fun rowsConstraintViewLayout() : LinearLayout
//    {
//        val layout                  = LinearLayoutBuilder()
//        var format                  = listWidget.format().constraintFormat.textFormat()
//
//        listWidget.variable(entityId).apDo { variable ->
//            variable.constraint().doMaybe {
//                if (!it.matchesValue(EngineTextListValue(this.itemStrings()), entityId)) {
//                    format = listWidget.format().constraintFormat.failTextFormat()
//                }
//            }
//        }
//
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.backgroundColor      = colorOrBlack(format.elementFormat().backgroundColorTheme(), entityId)
//
//        layout.paddingSpacing       = format.elementFormat().padding()
//        layout.marginSpacing        = format.elementFormat().margins()
//
//        return layout.linearLayout(context)
//    }
//
//    private fun rowConstraintTextView(constraintString : String) : TextView
//    {
//        val constraintView              = TextViewBuilder()
//
//        var format                  = listWidget.format().constraintFormat.textFormat()
//
//        listWidget.variable(entityId).apDo { variable ->
//            variable.constraint().doMaybe {
//                if (!it.matchesValue(EngineTextListValue(this.itemStrings()), entityId)) {
//                    format = listWidget.format().constraintFormat.failTextFormat()
//                }
//            }
//        }
//
//        constraintView.width            = LinearLayout.LayoutParams.WRAP_CONTENT
//        constraintView.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        constraintView.text             = constraintString
//
//
//
////        listWidget.variable(entityId).apDo { variable ->
////            variable.constraint().doMaybe {
////                when (it) {
////                    is TextListConstraintMaxSize -> {
////                        SheetData.number(it.maxSize, entityId).apDo {
////                            it.doMaybe {
////
////                            }
////                        }
////                    }
////                }
////            }
////        }
//
//        constraintView.color             = colorOrBlack(format.colorTheme(), entityId)
//
//        constraintView.sizeSp            = format.sizeSp()
//
//        constraintView.font              = Font.typeface(format.font(),
//                                                     format.fontStyle(),
//                                                     context)
//
//        return constraintView.textView(context)
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // VIEW > Pool
//    // -----------------------------------------------------------------------------------------
//
//    private fun poolView() : LinearLayout
//    {
//        val layout          = this.poolViewLayout()
//
//        layout.addView(this.titleBarView(ListWidgetEditType.Advanced))
//
//        layout.addView(this.poolItemsView())
//
//        return layout
//    }
//
//
//    private fun poolViewLayout() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//        val format              = listWidget.format().listFormat
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.orientation      = LinearLayout.VERTICAL
//
//        layout.paddingSpacing   = format.padding()
//        layout.marginSpacing    = format.margins()
//
//        layout.backgroundColor  = colorOrBlack(format.backgroundColorTheme(), entityId)
//
//        return layout.linearLayout(context)
//    }
//
//
//
//    private fun poolItemsView() : FlexboxLayout
//    {
//        val layout = this.poolItemsViewLayout()
//
//        listWidget.variable(entityId) apDo { variable ->
//            variable.constraint().doMaybe { constraint ->
//            constraint.constraintOfType(ConstraintTypeTextListMaxSize).doMaybe {
//                if (it is TextListConstraintMaxSize)
//                {
//                    SheetData.number(it.maxSize, entityId).apDo {
//                        it.doMaybe {
//                            val values = variable.values(entityId)
//
//                            values.forEach {
//                                layout.addView(this.poolItemActiveView(it))
//                            }
//
//                            val maxSize = it.toInt()
//                            val inactiveItems = maxSize - values.size
//
//                            for (i in 1..inactiveItems) {
//                                layout.addView(this.poolItemInactiveView())
//                            }
//
//                        }
//                    }
//                }
//            } }
//        }
//
//        return layout
//    }
//
//
//    private fun poolItemsViewLayout() : FlexboxLayout
//    {
//        val layout              = FlexboxLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
////        when (format.widgetFormat().elementFormat().alignment()) {
////            is Alignment.Center -> layout.justification = JustifyContent.CENTER
////        }
//
//        layout.direction        = FlexDirection.ROW
//        layout.wrap             = FlexWrap.WRAP
//
////        when (format.widgetFormat().elementFormat().verticalAlignment()) {
////            is VerticalAlignment.Middle -> layout.itemAlignment = AlignItems.CENTER
////        }
//
//        return layout.flexboxLayout(context)
//
//    }
//
//
//    private fun poolItemActiveView(value : Value) : LinearLayout
//    {
//        // (1) Declarations
//        // -------------------------------------------------------------------------------------
//
//        val layout              = LinearLayoutBuilder()
//        val label               = TextViewBuilder()
//
//        val format              = listWidget.format().itemFormat
//
//        // (2) Declarations
//        // -------------------------------------------------------------------------------------
//
//        layout.layoutType       = LayoutType.FLEXBOX
//
//        val width = format.elementFormat().width()
//        when (width) {
//            is Width.Fixed -> layout.widthDp = width.value.toInt()
//            else           -> layout.width = FlexboxLayout.LayoutParams.WRAP_CONTENT
//        }
//
//        val height = format.elementFormat().height()
//        when (height) {
//            is Height.Fixed -> layout.heightDp = height.value.toInt()
//            else            -> layout.height = FlexboxLayout.LayoutParams.WRAP_CONTENT
//        }
//
//        layout.backgroundColor  = colorOrBlack(format.elementFormat().backgroundColorTheme(),
//                                               entityId)
//
//        layout.corners          = format.elementFormat().corners()
//
//        layout.gravity          = Gravity.CENTER
//
//        layout.paddingSpacing    = format.elementFormat().padding()
//        layout.marginSpacing    = format.elementFormat().margins()
//
//        layout.onClick          = View.OnClickListener {
//            this.openListEditDialog(value.valueId().value)
//        }
//
//        layout.child(label)
//
//        // (2) Declarations
//        // -------------------------------------------------------------------------------------
//
//        label.width        = LinearLayout.LayoutParams.WRAP_CONTENT
//        label.height       = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        label.text         = value.valueString()
//
//        format.styleTextViewBuilder(label, entityId, context)
//
//        return layout.linearLayout(context)
//    }
//
//
//    private fun poolItemInactiveView() : LinearLayout
//    {
//        // (1) Declarations
//        // -------------------------------------------------------------------------------------
//
//        val layout              = LinearLayoutBuilder()
//        val label               = TextViewBuilder()
//
//        val format              = listWidget.format().itemInactiveFormat
//
//        // (2) Declarations
//        // -------------------------------------------------------------------------------------
//
//        layout.layoutType       = LayoutType.FLEXBOX
//
//        val width = format.elementFormat().width()
//        when (width) {
//            is Width.Fixed -> layout.widthDp = width.value.toInt()
//            else           -> layout.width = FlexboxLayout.LayoutParams.WRAP_CONTENT
//        }
//
//        val height = format.elementFormat().height()
//        when (height) {
//            is Height.Fixed -> layout.heightDp = height.value.toInt()
//            else            -> layout.height = FlexboxLayout.LayoutParams.WRAP_CONTENT
//        }
//
//        layout.backgroundColor  = colorOrBlack(format.elementFormat().backgroundColorTheme(),
//                                               entityId)
//
//        layout.corners          = format.elementFormat().corners()
//
//        layout.gravity          = Gravity.CENTER
//
//        layout.paddingSpacing   = format.elementFormat().padding()
//        layout.marginSpacing    = format.elementFormat().margins()
//
//        layout.onClick          = View.OnClickListener {
//            this.openListEditDialog()
//        }
//
//        layout.child(label)
//
//        // (2) Declarations
//        // -------------------------------------------------------------------------------------
//
//        label.width        = LinearLayout.LayoutParams.WRAP_CONTENT
//        label.height       = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        label.text         = listWidget.format.defaultItemText.value
//
//        format.styleTextViewBuilder(label, entityId, context)
//
//        return layout.linearLayout(context)
//    }



}




// -----------------------------------------------------------------------------------------
// | VIEW
// -----------------------------------------------------------------------------------------

/**
 * ListWidget view
 */
fun listWidgetViewGroup(
        listWidget : ListWidget,
        rowLayoutType : RowLayoutType,
        entityId : EntityId,
        context : Context
) : ViewGroup
{
    val layout = WidgetView.layout(listWidget.widgetFormat(), entityId, context, rowLayoutType)

    //configureTextWidgetViewClick(layout, textWidget, entityId, context)

    return layout
}

/**
 * TextWidget view
 */
//private fun configureTextWidgetViewClick(
//        viewGroup : ViewGroup,
//        widget : Widget,
//        entityId : EntityId,
//        context : Context
//)
//{
//    entityType(entityId).apDo { entityType ->
//        when (entityType)
//        {
//            is EntityTypeSheet ->
//            {
//                // NORMAL CLICK
//                viewGroup.setOnClickListener {
//                    widget.primaryAction(entityId, context)
//                }
//                // LONG CLICK
//                viewGroup.setOnLongClickListener {
//                    widget.secondaryAction(entityId, context)
//                    true
//                }
//            }
//        }
//
//    }
//}


fun listWidgetView(
        listWidget : ListWidget,
        data : ListWidgetViewData,
        theme : Theme,
        context : Context,
        groupContext : Maybe<GroupContext> = Nothing()) : View
{
    val format = listWidget.format

    return when (format) {
        is ListWidgetFormatCustom ->
            listWidgetCustomView(format, data, theme, context, groupContext)
        is ListWidgetFormatOfficial ->
            listWidgetOfficialView(format, data, theme, context, groupContext)
    }
}

