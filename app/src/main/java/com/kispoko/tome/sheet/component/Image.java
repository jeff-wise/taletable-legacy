
package com.kispoko.tome.sheet.component;


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
import com.kispoko.tome.sheet.Component;
import com.kispoko.tome.sheet.Group;
import com.kispoko.tome.type.Type;
import com.kispoko.tome.util.SQL;
import com.kispoko.tome.util.SerialBitmap;
import com.kispoko.tome.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Image
 */
public class Image extends Component implements Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private int imageViewId;
    private int chooseImageButtonId;
    private SerialBitmap serialBitmap;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Image(UUID id, UUID groupId, Type.Id typeId, Format format, List<String> actions,
                 Bitmap bitmap)
    {
        super(id, groupId, typeId, format, actions);

        if (bitmap != null) {
            this.serialBitmap = new SerialBitmap(bitmap);
            Log.d("***IMAGE", "set serial bitmap in cons");
        }
        else
            this.serialBitmap = null;
    }


    public static Image fromYaml(Map<String, Object> imageYaml)
    {
        // Values to parse
        UUID id = null;
        UUID groupId = null;
        Type.Id typeId = null;
        Format format = null;
        List<String> actions = null;

        // Parse Values
        Map<String, Object> formatYaml = (Map<String, Object>) imageYaml.get("format");

        // >> Format
        format = Component.parseFormatYaml(imageYaml);

        // >> Actions
        if (imageYaml.containsKey("actions"))
            actions = (List<String>) imageYaml.get("actions");

        return new Image(id, groupId, typeId, format, actions, null);
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

        this.save(Global.getDatabase(), null);
    }

    // >> Getters/Setters
    // ------------------------------------------------------------------------------------------

    public String componentName()
    {
        return "image";
    }


    public void runAction(String actionName, Context context, Rules rules)
    {
    }

    // >> Database
    // ------------------------------------------------------------------------------------------

    /**
     * Load a Group from the database.
     * @param database The sqlite database object.
     * @param groupConstructorId The id of the async page constructor.
     * @param componentId The database id of the group to load.
     */
    public static void load(final SQLiteDatabase database,
                            final UUID groupConstructorId,
                            final UUID componentId)
    {
        new AsyncTask<Void,Void,Image>()
        {

            @Override
            protected Image doInBackground(Void... args)
            {
                // Query Component
                String imageQuery =
                    "SELECT comp.group_id, comp.label, comp.row, comp.column, comp.width, " +
                           "comp.actions, im.image " +
                    "FROM Component comp " +
                    "INNER JOIN component_image im on im.component_id = comp.component_id " +
                    "WHERE comp.component_id =  " + SQL.quoted(componentId.toString());

                Cursor imageCursor = database.rawQuery(imageQuery, null);

                UUID groupId = null;
                String label = null;
                Integer row = null;
                Integer column = null;
                Integer width = null;
                List<String> actions = null;
                byte[] imageBlob = null;
                try {
                    imageCursor.moveToFirst();
                    groupId     = UUID.fromString(imageCursor.getString(0));
                    label       = imageCursor.getString(1);
                    row         = imageCursor.getInt(2);
                    column      = imageCursor.getInt(3);
                    width       = imageCursor.getInt(4);
                    actions     = new ArrayList<>(Arrays.asList(
                                        TextUtils.split(imageCursor.getString(5), ",")));
                    imageBlob   = imageCursor.getBlob(6);
                } catch (Exception e ) {
                    Log.d("***IMAGE", Log.getStackTraceString(e));
                } finally {
                    imageCursor.close();
                }

                Bitmap bitmap = null;
                if (imageBlob != null)
                    bitmap = Util.getImage(imageBlob);

                return new Image(componentId,
                                 groupId,
                                 null,
                                 new Format(label, row, column, width),
                                 actions,
                                 bitmap);
            }

            @Override
            protected void onPostExecute(Image image)
            {
                Group.getAsyncConstructor(groupConstructorId).addComponent(image);
            }

        }.execute();
    }


    /**
     * Save to the database.
     * @param database The SQLite database object.
     */
    public void save(final SQLiteDatabase database, final UUID groupTrackerId)
    {
        final Image thisImage = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                ContentValues componentRow = new ContentValues();

                componentRow.put("component_id", thisImage.getId().toString());
                componentRow.put("group_id", thisImage.getGroupId().toString());
                componentRow.put("data_type", thisImage.componentName());
                componentRow.put("label", thisImage.getLabel());
                componentRow.put("row", thisImage.getRow());
                componentRow.put("column", thisImage.getColumn());
                componentRow.put("width", thisImage.getWidth());
                componentRow.put("actions", TextUtils.join(",", thisImage.getActions()));
                componentRow.putNull("type_kind");
                componentRow.putNull("type_id");
                componentRow.putNull("text_value");

                database.insertWithOnConflict(SheetContract.Component.TABLE_NAME,
                                              null,
                                              componentRow,
                                              SQLiteDatabase.CONFLICT_REPLACE);

                ContentValues imageComponentRow = new ContentValues();
                imageComponentRow.put("component_id", thisImage.getId().toString());

                if (thisImage.serialBitmap != null) {
                    if (thisImage.serialBitmap.getBitmap() != null) {
                        byte[] bytes = Util.getBytes(thisImage.serialBitmap.getBitmap());
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
                if (groupTrackerId != null)
                    Group.getTracker(groupTrackerId).setComponentId(thisImage.getId());
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

        final Image thisImage = this;

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

                sheetActivity.setChooseImageAction(new ChooseImageAction(thisImage));

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
