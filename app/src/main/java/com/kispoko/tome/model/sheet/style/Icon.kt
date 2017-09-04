
package com.kispoko.tome.model.sheet.style


import com.kispoko.tome.R
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.model.theme.ColorTheme
import effect.effApply
import effect.effError
import effect.effValue
import effect.split
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
sealed class Icon : SQLSerializable, Serializable
{

    object Sword : Icon()
    {
        override fun asSQLValue() : SQLValue = SQLText({ "sword" })
    }

    object Shield : Icon()
    {
        override fun asSQLValue() : SQLValue = SQLText({ "shield" })
    }

    object DiceRoll : Icon()
    {
        override fun asSQLValue() : SQLValue = SQLText({ "dice_roll" })
    }

    object DiceRollFilled : Icon()
    {
        override fun asSQLValue() : SQLValue = SQLText({ "dice_roll_filled" })
    }

    object Coins : Icon()
    {
        override fun asSQLValue() : SQLValue = SQLText({ "coins" })
    }

    object Parchment : Icon()
    {
        override fun asSQLValue() : SQLValue = SQLText({ "parchment" })
    }

    object SwordOutline : Icon()
    {
        override fun asSQLValue() : SQLValue = SQLText({ "sword_outline" })
    }

    object Adventure : Icon()
    {
        override fun asSQLValue() : SQLValue = SQLText({ "adventure" })
    }

    object Mind : Icon()
    {
        override fun asSQLValue() : SQLValue = SQLText({ "mindd" })
    }

    object Running : Icon()
    {
        override fun asSQLValue() : SQLValue = SQLText({ "running" })
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
                      val colorTheme : Prim<ColorTheme>,
                      val size : Prim<IconSize>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.colorTheme.name    = "color_theme"
        this.size.name          = "size"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(colorTheme : ColorTheme,
                size : IconSize)
        : this(UUID.randomUUID(),
               Prim(colorTheme),
               Prim(size))


    companion object : Factory<IconFormat>
    {

        private val defaultColorTheme   = ColorTheme.black
        private val defaultIconSize     = IconSize.default()


        override fun fromDocument(doc: SchemaDoc): ValueParser<IconFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply(::IconFormat,
                         // Color Theme
                         split(doc.maybeAt("color_theme"),
                               effValue(defaultColorTheme),
                               { ColorTheme.fromDocument(it) }),
                         // Size
                         split(doc.maybeAt("size"),
                             effValue(defaultIconSize),
                             { IconSize.fromDocument(it) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = IconFormat(defaultColorTheme, defaultIconSize)

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun colorTheme() : ColorTheme = this.colorTheme.value

    fun size() : IconSize = this.size.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "icon_format"

    override val modelObject = this

}


/**
 * Icon Size
 */
data class IconSize(val width : Int, val height : Int) : SQLSerializable, Serializable
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
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({ SerializationUtils.serialize(this) })

}

