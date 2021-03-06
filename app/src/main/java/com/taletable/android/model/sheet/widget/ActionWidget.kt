
package com.taletable.android.model.sheet.widget


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.activity.session.SheetActivityRequest
import com.taletable.android.activity.sheet.procedure.RunProcedureActivity
import com.taletable.android.activity.entity.engine.procedure.ProcedureDialog
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.lib.ui.Font
import com.taletable.android.lib.ui.ImageViewBuilder
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.sheet.style.Height
import com.taletable.android.model.sheet.style.IconType
import com.taletable.android.model.sheet.style.TextFormat
import com.taletable.android.model.sheet.style.Width
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack
import com.taletable.android.rts.entity.sheet.UpdateTargetActionWidget
import com.taletable.android.util.Util
import effect.*
import maybe.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable


/**
 * Action Widget Format
 */
data class ActionWidgetFormat(val widgetFormat : WidgetFormat,
                              val viewType : ActionWidgetViewType,
                              val descriptionFormat : TextFormat,
                              val descriptionInactiveFormat : Maybe<TextFormat>,
                              val buttonFormat : TextFormat,
                              val buttonInactiveFormat : Maybe<TextFormat>,
                              val buttonIcon : Maybe<IconType>)
                               : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ActionWidgetFormat>
    {

        private fun defaultWidgetFormat()               = WidgetFormat.default()
        private fun defaultViewType()                   = ActionWidgetViewType.InlineLeftButton
        private fun defaultDescriptionFormat()          = TextFormat.default()
        private fun defaultButtonFormat()               = TextFormat.default()
        private fun defaultButtonIcon()                 = Nothing<IconType>()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<ActionWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::ActionWidgetFormat,
                     // Widget Format
                     split(doc.maybeAt("widget_format"),
                           effValue(defaultWidgetFormat()),
                           { WidgetFormat.fromDocument(it) }),
                     // View Type
                     split(doc.maybeAt("view_type"),
                           effValue<ValueError,ActionWidgetViewType>(defaultViewType()),
                           { ActionWidgetViewType.fromDocument(it) }),
                     // Description Format
                     split(doc.maybeAt("description_format"),
                           effValue(defaultDescriptionFormat()),
                           { TextFormat.fromDocument(it) }),
                     // Description Inactive Format
                     split(doc.maybeAt("description_inactive_format"),
                           effValue<ValueError,Maybe<TextFormat>>(Nothing()),
                           { apply(::Just, TextFormat.fromDocument(it)) }),
                     // Button Format
                     split(doc.maybeAt("button_format"),
                           effValue(defaultButtonFormat()),
                           { TextFormat.fromDocument(it) }),
                     // Button Inactive Format
                     split(doc.maybeAt("button_inactive_format"),
                           effValue<ValueError,Maybe<TextFormat>>(Nothing()),
                           { apply(::Just, TextFormat.fromDocument(it)) }),
                     // Button Icon
                     split(doc.maybeAt("button_icon"),
                           effValue<ValueError,Maybe<IconType>>(defaultButtonIcon()),
                           { apply(::Just, IconType.fromDocument(it)) })
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = ActionWidgetFormat(defaultWidgetFormat(),
                                           defaultViewType(),
                                           defaultDescriptionFormat(),
                                           Nothing<TextFormat>(),
                                           defaultButtonFormat(),
                                           Nothing<TextFormat>(),
                                           defaultButtonIcon())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat().toDocument(),
        "view_type" to this.viewType().toDocument(),
        "description_format" to this.descriptionFormat().toDocument(),
        "button_format" to this.buttonFormat().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat


    fun viewType() : ActionWidgetViewType = this.viewType


    fun descriptionFormat() : TextFormat = this.descriptionFormat


    fun descriptionInactiveFormat() : Maybe<TextFormat> = this.descriptionInactiveFormat


    fun buttonFormat() : TextFormat = this.buttonFormat


    fun buttonInactiveFormat() : Maybe<TextFormat> = this.buttonInactiveFormat


    fun buttonIcon() : Maybe<IconType> = this.buttonIcon

}


