
package com.kispoko.tome.model.engine.message


import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.db.DB_MessageValue
import com.kispoko.tome.db.messageTable
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue3
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.SQLBlob
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.ui.CustomTypefaceSpan
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.model.engine.variable.VariableId
import com.kispoko.tome.model.sheet.style.TextFormat
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.rts.entity.variable
import com.kispoko.tome.util.Util
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable
import java.util.*



/**
 * Message
 */
data class Message(override val id : UUID,
                   val template : MessageTemplate,
                   val parts : List<MessagePart>,
                   val textFormat : TextFormat)
                    : ProdType, ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(template: MessageTemplate,
                parts: List<MessagePart>,
                textFormat: TextFormat)
        : this(UUID.randomUUID(),
               template,
               parts,
               textFormat)


    companion object : Factory<Message>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<Message> = when (doc)
        {
            is DocDict ->
            {
                apply(::Message,
                      // Template String
                      doc.at("template") ap { MessageTemplate.fromDocument(it) },
                      // Parts
                      split(doc.maybeList("parts"),
                            effValue(listOf()),
                            { it.map { MessagePart.fromDocument(it) } }),
                      // Text Format
                      split(doc.maybeAt("text_format"),
                            effValue(TextFormat.default()),
                            { TextFormat.fromDocument(it) })
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun template() : MessageTemplate = this.template


    fun parts() : List<MessagePart> = this.parts


    fun textFormat() : TextFormat = this.textFormat


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "template" to DocText(this.template.value),
        "text_format" to this.textFormat.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // PRODUCT TYPE
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_MessageValue =
        RowValue3(messageTable,
                  PrimValue(this.template),
                  PrimValue(MessageParts(this.parts)),
                  ProdValue(this.textFormat))


    // -----------------------------------------------------------------------------------------
    // TO STRING
    // -----------------------------------------------------------------------------------------

    fun stringFromVariables(entityId : EntityId) : String
    {
        if (this.parts.isEmpty())
            return this.template().value

        val variableStrings = this.parts.mapM { part ->
            variable(VariableId(part.key()), entityId).apply { it.valueString(entityId) }
        }

        return when (variableStrings)
        {
            is Val -> {
                templateString(variableStrings.value)

            }
            is Err -> {
                ApplicationLog.error(variableStrings.error)
                template().value
            }
        }
    }


    fun templateString(variableStrings : List<String>) : String
    {
        val baseString = this.template().value
        val parts = baseString.split("$$$")

        if ((parts.size - 1) != variableStrings.size)
            return baseString

        return when (parts.size)
        {
            0 -> baseString
            1 -> parts[0]
            else ->
            {
                var stringBuilder = parts[0]

                val partsRest = parts.takeLast(parts.size - 1)

                partsRest.forEachIndexed { index, s ->
                    stringBuilder += variableStrings[index]
                    stringBuilder += s
                }

                return stringBuilder
            }
        }
    }


    fun templateSpannable(variableStrings : List<String>,
                          entityId : EntityId,
                          context : Context) : SpannableStringBuilder
    {
        val baseString = this.template().value
        val templateParts = baseString.split("$$$")

        if (templateParts.size != (variableStrings.size + 1))
            return SpannableStringBuilder(baseString)

        if (variableStrings.size != this.parts.size)
            return SpannableStringBuilder(baseString)

        return when (templateParts.size)
        {
            0 -> SpannableStringBuilder(baseString)
            1 -> SpannableStringBuilder(templateParts[0])
            else ->
            {
                val builder = SpannableStringBuilder()
                var currentIndex = 0

                val part1String = templateParts[0]
                builder.append(part1String)
                currentIndex += part1String.length

                this.formatSpans(this.textFormat(), entityId, context).forEach {
                    builder.setSpan(it, 0, currentIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                }

                val partsRest = templateParts.takeLast(templateParts.size - 1)

                partsRest.forEachIndexed { index, s ->
                    val partString = variableStrings[index]
                    builder.append(partString)

                    var nextIndex = currentIndex + partString.length
                    this.formatSpans(this.parts[index].format(), entityId, context).forEach {
                        builder.setSpan(it, currentIndex, nextIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                    }
                    Log.d("***MESSAGE", "part format: ${this.parts[index].format()}")

                    currentIndex = nextIndex

                    val templateString = templateParts[index + 1]

                    nextIndex = currentIndex + templateString.length
                    builder.append(templateString)

                    this.formatSpans(this.textFormat(), entityId, context).forEach {
                        builder.setSpan(it, currentIndex, nextIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                    }

                    currentIndex = nextIndex
                }

                return builder
            }
        }
    }


    private fun formatSpans(textFormat : TextFormat,
                            entityId : EntityId,
                            context : Context) : List<Any>
    {
        val sizePx = Util.spToPx(textFormat.sizeSp(), context)
        val sizeSpan = AbsoluteSizeSpan(sizePx)

        val typeface = Font.typeface(textFormat.font(), textFormat.fontStyle(), context)

        val typefaceSpan = CustomTypefaceSpan(typeface)

        var color = colorOrBlack(textFormat.colorTheme(), entityId)
        val colorSpan = ForegroundColorSpan(color)

        var bgColor = colorOrBlack(textFormat.elementFormat().backgroundColorTheme(), entityId)
        val bgColorSpan = BackgroundColorSpan(bgColor)

        return listOf(sizeSpan, typefaceSpan, colorSpan, bgColorSpan)
    }


}



/**
 * Message Template
 */
data class MessageTemplate(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<MessageTemplate>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<MessageTemplate> = when (doc)
        {
            is DocText -> effValue(MessageTemplate(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = SQLText({this.value})

}


/**
 * Message Parts
 */
data class MessageParts(val parts : List<MessagePart>) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({ SerializationUtils.serialize(this)})

}



/**
 * Message Part
 */
data class MessagePart(val key : MessageKey,
                       val format : TextFormat)
                        : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<MessagePart>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<MessagePart> = when (doc)
        {
            is DocDict ->
            {
                apply(::MessagePart,
                      // Key
                      doc.at("key") ap { MessageKey.fromDocument(it) },
                      // Text Format
                      split(doc.maybeAt("format"),
                            effValue(TextFormat.default()),
                            { TextFormat.fromDocument(it) })
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun key() : String = this.key.value


    fun format() : TextFormat = this.format


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "key" to DocText(this.key.value),
        "format" to this.format.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({ SerializationUtils.serialize(this)})


}


/**
 * Message Key
 */
data class MessageKey(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<MessageKey>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<MessageKey> = when (doc)
        {
            is DocText -> effValue(MessageKey(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = SQLText({this.value})

}


