
package com.kispoko.tome.model.sheet.widget.tab;


import com.kispoko.tome.model.sheet.BackgroundColor;
import com.kispoko.tome.model.sheet.Spacing;
import com.kispoko.tome.model.sheet.widget.util.TextStyle;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;

import java.io.Serializable;
import java.util.UUID;



/**
 * Tab Widget Format
 */
public class TabWidgetFormat extends Model
                             implements ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                                id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private ModelFunctor<TextStyle>             tabDefaultTextStyle;
    private ModelFunctor<TextStyle>             tabSelectedTextStyle;

    private PrimitiveFunctor<Boolean>           underlineSelected;
    private PrimitiveFunctor<Integer>           underlineThickness;

    private ModelFunctor<Spacing>               tabMargins;
    private PrimitiveFunctor<Integer>           tabPaddingVertical;
    private PrimitiveFunctor<Height>            tabHeight;

    private PrimitiveFunctor<BackgroundColor>   tabBackgroundColor;
    private PrimitiveFunctor<Corners>           tabCorners;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public TabWidgetFormat()
    {
        this.id                     = null;

        this.tabDefaultTextStyle    = ModelFunctor.empty(TextStyle.class);
        this.tabSelectedTextStyle   = ModelFunctor.empty(TextStyle.class);

        this.underlineSelected      = new PrimitiveFunctor<>(null, Boolean.class);
        this.underlineThickness     = new PrimitiveFunctor<>(null, Integer.class);

        this.tabMargins             = ModelFunctor.empty(Spacing.class);
        this.tabPaddingVertical     = new PrimitiveFunctor<>(null, Integer.class);
        this.tabHeight              = new PrimitiveFunctor<>(null, Height.class);

        this.tabBackgroundColor     = new PrimitiveFunctor<>(null, BackgroundColor.class);
        this.tabCorners             = new PrimitiveFunctor<>(null, Corners.class);
    }


    public TabWidgetFormat(UUID id,
                           TextStyle tabDefaultTextStyle,
                           TextStyle tabSelectedTextStyle,
                           Boolean underlineSelected,
                           Integer underlineThickeness,
                           Spacing tabMargins,
                           Integer tabPaddingVertical,
                           Height tabHeight,
                           BackgroundColor tabBackgroundColor,
                           Corners tabCorners)
    {
        this.id                     = id;

        this.tabDefaultTextStyle    = ModelFunctor.full(tabDefaultTextStyle, TextStyle.class);
        this.tabSelectedTextStyle   = ModelFunctor.full(tabSelectedTextStyle, TextStyle.class);

        this.underlineSelected      = new PrimitiveFunctor<>(underlineSelected, Boolean.class);
        this.underlineThickness     = new PrimitiveFunctor<>(underlineThickeness, Integer.class);

        this.tabMargins             = ModelFunctor.full(tabMargins, Spacing.class);
        this.tabPaddingVertical     = new PrimitiveFunctor<>(tabPaddingVertical, Integer.class);
        this.tabHeight              = new PrimitiveFunctor<>(tabHeight, Height.class);

        this.tabBackgroundColor     = new PrimitiveFunctor<>(tabBackgroundColor,
                                                             BackgroundColor.class);
        this.tabCorners             = new PrimitiveFunctor<>(tabCorners, Corners.class);

        this.setTabDefaultTextStyle(tabDefaultTextStyle);
        this.setTabSelectedTextStyle(tabSelectedTextStyle);

        this.setUnderlineSelected(underlineSelected);
        this.setUnderlineThickness(underlineThickeness);

        this.setTabMargins(tabMargins);

        this.setTabCorners(tabCorners);
        this.setTabBackgroundColor(tabBackgroundColor);
    }


    /**
     * Create a Tab Widget Format from its yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Tab Widget Format.
     * @throws YamlParseException
     */
    public static TabWidgetFormat fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return TabWidgetFormat.asDefault();

        UUID            id                   = UUID.randomUUID();

        TextStyle       tabDefaultTextStyle  = TextStyle.fromYaml(
                                                        yaml.atMaybeKey("tab_default_text_style"));
        TextStyle       tabSelectedTextStyle = TextStyle.fromYaml(
                                                        yaml.atMaybeKey("tab_selected_text_style"));

        Boolean         underlineSelected    = yaml.atMaybeKey("underline_selected_tab")
                                                   .getBoolean();
        Integer         underlineThickness   = yaml.atMaybeKey("underline_thickness").getInteger();

        Spacing         tabMargins          = Spacing.fromYaml(yaml.atMaybeKey("tab_margins"));
        Integer         tabPaddingVertical  = yaml.atMaybeKey("tab_padding_vertical").getInteger();
        Height          tabHeight           = Height.fromYaml(yaml.atMaybeKey("tab_height"));

        BackgroundColor tabBGColor           = BackgroundColor.fromYaml(
                                                        yaml.atMaybeKey("tab_background_color"));
        Corners         tabCorners           = Corners.fromYaml(yaml.atMaybeKey("tab_corners"));

        return new TabWidgetFormat(id, tabDefaultTextStyle, tabSelectedTextStyle,
                                   underlineSelected, underlineThickness, tabMargins,
                                   tabPaddingVertical, tabHeight, tabBGColor, tabCorners);
    }


    /**
     * Create a Tab Widget Format with default values.
     * @return The default Tab Widget Format.
     */
    private static TabWidgetFormat asDefault()
    {
        TabWidgetFormat format = new TabWidgetFormat();

        format.setId(UUID.randomUUID());

        format.setTabDefaultTextStyle(null);
        format.setTabSelectedTextStyle(null);

        format.setUnderlineSelected(null);
        format.setUnderlineThickness(null);

        format.setTabMargins(null);
        format.setTabPaddingVertical(null);
        format.setTabHeight(null);

        format.setTabBackgroundColor(null);
        format.setTabCorners(null);

        return format;
    }


    // API
    // -----------------------------------------------------------------------------------------

    // > Model
    // --------------------------------------------------------------------------------------

    // ** Id
    // --------------------------------------------------------------------------------------

    public UUID getId()
    {
        return this.id;
    }


    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Load
    // --------------------------------------------------------------------------------------

    /**
     * Called when the Text Widget Format is completely loaded.
     */
    public void onLoad() { }


    // > To Yaml
    // --------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putYaml("tab_default_text_style", this.tabDefaultTextStyle())
                .putYaml("tab_selected_text_style", this.tabSelectedTextStyle())
                .putBoolean("underline_selected_tab", this.underlineSelected())
                .putInteger("underline_thickenss", this.underlineThickness())
                .putYaml("tab_margins", this.tabMargins())
                .putInteger("tab_padding_vertical", this.tabPaddingVertical())
                .putYaml("tab_height", this.tabHeight())
                .putYaml("tab_background_color", this.tabBackgroundColor())
                .putYaml("tab_corners", this.tabCorners());
    }


    // > State
    // -----------------------------------------------------------------------------------------

    // ** Tab Default Text Style
    // -----------------------------------------------------------------------------------------

    /**
     * The tab default text style.
     * @return The text style.
     */
    public TextStyle tabDefaultTextStyle()
    {
        return this.tabDefaultTextStyle.getValue();
    }


    /**
     * Set the tab default text style. If null, sets a default style.
     * @param style The style.
     */
    public void setTabDefaultTextStyle(TextStyle style)
    {
        if (style != null) {
            this.tabDefaultTextStyle.setValue(style);
        }
        else {
            TextStyle defaultTabStyle = new TextStyle(UUID.randomUUID(),
                                                      TextColor.THEME_DARK,
                                                      TextSize.MEDIUM_SMALL);
            this.tabDefaultTextStyle.setValue(defaultTabStyle);
        }
    }


    // ** Tab Selected Text Style
    // -----------------------------------------------------------------------------------------

    /**
     * The tab selected text style.
     * @return The text style.
     */
    public TextStyle tabSelectedTextStyle()
    {
        return this.tabSelectedTextStyle.getValue();
    }


    /**
     * Set the tab selected text style. If null, sets a default style.
     * @param style The style.
     */
    public void setTabSelectedTextStyle(TextStyle style)
    {
        if (style != null) {
            this.tabSelectedTextStyle.setValue(style);
        }
        else {
            TextStyle defaultTabStyle = new TextStyle(UUID.randomUUID(),
                                                      TextColor.THEME_MEDIUM_LIGHT,
                                                      TextSize.MEDIUM_SMALL);
            this.tabSelectedTextStyle.setValue(defaultTabStyle);
        }
    }


    // ** Underline Selected?
    // -----------------------------------------------------------------------------------------

    /**
     * Underline selected tab?
     * @return
     */
    public Boolean underlineSelected()
    {
        return this.underlineSelected.getValue();
    }


    /**
     * Set flag to display underline beneath selected tab. If null, defaults to TRUE.
     * @param showUnderline Show underline?
     */
    public void setUnderlineSelected(Boolean showUnderline)
    {
        if (showUnderline != null)
            this.underlineSelected.setValue(showUnderline);
        else
            this.underlineSelected.setValue(true);
    }


    // ** Underline Thickness
    // -----------------------------------------------------------------------------------------

    /**
     * The underline thickness (in DP).
     * @return The thickness.
     */
    public Integer underlineThickness()
    {
        return this.underlineThickness.getValue();
    }


    /**
     * Set the underline thickness. If null, defaults to 2 dp.
     * @param underlineThickness The thickness.
     */
    public void setUnderlineThickness(Integer underlineThickness)
    {
        if (underlineThickness != null)
            this.underlineThickness.setValue(underlineThickness);
        else
            this.underlineThickness.setValue(2);
    }


    // ** Tab Margins
    // -----------------------------------------------------------------------------------------

    /**
     * The margins around each tab view.
     * @return The spacing.
     */
    public Spacing tabMargins()
    {
        return this.tabMargins.getValue();
    }


    /**
     * Set the tab margins. If null, sets default margins.
     * @param spacing The spacing.
     */
    public void setTabMargins(Spacing spacing)
    {
        if (spacing != null)
            this.tabMargins.setValue(spacing);
        else
            this.tabMargins.setValue(Spacing.asDefault());
    }


    // ** Tab Padding Vertical
    // -----------------------------------------------------------------------------------------

    /**
     * The vertical padding around the tab text (in DP). May be null.
     * @return The tab vertical padding.
     */
    public Integer tabPaddingVertical()
    {
        return this.tabPaddingVertical.getValue();
    }


    /**
     * Set the vertical tab padding.
     * @param padding The padding.
     */
    public void setTabPaddingVertical(Integer padding)
    {
        this.tabPaddingVertical.setValue(padding);
    }


    // ** Tab Height
    // -----------------------------------------------------------------------------------------

    /**
     * The tab height. May be null, in which case the height is determined as a function of the
     * tab text size.
     * @return The height.
     */
    public Height tabHeight()
    {
        return this.tabHeight.getValue();
    }


    /**
     * Set the tab height.
     * @param height The height.
     */
    public void setTabHeight(Height height)
    {
        this.tabHeight.setValue(height);
    }


    // ** Tab Background Color
    // -----------------------------------------------------------------------------------------

    /**
     * The tab background color.
     * @return The background color.
     */
    public BackgroundColor tabBackgroundColor()
    {
        return this.tabBackgroundColor.getValue();
    }


    /**
     * Set the tab background color. Defaults to MEDIUM
     * @param backgroundColor The background color.
     */
    public void setTabBackgroundColor(BackgroundColor backgroundColor)
    {
        if (backgroundColor != null)
            this.tabBackgroundColor.setValue(backgroundColor);
        else
            this.tabBackgroundColor.setValue(BackgroundColor.NONE);
    }


    // ** Tab Corners
    // -----------------------------------------------------------------------------------------

    /**
     * The tab corner size.
     * @return The corners.
     */
    public Corners tabCorners()
    {
        return this.tabCorners.getValue();
    }


    /**
     * Set the tab corners size. If null, a default is SMALL.
     * @param corners The corners.
     */
    public void setTabCorners(Corners corners)
    {
        if (corners != null)
            this.tabCorners.setValue(corners);
        else
            this.tabCorners.setValue(Corners.SMALL);
    }

}
