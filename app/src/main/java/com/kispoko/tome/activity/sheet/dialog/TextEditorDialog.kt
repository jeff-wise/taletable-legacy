
package com.kispoko.tome.activity.sheet.dialog


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.EngineValueText
import com.kispoko.tome.model.game.engine.variable.VariableId
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.rts.entity.updateVariable



/**
 * Text Editor Dialog
 */
class TextEditorDialog : DialogFragment()
{


    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var title        : String? = null
    private var text         : String? = null
    private var variableId   : VariableId? = null
    private var entityId     : EntityId? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(title : String,
                        text : String,
                        variableId : VariableId,
                        entityId : EntityId) : TextEditorDialog
        {
            val dialog = TextEditorDialog()

            val args = Bundle()
            args.putString("text", text)
            args.putString("title", title)
            args.putSerializable("variable_id", variableId)
            args.putSerializable("entity_id", entityId)

            dialog.arguments = args

            return dialog
        }
    }


    // -----------------------------------------------------------------------------------------
    // DIALOG FRAGMENT
    // -----------------------------------------------------------------------------------------

    override fun onCreateDialog(savedInstanceState : Bundle?) : Dialog
    {
        // (1) Read State
        // -------------------------------------------------------------------------------------

        this.title      = arguments.getString("title")
        this.text       = arguments.getString("text")
        this.variableId = arguments.getSerializable("variable_id") as VariableId?
        this.entityId   = arguments.getSerializable("entity_id") as EntityId


        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(activity)

        val dialogLayout = this.dialogLayout()

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setContentView(dialogLayout)

        val width  = context.resources.getDimension(R.dimen.action_dialog_width)
        val height = LinearLayout.LayoutParams.WRAP_CONTENT

        dialog.window.setLayout(width.toInt(), height)

        return dialog
    }


    override fun onCreateView(inflater : LayoutInflater?,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        val title      = this.title
        val text       = this.text
        val entityId   = this.entityId
        val variableId = this.variableId

        return if (title != null && text != null && entityId != null && variableId != null)
        {
            val viewBuilder = TextEditorViewBuilder(title,
                                                    text,
                                                    variableId,
                                                    this,
                                                    entityId,
                                                    context)
            viewBuilder.view()
        }
        else
        {
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }


    // -----------------------------------------------------------------------------------------
    // DIALOG LAYOUT
    // -----------------------------------------------------------------------------------------

    fun dialogLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        return layout.linearLayout(context)
    }



}


class TextEditorViewBuilder(val title : String,
                            val text : String,
                            val variableId : VariableId,
                            val dialog : DialogFragment,
                            val entityId : EntityId,
                            val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var valueView : TextView? = null


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        // Header
        layout.addView(this.headerView())

        // Edit Field
        val editValueView = this.editValueView()
        this.valueView = editValueView
        layout.addView(editValueView)

//        layout.addView(this.dividerView(sheetUIContext))

        // Footer View
        layout.addView(this.footerView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp             = LinearLayout.LayoutParams.MATCH_PARENT

//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        layout.backgroundColor      = Color.WHITE // SheetManager.color(sheetUIContext.sheetId, colorTheme)

        layout.corners              = Corners(3.0, 3.0, 3.0, 3.0)

        return layout.linearLayout(context)
    }


    // Header
    // -----------------------------------------------------------------------------------------

    private fun headerView() : LinearLayout
    {
        val layout = this.headerViewLayout()

        val mainLayout = this.headerMainViewLayout()

        // Name
        mainLayout.addView(this.nameView())

        layout.addView(mainLayout)

        layout.addView(this.dividerView())

        // Full Editor Button
        //layout.addView(this.fullEditorButtonView())

        //val buttonsLayout = this.headerButtonsLayout()
//        buttonsLayout.addView(this.fullEditorButtonView())
        //buttonsLayout.addView(this.doneButtonView())

        // Done Button
        //layout.addView(buttonsLayout)

        return layout
    }


    private fun headerMainViewLayout() : RelativeLayout
    {
        val layout                  = RelativeLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.padding.topDp        = 8f
        layout.padding.bottomDp     = 8f

        layout.padding.leftDp       = 10f
        layout.padding.rightDp      = 12f

//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))
        layout.backgroundColor      = Color.WHITE //  SheetManager.color(sheetUIContext.sheetId, colorTheme)

        layout.gravity              = Gravity.CENTER_VERTICAL

        layout.corners              = Corners(3.0, 3.0, 0.0, 0.0)

        return layout.relativeLayout(context)
    }


    private fun headerViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))
