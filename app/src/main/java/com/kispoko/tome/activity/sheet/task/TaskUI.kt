
package com.kispoko.tome.activity.sheet.task


import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.sheet.SheetActivity
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.LayoutType
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.engine.task.Task
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.entityEngineState



class TaskUI(val entityId : EntityId,
             val theme : Theme,
             val sheetActivity : SheetActivity)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val context = sheetActivity


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        // Task List
        entityEngineState(entityId).apDo {
            it.activeTasks().forEach {
                layout.addView(this.taskView(it))
            }
        }


        return layout
    }


    fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.leftDp   = 6f
        layout.padding.rightDp  = 6f

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_7"))))
        layout.backgroundColor = theme.colorOrBlack(bgColorTheme)

        return layout.linearLayout(context)
    }


    // VIEWS > Task
    // -----------------------------------------------------------------------------------------


    private fun taskView(task : Task) : LinearLayout
    {
        val layout = this.taskViewLayout()

        layout.addView(this.taskTitleView(task.title().value))

        layout.addView(this.taskDescriptionView(task.description().value))

        layout.addView(this.taskFooterView())

        return layout
    }


    private fun taskViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.backgroundColor  = Color.WHITE

        layout.corners          = Corners(3.0, 3.0, 3.0, 3.0)

        layout.padding.topDp    = 8f
        layout.padding.bottomDp = 8f
        layout.padding.leftDp   = 8f
        layout.padding.rightDp  = 8f

        layout.margin.topDp     = 8f

        return layout.linearLayout(context)
    }


    private fun taskTitleView(titleString : String) : TextView
    {
        val title                = TextViewBuilder()

        title.layoutType         = LayoutType.RELATIVE
        title.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        title.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        title.addRule(RelativeLayout.ALIGN_PARENT_START)
        title.addRule(RelativeLayout.CENTER_VERTICAL)

        title.text               = titleString

        title.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Medium,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        title.color              = theme.colorOrBlack(colorTheme)

        title.sizeSp             = 18f

        return title.textView(context)
    }



    private fun taskDescriptionView(descriptionString : String) : TextView
    {
        val desc                = TextViewBuilder()

        desc.layoutType         = LayoutType.RELATIVE
        desc.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        desc.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        desc.addRule(RelativeLayout.ALIGN_PARENT_START)
        desc.addRule(RelativeLayout.CENTER_VERTICAL)

        desc.text               = descriptionString

        desc.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Regular,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_14"))))
        desc.color              = theme.colorOrBlack(colorTheme)

        desc.sizeSp             = 16f

        return desc.textView(context)
    }


    private fun taskFooterView() : LinearLayout
    {
        val layout = this.taskFooterViewLayout()

        layout.addView(this.taskFooterButtonView(R.string.later))

        layout.addView(this.taskFooterButtonView(R.string._do))

        return layout
    }


    private fun taskFooterViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.END

        layout.padding.rightDp  = 10f
        layout.padding.leftDp   = 10f
        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 10f

        return layout.linearLayout(context)
    }


    private fun taskFooterButtonView(labelId : Int) : TextView
    {
        val desc                = TextViewBuilder()

        desc.layoutType         = LayoutType.RELATIVE
        desc.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        desc.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        desc.addRule(RelativeLayout.ALIGN_PARENT_START)
        desc.addRule(RelativeLayout.CENTER_VERTICAL)

        desc.text               = context.getString(labelId).toUpperCase()

        desc.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Bold,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
        desc.color              = theme.colorOrBlack(colorTheme)

        desc.sizeSp             = 16f

        desc.padding.leftDp     = 12f

        return desc.textView(context)
    }

}


