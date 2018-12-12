
package com.taletable.android.activity.sheet.task


import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.activity.session.SessionActivity
import com.taletable.android.activity.sheet.dialog.VariableToggleEditorDialog
import com.taletable.android.lib.ui.*
import com.taletable.android.model.engine.task.Task
import com.taletable.android.model.engine.task.TaskActionToggleVariables
import com.taletable.android.model.sheet.style.Corners
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle
import com.taletable.android.model.theme.*
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.entityEngineState



class TaskUI(val entityId : EntityId,
             val theme : Theme,
             val sheetActivity : SessionActivity)
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
        val scrollView = this.scrollView()

        val layout = this.viewLayout()

        // Task List
        entityEngineState(entityId).apDo {
            it.activeTasks().forEach {
                layout.addView(this.taskView(it))
            }
        }

        scrollView.addView(layout)

        return scrollView
    }


    fun scrollView() : ScrollView
    {
        val scrollView      = ScrollViewBuilder()

        scrollView.width    = LinearLayout.LayoutParams.MATCH_PARENT
        scrollView.height   = LinearLayout.LayoutParams.MATCH_PARENT

        return scrollView.scrollView(context)
    }


    fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.padding.leftDp   = 6f
        layout.padding.rightDp  = 6f

        layout.padding.bottomDp = 60f

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_9"))))
        layout.backgroundColor = theme.colorOrBlack(bgColorTheme)

        return layout.linearLayout(context)
    }


    // VIEWS > Task
    // -----------------------------------------------------------------------------------------


    private fun taskView(task : Task) : LinearLayout
    {
        val layout = this.taskViewLayout(task)

        layout.addView(this.taskTitleView(task.title().value))

        layout.addView(this.taskDescriptionView(task.description().value))

        layout.addView(this.taskFooterView(task.actionName().value))

        return layout
    }


    private fun taskViewLayout(task : Task) : LinearLayout
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

        layout.margin.topDp     = 4f

//        layout.elevation        = 2f

        layout.margin.bottomDp  = 4f

        layout.onClick          = View.OnClickListener {
            val taskAction = task.action()
            when (taskAction)
            {
                is TaskActionToggleVariables ->
                {
                    val dialog = VariableToggleEditorDialog.newInstance(taskAction.variableIds(),
                                                                        task,
                                                                        task.actionDescription().value,
                                                                        null,
                                                                        entityId)
                    dialog.show(sheetActivity.supportFragmentManager, "")
                }
            }

        }

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
                                                 TextFontStyle.Bold,
                                                 context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_10"))))
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
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        desc.color              = theme.colorOrBlack(colorTheme)

        desc.sizeSp             = 16f

        return desc.textView(context)
    }


    private fun taskFooterView(actionName : String) : LinearLayout
    {
        val layout = this.taskFooterViewLayout()

        layout.addView(this.taskFooterLaterButtonView())

        layout.addView(this.taskFooterActionButtonView(actionName))

        return layout
    }


    private fun taskFooterViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = Gravity.END

        layout.padding.rightDp  = 8f
        layout.padding.leftDp   = 8f
        layout.padding.topDp    = 10f
        layout.padding.bottomDp = 10f

        return layout.linearLayout(context)
    }


    private fun taskFooterLaterButtonView() : TextView
    {
        val desc                = TextViewBuilder()

        desc.layoutType         = LayoutType.RELATIVE
        desc.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        desc.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        desc.addRule(RelativeLayout.ALIGN_PARENT_START)
        desc.addRule(RelativeLayout.CENTER_VERTICAL)

        desc.text               = context.getString(R.string.later) //.toUpperCase()

        desc.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.Bold,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
        desc.color              = theme.colorOrBlack(colorTheme)

        desc.sizeSp             = 16f

        return desc.textView(context)
    }


    private fun taskFooterActionButtonView(actionName : String) : TextView
    {
        val desc                = TextViewBuilder()

        desc.layoutType         = LayoutType.RELATIVE
        desc.width              = LinearLayout.LayoutParams.WRAP_CONTENT
        desc.height             = LinearLayout.LayoutParams.WRAP_CONTENT

        desc.addRule(RelativeLayout.ALIGN_PARENT_START)
        desc.addRule(RelativeLayout.CENTER_VERTICAL)

        val bgColorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_90"))))
        desc.backgroundColor    = theme.colorOrBlack(bgColorTheme)

        desc.text               = actionName.toUpperCase()

        desc.font               = Font.typeface(TextFont.default(),
                                                TextFontStyle.BoldItalic,
                                                context)

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_20"))))
        desc.color              = theme.colorOrBlack(colorTheme)
        desc.color              = Color.WHITE

        desc.sizeSp             = 16f

        desc.margin.leftDp      = 22f

        desc.corners            = Corners(2.0, 2.0, 2.0, 2.0)

        desc.padding.leftDp     = 8f
        desc.padding.rightDp    = 8f
        desc.padding.topDp      = 4f
        desc.padding.bottomDp   = 4f

        return desc.textView(context)
    }


}


