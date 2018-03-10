
package com.kispoko.tome.model.sheet.widget


import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.PaintDrawable
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.entity.engine.dice.DiceRollerActivity
import com.kispoko.tome.db.DB_WidgetRollFormatValue
import com.kispoko.tome.db.widgetRollFormatTable
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue6
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.ImageViewBuilder
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.Height
import com.kispoko.tome.model.sheet.style.TextFormat
import com.kispoko.tome.model.sheet.style.Width
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.util.Util
import effect.*
import maybe.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Roll Widget Format
 */
data class RollWidgetFormat(override val id : UUID,
                            val widgetFormat : WidgetFormat,
                            val viewType : RollWidgetViewType,
                            val descriptionFormat : TextFormat,
                            val descriptionRollFormat : Maybe<TextFormat>,
                            val buttonFormat : TextFormat,
                            val buttonRollFormat : Maybe<TextFormat>,
                            val rollTextLocation : RollTextLocation,
                            val rollTextFormat : TextFormat)
                             : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                viewType : RollWidgetViewType,
                descriptionFormat : TextFormat,
                descriptionRollFormat : Maybe<TextFormat>,
                buttonFormat : TextFormat,
                buttonRollFormat : Maybe<TextFormat>,
                rollTextLocation : RollTextLocation,
                rollTextFormat : TextFormat)
        : this(UUID.randomUUID(),
               widgetFormat,
               viewType,
               descriptionFormat,
               descriptionRollFormat,
               buttonFormat,
               buttonRollFormat,
               rollTextLocation,
               rollTextFormat)


    companion object : Factory<RollWidgetFormat>
    {

        private fun defaultWidgetFormat()       = WidgetFormat.default()
        private fun defaultViewType()           = RollWidgetViewType.InlineLeftButton
        private fun defaultDescriptionFormat()  = TextFormat.default()
        private fun defaultButtonFormat()       = TextFormat.default()
        private fun defaultRollTextLocation()   = RollTextLocation.None
        private fun defaultRollTextFormat()     = TextFormat.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<RollWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::RollWidgetFormat,
                     // Widget Format
                     split(doc.maybeAt("widget_format"),
                           effValue(defaultWidgetFormat()),
                           { WidgetFormat.fromDocument(it) }),
                     // View Type
                     split(doc.maybeAt("view_type"),
                           effValue<ValueError,RollWidgetViewType>(defaultViewType()),
                           { RollWidgetViewType.fromDocument(it) }),
                     // Description Format
                     split(doc.maybeAt("description_format"),
                           effValue(defaultDescriptionFormat()),
                           { TextFormat.fromDocument(it) }),
                     // Description Roll Format
                     split(doc.maybeAt("description_roll_format"),
                           effValue<ValueError,Maybe<TextFormat>>(Nothing()),
                           { apply(::Just, TextFormat.fromDocument(it)) }),
                     // Button Format
                     split(doc.maybeAt("button_format"),
                           effValue(defaultButtonFormat()),
                           { TextFormat.fromDocument(it) }),
                     // Button Roll Format
                     split(doc.maybeAt("button_roll_format"),
                           effValue<ValueError,Maybe<TextFormat>>(Nothing()),
                           { apply(::Just, TextFormat.fromDocument(it)) }),
                     // Roll Text Location
                     split(doc.maybeAt("roll_text_location"),
                           effValue<ValueError,RollTextLocation>(defaultRollTextLocation()),
                           { RollTextLocation.fromDocument(it) }),
                     // Roll Text Format
                     split(doc.maybeAt("roll_text_format"),
                           effValue(defaultRollTextFormat()),
                           { TextFormat.fromDocument(it) })
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = RollWidgetFormat(defaultWidgetFormat(),
                                         defaultViewType(),
                                         defaultDescriptionFormat(),
                                         Nothing(),
                                         defaultButtonFormat(),
                                         Nothing(),
                                         defaultRollTextLocation(),
                                         defaultRollTextFormat())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat().toDocument(),
        "view_type" to this.viewType().toDocument(),
        "description_format" to this.descriptionFormat().toDocument(),
        "button_format" to this.buttonFormat().toDocument(),
        "roll_text_location" to this.buttonFormat().toDocument(),
        "roll_text_format" to this.buttonFormat().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat


    fun viewType() : RollWidgetViewType = this.viewType


    fun descriptionFormat() : TextFormat = this.descriptionFormat


    fun descriptionRollFormat() : Maybe<TextFormat> = this.descriptionRollFormat


    fun buttonFormat() : TextFormat = this.buttonFormat


    fun buttonRollFormat() : Maybe<TextFormat> = this.buttonRollFormat


    fun rollTextLocation() : RollTextLocation = this.rollTextLocation


    fun rollTextFormat() : TextFormat = this.rollTextFormat


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetRollFormatValue =
        RowValue6(widgetRollFormatTable,
                  ProdValue(this.widgetFormat),
                  PrimValue(this.viewType),
                  ProdValue(this.descriptionFormat),
                  ProdValue(this.buttonFormat),
                  PrimValue(this.rollTextLocation),
                  ProdValue(this.rollTextFormat))

}


