
package com.kispoko.tome.activity.sheet.dialog


import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.game.engine.summation.SummationActivity
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.summation.Summation
import com.kispoko.tome.model.game.engine.summation.term.TermSummary
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.sheet.SheetUIContext
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.util.Util



/**
 * Summation Dialog Fragment
 */
class SummationDialogFragment : DialogFragment()
{


    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var summation        : Summation? = null
    private var summmationLabel  : String? = null
    private var sheetContext     : SheetContext? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(summation : Summation,
                        summationLabel : String,
                        sheetContext: SheetContext) : SummationDialogFragment
        {
            val dialog = SummationDialogFragment()

            val args = Bundle()
            args.putSerializable("summation", summation)
            args.putString("summation_label", summationLabel)
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

        this.summation        = arguments.getSerializable("summation") as Summation
        this.summmationLabel  = arguments.getString("summation_label")
        this.sheetContext     = arguments.getSerializable("sheet_context") as SheetContext


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

            val summation      = this.summation
            val summationLabel = this.summmationLabel

            if (summation != null && summationLabel != null) {
                val viewBuilder = SummationViewBuilder(summation,
                                                       summationLabel,
                                                       sheetUIContext,
                                                       this)
                return viewBuilder.view()
            }
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


class SummationViewBuilder(val summation : Summation,
                           val summationLabel : String,
                           val sheetUIContext : SheetUIContext,
                           val dialog : DialogFragment)
{

    // -----------------------------------------------------------------------------------------yy
    // PROPERTIES
    // -----------------------------------------------------------------------------------------yy

    val sheetContext = SheetContext(sheetUIContext)
    val activity = sheetUIContext.context as AppCompatActivity


    // -----------------------------------------------------------------------------------------yy
    // VIEWS
    // -----------------------------------------------------------------------------------------yy

    fun view() : View
    {
        val layout = this.viewLayout()

        // Header
        layout.addView(this.nameView())

        // Main View
        layout.addView(this.mainView())

        // Summmation
        layout.addView(this.summationView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

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


    private fun nameView() : TextView
    {
        val name = TextViewBuilder()

        name.width          = LinearLayout.LayoutParams.MATCH_PARENT
        name.height         = LinearLayout.LayoutParams.WRAP_CONTENT

        name.gravity        = Gravity.CENTER_HORIZONTAL

        name.text           = summationLabel.toUpperCase()

        name.font           = Font.typeface(TextFont.FiraSans,
                                            TextFontStyle.Regular,
                                            sheetUIContext.context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_25")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        name.color          = SheetManager.color(sheetUIContext.sheetId, colorTheme)


        name.corners              = Corners(TopLeftCornerRadius(2f),
                                            TopRightCornerRadius(2f),
                                            BottomRightCornerRadius(0f),
                                            BottomLeftCornerRadius(0f))

        name.sizeSp         = 11f

        name.padding.topDp  = 15f

        return name.textView(sheetUIContext.context)
    }


    private fun mainView() : RelativeLayout
    {
        val layout = this.mainViewLayout()

        // Info Button
        val infoOnClick = View.OnClickListener {  }
        layout.addView(this.buttonView(R.drawable.icon_help, true, infoOnClick))

        // Total
        val total = summation.value(sheetContext)
        layout.addView(this.totalView(Util.doubleString(total)))

        // Edit Button
        val editOnClick = View.OnClickListener {
            val intent = Intent(activity, SummationActivity::class.java)
            intent.putExtra("summation_id", summation.summationId())
            intent.putExtra("sheet_context", sheetContext)
            activity.startActivity(intent)
            dialog.dismiss()
        }
        layout.addView(this.buttonView(R.drawable.icon_edit_property, false, editOnClick))

        return layout
    }


    private fun mainViewLayout() : RelativeLayout
    {
        val layout              = RelativeLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        return layout.relativeLayout(sheetUIContext.context)
    }


    private fun totalView(totalString : String) : TextView
    {
        val total               = TextViewBuilder()

        total.layoutType        = LayoutType.RELATIVE
        total.width             = RelativeLayout.LayoutParams.WRAP_CONTENT
        total.height            = RelativeLayout.LayoutParams.WRAP_CONTENT

        total.addRule(RelativeLayout.CENTER_HORIZONTAL)

        //total.gravity           = Gravity.CENTER_HORIZONTAL

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        total.backgroundColor   = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)


        total.font              = Font.typeface(TextFont.FiraSans,
                                                TextFontStyle.Light,
                                                sheetUIContext.context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        total.color             = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        total.sizeSp            = 38f

        total.text              = totalString


        return total.textView(sheetUIContext.context)
    }


    private fun buttonView(iconId : Int,
                           isLeft : Boolean,
                           onClick : View.OnClickListener) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType       = LayoutType.RELATIVE
        layout.width            = RelativeLayout.LayoutParams.WRAP_CONTENT
        layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.gravity          = Gravity.CENTER

        layout.padding.topDp     = 5f

        if (isLeft)
        {
            layout.addRule(RelativeLayout.ALIGN_PARENT_START)
            layout.margin.leftDp = 20f
        }
        else
        {
            layout.addRule(RelativeLayout.ALIGN_PARENT_END)
            layout.margin.rightDp = 20f
        }

        layout.addRule(RelativeLayout.CENTER_VERTICAL)

        layout.onClick              = onClick

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp            = 24
        icon.heightDp           = 24

        icon.image              = iconId

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_1")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        icon.color             = SheetManager.color(sheetUIContext.sheetId, iconColorTheme)

        return layout.linearLayout(sheetUIContext.context)
    }




    // Summation
    // -----------------------------------------------------------------------------------------

    private fun summationView() : LinearLayout
    {
        val layout = this.summationViewLayout()

        // Components
        val termSummaries = summation.summary(sheetContext)
        layout.addView(this.componentsView(termSummaries))

        return layout
    }


    private fun summationViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        //layout.margin.bottomDp      = 10f
        layout.margin.topDp         = 10f

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun componentsView(termSummaries : List<TermSummary>) : LinearLayout
    {
        val layout = this.componentsViewLayout()

        termSummaries.forEach { layout.addView(this.componentView(it)) }

        return layout
    }


    private fun componentsViewLayout() : LinearLayout
    {
        val layout              =  LinearLayoutBuilder()

        layout.orientation      = LinearLayout.VERTICAL
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun componentView(termSummary : TermSummary) : LinearLayout
    {
        val layout = this.componentViewLayout()

        // Name
        if (termSummary.name != null)
            layout.addView(this.componentHeaderView(termSummary.name))

        // Components
        termSummary.components.forEach {
            layout.addView(this.componentItemView(it.name, it.value))
        }

        return layout
    }


    private fun componentViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun componentHeaderView(termName : String) : TextView
    {
        val header                  = TextViewBuilder()

        header.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        header.padding.topDp        = 3f
        header.padding.leftDp       = 11f
        header.padding.bottomDp     = 3f

        header.text                 = termName

        header.font                 = Font.typeface(TextFont.FiraSans,
                                                    TextFontStyle.Regular,
                                                    sheetUIContext.context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_29")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        header.color                = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        header.sizeSp               = 12f

        return header.textView(sheetUIContext.context)
    }



    private fun componentItemView(nameString : String,
                                  valueString : String) : RelativeLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout  = RelativeLayoutBuilder()
        val name    = TextViewBuilder()
        val value   = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.orientation              = LinearLayout.HORIZONTAL
        layout.width                    = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.padding.leftDp           = 8f
        layout.padding.rightDp          = 12f
        layout.padding.topDp            = 10f
        layout.padding.bottomDp         = 10f

        layout.margin.leftDp            = 3f
        layout.margin.rightDp           = 3f
        layout.margin.bottomDp          = 3f

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        layout.backgroundColor          = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        layout.corners                  = Corners(TopLeftCornerRadius(2f),
                                                  TopRightCornerRadius(2f),
                                                  BottomRightCornerRadius(2f),
                                                  BottomLeftCornerRadius(2f))

        layout.child(name)
              .child(value)

        // (3 A) Name
        // -------------------------------------------------------------------------------------

        name.layoutType                 = LayoutType.RELATIVE
        name.width                      = RelativeLayout.LayoutParams.WRAP_CONTENT
        name.height                     = RelativeLayout.LayoutParams.WRAP_CONTENT

        name.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        name.addRule(RelativeLayout.CENTER_VERTICAL)

        name.text                       = nameString

        name.font                       = Font.typeface(TextFont.FiraSans,
                                                        TextFontStyle.Regular,
                                                        sheetUIContext.context)

        name.sizeSp                     = 14f

        val nameColorTheme = ColorTheme(setOf(
                                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_15")),
                                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        name.color                      = SheetManager.color(sheetUIContext.sheetId,
                                                             nameColorTheme)

        // (3 B) Value
        // -------------------------------------------------------------------------------------

        value.layoutType                = LayoutType.RELATIVE
        value.width                     = RelativeLayout.LayoutParams.WRAP_CONTENT
        value.height                    = RelativeLayout.LayoutParams.WRAP_CONTENT

        value.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        name.addRule(RelativeLayout.CENTER_VERTICAL)

        value.text                      = valueString

        value.font                      = Font.typeface(TextFont.FiraSans,
                                                        TextFontStyle.Bold,
                                                        sheetUIContext.context)

        value.sizeSp                    = 16f

        val valueColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_9")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
        value.color                     = SheetManager.color(sheetUIContext.sheetId,
                                                             valueColorTheme)

        return layout.relativeLayout(sheetUIContext.context)
    }


}

