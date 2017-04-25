
package com.kispoko.tome.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.widget.ImageWidget;
import com.kispoko.tome.util.UI;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;



/**
 * Image Widget Activity
 */
public class ImageWidgetActivity extends AppCompatActivity
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private ImageWidget imageWidget;


    // ACTIVITY LIFECYCLE EVENTS
    // ------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_form_basic);

        // > Read Parameters
        if (getIntent().hasExtra("widget")) {
            this.imageWidget = (ImageWidget) getIntent().getSerializableExtra("widget");
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
        String title = this.imageWidget.data().format().label();
        if (title == null)
            title = "Image Widget";

        UI.initializeToolbar(this, title);
    }


    private void initializeView()
    {
//        LinearLayout contentLayout = (LinearLayout) findViewById(R.id.content);
//        contentLayout.addView(formView());
    }


    // > Views
    // -----------------------------------------------------------------------------------------

    private LinearLayout formView()
    {
        LinearLayout layout = formLayout();

        // [1] Define Fields
        // -------------------------------------------------------------------------------------

        // > Name Field
        // -------------------------------------------------------------------------------------

        String name = this.imageWidget.data().format().label();

//        LinearLayout nameField =
//                Form.field(
//                    R.string.image_widget_field_name_label,
//                    R.string.image_widget_field_name_description,
//                    Form.textInput(name, this),
//                    this);

        // > Width
        // -------------------------------------------------------------------------------------

        String width = this.imageWidget.data().format().width().toString();

//        LinearLayout widthField =
//                Form.field(
//                        R.string.image_widget_field_width_label,
//                        R.string.image_widget_field_width_description,
//                        Form.textInput(width, this),
//                        this);

        // > Image
        // -------------------------------------------------------------------------------------

//        LinearLayout imageField =
//                Form.field(
//                        R.string.image_widget_field_width_label,
//                        R.string.image_widget_field_width_description,
//                        Form.textInput(width, this),
//                        this);


        // [2] Add Fields
        // -------------------------------------------------------------------------------------

//        layout.addView(nameField);
//        layout.addView(widthField);
//

        return layout;
    }


    private LinearLayout formLayout()
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.left         = R.dimen.form_padding_horz;
        layout.padding.right        = R.dimen.form_padding_horz;
        layout.padding.top          = R.dimen.form_padding_vert;
        layout.padding.bottom       = R.dimen.form_padding_vert;

        return layout.linearLayout(this);
    }


}
