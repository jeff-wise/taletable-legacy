
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
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.sheet.SheetUIContext
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetManager



/**
 * Text Editor Dialog
 */
class TextEditorDialogFragment : DialogFragment()
{


    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var title            : String? = null
    private var text             : String? = null
    private var sheetContext: SheetContext? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(title : String,
                        text : String,
                        sheetContext : SheetContext) : TextEditorDialogFragment
        {
            val dialog = TextEditorDialogFragment()

            val args = Bundle()
            args.putString("text", text)
            args.putString("title", title)
            args.putSerializable("sheet_context", sheetContext)
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

        this.title        = arguments.getString("title")
        this.text         = arguments.getString("text")
        this.sheetContext = arguments.getSerializable("sheet_context") as SheetContext


        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(activity)

        val sheetContext = this.sheetContext
        if (sheetContext != null)
        {
            val sheetUIContext = SheetUIContext(sheetContext, context)

            val dialogLayout = this.dialogLayout(sheetUIContext)

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.setContentView(dialogLayout)

            val width  = context.resources.getDimension(R.dimen.action_dialog_width)
            val height = LinearLayout.LayoutParams.WRAP_CONTENT

            dialog.window.setLayout(width.toInt(), height)
        }

        return dialog
    }


    override fun onCreateView(inflater : LayoutInflater?,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        val sheetContext = this.sheetContext
        if (sheetContext != null)
        {
            val sheetUIContext  = SheetUIContext(sheetContext, context)

            val title      = this.title
            val text = this.text

            if (title != null && text != null)
                return TextEditorView.view(title, text, sheetUIContext)
            else
                return super.onCreateView(inflater, container, savedInstanceState)
        }
        else
        {
            return super.onCreateView(inflater, container, savedInstanceState)
        }
    }


    // -----------------------------------------------------------------------------------------
    // DIALOG LAYOUT
    // -----------------------------------------------------------------------------------------

