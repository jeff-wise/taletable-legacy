
package com.kispoko.tome.model.sheet.style


import com.kispoko.tome.R
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue2
import com.kispoko.tome.lib.orm.schema.PrimValue
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
sealed class Icon : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CASES
    // -----------------------------------------------------------------------------------------

    object Sword : Icon()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "sword" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("sword")

    }

    object Shield : Icon()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "shield" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("shield")

    }

    object DiceRoll : Icon()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "dice_roll" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("dice_roll")

    }

    object DiceRollFilled : Icon()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "dice_roll_filled" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("dice_roll_filled")

    }

    object Campfire : Icon()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "campfire" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("campfire")

    }

    object Coins : Icon()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "coins" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("coins")

    }

    object Parchment : Icon()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "parchment" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("parchment")

    }

    object SleepingBag : Icon()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "sleeping_bag" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("sleeping_bag")

    }

    object SwordOutline : Icon()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "sword_outline" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("sword_outline")

    }

    object Adventure : Icon()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "adventure" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("adventure")

    }

    object Mind : Icon()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "mind" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("mind")

    }

    object Running : Icon()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "running" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("running")

    }

    object HeartPlus : Icon()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "heart_plus" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("heart_plus")

    }

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<Icon> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "adventure"         -> effValue<ValueError,Icon>(Icon.Adventure)
                "campfire"          -> effValue<ValueError,Icon>(Icon.Campfire)
                "coins"             -> effValue<ValueError,Icon>(Icon.Coins)
                "dice_roll"         -> effValue<ValueError,Icon>(Icon.DiceRoll)
                "dice_roll_filled"  -> effValue<ValueError,Icon>(Icon.DiceRollFilled)
                "heart_plus"        -> effValue<ValueError,Icon>(Icon.HeartPlus)
                "mind"              -> effValue<ValueError,Icon>(Icon.Mind)
                "parchment"         -> effValue<ValueError,Icon>(Icon.Parchment)
                "running"           -> effValue<ValueError,Icon>(Icon.Running)
                "shield"            -> effValue<ValueError,Icon>(Icon.Shield)
                "sleeping_bag"      -> effValue<ValueError,Icon>(Icon.SleepingBag)
                "sword"             -> effValue<ValueError,Icon>(Icon.Sword)
                "sword_outline"     -> effValue<ValueError,Icon>(Icon.SwordOutline)
                else                -> effError<ValueError,Icon>(
                                            UnexpectedValue("Icon", doc.text, doc.path))
            }
            else            -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    fun drawableResId() : Int = when (this)
    {
        is Adventure        -> R.drawable.icon_adventure
        is Campfire         -> R.drawable.icon_campfire
        is Coins            -> R.drawable.icon_coins
        is DiceRoll         -> R.drawable.icon_dice_roll
        is DiceRollFilled   -> R.drawable.icon_dice_roll_filled
        is HeartPlus        -> R.drawable.icon_heart_plus
        is Mind             -> R.drawable.icon_mind
        is Parchment        -> R.drawable.icon_parchment
        is Running          -> R.drawable.icon_running
        is Shield           -> R.drawable.icon_shield
        is SleepingBag      -> R.drawable.icon_sleeping_bag
        is Sword            -> R.drawable.icon_sword
        is SwordOutline     -> R.drawable.icon_sword_outline
    }

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
                effApply(::IconSize, doc.int("width"), doc.int("width"))
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

