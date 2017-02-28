
package com.kispoko.tome.sheet.widget.log;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.Background;
import com.kispoko.tome.sheet.DividerType;
import com.kispoko.tome.sheet.group.GroupParent;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;



/**
 * Log Entry
 */
public class LogEntry implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                                id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>            title;
    private PrimitiveFunctor<GregorianCalendar> date;
    private PrimitiveFunctor<String>            author;
    private PrimitiveFunctor<String>            summary;
    private PrimitiveFunctor<String>            text;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public LogEntry()
    {
        this.id         = null;

        this.title      = new PrimitiveFunctor<>(null, String.class);
        this.date       = new PrimitiveFunctor<>(null, GregorianCalendar.class);
        this.author     = new PrimitiveFunctor<>(null, String.class);
        this.summary    = new PrimitiveFunctor<>(null, String.class);
        this.text       = new PrimitiveFunctor<>(null, String.class);
    }


    public LogEntry(UUID id,
                    String title,
                    GregorianCalendar date,
                    String author,
                    String summary,
                    String text)
    {
        this.id         = id;

        this.title      = new PrimitiveFunctor<>(title, String.class);
        this.date       = new PrimitiveFunctor<>(date, GregorianCalendar.class);
        this.author     = new PrimitiveFunctor<>(author, String.class);
        this.summary    = new PrimitiveFunctor<>(summary, String.class);
        this.text       = new PrimitiveFunctor<>(text, String.class);
    }


    public static LogEntry fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID              id        = UUID.randomUUID();

        String            title     = yaml.atKey("title").getTrimmedString();
        GregorianCalendar date      = yaml.atKey("date").getCalendar();
        String            author    = yaml.atKey("author").getTrimmedString();
        String            summary   = yaml.atMaybeKey("summary").getTrimmedString();
        String            text      = yaml.atKey("text").getTrimmedString();

        return new LogEntry(id, title, date, author, summary, text);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    /**
     * Get the model identifier.
     * @return The model UUID.
     */
    public UUID getId()
    {
        return this.id;
    }


    /**
     * Set the model identifier.
     * @param id The new model UUID.
     */
    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Load
    // ------------------------------------------------------------------------------------------

    /**
     * This method is called when the RulesEngine is completely loaded for the first time.
     */
    public void onLoad() { }


    // > Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Action Widget's yaml representation.
     * @return
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("title", this.title())
                .putCalendar("date", this.date())
                .putString("author", this.author())
                .putString("summary", this.summary())
                .putString("text", this.text());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * The entry title.
     * @return The title.
     */
    public String title()
    {
        return this.title.getValue();
    }


    /**
     * The entry date.
     * @return The date.
     */
    public GregorianCalendar date()
    {
        return this.date.getValue();
    }


    /**
     * The entry's author.
     * @return The author.
     */
    public String author()
    {
        return this.author.getValue();
    }


    /**
     * The entry summary. A short description displayed in the header.
     * @return The summary.
     */
    public String summary()
    {
        return this.summary.getValue();
    }


    /**
     * The entry's text.
     * @return The text.
     */
    public String text()
    {
        return this.text.getValue();
    }


    // > View
    // -----------------------------------------------------------------------------------------

    public View view(DividerType dividerType, GroupParent groupParent, Context context)
    {
        LinearLayout layout = viewLayout(context);

        // > Divider
        layout.addView(dividerView(dividerType, groupParent.background(), context));

        // > Header
        layout.addView(headerView(context));

        // > Text
        // layout.addView(textView(context));

        return layout;
    }


    // INTERNAL
    // -----------------------------------------------------------------------------------------

    // > Views
    // -----------------------------------------------------------------------------------------

    private LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.left         = R.dimen.widget_log_entry_layout_padding_horz;
        layout.padding.right        = R.dimen.widget_log_entry_layout_padding_horz;

        layout.padding.bottom       = R.dimen.widget_log_entry_layout_padding_vert;

        return layout.linearLayout(context);
    }


    private LinearLayout headerView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout  = new LinearLayoutBuilder();
        TextViewBuilder     title   = new TextViewBuilder();
        TextViewBuilder     date    = new TextViewBuilder();
        TextViewBuilder     author  = new TextViewBuilder();
        TextViewBuilder     summary = new TextViewBuilder();

        // TODO other locales?
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE MMM dd, yyyy", Locale.US);
        String dateString           = dateFormat.format(this.date().getTime());

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.child(title)
              .child(date)
              .child(author)
              .child(summary);

        // [3 A] Title
        // -------------------------------------------------------------------------------------

        title.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        title.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        title.text                  = this.title();
        title.font                  = Font.serifFontRegular(context);
        title.color                 = R.color.dark_blue_hlx_4;
        title.size                  = R.dimen.widget_log_entry_title_text_size;

        title.margin.bottom         = R.dimen.widget_log_entry_title_margin_bottom;

        // [3 B] Date
        // -------------------------------------------------------------------------------------

        date.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        date.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        date.text                   = dateString;
        date.font                   = Font.serifFontRegular(context);
        date.color                  = R.color.dark_blue_hl_8;
        date.size                   = R.dimen.widget_log_entry_date_text_size;

        date.margin.bottom          = R.dimen.widget_log_entry_date_margin_bottom;

        // [3 C] Author
        // -------------------------------------------------------------------------------------

        author.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        author.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        author.textSpan             = this.authorSpan(context);
        author.font                 = Font.serifFontRegular(context);
        author.color                = R.color.dark_blue_hl_9;
        author.size                 = R.dimen.widget_log_entry_author_text_size;

        author.margin.bottom        = R.dimen.widget_log_entry_author_margin_bottom;

        // [3 D] Summary
        // -------------------------------------------------------------------------------------

        summary.width               = LinearLayout.LayoutParams.WRAP_CONTENT;
        summary.height              = LinearLayout.LayoutParams.WRAP_CONTENT;

        summary.text                = this.summary();
        summary.font                = Font.serifFontItalic(context);
        summary.color               = R.color.dark_blue_hl_5;
        summary.size                = R.dimen.widget_log_entry_summary_text_size;


        return layout.linearLayout(context);
    }


    private SpannableStringBuilder authorSpan(Context context)
    {
        String authorString = context.getString(R.string.by) + " " + this.author();

        SpannableStringBuilder spanBuilder = new SpannableStringBuilder(authorString);

        StyleSpan valueBoldSpan = new StyleSpan(Typeface.BOLD);
        spanBuilder.setSpan(valueBoldSpan, 3, authorString.length(), 0);

        int colorResourceId = R.color.dark_blue_hl_2;
        int colorId = ContextCompat.getColor(context, colorResourceId);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(colorId);
        spanBuilder.setSpan(colorSpan, 3, authorString.length(), 0);

        return spanBuilder;
    }


    private TextView textView(Context context)
    {
        TextViewBuilder text = new TextViewBuilder();

        text.width              = LinearLayout.LayoutParams.MATCH_PARENT;
        text.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        text.text               = this.text();
        text.font               = Font.serifFontRegular(context);
        text.color              = R.color.dark_blue_hl_8;
        text.size               = R.dimen.widget_log_entry_text_text_size;

        text.padding.left       = R.dimen.widget_log_entry_text_padding_horz;
        text.padding.right      = R.dimen.widget_log_entry_text_padding_horz;

        return text.textView(context);
    }


    private LinearLayout dividerView(DividerType dividerType,
                                     Background background,
                                     Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = R.dimen.one_dp;

        layout.margin.bottom    = R.dimen.widget_log_entry_layout_padding_vert;

        layout.backgroundColor  = dividerType.colorIdWithBackground(background);

        return layout.linearLayout(context);
    }

}