//        layout.backgroundColor      = Color.WHITE //  SheetManager.color(sheetUIContext.sheetId, colorTheme)

//        layout.gravity              = Gravity.CENTER_VERTICAL

//        layout.corners              = Corners(3.0, 3.0, 0.0, 0.0)

        return layout.linearLayout(context)
    }


    private fun nameView() : TextView
    {
        val name            = TextViewBuilder()

        name.layoutType     = LayoutType.RELATIVE
        name.width          = RelativeLayout.LayoutParams.WRAP_CONTENT
        name.height         = RelativeLayout.LayoutParams.WRAP_CONTENT

        name.addRule(RelativeLayout.ALIGN_PARENT_START)
        name.addRule(RelativeLayout.CENTER_VERTICAL)

        name.text           = this.title

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
        name.color          = colorOrBlack(colorTheme, entityId)

        name.font           = Font.typeface(TextFont.default(),
                                            TextFontStyle.Regular,
                                            context)

        name.sizeSp         = 16f

        return name.textView(context)
    }


//    private fun headerButtonsLayout() : LinearLayout
//    {
//        val layout                  = LinearLayoutBuilder()
//
//        layout.layoutType           = LayoutType.RELATIVE
//        layout.orientation          = LinearLayout.HORIZONTAL
//        layout.width                = RelativeLayout.LayoutParams.WRAP_CONTENT
//        layout.height               = RelativeLayout.LayoutParams.WRAP_CONTENT
//
//        layout.gravity              = Gravity.CENTER_VERTICAL
//
//        layout.addRule(RelativeLayout.ALIGN_PARENT_END)
//        layout.addRule(RelativeLayout.CENTER_VERTICAL)
//
//        return layout.linearLayout(sheetUIContext.context)
//    }



    // Value
    // -----------------------------------------------------------------------------------------

    private fun editValueView() : EditText
    {
        val value = EditTextBuilder()

        value.width                 = LinearLayout.LayoutParams.MATCH_PARENT
        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        value.font                  = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Regular,
                                                    context)

        val textColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_16")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        value.color                 = colorOrBlack(textColorTheme, entityId)

        value.sizeSp                = 19f

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_5"))))
        value.backgroundColor       = Color.WHITE // SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        value.backgroundResource    = R.drawable.bg_edit_text_no_style

//        value.underlineColor        = R.color.dark_blue_hl_1

        value.minHeightDp           = 90f

        value.gravity               = Gravity.TOP

        value.padding.leftDp        = 8f
        value.padding.rightDp       = 8f
        value.padding.topDp         = 8f
        value.padding.bottomDp      = 8f

        // value.corners               = Corners(3.0, 3.0, 0.0, 0.0)

//        value.margin.leftDp         = 2f
//        value.margin.rightDp        = 2f
        //value.margin.bottomDp       = 2f

        value.text                  = this.text

        return value.editText(context)
    }


    // Footer
    // -----------------------------------------------------------------------------------------

    private fun footerView() : LinearLayout
    {
        val layout = footerViewLayout()

        val mainLayout = this.footerMainViewLayout()

        // Full Editor Button
        mainLayout.addView(this.fullEditorButtonView())

        // Done Button
        mainLayout.addView(this.doneButtonView())

        //layout.addView(this.dividerView())

        layout.addView(mainLayout)


        return layout
    }


    private fun footerViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.backgroundColor  = Color.WHITE

        layout.corners          = Corners(0.0, 0.0, 2.0, 2.0)

        return layout.linearLayout(context)
    }


    private fun footerMainViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.END or Gravity.CENTER_VERTICAL

        layout.padding.topDp    = 8f
        layout.padding.bottomDp = 8f

        layout.padding.leftDp = 8f
        layout.padding.rightDp = 8f

        return layout.linearLayout(context)
    }


    private fun doneButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout      = LinearLayoutBuilder()
        val icon        = ImageViewBuilder()
        val label       = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

