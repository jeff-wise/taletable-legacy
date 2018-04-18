
package com.kispoko.tome.model.sheet.style


import com.kispoko.tome.R
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue2
import com.kispoko.tome.lib.orm.RowValue3
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.model.theme.ColorTheme
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable
import java.util.*



/**
 * Icon
 */
sealed class IconType : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CASES
    // -----------------------------------------------------------------------------------------

    object Adventure : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "adventure" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("adventure")

    }

    object Armor : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "armor" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("armor")

    }

    object Campfire : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "campfire" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("campfire")

    }

    object Cancel : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "cancel" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("cancel")

    }

    object CancelBold : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "cancel_bold" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("cancel_bold")

    }

    object CheckBold : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "check_bold" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("check_bold")

    }

    object ChevronDownBold : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "chevron_down_bold" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("chevron_down_bold")

    }

    object ChevronRightBold : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "chevron_right_bold" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("chevron_right_bold")

    }

    object ChevronDown : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "chevron_down" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("chevron_down")

    }

    object ChevronRight : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "chevron_right" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("chevron_right")

    }

    object ChevronUp : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "chevron_up" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("chevron_up")

    }

    object Coins : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "coins" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("coins")

    }

    object DiceRoll : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "dice_roll" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("dice_roll")

    }

    object DiceRollFilled : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "dice_roll_filled" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("dice_roll_filled")

    }

    object Doge : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "doge" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("doge")

    }

    object Heart : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "heart" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("heart")

    }

    object HeartPlus : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "heart_plus" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("heart_plus")

    }

    object InfoBubble : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "info_bubble" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("info_bubble")

    }

    object Mind : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "mind" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("mind")

    }

    object Parchment : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "parchment" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("parchment")

    }

    object Running : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "running" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("running")

    }


    object Shield : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "shield" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("shield")

    }

    object Shirt : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "shirt" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("shirt")

    }

    object Skull : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "skull" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("skull")

    }

    object SkullAndCrossbones : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "skull_and_crossbones" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("skull_and_crossbones")

    }

    object SleepingBag : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "sleeping_bag" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("sleeping_bag")

    }

    object Sword : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "sword" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("sword")

    }

    object SwordOutline : IconType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "sword_outline" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("sword_outline")

    }

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<IconType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "adventure"             -> effValue<ValueError, IconType>(IconType.Adventure)
                "armor"                 -> effValue<ValueError, IconType>(IconType.Armor)
                "campfire"              -> effValue<ValueError, IconType>(IconType.Campfire)
                "cancel"                -> effValue<ValueError, IconType>(IconType.Cancel)
                "cancel_bold"           -> effValue<ValueError, IconType>(IconType.CancelBold)
                "check_bold"            -> effValue<ValueError, IconType>(IconType.CheckBold)
                "chevron_down_bold"     -> effValue<ValueError, IconType>(IconType.ChevronDownBold)
                "chevron_right_bold"    -> effValue<ValueError, IconType>(IconType.ChevronRightBold)
                "chevron_right"         -> effValue<ValueError, IconType>(IconType.ChevronRight)
                "chevron_down"          -> effValue<ValueError, IconType>(IconType.ChevronDown)
                "chevron_up"            -> effValue<ValueError, IconType>(IconType.ChevronUp)
                "coins"                 -> effValue<ValueError, IconType>(IconType.Coins)
                "dice_roll"             -> effValue<ValueError, IconType>(IconType.DiceRoll)
                "dice_roll_filled"      -> effValue<ValueError, IconType>(IconType.DiceRollFilled)
                "doge"                  -> effValue<ValueError, IconType>(IconType.Doge)
                "heart"                 -> effValue<ValueError, IconType>(IconType.Heart)
                "heart_plus"            -> effValue<ValueError, IconType>(IconType.HeartPlus)
                "info_bubble"           -> effValue<ValueError, IconType>(IconType.InfoBubble)
                "mind"                  -> effValue<ValueError, IconType>(IconType.Mind)
                "parchment"             -> effValue<ValueError, IconType>(IconType.Parchment)
                "running"               -> effValue<ValueError, IconType>(IconType.Running)
                "shield"                -> effValue<ValueError, IconType>(IconType.Shield)
                "shirt"                 -> effValue<ValueError, IconType>(IconType.Shirt)
                "sleeping_bag"          -> effValue<ValueError, IconType>(IconType.SleepingBag)
                "skull"                 -> effValue<ValueError, IconType>(IconType.Skull)
                "skull_and_crossbones"  -> effValue<ValueError, IconType>(IconType.SkullAndCrossbones)
                "sword"                 -> effValue<ValueError, IconType>(IconType.Sword)
                "sword_outline"         -> effValue<ValueError, IconType>(IconType.SwordOutline)
                else                    -> effError<ValueError, IconType>(
                                               UnexpectedValue("IconType", doc.text, doc.path))
            }
            else            -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    fun drawableResId() : Int = when (this)
    {
        is Adventure            -> R.drawable.icon_adventure
        is Armor                -> R.drawable.icon_armor
        is Campfire             -> R.drawable.icon_campfire
        is Cancel               -> R.drawable.icon_toolbar_cancel
        is CancelBold           -> R.drawable.icon_cancel_bold
        is CheckBold            -> R.drawable.icon_check_bold
        is ChevronDown          -> R.drawable.icon_chevron_down
        is ChevronUp            -> R.drawable.icon_chevron_up
        is ChevronDownBold      -> R.drawable.icon_chevron_down_bold
        is ChevronRightBold     -> R.drawable.icon_chevron_right_bold
        is ChevronRight         -> R.drawable.icon_chevron_right
        is Coins                -> R.drawable.icon_coins
        is DiceRoll             -> R.drawable.icon_dice_roll
        is DiceRollFilled       -> R.drawable.icon_dice_roll_filled
        is Doge                 -> R.drawable.icon_doge
        is Heart                -> R.drawable.icon_heart
        is HeartPlus            -> R.drawable.icon_heart_plus
        is InfoBubble           -> R.drawable.icon_info_bubble
        is Mind                 -> R.drawable.icon_mind
        is Parchment            -> R.drawable.icon_parchment
        is Running              -> R.drawable.icon_running
        is Shield               -> R.drawable.icon_shield
        is Shirt                -> R.drawable.icon_shirt
        is SleepingBag          -> R.drawable.icon_sleeping_bag
        is Skull                -> R.drawable.icon_skull
        is SkullAndCrossbones   -> R.drawable.icon_skull_and_crossbones
        is Sword                -> R.drawable.icon_sword
        is SwordOutline         -> R.drawable.icon_sword_outline
    }

}