/**
 * Roll Widget View Type
 */
sealed class RollWidgetViewType : ToDocument, SQLSerializable, Serializable
{

    object InlineLeftButton : RollWidgetViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "inline_left_button" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("inline_left_button")

    }


    object InlineRightButton : RollWidgetViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "inline_right_button" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("inline_right_button")

    }


    object InlineLeftButtonUseDialog : RollWidgetViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "inline_left_button_use_dialog" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("inline_left_button_use_dialog")

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<RollWidgetViewType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "inline_left_button"            -> effValue<ValueError,RollWidgetViewType>(
                                                       RollWidgetViewType.InlineLeftButton)
                "inline_right_button"           -> effValue<ValueError,RollWidgetViewType>(
                                                    RollWidgetViewType.InlineRightButton)
                "inline_left_button_use_dialog" -> effValue<ValueError,RollWidgetViewType>(
                                                       RollWidgetViewType.InlineLeftButtonUseDialog)
                else                 -> effError<ValueError,RollWidgetViewType>(
                                            UnexpectedValue("RollWidgetViewType", doc.text, doc.path))
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}


/**
 * Roll Widget Description
 */
data class RollWidgetDescription(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RollWidgetDescription>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<RollWidgetDescription> = when (doc)
        {
            is DocText -> effValue(RollWidgetDescription(doc.text))
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


/**
 * Roll Widget Result Description
 */
data class RollWidgetResultDescription(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RollWidgetResultDescription>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<RollWidgetResultDescription> = when (doc)
        {
            is DocText -> effValue(RollWidgetResultDescription(doc.text))
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


/**
 * Roll Text Location
 */
sealed class RollTextLocation : ToDocument, SQLSerializable, Serializable
{

    object None : RollTextLocation()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "none" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("none")
    }


    object Button : RollTextLocation()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "button" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("button")
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<RollTextLocation> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "none"   -> effValue<ValueError,RollTextLocation>(RollTextLocation.None)
                "button" -> effValue<ValueError,RollTextLocation>(RollTextLocation.Button)
                else     -> effError<ValueError,RollTextLocation>(
                                UnexpectedValue("RollTextLocation", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}


class RollWidgetViewBuilder(val rollWidget : RollWidget,
                            val entityId : EntityId,
                            val context : Context)
{

    // STATE
    // -----------------------------------------------------------------------------------------

    var isRoll : Boolean = false

    val diceRolls = rollWidget.rollGroup().diceRolls(entityId)

    var buttonLayout : LinearLayout? = null
    var buttonIconView : ImageView? = null
    var buttonResultTextView : TextView? = null
    var buttonRollTextView : TextView? = null

    var descriptionTextView : TextView? = null


    private fun updateButtonView()
    {
        val bgDrawable = PaintDrawable()

        // NORMAL VIEW
        if (isRoll)
        {
            this.isRoll = false

            val format = rollWidget.format().buttonFormat()

            buttonResultTextView?.visibility = View.GONE
            buttonIconView?.visibility = View.VISIBLE
            buttonRollTextView?.visibility = View.VISIBLE


            val bgColor = colorOrBlack(format.elementFormat().backgroundColorTheme(), entityId)
            bgDrawable.colorFilter = PorterDuffColorFilter(bgColor, PorterDuff.Mode.SRC_IN)

            bgDrawable.setCornerRadii(format.elementFormat().corners().radiiArray())

            buttonRollTextView?.setTextColor(colorOrBlack(format.colorTheme(), entityId))

            buttonRollTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, format.sizeSp())

            val padding = format.elementFormat().padding()
            buttonLayout?.setPadding(padding.leftPx(), padding.topPx(), padding.rightPx(), padding.bottomPx())

        }
        // ROLL VIEW
        else
        {
            this.isRoll = true

            val buttonRollFormat = rollWidget.format().buttonRollFormat()
            val format = when (buttonRollFormat) {
                    is Just -> buttonRollFormat.value
                    else    -> rollWidget.format().buttonFormat()
                }

            // TODO do example
//            val diceRoll = GameManager.engine(sheetUIContext.gameId)
//                             .apply { it.summation(rollWidget.rollSummationId()) }
//                             .apply { it.diceRoll(SheetContext(sheetUIContext)) }


            if (this.diceRolls.isNotEmpty())
            {
                val diceRoll = this.diceRolls[0]

                buttonIconView?.visibility = View.GONE
                buttonRollTextView?.visibility = View.GONE
                buttonResultTextView?.visibility = View.VISIBLE
                buttonResultTextView?.text = diceRoll.roll().toString()
            }

            val bgColor = colorOrBlack(format.elementFormat().backgroundColorTheme(), entityId)
            bgDrawable.colorFilter = PorterDuffColorFilter(bgColor, PorterDuff.Mode.SRC_IN)

            bgDrawable.setCornerRadii(format.elementFormat().corners().radiiArray())

            buttonResultTextView?.setTextColor(colorOrBlack(format.colorTheme(), entityId))

            buttonResultTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, format.sizeSp())

            val padding = format.elementFormat().padding()
            buttonLayout?.setPadding(padding.leftPx(), padding.topPx(), padding.rightPx(), padding.bottomPx())

        }

        buttonLayout?.background = bgDrawable

    }


    private fun updateDescriptionView()
    {
        val bgDrawable = PaintDrawable()

        val format = if (this.isRoll) {
            val descriptionRolllFormat = rollWidget.format().descriptionRollFormat()
            when (descriptionRolllFormat) {
                is Just -> descriptionRolllFormat.value
                else    -> rollWidget.format().descriptionFormat()
            }
        }
        else {
            rollWidget.format().descriptionFormat()
        }

        val bgColor = colorOrBlack(format.elementFormat().backgroundColorTheme(), entityId)
        bgDrawable.colorFilter = PorterDuffColorFilter(bgColor, PorterDuff.Mode.SRC_IN)

        bgDrawable.setCornerRadii(format.elementFormat().corners().radiiArray())

        descriptionTextView?.setTextColor(colorOrBlack(format.colorTheme(), entityId))

        descriptionTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, format.sizeSp())

        descriptionTextView?.background = bgDrawable

        val rollDescription = rollWidget.resultDescription()
        if (this.isRoll)
        {
            when (rollDescription) {
                is Just -> {
                    descriptionTextView?.text = rollDescription.value.value
                }
            }
        }
        else
        {
            val description = rollWidget.description()
            when (description) {
                is Just -> descriptionTextView?.text = description.value.value
            }

        }
    }


    private fun update()
    {
        this.updateButtonView()
        this.updateDescriptionView()
    }


    private fun openDiceRoller()
    {
        val activity = context as AppCompatActivity
        val intent = Intent(activity, DiceRollerActivity::class.java)
        intent.putExtra("dice_roll_group", rollWidget.rollGroup())
        intent.putExtra("auto_rolls", 1)
        intent.putExtra("entity_id", entityId)
        activity.startActivity(intent)
    }


//    private fun diceRoll() : AppEff<DiceRoll> =
//        GameManager.engine(sheetUIContext.gameId)
//            .apply { it.summation(rollWidget.rollSummationId()) }
//            .apply { it.diceRoll(SheetContext(sheetUIContext)) }


    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = WidgetView.layout(rollWidget.widgetFormat(), entityId, context)

        val viewId = Util.generateViewId()
        layout.id = viewId
        rollWidget.layoutId = viewId

        updateContentView(layout)

        return layout
    }


    fun updateContentView(layout : LinearLayout)
    {
        val contentLayout = layout.findViewById(R.id.widget_content_layout) as LinearLayout

        contentLayout.removeAllViews()

        when (rollWidget.format().viewType())
        {
            is RollWidgetViewType.InlineLeftButton ->
            {
                contentLayout.addView(this.inlineLeftButtonView())

//                layout.setOnClickListener {
//                    this.updateButtonView()
//                    this.updateDescriptionView()
//                }
            }
            is RollWidgetViewType.InlineRightButton ->
            {
                contentLayout.addView(this.inlineRightButtonView())

//                layout.setOnClickListener {
//                    this.updateButtonView()
//                    this.updateDescriptionView()
//                }
            }
            is RollWidgetViewType.InlineLeftButtonUseDialog ->
            {
//                layout.setOnClickListener {
//                    val activity = context as SheetActivity
//                    val dialog = DiceRollDialog.newInstance(rollWidget.rollGroup(),
//                                                            entityId,
//                                                            1)
//                    dialog.show(activity.supportFragmentManager, "")
//                }

                contentLayout.addView(this.inlineLeftButtonView())
            }
        }
    }


    private fun inlineLeftButtonView() : LinearLayout
    {
        val layout = this.inlineLeftButtonViewLayout()

        // Button
        val buttonLayout = this.inlineLeftButtonButtonViewLayout()
        this.buttonLayout = buttonLayout

        buttonLayout.setOnClickListener {
            this.openDiceRoller()
        }

        // Button > Icon
        val buttonIconView = this.inlineLeftButtonButtonIconView()
        buttonLayout.addView(buttonIconView)
        this.buttonIconView = buttonIconView

        // Button > Result Text
        val buttonResultTextView = this.inlineLeftButtonButtonResultTextView()
        buttonLayout.addView(buttonResultTextView)
        this.buttonResultTextView = buttonResultTextView

        // Button > Roll Text
        when (rollWidget.format().rollTextLocation()) {
            is RollTextLocation.Button -> {
                val buttonRollTextView = this.inlineLeftButtonButtonRollTextView()
                buttonLayout.addView(buttonRollTextView)
                this.buttonRollTextView = buttonRollTextView
            }
            else -> {
                Log.d("***ROLL WIDGET", "no location")
            }
        }

        layout.addView(buttonLayout)

        val descriptionView = this.inlineLeftButtonDescriptionView()
        descriptionView.setOnClickListener {
            this.updateButtonView()
            this.updateDescriptionView()
        }
        this.descriptionTextView = descriptionView
        layout.addView(descriptionView)

        return layout
    }





    private fun inlineLeftButtonViewLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        val width = rollWidget.widgetFormat().elementFormat().width()
        when (width) {
            is Width.Wrap -> {
                layout.width = LinearLayout.LayoutParams.WRAP_CONTENT
            }
            is Width.Fixed -> {
                layout.widthDp = width.value.toInt()
            }
        }

        val height = rollWidget.widgetFormat().elementFormat().height()
        when (height) {
            is Height.Wrap -> {
                layout.height = LinearLayout.LayoutParams.WRAP_CONTENT
            }
            is Height.Fixed -> {
                layout.heightDp = height.value.toInt()
            }
        }

        layout.orientation  = LinearLayout.HORIZONTAL

        return layout.linearLayout(context)
    }


    private fun inlineLeftButtonButtonViewLayout() : LinearLayout
    {

        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout      = LinearLayoutBuilder()

        val format = if (this.isRoll) {
            val buttonRollFormat = rollWidget.format().buttonRollFormat()
            when (buttonRollFormat) {
                is Just -> buttonRollFormat.value
                else    -> rollWidget.format().buttonFormat()
            }
        }
        else {
            rollWidget.format().buttonFormat()
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
        val icon            = ImageViewBuilder()
        val format          = rollWidget.format().buttonFormat()

        icon.iconSize       = format.iconFormat().size()

        icon.image          = R.drawable.icon_dice_roll_filled

        icon.color          = colorOrBlack(format.iconFormat().colorTheme(), entityId)

        return icon.imageView(context)
    }


    private fun inlineLeftButtonButtonResultTextView() : TextView
    {
        val result        = TextViewBuilder()

        val format = if (this.isRoll) {
            val buttonRollFormat = rollWidget.format().buttonRollFormat()
            when (buttonRollFormat) {
                is Just -> buttonRollFormat.value
                else    -> rollWidget.format().buttonFormat()
            }
        }
        else {
            rollWidget.format().buttonFormat()
        }

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


    private fun inlineLeftButtonButtonRollTextView() : TextView
    {
        val roll        = TextViewBuilder()
        val format      = rollWidget.format().rollTextFormat()

        if (this.diceRolls.isNotEmpty())
        {
            val diceRoll = this.diceRolls[0]
            roll.text = format.rollFormat().rollString(diceRoll)
        }

        roll.width          = LinearLayout.LayoutParams.WRAP_CONTENT
        roll.height         = LinearLayout.LayoutParams.MATCH_PARENT

        roll.gravity        = format.elementFormat().verticalAlignment().gravityConstant() or
                                format.elementFormat().alignment().gravityConstant()

        roll.font           = Font.typeface(format.font(),
                                            format.fontStyle(),
                                            context)

        roll.sizeSp         = format.sizeSp()

        roll.color          = colorOrBlack(format.colorTheme(), entityId)

        roll.paddingSpacing = format.elementFormat().padding()
        roll.marginSpacing  = format.elementFormat().margins()

        return roll.textView(context)
    }


    private fun inlineLeftButtonDescriptionView() : TextView
    {
        val description         = TextViewBuilder()

        val format = if (this.isRoll) {
            val descriptionRolllFormat = rollWidget.format().descriptionRollFormat()
            when (descriptionRolllFormat) {
                is Just -> descriptionRolllFormat.value
                else    -> rollWidget.format().descriptionFormat()
            }
        }
        else {
            rollWidget.format().descriptionFormat()
        }

        description.width           = LinearLayout.LayoutParams.WRAP_CONTENT
        description.height          = LinearLayout.LayoutParams.MATCH_PARENT

        description.gravity         = format.elementFormat().verticalAlignment().gravityConstant() or
                format.elementFormat().alignment().gravityConstant()

        val descriptionString = rollWidget.description()
        when (descriptionString) {
            is Just -> description.text = descriptionString.value.value
        }

        description.font            = Font.typeface(format.font(),
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


    private fun inlineRightButtonView() : LinearLayout
    {
        val layout = this.inlineLeftButtonViewLayout()

        val descriptionView = this.inlineLeftButtonDescriptionView()
        descriptionView.setOnClickListener {

            this.update()
        }
        this.descriptionTextView = descriptionView
        layout.addView(descriptionView)

        // Button
        val buttonLayout = this.inlineLeftButtonButtonViewLayout()
        this.buttonLayout = buttonLayout

        buttonLayout.setOnClickListener {
            this.openDiceRoller()
        }

        // Button > Icon
        val buttonIconView = this.inlineLeftButtonButtonIconView()
        buttonLayout.addView(buttonIconView)
        this.buttonIconView = buttonIconView

        // Button > Result Text
        val buttonResultTextView = this.inlineLeftButtonButtonResultTextView()
        buttonLayout.addView(buttonResultTextView)
        this.buttonResultTextView = buttonResultTextView

        // Button > Roll Text
        when (rollWidget.format().rollTextLocation()) {
            is RollTextLocation.Button -> {
                val buttonRollTextView = this.inlineLeftButtonButtonRollTextView()
                buttonLayout.addView(buttonRollTextView)
                this.buttonRollTextView = buttonRollTextView
            }
            else -> {
                Log.d("***ROLL WIDGET", "no location")
            }
        }

        layout.addView(buttonLayout)

        return layout
    }


}