//        layout.gravity          = Gravity.END or Gravity.CENTER_VERTICAL

        val bgColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("green_90"))))
        layout.backgroundColor  = colorOrBlack(bgColorTheme, entityId)

//        layout.backgroundResource = R.drawable.bg_dialog_text_edit_done

         layout.corners              = Corners(4.0, 4.0, 4.0, 4.0)

//        layout.margin.leftDp    = 1f

        layout.padding.topDp    = 6f
        layout.padding.bottomDp = 6f
        layout.padding.leftDp = 12f
        layout.padding.rightDp = 12f


        layout.onClick          = View.OnClickListener {
            val currentValue = this.valueView?.text?.toString()
            if (currentValue != null)
            {
                updateVariable(variableId,
                               EngineValueText(currentValue),
                               entityId)
                dialog.dismiss()
//                when (updateTarget)
//                {
//                    is UpdateTargetStoryWidgetPart ->
//                    {
//                        if (variableId != null)
//                        {
//
//                        }
//                    }
//                    is UpdateTargetTextWidget ->
//                    {
//                        val textWidgetUpdate = TextWidgetUpdateSetText(updateTarget.textWidgetId,
//                                                                       currentValue)
//                        SheetManager.updateSheet(sheetUIContext.sheetId,
//                                                 textWidgetUpdate,
//                                                 sheetUIContext.sheetUI())
//                        dialog.dismiss()
//                    }
//                }
            }
        }

        layout// .child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp        = 30
        icon.heightDp       = 30

        icon.image          = R.drawable.icon_check

//        val iconColorTheme  = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_14")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("green"))))
        icon.color          = Color.WHITE //SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

//        icon.margin.rightDp = 5f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        label.text          = context.getString(R.string.done).toUpperCase()
//        label.textId        = R.string.done

//        val labelColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_14")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("green"))))
        label.color         = Color.WHITE //  SheetManager.color(sheetUIContext.sheetId, labelColorTheme)

        label.font          = Font.typeface(TextFont.default(),
                                            TextFontStyle.Bold,
                                            context)

        label.sizeSp        = 17f

        return layout.linearLayout(context)
    }


    private fun fullEditorButtonView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout      = LinearLayoutBuilder()
        val icon        = ImageViewBuilder()
        val label       = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity          = Gravity.CENTER

        val bgColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        layout.backgroundColor  = colorOrBlack(bgColorTheme, entityId)

//        layout.backgroundResource = R.drawable.bg_dialog_text_edit_done

         layout.corners              = Corners(4.0, 4.0, 4.0, 4.0)

        layout.margin.rightDp    = 10f

        layout.padding.topDp    = 6f
        layout.padding.bottomDp = 6f
        layout.padding.leftDp = 10f
        layout.padding.rightDp = 10f

        layout.child(label)

//        // (3 A) Icon
//        // -------------------------------------------------------------------------------------

        icon.widthDp        = 28
        icon.heightDp       = 28

        icon.image          = R.drawable.icon_open_in_window


        val iconColorTheme  = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_green_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_15"))))
        icon.color          = colorOrBlack(iconColorTheme, entityId)

        //icon.margin.rightDp = 5f

        // (3 B) Label
        // -------------------------------------------------------------------------------------

        label.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height        = LinearLayout.LayoutParams.WRAP_CONTENT

//        label.text          = sheetUIContext.context.getString(R.string.done).toUpperCase()
        label.textId        = R.string.full_editor

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_25")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        label.color         = colorOrBlack(labelColorTheme, entityId)

        label.font          = Font.typeface(TextFont.default(),
                                            TextFontStyle.Bold,
                                            context)

        label.sizeSp        = 17f

        return layout.linearLayout(context)
    }


    private fun dividerView() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_4"))))
        layout.backgroundColor  = colorOrBlack(colorTheme, entityId)

        return layout.linearLayout(context)
    }

}

