
package com.kispoko.tome.model.sheet.style


import com.kispoko.tome.R
import com.kispoko.tome.db.DB_IconFormat
import com.kispoko.tome.db.dbIconFormat
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Val
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.ProdType
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

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<Icon> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "sword"             -> effValue<ValueError,Icon>(Icon.Sword)
                "shield"            -> effValue<ValueError,Icon>(Icon.Shield)
                "dice_roll"         -> effValue<ValueError,Icon>(Icon.DiceRoll)
                "dice_roll_filled"  -> effValue<ValueError,Icon>(Icon.DiceRollFilled)
                "coins"             -> effValue<ValueError,Icon>(Icon.Coins)
                "parchment"         -> effValue<ValueError,Icon>(Icon.Parchment)
                "sword_outline"     -> effValue<ValueError,Icon>(Icon.SwordOutline)
                "adventure"         -> effValue<ValueError,Icon>(Icon.Adventure)
                "mind"              -> effValue<ValueError,Icon>(Icon.Mind)
                "running"           -> effValue<ValueError,Icon>(Icon.Running)
                else                -> effError<ValueError,Icon>(
                                            UnexpectedValue("Icon", doc.text, doc.path))
            }
            else            -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    fun drawableResId() : Int = when (this)
    {
        is Shield           -> R.drawable.icon_shield
        is Sword            -> R.drawable.icon_sword
        is DiceRoll         -> R.drawable.icon_dice_roll
        is DiceRollFilled   -> R.drawable.icon_dice_roll_filled
        is Coins            -> R.drawable.icon_coins
        is Parchment        -> R.drawable.icon_parchment
        is SwordOutline     -> R.drawable.icon_sword_outline
        is Adventure        -> R.drawable.icon_adventure
        is Mind             -> R.drawable.icon_mind
        is Running          -> R.drawable.icon_running
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


    override fun row() : DB_IconFormat = dbIconFormat(this.colorTheme, this.size)

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

