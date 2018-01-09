
package com.kispoko.tome.lib.ui;


import android.content.Context;
import android.view.View;

import com.kispoko.tome.model.sheet.style.Spacing;

import java.util.ArrayList;
import java.util.List;

import br.tiagohm.markdownview.MarkdownView;
import br.tiagohm.markdownview.css.StyleSheet;


/**
 * Markdown View Builder
 */
public class MarkdownViewBuilder implements ViewBuilder
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    public Integer                  id;

    public LayoutType               layoutType;

    public Integer                  height;
    public Integer                  width;
    public Float                    weight;

    public Integer                  gravity;
    public Integer                  layoutGravity;

    public String                   markdownText;
    public StyleSheet               stylesheet;

    public Padding                  padding;
    public Spacing                  paddingSpacing;

    public Margins                  margin;
    public Spacing                  marginSpacing;

    public List<Integer> rules;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public MarkdownViewBuilder()
    {
        this.id                 = null;

        this.layoutType         = LayoutType.LINEAR;

        this.height             = null;
        this.width              = null;
        this.weight             = null;

        this.gravity            = null;
        this.layoutGravity      = null;

        this.padding            = new Padding();
        this.paddingSpacing     = null;

        this.margin             = new Margins();
        this.marginSpacing      = null;

        this.rules              = new ArrayList<>();
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Attributes
    // ------------------------------------------------------------------------------------------

    public MarkdownViewBuilder addRule(int verb)
    {
        this.rules.add(verb);
        return this;
    }


    // > View Builder
    // ------------------------------------------------------------------------------------------

    public View view(Context context)
    {
        return this.textView(context);
    }


    // > Markdown View
    // ------------------------------------------------------------------------------------------

    public MarkdownView textView(Context context)
    {
        MarkdownView markdownView = new MarkdownView(context);

        // [1] Text View
        // --------------------------------------------------------------------------------------

        // > Id
        // --------------------------------------------------------------------------------------

        if (this.id != null)
            markdownView.setId(this.id);

        // > Padding
        // --------------------------------------------------------------------------------------

        if (this.paddingSpacing != null) {
            markdownView.setPadding(this.paddingSpacing.leftPx(),
                                    this.paddingSpacing.topPx(),
                                    this.paddingSpacing.rightPx(),
                                    this.paddingSpacing.bottomPx());
        }
        else
        {
            markdownView.setPadding(this.padding.left(context),
                                    this.padding.top(context),
                                    this.padding.right(context),
                                    this.padding.bottom(context));
        }


        // [2] Layout
        // --------------------------------------------------------------------------------------

        LayoutParamsBuilder layoutParamsBuilder;
        layoutParamsBuilder = new LayoutParamsBuilder(this.layoutType, context);

        // > Width
        // --------------------------------------------------------------------------------------

        if (this.width != null)
            layoutParamsBuilder.setWidth(this.width);

        // > Height
        // --------------------------------------------------------------------------------------

        if (this.height != null)
            layoutParamsBuilder.setHeight(this.height);

        // > Weight
        // --------------------------------------------------------------------------------------

        if (this.weight != null)
            layoutParamsBuilder.setWeight(this.weight);

        // > Gravity
        // --------------------------------------------------------------------------------------

        if (this.layoutGravity != null)
            layoutParamsBuilder.setGravity(this.layoutGravity);

        // > Margins
        // --------------------------------------------------------------------------------------

        if (this.marginSpacing != null)
            layoutParamsBuilder.setMargins(this.marginSpacing);
        else
            layoutParamsBuilder.setMargins(this.margin);

        // > Rules (Relative Layout Only)
        // --------------------------------------------------------------------------------------

        layoutParamsBuilder.setRules(this.rules);


        markdownView.setLayoutParams(layoutParamsBuilder.layoutParams());

        // [3] Markdown
        // --------------------------------------------------------------------------------------

        if (this.stylesheet != null)
            markdownView.addStyleSheet(this.stylesheet);

        if (this.markdownText != null)
            markdownView.loadMarkdown(this.markdownText);

        return markdownView;
    }

}