/**
 * Action Widget View Type
 */
sealed class ActionWidgetViewType : ToDocument, SQLSerializable, Serializable
{

    object InlineLeftButton : ActionWidgetViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "inline_left_button" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("inline_left_button")

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<ActionWidgetViewType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "inline_left_button" -> effValue<ValueError,ActionWidgetViewType>(
                                            ActionWidgetViewType.InlineLeftButton)
                else                 -> effError<ValueError,ActionWidgetViewType>(
                                            UnexpectedValue("ActionWidgetViewType", doc.text, doc.path))
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}


/**
 * Action Widget Description
 */
data class ActionWidgetDescription(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ActionWidgetDescription>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ActionWidgetDescription> = when (doc)
        {
            is DocText -> effValue(ActionWidgetDescription(doc.text))
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


class ActionWidgetViewBuilder(val actionWidget : ActionWidget,
                              val entityId : EntityId,
                              val context : Context)
{

    // STATE
    // -----------------------------------------------------------------------------------------

    var buttonLayout : LinearLayout? = null
    var buttonIconView : ImageView? = null
    var descriptionTextView : TextView? = null

    var isActive : Boolean = true


    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = WidgetView.layout(actionWidget.widgetFormat(), entityId, context)

        val viewId = Util.generateViewId()
        actionWidget.layoutViewId = viewId
        layout.id = viewId

        val contentLayout = layout.findViewById<LinearLayout>(R.id.widget_content_layout)

        contentLayout.addView(this.inlineLeftButtonView())

        return layout
    }


    fun inlineLeftButtonView() : LinearLayout
    {
        val layout = this.inlineLeftButtonViewLayout()

        this.isActive = actionWidget.isActive(entityId)

        // Button
        val buttonLayout = this.inlineLeftButtonButtonViewLayout()
        this.buttonLayout = buttonLayout

        // Button > Icon
        val buttonIconView = this.inlineLeftButtonButtonIconView()
        buttonLayout.addView(buttonIconView)
        this.buttonIconView = buttonIconView

        // Button > Text
        val buttonTextView = this.inlineLeftButtonButtonTextView()
        buttonLayout.addView(buttonTextView)
        layout.addView(buttonLayout)

        val descriptionView = this.inlineLeftButtonDescriptionView()
        this.descriptionTextView = descriptionView
        layout.addView(descriptionView)

        return layout
    }


    private fun inlineLeftButtonViewLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        val width = actionWidget.widgetFormat().elementFormat().width()
        when (width) {
            is Width.Wrap -> {
                layout.width = LinearLayout.LayoutParams.WRAP_CONTENT
            }
            is Width.Fixed -> {
                layout.widthDp = width.value.toInt()
            }
        }

        val height = actionWidget.widgetFormat().elementFormat().height()
        when (height) {
            is Height.Wrap -> {
                layout.height = LinearLayout.LayoutParams.WRAP_CONTENT
            }
            is Height.Fixed -> {
                layout.heightDp = height.value.toInt()
            }
        }

        layout.orientation  = LinearLayout.HORIZONTAL

        //layout.layoutGravity = Gravity.CENTER

        layout.onClick      = View.OnClickListener {
            if (this.isActive)
            {
                actionWidget.procedure(entityId) apDo { procedure ->

                    // If has parameters, use the activity
                    if (procedure.hasParameters(entityId) || procedure.descriptionLength() > 110) {
                        val activity = context as AppCompatActivity
                        val intent = Intent(activity, RunProcedureActivity::class.java)
                        intent.putExtra("procedure_id", actionWidget.procedureId())
                        intent.putExtra("entity_id", entityId)
                        activity.startActivityForResult(intent, SheetActivityRequest.PROCEDURE_INVOCATION)
                    }
                    // Otherwise, use the dialog
                    else {
                        val dialog = ProcedureDialog.newInstance(actionWidget.procedureId(),
                                                                 UpdateTargetActionWidget(actionWidget.widgetId()),
                                                                 entityId)
                        val activity = context as AppCompatActivity
                        dialog.show(activity.supportFragmentManager, "")
                    }
                }
            }
            else
            {
                actionWidget.setActive(entityId)
            }
        }

        return layout.linearLayout(context)
    }


    private fun inlineLeftButtonButtonViewLayout() : LinearLayout
    {

        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout      = LinearLayoutBuilder()

        var format = actionWidget.format().buttonFormat()
        if (!this.isActive) {
            val inactiveFormat = actionWidget.format().buttonInactiveFormat()
            when (inactiveFormat) {
                is Just -> format = inactiveFormat.value
            }
        }

        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        val width = format.elementFormat().width()
        when (width) {
            is Width.Wrap -> {
                layout.width = LinearLayout.LayoutParams.WRAP_CONTENT
            }
            is Width.Fixed -> {
                layout.widthDp = width.value.toInt()
            }
        }

        layout.backgroundColor  = colorOrBlack(format.elementFormat().backgroundColorTheme(),
                                               entityId)

        layout.corners          = format.elementFormat().corners()

        layout.gravity    = format.elementFormat().verticalAlignment().gravityConstant() or
                                        format.elementFormat().alignment().gravityConstant()

        layout.paddingSpacing   = format.elementFormat().padding()

        return layout.linearLayout(context)
    }


    private fun inlineLeftButtonButtonIconView() : ImageView
    {
        val icon        = ImageViewBuilder()
        val format      = actionWidget.format().buttonFormat()

        icon.widthDp    = format.iconFormat().size().width
        icon.heightDp   = format.iconFormat().size().height


        if (this.isActive)
        {
            val iconType = actionWidget.format().buttonIcon()
            when (iconType) {
                is Just    -> icon.image = iconType.value.drawableResId()
                is Nothing -> icon.image = R.drawable.icon_dice_roll_filled
            }
        }
        else
        {
            icon.image = R.drawable.icon_refresh
        }

        icon.color          = colorOrBlack(format.colorTheme(), entityId)

        return icon.imageView(context)
    }


    private fun inlineLeftButtonButtonTextView() : TextView
    {
        val result        = TextViewBuilder()

        val format = actionWidget.format().buttonFormat()

        result.width          = LinearLayout.LayoutParams.WRAP_CONTENT
        result.height         = LinearLayout.LayoutParams.MATCH_PARENT

        result.gravity        = format.elementFormat().verticalAlignment().gravityConstant() or
                                format.elementFormat().alignment().gravityConstant()

        result.font           = Font.typeface(format.font(),
                                            format.fontStyle(),
                                            context)

        result.sizeSp         = format.sizeSp()

        result.color          = colorOrBlack(format.colorTheme(), entityId)

        result.visibility     = View.GONE

        return result.textView(context)
    }


    private fun inlineLeftButtonDescriptionView() : TextView
    {
        val description         = TextViewBuilder()

        var format = actionWidget.format().descriptionFormat()
        if (!this.isActive) {
            val inactiveFormat = actionWidget.format().descriptionInactiveFormat()
            when (inactiveFormat) {
                is Just -> format = inactiveFormat.value
            }
        }

        description.width       = LinearLayout.LayoutParams.WRAP_CONTENT
        description.height      = LinearLayout.LayoutParams.MATCH_PARENT

        description.gravity    = format.elementFormat().verticalAlignment().gravityConstant() or
                format.elementFormat().alignment().gravityConstant()

        val descriptionString = actionWidget.description()
        when (descriptionString) {
            is Just -> description.text = descriptionString.value.value
        }

        description.font        = Font.typeface(format.font(),
                                                format.fontStyle(),
                                                context)

        description.sizeSp          = format.sizeSp()

        description.color           = colorOrBlack(format.colorTheme(), entityId)
        description.backgroundColor = colorOrBlack(format.elementFormat().backgroundColorTheme(),
                                                   entityId)

        description.corners         = format.elementFormat().corners()

        description.paddingSpacing  = format.elementFormat().padding()

        return description.textView(context)
    }

}
