
package com.kispoko.tome.activity.sheet.dialog


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.summation.Summation
import com.kispoko.tome.model.game.engine.summation.term.TermSummary
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.util.Util



/**
 * Summation Dialog Fragment
 */
class SummationDialog : DialogFragment()
{


    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var summation       : Summation? = null
    private var summmationLabel : String? = null
    private var entityId        : EntityId? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(summation : Summation,
                        summationLabel : String,
                        entityId : EntityId) : SummationDialog
        {
            val dialog = SummationDialog()

            val args = Bundle()
            args.putSerializable("summationWithId", summation)
            args.putString("summation_label", summationLabel)
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

        this.summation       = arguments.getSerializable("summationWithId") as Summation
        this.summmationLabel = arguments.getString("summation_label")
        this.entityId        = arguments.getSerializable("entity_id") as EntityId


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
        val summation      = this.summation
        val summationLabel = this.summmationLabel
        val entityId       = this.entityId

        return if (summation != null && summationLabel != null && entityId != null) {
            val viewBuilder = SummationViewBuilder(summation,
                                                   summationLabel,
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


class SummationViewBuilder(val summation : Summation,
                           val summationLabel : String,
                           val dialog : DialogFragment,
                           val entityId : EntityId,
                           val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val activity = context as AppCompatActivity


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        // Header
        //layout.addView(this.nameView())

        // Main View
        //layout.addView(this.mainView())
        layout.addView(this.headerView())

        layout.addView(this.componentDividerView())

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
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
        layout.backgroundColor      = colorOrBlack(colorTheme, entityId)

        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)

        return layout.linearLayout(context)
    }


    // HEADER VIEW
    // -----------------------------------------------------------------------------------------



    private fun headerView() : LinearLayout
    {
        val layout = this.headerViewLayout()

        val mainLayout = this.headerMainViewLayout()

        // Name
        mainLayout.addView(this.nameView())

        // Total
        val total = summation.value(entityId)
        mainLayout.addView(this.totalView(Util.doubleString(total)))

        layout.addView(mainLayout)

//        layout.addView(this.headerBottomBorderView())

        return layout
    }


    private fun headerViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
//        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)
        layout.backgroundColor  = Color.WHITE

        layout.corners          = Corners(2.0, 2.0, 0.0, 0.0)

        return layout.linearLayout(context)
    }



    private fun headerMainViewLayout() : RelativeLayout
    {
        val layout              = RelativeLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.padding.topDp    = 6f
        layout.padding.bottomDp = 6f
        layout.padding.leftDp   = 6f
        layout.padding.rightDp  = 6f

        layout.corners          = Corners(2.0, 2.0, 0.0, 0.0)

        layout.backgroundColor  = Color.WHITE

        return layout.relativeLayout(context)
    }


//    private fun headerBottomBorderView() : LinearLayout
//    {
//        val layout              = LinearLayoutBuilder()
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.heightDp         = 1
//
//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_10")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_6"))))
//        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, colorTheme)
//
//        return layout.linearLayout(sheetUIContext.context)
//    }


    private fun nameView() : TextView
    {
        val name = TextViewBuilder()

        name.layoutType     = LayoutType.RELATIVE
        name.width          = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height         = LinearLayout.LayoutParams.WRAP_CONTENT

        name.addRule(RelativeLayout.ALIGN_PARENT_START)
        name.addRule(RelativeLayout.CENTER_VERTICAL)

        name.text           = summationLabel

        name.font           = Font.typeface(TextFont.default(),
                                            TextFontStyle.Bold,
                                            context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        name.color          = colorOrBlack(colorTheme, entityId)

        name.sizeSp         = 18f

        return name.textView(context)
    }


    private fun totalView(totalString : String) : LinearLayout
    {
        // (1) Delcarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val icon            = ImageViewBuilder()
        val total           = TextViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType       = LayoutType.RELATIVE
        layout.width            = RelativeLayout.LayoutParams.WRAP_CONTENT
        layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.addRule(RelativeLayout.ALIGN_PARENT_END)
        layout.addRule(RelativeLayout.CENTER_VERTICAL)

        layout.gravity          = Gravity.CENTER_VERTICAL

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_green_4")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
//        layout.backgroundColor   = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

//        layout.padding.topDp     = 6f
//        layout.padding.bottomDp  = 6f
//        layout.padding.leftDp    = 14f
//        layout.padding.rightDp   = 14f

        //layout.corners          = Corners(3.0, 3.0, 3.0, 3.0)

        layout// .child(icon)
              .child(total)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp                = 22
        icon.heightDp               = 22

        icon.image                  = R.drawable.icon_edit

        val iconColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
        icon.color                  = colorOrBlack(iconColorTheme, entityId)

//        icon.margin.rightDp         = 7f

        // (3 B) Total
        // -------------------------------------------------------------------------------------

        total.width             = RelativeLayout.LayoutParams.WRAP_CONTENT
        total.height            = RelativeLayout.LayoutParams.WRAP_CONTENT

        total.text              = totalString

        total.font              = Font.typeface(TextFont.default(),
                                                TextFontStyle.Bold,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue"))))
        total.color             = colorOrBlack(colorTheme, entityId)



        total.sizeSp            = 22f

        return layout.linearLayout(context)
    }


    // Summation
    // -----------------------------------------------------------------------------------------

    private fun summationView() : ScrollView
    {
        val layout = this.summationScrollView()

        // Components
        val termSummaries = summation.summary(entityId)
        layout.addView(this.componentsView(termSummaries))

        return layout
    }


    private fun summationScrollView() : ScrollView
    {
        val scrollView          = ScrollViewBuilder()

        scrollView.width        = LinearLayout.LayoutParams.MATCH_PARENT
        scrollView.heightDp     = 300

//        scrollView.fadingEnabled = false

        return scrollView.scrollView(context)
    }


    private fun componentsView(termSummaries : List<TermSummary>) : LinearLayout
    {
        val layout = this.componentsViewLayout()

        termSummaries.forEach {
            layout.addView(this.componentView(it))
        }

        return layout
    }


    private fun componentsViewLayout() : LinearLayout
    {
        val layout              =  LinearLayoutBuilder()

        layout.orientation      = LinearLayout.VERTICAL
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        return layout.linearLayout(context)
    }


    private fun componentView(termSummary : TermSummary) : LinearLayout
    {
        val layout = this.componentViewLayout()

        // Name
        if (termSummary.name != null) {
            layout.addView(this.componentHeaderView(termSummary.name))
            //layout.addView(this.componentDividerView())
        }

        // Components
        termSummary.components.forEach {
            layout.addView(this.componentItemView(it.name, it.value))
            //layout.addView(this.componentDividerView())
        }

        return layout
    }


    private fun componentViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

//        layout.margin.topDp         = 1f
//        layout.margin.leftDp        = 2f
//        layout.margin.rightDp       = 2f

        layout.padding.bottomDp     = 6f
//        layout.padding.leftDp       = 10f
//        layout.padding.rightDp       = 10f

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_25")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_3"))))
//        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

        return layout.linearLayout(context)
    }


    private fun componentHeaderView(termName : String) : TextView
    {
        val header                  = TextViewBuilder()

        header.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        header.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        header.padding.topDp        = 4f
        header.padding.bottomDp     = 2f
        header.margin.leftDp        = 4f

        header.text                 = termName

        header.font                 = Font.typeface(TextFont.default(),
                                                    TextFontStyle.Bold,
                                                    context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_25")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
        header.color                = colorOrBlack(colorTheme, entityId)

        header.sizeSp               = 15f

        return header.textView(context)
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

        layout.width                    = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height                   = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.margin.topDp             = 2f
        layout.margin.rightDp           = 2f
        layout.margin.leftDp            = 2f

        layout.padding.topDp            = 6f
        layout.padding.bottomDp         = 6f
        layout.padding.leftDp           = 4f
        layout.padding.rightDp          = 4f

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
//        layout.backgroundColor          = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

//        layout.corners                  = Corners(TopLeftCornerRadius(2f),
//                                                  TopRightCornerRadius(2f),
//                                                  BottomRightCornerRadius(2f),
//                                                  BottomLeftCornerRadius(2f))

        layout.backgroundColor          = Color.WHITE

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

        name.font                       = Font.typeface(TextFont.default(),
                                                        TextFontStyle.Regular,
                                                        context)

        name.sizeSp                     = 16f

        val nameColorTheme = ColorTheme(setOf(
                                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_15")),
                                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        name.color                      = colorOrBlack(nameColorTheme, entityId)

        // (3 B) Value
        // -------------------------------------------------------------------------------------

        value.layoutType                = LayoutType.RELATIVE
        value.width                     = RelativeLayout.LayoutParams.WRAP_CONTENT
        value.height                    = RelativeLayout.LayoutParams.WRAP_CONTENT

        value.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        value.addRule(RelativeLayout.CENTER_VERTICAL)

        value.text                      = valueString

        value.font                      = Font.typeface(TextFont.default(),
                                                        TextFontStyle.Medium,
                                                        context)

        value.sizeSp                    = 17f

        val valueColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_17")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
        value.color                     = colorOrBlack(valueColorTheme, entityId)

        return layout.relativeLayout(context)
    }


    private fun componentDividerView() : LinearLayout
    {
        val divider = LinearLayoutBuilder()

        divider.width               = LinearLayout.LayoutParams.MATCH_PARENT
        divider.heightDp            = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_12")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_4"))))
        divider.backgroundColor     = colorOrBlack(colorTheme, entityId)

        return divider.linearLayout(context)
    }


}