    fun dialogLayout(sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        val colorTheme = ColorTheme(setOf(
                            ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                            ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        return layout.linearLayout(context)
    }



}


object TextEditorView
{

    fun view(title : String, text : String, sheetUIContext: SheetUIContext) : View
    {
        val layout = this.viewLayout(sheetUIContext)

        // Header
        layout.addView(this.headerView(title, sheetUIContext))

        layout.addView(this.dividerView(sheetUIContext))

        // Edit Field
        val editValueView = this.editValueView(text, sheetUIContext)
        layout.addView(editValueView)

        // Footer View
        layout.addView(this.footerView(editValueView, sheetUIContext))

        return layout
    }


    private fun viewLayout(sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp             = LinearLayout.LayoutParams.MATCH_PARENT

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        layout.corners              = Corners(TopLeftCornerRadius(2f),
                                              TopRightCornerRadius(2f),
                                              BottomRightCornerRadius(2f),
                                              BottomLeftCornerRadius(2f))

        return layout.linearLayout(sheetUIContext.context)
    }


    // Header
    // -----------------------------------------------------------------------------------------

    private fun headerView(nameString : String, sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout = this.headerViewLayout(sheetUIContext)

        // Name
        layout.addView(this.nameView(nameString, sheetUIContext))

        // Full Editor Button

        return layout
    }


    private fun headerViewLayout(sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.HORIZONTAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.padding.topDp        = 8f
        layout.padding.bottomDp     = 8f

        layout.padding.leftDp       = 12f
        layout.padding.rightDp      = 12f

//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_8")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
//        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        layout.gravity              = Gravity.CENTER_VERTICAL

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun nameView(nameString : String, sheetUIContext: SheetUIContext) : TextView
    {
        val name            = TextViewBuilder()

        name.width          = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height         = LinearLayout.LayoutParams.WRAP_CONTENT

        name.text           = nameString

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_20")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        name.color          = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        name.font           = Font.typeface(TextFont.FiraSans,
                                            TextFontStyle.Regular,
                                            sheetUIContext.context)

        name.sizeSp         = 15f

        return name.textView(sheetUIContext.context)
    }


    private fun fullEditorButton(sheetUIContext: SheetUIContext) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout = LinearLayoutBuilder()
        val button = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.margin.rightDp   = 15f
        layout.margin.topDp     = 2f

        layout.onClick          = View.OnClickListener {
//            val intent = Intent(context, TextEditorActivity.class)
//            intent.putExtra("text_widget", textWidget)
//            context.dismiss()
//            context.startActivity(intent)
        }

        layout.child(button)

        // (3) Button
        // -------------------------------------------------------------------------------------

        button.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        button.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        button.text             = sheetUIContext.context.getString(R.string.full_editor)

//        button.font             = Font.type
//
//        button.color            = R.color.dark_blue_1

        button.sizeSp           = 16f

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun dividerView(sheetUIContext: SheetUIContext) : LinearLayout
    {
        val divider             = LinearLayoutBuilder()

        divider.width           = LinearLayout.LayoutParams.MATCH_PARENT
        divider.heightDp        = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_11")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        divider.backgroundColor = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        return divider.linearLayout(sheetUIContext.context)
    }


    // Value
    // -----------------------------------------------------------------------------------------

    private fun editValueView(valueString : String, sheetUIContext: SheetUIContext) : EditText
    {
        val value = EditTextBuilder()

        value.width                 = LinearLayout.LayoutParams.MATCH_PARENT
        value.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        value.font                  = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Regular,
                                                    sheetUIContext.context)

        val textColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_16")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        value.color                 = SheetManager.color(sheetUIContext.sheetId, textColorTheme)

        value.sizeSp                = 17f

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_15")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
//        value.backgroundColor       = SheetManager.color

        value.backgroundResource    = R.drawable.bg_edit_text_no_style

        value.underlineColor        = R.color.dark_blue_hl_1

        value.minHeightDp           = 100f

        value.gravity               = Gravity.TOP

        value.margin.topDp          = 12f
        value.margin.bottomDp       = 12f

        value.margin.rightDp        = 12f
        value.padding.leftDp        = 12f

        value.text                  = valueString


        return value.editText(sheetUIContext.context)
    }


    // Footer
    // -----------------------------------------------------------------------------------------

    private fun footerView(editValueView : EditText, sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout = footerViewLayout(sheetUIContext.context)

        // Done Button
        layout.addView(this.doneButton(editValueView, sheetUIContext))

        return layout
    }


    private fun footerViewLayout(context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.orientation      = LinearLayout.HORIZONTAL
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.gravity          = Gravity.CENTER_VERTICAL or Gravity.END

        layout.margin.bottomDp  = 10f

        return layout.linearLayout(context)
    }



    private fun doneButton(editValueView : EditText, sheetUIContext: SheetUIContext) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout = LinearLayoutBuilder()
        val icon   = ImageViewBuilder()
        val label  = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
//        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

//        layout.padding.topDp        = 6f
//        layout.padding.bottomDp     = 6f
//        layout.padding.leftDp       = 6f
//        layout.padding.rightDp      = 10f

        layout.margin.rightDp       = 13f

//        layout.onClick              = View.OnClickListener {
//            @Override
//            public void onClick(View view)
//            {
//                sendTextWidgetUpdate(editValueView.getText().toString());
//                dismiss();
//            }
//        };

        layout//.child(icon)
              .child(label)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT

        icon.image                  = R.drawable.ic_dialog_done

//        icon.color                  = R.color.green_medium_dark

        icon.margin.rightDp         = 3f

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        label.text                  = sheetUIContext.context.getString(R.string.done).toUpperCase()

        val labelColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("green_1")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        label.color                 = SheetManager.color(sheetUIContext.sheetId, labelColorTheme)

        label.font                  = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Bold,
                                                    sheetUIContext.context)

        label.sizeSp                = 16f

        return layout.linearLayout(sheetUIContext.context)
    }



}

//    // > Update
//    // ------------------------------------------------------------------------------------------
//
//    private void sendTextWidgetUpdate(String newValue)
//    {
//        TextWidget.UpdateLiteralEvent event =
//                new TextWidget.UpdateLiteralEvent(this.textWidget.getId(), newValue);
//
//        EventBus.getDefault().post(event);
//    }
//
//
//    private void sendTextCellUpdate(String newValue)
//    {
//        TextCell.UpdateLiteralEvent event =
//                new TextCell.UpdateLiteralEvent(this.textCell.parentTableWidgetId(),
//                                                this.textCell.getId(),
//                                                newValue);
//
//        EventBus.getDefault().post(event);
//    }
//

//    // TARGET
//    // ------------------------------------------------------------------------------------------
//
//    private enum Target
//    {
//        TEXT_WIDGET,
//        TEXT_CELL
//    }

//}
