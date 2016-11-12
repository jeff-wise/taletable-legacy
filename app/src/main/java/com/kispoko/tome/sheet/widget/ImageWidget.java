
package com.kispoko.tome.sheet.widget;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kispoko.tome.Global;
import com.kispoko.tome.activity.sheet.ChooseImageAction;
import com.kispoko.tome.activity.SheetActivity;
import com.kispoko.tome.R;
import com.kispoko.tome.db.SheetContract;
import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.sheet.Group;
import com.kispoko.tome.rules.programming.Variable;
import com.kispoko.tome.sheet.widget.table.Cell;
import com.kispoko.tome.type.Type;
import com.kispoko.tome.util.database.SQL;
import com.kispoko.tome.util.SerialBitmap;
import com.kispoko.tome.util.TrackerId;
import com.kispoko.tome.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * ImageWidget
 */
public class ImageWidget extends WidgetData implements Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private int imageViewId;
    private int chooseImageButtonId;
    private SerialBitmap serialBitmap;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ImageWidget(UUID id, UUID groupId)
    {
        super(id, null, groupId, null, null, null, null);
        this.serialBitmap = null;
    }


    public ImageWidget(UUID id, String name, UUID groupId, Variable value, Type.Id typeId,
                       Format format, List<String> actions, Bitmap bitmap)
    {
        super(id, name, groupId, value, typeId, format, actions);

        this.serialBitmap = null;
        this.setBitmap(bitmap);
    }


    @SuppressWarnings("unchecked")
    public static ImageWidget fromYaml(UUID groupId, Map<String, Object> imageYaml)
    {
        // VALUES TO PARSE
        // --------------------------------------------------------------------------------------
        UUID id = UUID.randomUUID();
        String name = null;
        Format format = null;
        List<String> actions = null;

        // PARSE VALUES
        // --------------------------------------------------------------------------------------

        // > Top Level
        // --------------------------------------------------------------------------------------

        // ** Name
        if (imageYaml.containsKey("name"))
            name = (String) imageYaml.get("name");

        // ** Actions
        if (imageYaml.containsKey("actions"))
            actions = (List<String>) imageYaml.get("actions");

        // >> Format
        // --------------------------------------------------------------------------------------
        Map<String, Object> formatYaml = (Map<String, Object>) imageYaml.get("format");

        if (formatYaml != null)
        {
            // ** Format
            format = WidgetData.parseFormatYaml(imageYaml);
        }

        return new ImageWidget(id, name, groupId, null, null, format, actions, null);
    }


    // > API
    // ------------------------------------------------------------------------------------------

    public void setImageFromURI(Activity activity, Uri uri)
    {
        ImageView imageView = (ImageView) activity.findViewById(this.imageViewId);
        Button chooseImageButton = (Button) activity.findViewById(this.chooseImageButtonId);

        imageView.setVisibility(View.VISIBLE);
        chooseImageButton.setVisibility(View.GONE);
        imageView.setImageURI(uri);

        this.serialBitmap = new SerialBitmap(((BitmapDrawable)imageView.getDrawable()).getBitmap());

        this.save(null);
    }


    // >> Getters/Setters
    // ------------------------------------------------------------------------------------------

    public String componentName()
    {
        return "image";
    }


    public void setBitmap(Bitmap bitmap) {
        if (bitmap != null)
            this.serialBitmap = new SerialBitmap(bitmap);
    }


    public void runAction(String actionName, Context context, Rules rules)
    {
    }

    // >> Database
    // ------------------------------------------------------------------------------------------

    /**
     * Load a Group from the database.
     * @param trackerId The async tracker ID of the caller.
     */
    public void load(final TrackerId trackerId)
    {

        final ImageWidget thisImageWidget = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // ModelQuery WidgetData
                String imageQuery =
                    "SELECT comp.name, comp.label, comp.show_label, comp.row, comp.column, " +
                           "comp.width, comp.actions, im.image " +
                    "FROM WidgetData comp " +
                    "INNER JOIN component_image im on im.component_id = comp.component_id " +
                    "WHERE comp.component_id =  " + SQL.quoted(thisImageWidget.getId().toString());

                Cursor imageCursor = database.rawQuery(imageQuery, null);

                String name = null;
                String label = null;
                Boolean showLabel = null;
                Integer row = null;
                Integer column = null;
                Integer width = null;
                List<String> actions = null;
                byte[] imageBlob = null;
                try {
                    imageCursor.moveToFirst();
                    name        = imageCursor.getString(0);
                    label       = imageCursor.getString(1);
                    showLabel   = SQL.intAsBool(imageCursor.getInt(2));
                    row         = imageCursor.getInt(3);
                    column      = imageCursor.getInt(4);
                    width       = imageCursor.getInt(5);
                    actions     = new ArrayList<>(Arrays.asList(
                                        TextUtils.split(imageCursor.getString(6), ",")));
                    imageBlob   = imageCursor.getBlob(7);
                } catch (Exception e ) {
                    Log.d("***IMAGE", Log.getStackTraceString(e));
                } finally {
                    imageCursor.close();
                }

                Bitmap bitmap = null;
                if (imageBlob != null)
                    bitmap = Util.getImage(imageBlob);

                thisImageWidget.setName(name);
                thisImageWidget.setLabel(label);
                thisImageWidget.setShowLabel(showLabel);
                thisImageWidget.setRow(row);
                thisImageWidget.setColumn(column);
                thisImageWidget.setWidth(width);
                thisImageWidget.setActions(actions);
                thisImageWidget.setBitmap(bitmap);

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                UUID trackerCode = trackerId.getCode();
                switch (trackerId.getTarget()) {
                    case GROUP:
                        Group.getAsyncTracker(trackerCode).markComponentId(thisImageWidget.getId());
                        break;
                    case CELL:
                        Cell.getAsyncTracker(trackerCode).markComponent();
                        break;
                }
            }

        }.execute();
    }


    /**
     * Save to the database.
     * @param trackerId The async tracker ID of the caller.
     */
    public void save(final TrackerId trackerId)
    {
        final ImageWidget thisImageWidget = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // > Save WidgetData Row
                // ------------------------------------------------------------------------------
                ContentValues componentRow = new ContentValues();

                thisImageWidget.putComponentSQLRows(componentRow);

                componentRow.putNull("text_value");

                database.insertWithOnConflict(SheetContract.Component.TABLE_NAME,
                                              null,
                                              componentRow,
                                              SQLiteDatabase.CONFLICT_REPLACE);

                // > Save ImageComponent Row
                // ------------------------------------------------------------------------------
                ContentValues imageComponentRow = new ContentValues();

                imageComponentRow.put("component_id", thisImageWidget.getId().toString());

                if (thisImageWidget.serialBitmap != null) {
                    if (thisImageWidget.serialBitmap.getBitmap() != null) {
                        byte[] bytes = Util.getBytes(thisImageWidget.serialBitmap.getBitmap());
                        imageComponentRow.put("image", bytes);
                        Log.d("***IMAGE", "image saved " + Integer.toString(bytes.length));
                    }
                } else {
                    imageComponentRow.putNull("image");
                }

                database.insertWithOnConflict(SheetContract.ComponentImage.TABLE_NAME,
                                              null,
                                              imageComponentRow,
                                              SQLiteDatabase.CONFLICT_REPLACE);

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                if (trackerId == null) return;

                UUID trackerCode = trackerId.getCode();
                switch (trackerId.getTarget()) {
                    case GROUP:
                        Group.getAsyncTracker(trackerCode).markComponentId(thisImageWidget.getId());
                        break;
                    case CELL:
                        Cell.getAsyncTracker(trackerCode).markComponent();
                        break;
                }
            }

        }.execute();
    }


    // >> Views
    // ------------------------------------------------------------------------------------------

    /**
     * Get the view for the image component. Depending on the mode, it will either display an
     * image or a button that allows the user to choose an image.
     * Use setMode to change the view dynamically.
     * @param context
     * @return
     */
    public View getDisplayView(final Context context, Rules rules)
    {
        // Layout
        final LinearLayout imageLayout = this.linearLayout(context, rules);
        imageLayout.setGravity(Gravity.CENTER);
        //imageLayout.setLayoutParams(com.kispoko.tome.util.Util.linearLayoutParamsMatch());

        // Views
        final ImageView imageView = this.imageView(context);
        this.imageViewId = Util.generateViewId();
        imageView.setId(this.imageViewId);

        final Button chooseImageButton = this.chooseImageButton(context);
        this.chooseImageButtonId = Util.generateViewId();
        chooseImageButton.setId(this.chooseImageButtonId);

        final ImageWidget thisImageWidget = this;

        chooseImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SheetActivity sheetActivity = (SheetActivity) context;

                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                }

                sheetActivity.setChooseImageAction(new ChooseImageAction(thisImageWidget));

                sheetActivity.startActivityForResult(intent, SheetActivity.CHOOSE_IMAGE_FROM_FILE);

            }
        });


        // Add views to layout
        imageLayout.addView(imageView);
        imageLayout.addView(chooseImageButton);

        // Have a picture, show it
        if (this.serialBitmap != null)
        {
            chooseImageButton.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(this.serialBitmap.getBitmap());
            Log.d("***IMAGE", "set image bitmap");
        }
        // No stored picture, give user upload button
        else {
            chooseImageButton.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        }


        return imageLayout;
    }


    public View getEditorView(Context context, Rules rules)
    {
        return new LinearLayout(context);
    }


    // > INTERNAL
    // ------------------------------------------------------------------------------------------


    private ImageView imageView(Context context)
    {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setAdjustViewBounds(true);

        LinearLayout.LayoutParams imageViewLayoutParams = Util.linearLayoutParamsWrap();
        int imageViewHeight = (int) context.getResources()
                .getDimension(R.dimen.comp_image_image_height);
        imageViewLayoutParams.height = imageViewHeight;
        imageView.setLayoutParams(imageViewLayoutParams);

        return imageView;
    }


    private Button chooseImageButton(Context context)
    {
        final Button button = new Button(context);


        // Button text appearance
        button.setText("Choose a Picture");
        int textSize = (int) context.getResources()
                .getDimension(R.dimen.comp_image_button_text_size);
        button.setTextSize(textSize);
        button.setTextColor(ContextCompat.getColor(context, R.color.text));

        // Set button icon
        button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_a_photo_17dp, 0, 0, 0);

        // Configure button padding
        int buttonPadding = (int) context.getResources()
                .getDimension(R.dimen.comp_image_button_padding);
        int buttonIconPadding = (int) context.getResources()
                .getDimension(R.dimen.comp_image_button_icon_padding);
        button.setPadding(buttonPadding, 0, buttonPadding, 0);
        button.setCompoundDrawablePadding(buttonIconPadding);

        // Configure button layout params
        LinearLayout.LayoutParams buttonLayoutParams = Util.linearLayoutParamsWrap();

        // >> Button margins
        int buttonVertMargins = (int) context.getResources()
                                             .getDimension(R.dimen.comp_image_button_vert_margins);
        buttonLayoutParams.setMargins(0, buttonVertMargins, 0, buttonVertMargins);

        button.setLayoutParams(buttonLayoutParams);

        return button;
    }


}
