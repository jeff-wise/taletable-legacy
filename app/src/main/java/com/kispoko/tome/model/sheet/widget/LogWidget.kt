
package com.kispoko.tome.model.sheet.widget


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.theme.ColorId
import effect.*
import lulo.document.DocDict
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.util.*



/**
 * Log Entry
 */
data class LogEntry(override val id : UUID,
                    val title : Func<String>,
                    val author : Func<String>,
                    val summary : Func<String>,
                    val text : Func<String>) : Model
{
    companion object : Factory<LogEntry>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<LogEntry> = when (doc)
        {
            is DocDict -> effApply(::LogEntry,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Title
                                   effApply(::Prim, doc.text("title")),
                                   // Author
                                   effApply(::Prim, doc.text("author")),
                                   // Summary
                                   effApply(::Prim, doc.text("summary")),
                                   // text
                                   effApply(::Prim, doc.text("text")))
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

}


/**
 * Log Widget Format
 */
data class LogWidgetFormat(override val id : UUID,
                           val widgetFormat : Comp<WidgetFormat>,
                           val dividerColor : Func<ColorId>) : Model
{

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
