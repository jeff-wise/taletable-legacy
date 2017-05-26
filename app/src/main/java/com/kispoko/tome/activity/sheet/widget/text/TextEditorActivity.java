
package com.kispoko.tome.activity.sheet.widget.text;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.model.sheet.widget.TextWidget;
import com.kispoko.tome.util.UI;
import com.kispoko.tome.lib.ui.EditTextBuilder;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.LayoutType;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.RelativeLayoutBuilder;
import com.kispoko.tome.lib.ui.ScrollViewBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;



/**
 * Text Editor Activity
 */
public class TextEditorActivity extends AppCompatActivity
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private TextWidget textWidget;

    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_text_editor);

        // > Read Parameters
        if (getIntent().hasExtra("text_widget")) {
            this.textWidget = (TextWidget) getIntent().getSerializableExtra("text_widget");
        }

        initializeToolbar();

        initializeView();
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_choose_template, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the toolbar.
     */
    private void initializeToolbar()
    {
        String title = this.getString(R.string.text_editor);

        UI.initializeToolbar(this, title);
    }


    private void initializeView()
    {
        LinearLayout contentLayout = (LinearLayout) findViewById(R.id.text_editor_content);
        contentLayout.addView(view(this));
    }


    // > Views
    // ------------------------------------------------------------------------------------------

    private RelativeLayout view(Context context)
    {
        RelativeLayout layout = viewLayout(context);

        // > Edit View
        layout.addView(editorView(context));

        // > Footer View
        layout.addView(footerView(context));

        return layout;
    }


    private RelativeLayout viewLayout(Context context)
    {
        RelativeLayoutBuilder layout = new RelativeLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT;

        return layout.relativeLayout(context);
    }


    private ScrollView editorView(Context context)
    {
        ScrollView scrollView = editorScrollView(context);

        scrollView.addView(editorContentView(context));

        return scrollView;
    }


    private ScrollView editorScrollView(Context context)
    {
        ScrollViewBuilder scrollView = new ScrollViewBuilder();

        scrollView.layoutType   = LayoutType.RELATIVE;
        scrollView.height       = RelativeLayout.LayoutParams.MATCH_PARENT;
        scrollView.width        = RelativeLayout.LayoutParams.MATCH_PARENT;

        return scrollView.scrollView(context);
    }


    private LinearLayout editorContentView(Context context)
    {
        LinearLayout layout = editorContentViewLayout(context);

        // > Header
        layout.addView(editorContentHeaderView(context));

        // > Divider View
        layout.addView(dividerView(context));

        // > Edit Text
        layout.addView(editTextView(context));

        return layout;
    }


    private LinearLayout editorContentViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.VERTICAL;

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundColor  = R.color.dark_blue_9;

        layout.padding.leftDp   = 10f;
        layout.padding.rightDp  = 10f;

        return layout.linearLayout(context);
    }


    private RelativeLayout editorContentHeaderView(Context context)
    {
        RelativeLayout layout = editorContentHeaderViewLayout(context);

        // > Name
        layout.addView(nameView(context));

        return layout;
    }


    private RelativeLayout editorContentHeaderViewLayout(Context context)
    {
        RelativeLayoutBuilder layout = new RelativeLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.topDp    = 10f;
        layout.padding.bottomDp = 5f;

        layout.margin.topDp     = 5f;

        return layout.relativeLayout(context);
    }


    private TextView nameView(Context context)
    {
        TextViewBuilder name = new TextViewBuilder();

        name.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
        name.height         = LinearLayout.LayoutParams.WRAP_CONTENT;

        if (this.textWidget != null)
            name.text           = this.textWidget.data().name();

        name.font           = Font.serifFontRegular(context);
        name.color          = R.color.dark_blue_hl_5;
        name.sizeSp         = 20f;

        return name.textView(context);
    }


    private LinearLayout dividerView(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.heightDp         = 1;

        layout.backgroundColor  = R.color.dark_blue_4;

        return layout.linearLayout(context);
    }


    private EditText editTextView(Context context)
    {
        EditTextBuilder text = new EditTextBuilder();

        text.width              = LinearLayout.LayoutParams.MATCH_PARENT;
        text.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        text.backgroundColor    = R.color.dark_blue_9;
        text.backgroundResource = R.drawable.bg_edit_text_no_style;

        if (this.textWidget != null)
            text.text           = this.textWidget.value();

        text.font               = Font.serifFontRegular(context);
        text.color              = R.color.dark_blue_hl_2;
        text.sizeSp             = 17f;

        text.padding.topDp      = 10f;

        return text.editText(context);
    }


    private RelativeLayout footerView(Context context)
    {
        RelativeLayout layout = footerViewLayout(context);

        // > Save Status
        layout.addView(saveStatusView(context));

        return layout;
    }


    private RelativeLayout footerViewLayout(Context context)
    {
        RelativeLayoutBuilder layout = new RelativeLayoutBuilder();

        layout.layoutType       = LayoutType.RELATIVE;
        layout.width            = RelativeLayout.LayoutParams.MATCH_PARENT;
        layout.heightDp         = 50;

        layout.backgroundColor  = R.color.dark_blue_11;

        layout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        return layout.relativeLayout(context);
    }


    private TextView saveStatusView(Context context)
    {
        TextViewBuilder status = new TextViewBuilder();

        status.layoutType   = LayoutType.RELATIVE;
        status.width        = RelativeLayout.LayoutParams.WRAP_CONTENT;
        status.height       = RelativeLayout.LayoutParams.WRAP_CONTENT;

        status.text         = "Saved";
        status.color        = R.color.dark_blue_hl_6;
        status.font         = Font.serifFontItalic(context);
        status.sizeSp       = 17f;

        status.addRule(RelativeLayout.CENTER_IN_PARENT);

        return status.textView(context);
    }


}
