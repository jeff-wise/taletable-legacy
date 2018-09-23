
package com.taletable.android.activity.entity.engine.summation


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.activity.sheet.dialog.NumberEditorDialog
import com.taletable.android.lib.ui.*
import com.taletable.android.model.engine.reference.NumberReferenceVariable
import com.taletable.android.model.engine.summation.Summation
import com.taletable.android.model.engine.summation.term.SummationTerm
import com.taletable.android.model.engine.summation.term.SummationTermNumber
import com.taletable.android.model.engine.summation.term.TermSummary
import com.taletable.android.model.engine.variable.VariableId
import com.taletable.android.model.sheet.style.*
import com.taletable.android.model.theme.ColorId
import com.taletable.android.model.theme.ColorTheme
import com.taletable.android.model.theme.ThemeColorId
import com.taletable.android.model.theme.ThemeId
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack
import com.taletable.android.rts.entity.numberVariable
import com.taletable.android.rts.entity.sheet.UpdateTargetVariable
import com.taletable.android.util.Util



/**
 * Summation Dialog Fragment
 */
class SummationDialog : BottomSheetDialogFragment()
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

        this.summation       = arguments?.getSerializable("summationWithId") as Summation
        this.summmationLabel = arguments?.getString("summation_label")
        this.entityId        = arguments?.getSerializable("entity_id") as EntityId


        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(context)

        val dialogLayout = this.dialogLayout()

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.window.attributes.windowAnimations = R.style.DialogAnimation

        dialog.setContentView(dialogLayout)

        val window = dialog.window
        val wlp = window.attributes

        wlp.gravity = Gravity.BOTTOM
        window.attributes = wlp

        val width  = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT

        dialog.window.setLayout(width, height)

        return dialog
    }


    override fun onCreateView(inflater : LayoutInflater,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        val summation      = this.summation
        val summationLabel = this.summmationLabel
        val entityId       = this.entityId
        val context        = this.context

        return if (summation != null && summationLabel != null && entityId != null && context != null) {
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

        layout.padding.leftDp        = 30f
        layout.padding.rightDp       = 30f

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

        // Footer
        // layout.addView(this.footerView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.backgroundColor      = Color.WHITE

        layout.corners              = Corners(10.0, 10.0, 0.0, 0.0)

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

        //layout.addView(this.headerBottomBorderView())

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

//        layout.corners          = Corners(6.0, 6.0, 0.0, 0.0)
        layout.corners              = Corners(10.0, 10.0, 0.0, 0.0)

        return layout.linearLayout(context)
    }



    private fun headerMainViewLayout() : RelativeLayout
    {
        val layout              = RelativeLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 10f
        layout.padding.leftDp   = 14f
        layout.padding.rightDp  = 16f

        layout.corners          = Corners(10.0, 10.0, 0.0, 0.0)

        layout.backgroundColor  = Color.WHITE

        return layout.relativeLayout(context)
    }


    private fun headerBottomBorderView() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp         = 1

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("medium_grey_10")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_6"))))
        layout.backgroundColor  = colorOrBlack(colorTheme, entityId)

        return layout.linearLayout(context)
    }


    private fun nameView() : TextView
    {
        val name = TextViewBuilder()

        name.layoutType     = LayoutType.RELATIVE
        name.width          = LinearLayout.LayoutParams.WRAP_CONTENT
        name.height         = LinearLayout.LayoutParams.WRAP_CONTENT

        name.addRule(RelativeLayout.ALIGN_PARENT_START)
        name.addRule(RelativeLayout.CENTER_VERTICAL)

        name.text           = summationLabel

        name.font           = Font.typeface(TextFont.RobotoCondensed,
                                            TextFontStyle.Regular,
                                            context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        name.color          = colorOrBlack(colorTheme, entityId)

        name.sizeSp         = 22f

        return name.textView(context)
    }


    // Footer
    // -----------------------------------------------------------------------------------------

    private fun footerView() : RelativeLayout
    {
        val layout = this.footerViewLayout()

        // Name
        layout.addView(totalLabelView())

        // Total
        val total = summation.value(entityId)
        layout.addView(this.totalView(Util.doubleString(total)))

        return layout
    }


    private fun footerViewLayout() : RelativeLayout
    {
        val layout              = RelativeLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_80"))))
        layout.backgroundColor  = colorOrBlack(bgColorTheme, entityId)
//        layout.backgroundColor  = Color.WHITE

        layout.corners          = Corners(2.0, 2.0, 2.0, 2.0)

        layout.margin.leftDp    = 1f
        layout.margin.rightDp    = 1f
        layout.margin.bottomDp    = 2f

        layout.padding.leftDp   = 6f
        layout.padding.rightDp   = 6f
        layout.padding.topDp   = 8f
        layout.padding.bottomDp   = 8f

        return layout.relativeLayout(context)
    }


    private fun totalLabelView() : TextView
    {
        val name            = TextViewBuilder()

        name.layoutType     = LayoutType.RELATIVE
        name.width          = RelativeLayout.LayoutParams.WRAP_CONTENT
        name.height         = RelativeLayout.LayoutParams.WRAP_CONTENT

        name.addRule(RelativeLayout.ALIGN_PARENT_START)
        name.addRule(RelativeLayout.CENTER_VERTICAL)

        name.textId           = R.string.total

        name.font           = Font.typeface(TextFont.RobotoCondensed,
                                            TextFontStyle.Bold,
                                            context)

//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
//        name.color          = colorOrBlack(colorTheme, entityId)
        name.color          = Color.WHITE

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

        layout.gravity          = Gravity.CENTER

        layout.backgroundResource   = R.drawable.bg_summation_dialog_total

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

        total.font              = Font.typeface(TextFont.RobotoCondensed,
                                                TextFontStyle.Bold,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_5")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
        total.color             = colorOrBlack(colorTheme, entityId)
        total.color             = Color.WHITE



        total.sizeSp            = 20f

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
        scrollView.heightDp     = 250
//        scrollView.height       = LinearLayout.LayoutParams.WRAP_CONTENT

//        scrollView.fadingEnabled = false

        return scrollView.scrollView(context)
    }


    private fun componentsView(termSummaries : List<TermSummary>) : LinearLayout
    {
        val layout = this.componentsViewLayout()

        termSummaries.forEachIndexed { index, termSummary ->
            layout.addView(this.componentView(termSummary, index))
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


    private fun componentView(termSummary : TermSummary, index : Int) : LinearLayout
    {
        val layout = this.componentViewLayout()

        if (index != 0)
            layout.addView(this.componentDividerView())

        // Name
        if (termSummary.name != null) {
            layout.addView(this.componentHeaderView(termSummary.name))
        }

        // Components
        termSummary.components.forEach {
            layout.addView(this.componentItemView(it.name, it.value, termSummary.term))
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
        layout.margin.leftDp        = 14f
        layout.margin.rightDp       = 26f

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

        header.padding.topDp        = 8f
        header.padding.bottomDp     = 0f
//        header.margin.leftDp        = 14f

        header.text                 = termName

        header.font                 = Font.typeface(TextFont.RobotoCondensed,
                                                    TextFontStyle.Regular,
                                                    context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_25")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_24"))))
        header.color                = colorOrBlack(colorTheme, entityId)

        header.sizeSp               = 15f

        return header.textView(context)
    }



    private fun componentItemView(nameString : String,
                                  valueString : String,
                                  term : SummationTerm) : RelativeLayout
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

        // layout.margin.topDp             = 2f
//        layout.margin.rightDp           = 2f
//        layout.margin.leftDp            = 2f

        layout.padding.topDp            = 8f
        layout.padding.bottomDp         = 6f

//        val bgColorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("dark_grey_6")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey"))))
//        layout.backgroundColor          = SheetManager.color(sheetUIContext.sheetId, bgColorTheme)

//        layout.corners                  = Corners(TopLeftCornerRadius(2f),
//                                                  TopRightCornerRadius(2f),
//                                                  BottomRightCornerRadius(2f),
//                                                  BottomLeftCornerRadius(2f))

        when (term) {
            is SummationTermNumber -> {
                val termReference = term.numberReference()
                when (termReference) {
                    is NumberReferenceVariable -> {
                        val variableReference = termReference.variableReference
                        when (variableReference) {
                            is VariableId -> {
                                numberVariable(variableReference, entityId) apDo { variable ->

                                    layout.onClick = View.OnClickListener {
                                        val editDialog = NumberEditorDialog.newInstance(
                                                            variable.valueOrZero(entityId),
                                                            variable.label().value,
                                                            UpdateTargetVariable(variableReference),
                                                            entityId)
                                        editDialog.show(activity.supportFragmentManager, "")
                                        dialog.dismiss()
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }

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

        name.font                       = Font.typeface(TextFont.RobotoCondensed,
                                                        TextFontStyle.Regular,
                                                        context)

        name.sizeSp                     = 18f

        val nameColorTheme = ColorTheme(setOf(
                                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_15")),
                                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
        name.color                      = colorOrBlack(nameColorTheme, entityId)

        // (3 B) Value
        // -------------------------------------------------------------------------------------

        value.layoutType                = LayoutType.RELATIVE
        value.width                     = RelativeLayout.LayoutParams.WRAP_CONTENT
        value.height                    = RelativeLayout.LayoutParams.WRAP_CONTENT

        value.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        value.addRule(RelativeLayout.CENTER_VERTICAL)

        value.text                      = valueString

        value.font                      = Font.typeface(TextFont.RobotoCondensed,
                                                        TextFontStyle.Bold,
                                                        context)

        value.sizeSp                    = 21f

        val valueColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_17")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_16"))))
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

