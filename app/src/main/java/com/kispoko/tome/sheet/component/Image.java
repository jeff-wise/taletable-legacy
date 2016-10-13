
package com.kispoko.tome.sheet.component;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kispoko.tome.activity.ChooseImageAction;
import com.kispoko.tome.activity.SheetActivity;
import com.kispoko.tome.R;
import com.kispoko.tome.db.SheetContract;
import com.kispoko.tome.sheet.Component;
import com.kispoko.tome.sheet.Group;
import com.kispoko.tome.type.Type;
import com.kispoko.tome.util.Util;

import java.io.Serializable;
import java.util.Map;



/**
 * Image
 */
public class Image extends Component implements Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Bitmap bitmap;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Image(Id id, Type.Id typeId, String label, Bitmap bitmap)
    {
        super(id, typeId, label);
        this.bitmap = bitmap;
    }


    public static Image fromYaml(Map<String, Object> imageYaml)
    {
        String label  = (String) imageYaml.get("label");

        return new Image(null, null, label, null);
    }


    // > API
    // ------------------------------------------------------------------------------------------

    // >> Getters/Setters
    // ------------------------------------------------------------------------------------------

    public String componentName()
    {
        return "image";
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
                            final Integer groupConstructorId,
                            final Integer componentId)
    {
        new AsyncTask<Void,Void,Void>()
        {

            protected Void doInBackground(Void... args)
            {
                // Query Component
                String imageQuery =
                    "SELECT comp.component_id, comp.label, im.image_id, im.image " +
                    "FROM Component comp " +
                    "INNER JOIN ComponentImage im on ComponentImage.component_id = Component.component_id " +
                    "WHERE Component.component_id =  " + Integer.toString(componentId);

                Cursor imageCursor = database.rawQuery(imageQuery, null);

                Long componentId;
                String label;
                Long imageId;
                byte[] imageBlob;
                try {
                    imageCursor.moveToFirst();
                    componentId = imageCursor.getLong(0);
                    label       = imageCursor.getString(1);
                    imageId     = imageCursor.getLong(2);
                    imageBlob   = imageCursor.getBlob(3);
                }
                // TODO log
                finally {
                    imageCursor.close();
                }

                Image image = new Image(new Id(componentId, imageId),
                                        null, label, Util.getImage(imageBlob));

                Group.asyncConstructorMap.get(groupConstructorId).addComponent(image);

                return null;
            }

        }.execute();
    }


    /**
     * Save to the database.
     * @param database The SQLite database object.
     * @param groupId The ID of the parent group object.
     */
    public void save(final SQLiteDatabase database, final Long groupId)
    {
        final Image thisImage = this;

        new AsyncTask<Void,Void,Void>()
        {
            protected Void doInBackground(Void... args)
            {
                ContentValues componentRow = new ContentValues();

                if (thisImage.getId() != null)
                    componentRow.put("component_id", thisImage.getId().getId());
                else
                    componentRow.putNull("component_id");
                componentRow.put("group_id", groupId);
                componentRow.put("data_type", thisImage.componentName());
                componentRow.put("label", thisImage.getLabel());
                componentRow.putNull("type_kind");
                componentRow.putNull("type_id");

                Long componentId = database.insertWithOnConflict(
                                                SheetContract.Component.TABLE_NAME,
                                                null,
                                                componentRow,
                                                SQLiteDatabase.CONFLICT_REPLACE);

                ContentValues imageComponentRow = new ContentValues();
                if (thisImage.getId() != null)
                    imageComponentRow.put("image_id", thisImage.getId().getSubId());
                else
                    imageComponentRow.putNull("image_id");
                imageComponentRow.put("component_id", componentId);
                imageComponentRow.put("image", Util.getBytes(thisImage.bitmap));

                Long imageId = database.insertWithOnConflict(
                                                SheetContract.ComponentImage.TABLE_NAME,
                                                null,
                                                imageComponentRow,
                                                SQLiteDatabase.CONFLICT_REPLACE);

                // Set ID in case of first insert and ID was Null
                thisImage.setId(new Id(componentId, imageId));

                return null;
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
    public View getDisplayView(final Context context)
    {
        // Layout
        LinearLayout imageLayout = Component.linearLayout(context);
        imageLayout.setGravity(Gravity.CENTER);
        imageLayout.setLayoutParams(com.kispoko.tome.util.Util.linearLayoutParamsMatch());

        // Views
        final ImageView imageView = this.imageView(context);
        final Button chooseImageButton = this.chooseImageButton(context);

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

                sheetActivity.setChooseImageAction(
                        new ChooseImageAction(imageView, chooseImageButton));

                sheetActivity.startActivityForResult(intent, SheetActivity.CHOOSE_IMAGE_FROM_FILE);

            }
        });

        chooseImageButton.setVisibility(View.VISIBLE);

        // Add views to layout
        imageLayout.addView(imageView);
        imageLayout.addView(chooseImageButton);

        return imageLayout;
    }


    public View getEditorView(Context context)
    {
        return new LinearLayout(context);
    }


    // > INTERNAL
    // ------------------------------------------------------------------------------------------


    private ImageView imageView(Context context)
    {
        ImageView imageView = new ImageView(context);
        imageView.setVisibility(View.GONE);
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

        button.setVisibility(View.GONE);

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
