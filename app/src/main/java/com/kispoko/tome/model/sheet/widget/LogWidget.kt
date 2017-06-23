
package com.kispoko.tome.model.sheet.widget


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.theme.ColorId
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Log Entry
 */
data class LogEntry(override val id : UUID,
                    val title : Prim<EntryTitle>,
                    val author : Prim<EntryAuthor>,
                    val summary : Prim<EntrySummary>,
                    val text : Prim<EntryText>) : Model
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.title.name     = "title"
        this.author.name    = "author"
        this.summary.name   = "summary"
        this.text.name      = "text"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(title : EntryTitle,
                author : EntryAuthor,
                summary : EntrySummary,
                text : EntryText)
        : this(UUID.randomUUID(),
               Prim(title),
               Prim(author),
               Prim(summary),
               Prim(text))


    companion object : Factory<LogEntry>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<LogEntry> = when (doc)
        {
            is DocDict -> effApply(::LogEntry,
                                   // Title
                                   doc.at("title") ap { EntryTitle.fromDocument(it) },
                                   // Author
                                   doc.at("author") ap { EntryAuthor.fromDocument(it) },
                                   // Summary
                                   doc.at("summary") ap { EntrySummary.fromDocument(it) },
                                   // text
                                   doc.at("text") ap { EntryText.fromDocument(it) }
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "log_entry"

    override val modelObject = this

}


/**
 * Entry Title
 */
data class EntryTitle(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<EntryTitle>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<EntryTitle> = when (doc)
        {
            is DocText -> effValue(EntryTitle(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Entry Author
 */
data class EntryAuthor(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<EntryAuthor>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<EntryAuthor> = when (doc)
        {
            is DocText -> effValue(EntryAuthor(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Entry Summary
 */
data class EntrySummary(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<EntrySummary>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<EntrySummary> = when (doc)
        {
            is DocText -> effValue(EntrySummary(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Entry Text
 */
data class EntryText(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<EntryText>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<EntryText> = when (doc)
        {
            is DocText -> effValue(EntryText(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Log Widget Format
 */
data class LogWidgetFormat(override val id : UUID,
                           val widgetFormat : Comp<WidgetFormat>,
                           val dividerColor : Func<ColorId>) : Model
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetFormat.name      = "widget_format"
        this.dividerColor.name      = "divider_color"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<LogWidgetFormat>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<LogWidgetFormat> = when (doc)
        {
            is DocDict -> effApply(::LogWidgetFormat,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Widget Format
                                   doc.at("widget_format") ap {
                                       effApply(::Comp, WidgetFormat.fromDocument(it))
                                   },
                                   // Divider Color
                                   doc.at("divider_color") ap {
                                       effApply(::Prim, ColorId.fromDocument(it))
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "log_widget_format"

    override val modelObject = this

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
