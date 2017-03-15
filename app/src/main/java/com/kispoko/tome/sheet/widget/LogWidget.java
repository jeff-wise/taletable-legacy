
package com.kispoko.tome.sheet.widget;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.sheet.group.GroupParent;
import com.kispoko.tome.sheet.widget.log.LogEntry;
import com.kispoko.tome.sheet.widget.log.LogWidgetFormat;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.functor.CollectionFunctor;
import com.kispoko.tome.util.functor.ModelFunctor;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.YamlParser;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;


/**
 * Log Widget
 */
public class LogWidget extends Widget
                       implements Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private CollectionFunctor<LogEntry>     entries;
    private ModelFunctor<LogWidgetFormat>   format;
    private ModelFunctor<WidgetData>        widgetData;


    // > Internal
    // -----------------------------------------------------------------------------------------

    private GroupParent                     groupParent;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public LogWidget()
    {
        this.id                 = null;

        this.entries            = CollectionFunctor.empty(LogEntry.class);
        this.format             = ModelFunctor.empty(LogWidgetFormat.class);
        this.widgetData         = ModelFunctor.empty(WidgetData.class);
    }


    public LogWidget(UUID id,
                     List<LogEntry> entries,
                     LogWidgetFormat format,
                     WidgetData widgetData)
    {
        this.id                 = id;

        this.entries            = CollectionFunctor.full(entries, LogEntry.class);
        this.format             = ModelFunctor.full(format, LogWidgetFormat.class);
        this.widgetData         = ModelFunctor.full(widgetData, WidgetData.class);

        this.initializeLogWidget();
    }


    /**
     * Create a Log Widget from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Log Widget
     * @throws YamlParseException
     */
    public static LogWidget fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID           id      = UUID.randomUUID();

        List<LogEntry> entries = yaml.atKey("entries").forEach(new YamlParser.ForEach<LogEntry>() {
            @Override
            public LogEntry forEach(YamlParser yaml, int index) throws YamlParseException {
                return LogEntry.fromYaml(yaml);
            }
        }, true);

        LogWidgetFormat format = LogWidgetFormat.fromYaml(yaml.atMaybeKey("format"));

        WidgetData     data    = WidgetData.fromYaml(yaml.atMaybeKey("data"), false);

        return new LogWidget(id, entries, format, data);
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
    public void onLoad()
    {
        this.initializeLogWidget();
    }


    // > Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Action Widget's yaml representation.
     * @return
     */
    public YamlBuilder toYaml()
    {
        YamlBuilder yaml = YamlBuilder.map();

        yaml.putList("entries", this.entries());
        yaml.putYaml("data", this.data());

        return yaml;
    }


    // > Widget
    // ------------------------------------------------------------------------------------------

    @Override
    public void initialize(GroupParent groupParent)
    {
        this.groupParent = groupParent;
    }


    @Override
    public WidgetData data()
    {
        return this.widgetData.getValue();
    }


    /**
     * The text widget's tile view.
     * @return The tile view.
     */
    @Override
    public View view(boolean rowHasLabel, Context context)
    {
        return this.widgetView(context);
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Entries
    // ------------------------------------------------------------------------------------------

    /**
     * The log entries.
     * @return The entries.
     */
    public List<LogEntry> entries()
    {
        return this.entries.getValue();
    }


    // ** Format
    // ------------------------------------------------------------------------------------------

    /**
     * The log widget formatting options.
     * @return The format.
     */
    public LogWidgetFormat format()
    {
        return this.format.getValue();
    }


    // INTERNAL
    // -----------------------------------------------------------------------------------------

    // > Initialize
    // -----------------------------------------------------------------------------------------

    private void initializeLogWidget()
    {
        // [1] Apply default format values
        // -------------------------------------------------------------------------------------

        // ** Width
        if (this.data().format().width() == null)
            this.data().format().setWidth(1);

        // ** Background
        if (this.data().format().background() == null)
            this.data().format().setBackground(BackgroundColor.NONE);
    }


    // > Views
    // -----------------------------------------------------------------------------------------

    private View widgetView(Context context)
    {
        LinearLayout layout = this.widgetViewLayout(context);

        for (LogEntry entry : this.entries()) {
            layout.addView(entry.view(this.format().dividerType(), this.groupParent, context));
        }

        return layout;
    }


    private LinearLayout widgetViewLayout(final Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }



}
