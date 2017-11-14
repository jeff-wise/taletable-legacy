
package com.kispoko.tome.model.sheet.widget


import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.model.ProdType
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.rts.sheet.SheetUIContext
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Log Entry
 */
data class LogEntry(override val id : UUID,
                    private val title : EntryTitle,
                    private val date : EntryDate,
                    private val author : EntryAuthor,
                    private val summary : Maybe<EntrySummary>,
                    private val text : EntryText) : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(title : EntryTitle,
                date : EntryDate,
                author : EntryAuthor,
                summary : Maybe<EntrySummary>,
                text : EntryText)
        : this(UUID.randomUUID(),
               title,
               date,
               author,
               summary,
               text)


    companion object : Factory<LogEntry>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<LogEntry> = when (doc)
        {
            is DocDict ->
            {
                apply(::LogEntry,
                      // Title
                      doc.at("title") ap { EntryTitle.fromDocument(it) },
                      // Date
                      doc.at("date") ap { EntryDate.fromDocument(it) },
                      // Author
                      doc.at("author") ap { EntryAuthor.fromDocument(it) },
                      // Summary
                      split(doc.maybeAt("summary"),
                            effValue<ValueError,Maybe<EntrySummary>>(Nothing()),
                            { apply(::Just, EntrySummary.fromDocument(it)) }),
                      // text
                      doc.at("text") ap { EntryText.fromDocument(it) }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "title" to this.title().toDocument(),
        "date" to this.date().toDocument(),
        "author" to this.author().toDocument(),
        "text" to this.text().toDocument()
    ))
    .maybeMerge(this.summary().apply {
        Just(Pair("summary", it.toDocument() as SchemaDoc)) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun title() : EntryTitle = this.title

    fun date() : EntryDate = this.date

    fun author() : EntryAuthor = this.author

    fun summary() : Maybe<EntrySummary> = this.summary

    fun text() : EntryText = this.text


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun row() : DB_WidgetLogEntry = dbWidgetLogEntry(this.title,
                                                              this.date,
                                                              this.author,
                                                              this.summary,
                                                              this.text)

}


/**
 * Entry Title
 */
data class EntryTitle(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<EntryTitle>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<EntryTitle> = when (doc)
        {
            is DocText -> effValue(EntryTitle(doc.text))
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

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Entry Date
 */
data class EntryDate(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<EntryDate>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<EntryDate> = when (doc)
        {
            is DocText -> effValue(EntryDate(doc.text))
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

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Entry Author
 */
data class EntryAuthor(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<EntryAuthor>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<EntryAuthor> = when (doc)
        {
            is DocText -> effValue(EntryAuthor(doc.text))
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

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Entry Summary
 */
data class EntrySummary(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<EntrySummary>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<EntrySummary> = when (doc)
        {
            is DocText -> effValue(EntrySummary(doc.text))
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

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Entry Text
 */
data class EntryText(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<EntryText>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<EntryText> = when (doc)
        {
            is DocText -> effValue(EntryText(doc.text))
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

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Entry View Type
 */
sealed class EntryViewType : ToDocument, SQLSerializable, Serializable
{

    object Vertical : EntryViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"vertical"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("vertical")

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<EntryViewType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "vertical"  -> effValue<ValueError,EntryViewType>(EntryViewType.Vertical)
                else        -> effError<ValueError,EntryViewType>(
                                            UnexpectedValue("EntryViewType", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}


/**
 * Log Widget Format
 */
data class LogWidgetFormat(override val id : UUID,
                           val widgetFormat : WidgetFormat,
                           val entryFormat : LogEntryFormat,
                           val entryViewType : EntryViewType)
                            : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                entryFormat : LogEntryFormat,
                entryViewType : EntryViewType)
        : this(UUID.randomUUID(),
               widgetFormat,
               entryFormat,
               entryViewType)


    companion object : Factory<LogWidgetFormat>
    {

        private fun defaultWidgetFormat()  = WidgetFormat.default()
        private fun defaultEntryFormat()   = LogEntryFormat.default()
        private fun defaultEntryViewType() = EntryViewType.Vertical


        override fun fromDocument(doc : SchemaDoc) : ValueParser<LogWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::LogWidgetFormat,
                      // Widget Format
                      split(doc.maybeAt("widget_format"),
                            effValue(defaultWidgetFormat()),
                            { WidgetFormat.fromDocument(it) }),
                      // Entry Format
                      split(doc.maybeAt("entry_format"),
                            effValue(defaultEntryFormat()),
                            { LogEntryFormat.fromDocument(it) }),
                      // View Type
                      split(doc.maybeAt("entry_view_type"),
                            effValue<ValueError,EntryViewType>(defaultEntryViewType()),
                            { EntryViewType.fromDocument(it) })
                )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = LogWidgetFormat(defaultWidgetFormat(),
                                        defaultEntryFormat(),
                                        defaultEntryViewType())
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat().toDocument(),
        "entry_format" to this.entryFormat().toDocument(),
        "entry_view_type" to this.entryViewType().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat

    fun entryFormat() : LogEntryFormat = this.entryFormat

    fun entryViewType() : EntryViewType = this.entryViewType


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun row() : DB_WidgetLogFormat =
            dbWidgetLogFormat(this.widgetFormat, this.entryFormat, this.entryViewType)

}



/**
 * Log Entry Format
 */
data class LogEntryFormat(override val id : UUID,
                          private val titleFormat : ElementFormat,
                          private val titleStyle : TextFormat,
                          private val authorFormat : ElementFormat,
                          private val authorStyle : TextFormat,
                          private val summaryFormat : ElementFormat,
                          private val summaryStyle : TextFormat,
                          private val bodyFormat : ElementFormat,
                          private val bodyStyle : TextFormat,
                          private val entryFormat : ElementFormat)
                           : ToDocument, ProdType, Serializable
{


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(titleFormat : ElementFormat,
                titleStyle : TextFormat,
                authorFormat : ElementFormat,
                authorStyle : TextFormat,
                summaryFormat : ElementFormat,
                summaryStyle : TextFormat,
                bodyFormat : ElementFormat,
                bodyStyle : TextFormat,
                entryFormat : ElementFormat)
        : this(UUID.randomUUID(),
               titleFormat,
               titleStyle,
               authorFormat,
               authorStyle,
               summaryFormat,
               summaryStyle,
               bodyFormat,
               bodyStyle,
               entryFormat)


    companion object : Factory<LogEntryFormat>
    {

        private fun defaultTitleFormat()   = ElementFormat.default()
        private fun defaultTitleStyle()    = TextFormat.default()
        private fun defaultAuthorFormat()  = ElementFormat.default()
        private fun defaultAuthorStyle()   = TextFormat.default()
        private fun defaultSummaryFormat() = ElementFormat.default()
        private fun defaultSummaryStyle()  = TextFormat.default()
        private fun defaultBodyFormat()    = ElementFormat.default()
        private fun defaultBodyStyle()     = TextFormat.default()
        private fun defaultEntryFormat()   = ElementFormat.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<LogEntryFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::LogEntryFormat,
                      // Title Format
                      split(doc.maybeAt("title_format"),
                            effValue(defaultTitleFormat()),
                            { ElementFormat.fromDocument(it) }),
                      // Title Style
                      split(doc.maybeAt("title_style"),
                            effValue(defaultTitleStyle()),
                            { TextFormat.fromDocument(it) }),
                      // Author Format
                      split(doc.maybeAt("author_format"),
                            effValue(defaultAuthorFormat()),
                            { ElementFormat.fromDocument(it) }),
                      // Author Style
                      split(doc.maybeAt("author_style"),
                            effValue(defaultAuthorStyle()),
                            { TextFormat.fromDocument(it) }),
                      // Summary Format
                      split(doc.maybeAt("summary_format"),
                            effValue(defaultSummaryFormat()),
                            { ElementFormat.fromDocument(it) }),
                      // Summary Style
                      split(doc.maybeAt("summary_style"),
                            effValue(defaultSummaryStyle()),
                            { TextFormat.fromDocument(it) }),
                      // Body Format
                      split(doc.maybeAt("body_format"),
                            effValue(defaultBodyFormat()),
                            { ElementFormat.fromDocument(it) }),
                      // Body Style
                      split(doc.maybeAt("body_style"),
                            effValue(defaultBodyStyle()),
                            { TextFormat.fromDocument(it) }),
                      // Entry Format
                      split(doc.maybeAt("entry_format"),
                            effValue(defaultEntryFormat()),
                            { ElementFormat.fromDocument(it) })
                    )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = LogEntryFormat(defaultTitleFormat(),
                                       defaultTitleStyle(),
                                       defaultAuthorFormat(),
                                       defaultAuthorStyle(),
                                       defaultSummaryFormat(),
                                       defaultSummaryStyle(),
                                       defaultBodyFormat(),
                                       defaultBodyStyle(),
                                       defaultEntryFormat())
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "title_format" to this.titleFormat().toDocument(),
        "title_style" to this.titleStyle().toDocument(),
        "author_format" to this.titleFormat().toDocument(),
        "author_style" to this.titleStyle().toDocument(),
        "summary_format" to this.titleFormat().toDocument(),
        "summary_style" to this.titleStyle().toDocument(),
        "body_format" to this.titleFormat().toDocument(),
        "body_style" to this.titleStyle().toDocument(),
        "entry_format" to this.titleFormat().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun titleFormat() : ElementFormat = this.titleFormat

    fun titleStyle() : TextFormat = this.titleStyle

    fun authorFormat() : ElementFormat = this.authorFormat

    fun authorStyle() : TextFormat = this.authorStyle

    fun summaryFormat() : ElementFormat = this.summaryFormat

    fun summaryStyle() : TextFormat = this.summaryStyle

    fun bodyFormat() : ElementFormat = this.bodyFormat

    fun bodyStyle() : TextFormat = this.bodyStyle

    fun entryFormat() : ElementFormat = this.entryFormat


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun row() : DB_WidgetLogEntryFormat =
            dbWidgetLogEntryFormat(this.titleFormat,
                                   this.titleStyle,
                                   this.authorFormat,
                                   this.authorStyle,
                                   this.summaryFormat,
                                   this.summaryStyle,
                                   this.bodyFormat,
                                   this.bodyStyle,
                                   this.entryFormat)

}


class LogViewBuilder(val logWidget : LogWidget,
                     val sheetUIContext : SheetUIContext)
{


    fun view() : View
    {
        val layout = WidgetView.layout(logWidget.widgetFormat(), sheetUIContext)

        layout.addView(entriesView())

        return layout
    }


    private fun entriesView() : LinearLayout
    {
        val layout = this.entriesViewLayout()

        logWidget.entries().forEach {
            layout.addView(this.entryView(it))
        }

        return layout
    }


    private fun entriesViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        return layout.linearLayout(sheetUIContext.context)
    }



    // ENTRY VIEW
    // -----------------------------------------------------------------------------------------

    private fun entryView(entry : LogEntry) : LinearLayout
    {
        val layout = this.entryViewLayout()

        val entryFormat = logWidget.format().entryFormat()

        // Title
        layout.addView(titleView(entry.title(),
                                 entryFormat.titleFormat(),
                                 entryFormat.titleStyle()))

        // Author
        layout.addView(authorView(entry.author(),
                                  entryFormat.authorStyle()))

        // Summary
        val maybeSummary = entry.summary()
        when (maybeSummary) {
            is Just -> layout.addView(summaryView(maybeSummary.value,
                                                  entryFormat.summaryFormat(),
                                                  entryFormat.summaryStyle()))
        }

        return layout
    }


    private fun entryViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()
        val format                  = logWidget.format().entryFormat().entryFormat()

        layout.orientation          = LinearLayout.VERTICAL

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId,
                                                         format.backgroundColorTheme())

        layout.marginSpacing        = format.margins()
        layout.paddingSpacing       = format.padding()

        layout.corners              = format.corners()

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun titleView(entryTitle : EntryTitle,
                          format : ElementFormat,
                          style : TextFormat) : TextView
    {
        val title               = TextViewBuilder()

        title.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        title.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        title.text              = entryTitle.value

        title.color             = SheetManager.color(sheetUIContext.sheetId, style.colorTheme())

        title.font              = Font.typeface(style.font(),
                                                style.fontStyle(),
                                                sheetUIContext.context)

        title.sizeSp            = style.sizeSp()

        return title.textView(sheetUIContext.context)
    }


    private fun authorView(entryAuthor : EntryAuthor,
                           style : TextFormat) : TextView
    {
        val author               = TextViewBuilder()

        author.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        author.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        author.text              = entryAuthor.value

        author.color             = SheetManager.color(sheetUIContext.sheetId, style.colorTheme())

        author.font              = Font.typeface(style.font(),
                                                style.fontStyle(),
                                                sheetUIContext.context)

        author.sizeSp            = style.sizeSp()

        return author.textView(sheetUIContext.context)
    }


    private fun summaryView(entrySummary : EntrySummary,
                            format : ElementFormat,
                            style : TextFormat) : TextView
    {
        val summary             = TextViewBuilder()

        summary.width           = LinearLayout.LayoutParams.WRAP_CONTENT
        summary.height          = LinearLayout.LayoutParams.WRAP_CONTENT

        summary.text            = entrySummary.value

        summary.color           = SheetManager.color(sheetUIContext.sheetId, style.colorTheme())

        summary.font            = Font.typeface(style.font(),
                                                style.fontStyle(),
                                                sheetUIContext.context)

        summary.sizeSp          = style.sizeSp()

        return summary.textView(sheetUIContext.context)
    }

}


//
//
//
//    // INTERNAL
//    // -----------------------------------------------------------------------------------------
//
//    // > Initialize
//    // -----------------------------------------------------------------------------------------
//
//    private void initializeLogWidget()
//    {
//        // [1] Apply default format values
//        // -------------------------------------------------------------------------------------
//
//        // ** Background
//        if (this.data().format().backgroundIsDefault())
//            this.data().format().setBackground(BackgroundColor.NONE);
//    }
//
//
//    // > Views
//    // -----------------------------------------------------------------------------------------
//
//    private View widgetView(Context context)
//    {
//        LinearLayout layout = this.widgetViewLayout(context);
//
//        for (LogEntry entry : this.entries()) {
//            layout.addView(entry.view(this.format().dividerType(), this.groupParent, context));
//        }
//
//        return layout;
//    }
//
//
//    private LinearLayout widgetViewLayout(final Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation          = LinearLayout.VERTICAL;
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        return layout.linearLayout(context);
//    }




//
//
//    // > View
//    // -----------------------------------------------------------------------------------------
//
//    public View view(DividerType dividerType, GroupParent groupParent, Context context)
//    {
//        LinearLayout layout = viewLayout(context);
//
//        // > Divider
//        layout.addView(dividerView(dividerType, groupParent.background(), context));
//
//        // > Header
//        layout.addView(headerView(context));
//
//        // > Text
//        // layout.addView(textView(context));
//
//        return layout;
//    }
//
//
//    // INTERNAL
//    // -----------------------------------------------------------------------------------------
//
//    // > Views
//    // -----------------------------------------------------------------------------------------
//
//    private LinearLayout viewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation          = LinearLayout.VERTICAL;
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.padding.left         = R.dimen.widget_log_entry_layout_padding_horz;
//        layout.padding.right        = R.dimen.widget_log_entry_layout_padding_horz;
//
//        layout.padding.bottom       = R.dimen.widget_log_entry_layout_padding_vert;
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout headerView(Context context)
//    {
//        // [1] Declarations
//        // -------------------------------------------------------------------------------------
//
//        LinearLayoutBuilder layout  = new LinearLayoutBuilder();
//        TextViewBuilder     title   = new TextViewBuilder();
//        TextViewBuilder     date    = new TextViewBuilder();
//        TextViewBuilder     author  = new TextViewBuilder();
//        TextViewBuilder     summary = new TextViewBuilder();
//
//        // TODO other locales?
//        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE MMM dd, yyyy", Locale.US);
//        String dateString           = dateFormat.format(this.date().getTime());
//
//        // [2] Layout
//        // -------------------------------------------------------------------------------------
//
//        layout.orientation          = LinearLayout.VERTICAL;
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.child(title)
//              .child(date)
//              .child(author)
//              .child(summary);
//
//        // [3 A] Title
//        // -------------------------------------------------------------------------------------
//
//        title.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
//        title.height                = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        title.text                  = this.title();
//        title.font                  = Font.serifFontBold(context);
//        title.color                 = R.color.dark_blue_hlx_6;
//        title.size                  = R.dimen.widget_log_entry_title_text_size;
//
//        title.margin.bottom         = R.dimen.widget_log_entry_title_margin_bottom;
//
//        // [3 B] Date
//        // -------------------------------------------------------------------------------------
//
//        date.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
//        date.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        date.text                   = dateString;
//        date.font                   = Font.serifFontRegular(context);
//        date.color                  = R.color.dark_blue_hl_9;
//        date.size                   = R.dimen.widget_log_entry_date_text_size;
//
//        date.margin.bottom          = R.dimen.widget_log_entry_date_margin_bottom;
//
//        // [3 C] Author
//        // -------------------------------------------------------------------------------------
//
//        author.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
//        author.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        author.textSpan             = this.authorSpan(context);
//        author.font                 = Font.serifFontRegular(context);
//        author.color                = R.color.dark_blue_hl_9;
//        author.size                 = R.dimen.widget_log_entry_author_text_size;
//
//        author.margin.bottom        = R.dimen.widget_log_entry_author_margin_bottom;
//
//        // [3 D] Summary
//        // -------------------------------------------------------------------------------------
//
//        summary.width               = LinearLayout.LayoutParams.WRAP_CONTENT;
//        summary.height              = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        summary.text                = this.summary();
//        summary.font                = Font.serifFontItalic(context);
//        summary.color               = R.color.dark_blue_hl_6;
//        summary.size                = R.dimen.widget_log_entry_summary_text_size;
//
//
//        return layout.linearLayout(context);
//    }
//
//
//    private SpannableStringBuilder authorSpan(Context context)
//    {
//        String authorString = context.getString(R.string.by) + " " + this.author();
//
//        SpannableStringBuilder spanBuilder = new SpannableStringBuilder(authorString);
//
//        StyleSpan valueBoldSpan = new StyleSpan(Typeface.BOLD);
//        spanBuilder.setSpan(valueBoldSpan, 3, authorString.length(), 0);
//
//        int colorResourceId = R.color.dark_blue_hl_2;
//        int colorId = ContextCompat.getColor(context, colorResourceId);
//        ForegroundColorSpan colorSpan = new ForegroundColorSpan(colorId);
//        spanBuilder.setSpan(colorSpan, 3, authorString.length(), 0);
//
//        return spanBuilder;
//    }
//
//
//    private LinearLayout dividerView(DividerType dividerType,
//                                     BackgroundColor background,
//                                     Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height           = R.dimen.one_dp;
//
//        layout.margin.bottom    = R.dimen.widget_log_entry_layout_padding_vert;
//
//        layout.backgroundColor  = dividerType.colorIdWithBackground(background);
//
//        return layout.linearLayout(context);
//    }
//
//}
