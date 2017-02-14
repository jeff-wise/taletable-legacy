
package com.kispoko.tome.sheet.widget.log;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
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
    private PrimitiveFunctor<String>            text;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public LogEntry()
    {
        this.id         = null;

        this.title      = new PrimitiveFunctor<>(null, String.class);
        this.date       = new PrimitiveFunctor<>(null, GregorianCalendar.class);
        this.author     = new PrimitiveFunctor<>(null, String.class);
        this.text       = new PrimitiveFunctor<>(null, String.class);
    }


    public LogEntry(UUID id, String title, GregorianCalendar date, String author, String text)
    {
        this.id         = id;

        this.title      = new PrimitiveFunctor<>(title, String.class);
        this.date       = new PrimitiveFunctor<>(date, GregorianCalendar.class);
        this.author     = new PrimitiveFunctor<>(author, String.class);
        this.text       = new PrimitiveFunctor<>(text, String.class);
    }


    public static LogEntry fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID              id        = UUID.randomUUID();

        String            title     = yaml.atKey("title").getTrimmedString();
        GregorianCalendar date      = yaml.atKey("date").getCalendar();
        String            author    = yaml.atKey("author").getTrimmedString();
        String            text      = yaml.atKey("text").getTrimmedString();

        return new LogEntry(id, title, date, author, text);
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
        YamlBuilder yaml = YamlBuilder.map();

        yaml.putString("title", this.title());
        yaml.putCalendar("date", this.date());
        yaml.putString("author", this.author());
        yaml.putString("text", this.text());

        return yaml;
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
     * The entry's text.
     * @return The text.
     */
    public String text()
    {
        return this.text.getValue();
    }


    // > View
    // -----------------------------------------------------------------------------------------

    public View view(Context context)
    {
        LinearLayout layout = viewLayout(context);

        // > Header
        layout.addView(headerView(context));

        // > Text
        layout.addView(textView(context));


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

        return layout.linearLayout(context);
    }


    private LinearLayout headerView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        TextViewBuilder     title  = new TextViewBuilder();
        TextViewBuilder     date   = new TextViewBuilder();
        TextViewBuilder     author = new TextViewBuilder();

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
              .child(author);

        // [3 A] Title
        // -------------------------------------------------------------------------------------

        title.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        title.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        title.text                  = this.title();
        title.font                  = Font.serifFontRegular(context);
        title.color                 = R.color.dark_blue_hlx_7;
        title.size                  = R.dimen.widget_log_entry_title_text_size;

        // [3 B] Date
        // -------------------------------------------------------------------------------------

        date.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        date.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        date.text                   = dateString;
        date.font                   = Font.serifFontRegular(context);
        date.color                  = R.color.dark_blue_hl_5;
        date.size                   = R.dimen.widget_log_entry_date_text_size;

        // [3 C] Author
        // -------------------------------------------------------------------------------------

        author.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        author.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        author.text                 = this.author();
        author.font                 = Font.serifFontRegular(context);
        author.color                = R.color.dark_blue_hl_8;
        author.size                 = R.dimen.widget_log_entry_author_text_size;


        return layout.linearLayout(context);
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

        return text.textView(context);
    }
}