/**
 * Icon
 */
data class Icon(override val id : UUID,
                val iconType : IconType,
                val elementFormat : ElementFormat,
                val iconFormat : IconFormat)
                 : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(iconType : IconType,
                elementFormat : ElementFormat,
                iconFormat : IconFormat)
        : this(UUID.randomUUID(),
               iconType,
               elementFormat,
               iconFormat)


    companion object : Factory<Icon>
    {

        private fun defaultElementFormat()  = ElementFormat.default()
        private fun defaultIconFormat()     = IconFormat.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<Icon> = when (doc)
        {
            is DocDict ->
            {
                apply(::Icon,
                      // Icon Type
                      doc.at("icon_type") ap { IconType.fromDocument(it) },
                      // Element Format
                      split(doc.maybeAt("element_format"),
                            effValue(defaultElementFormat()),
                            { ElementFormat.fromDocument(it) }),
                      // Icon Format
                      split(doc.maybeAt("icon_format"),
                            effValue(defaultIconFormat()),
                            { IconFormat.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default(iconType : IconType) = Icon(iconType,
                                                ElementFormat.default(),
                                                IconFormat.default())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "icon_type" to this.iconType().toDocument(),
        "element_format" to this.elementFormat().toDocument(),
        "icon_format" to this.iconFormat().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun iconType() : IconType = this.iconType


    fun elementFormat() : ElementFormat = this.elementFormat


    fun iconFormat() : IconFormat = this.iconFormat


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_IconValue =
        RowValue3(iconTable,
                  PrimValue(this.iconType),
                  ProdValue(this.elementFormat),
                  ProdValue(this.iconFormat))

}


/**
 * Icon Format
 */
data class IconFormat(override val id : UUID,
                      val colorTheme : ColorTheme,
                      val size : IconSize)
                       : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(colorTheme : ColorTheme,
                size : IconSize)
        : this(UUID.randomUUID(),
               colorTheme,
               size)


    companion object : Factory<IconFormat>
    {

        private fun defaultColorTheme()   = ColorTheme.black
        private fun defaultIconSize()     = IconSize.default()


        override fun fromDocument(doc: SchemaDoc): ValueParser<IconFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::IconFormat,
                      // Color Theme
                      split(doc.maybeAt("color_theme"),
                            effValue(defaultColorTheme()),
                            { ColorTheme.fromDocument(it) }),
                      // Size
                      split(doc.maybeAt("size"),
                          effValue(defaultIconSize()),
                          { IconSize.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = IconFormat(defaultColorTheme(), defaultIconSize())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "color_theme" to this.colorTheme().toDocument(),
        "size" to this.size().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun colorTheme() : ColorTheme = this.colorTheme

    fun size() : IconSize = this.size


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_IconFormatValue =
        RowValue2(iconFormatTable, PrimValue(this.colorTheme),
                                   PrimValue(this.size))

}


/**
 * Icon Size
 */
data class IconSize(val width : Int,
                    val height : Int) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<IconSize>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<IconSize> = when (doc)
        {
            is DocDict ->
            {
                effApply(::IconSize, doc.int("width"), doc.int("height"))
            }
            else       -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        fun default() = IconSize(20, 20)
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "width" to DocNumber(this.width.toDouble()),
        "height" to DocNumber(this.height.toDouble())
    ))


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({ SerializationUtils.serialize(this) })

}

