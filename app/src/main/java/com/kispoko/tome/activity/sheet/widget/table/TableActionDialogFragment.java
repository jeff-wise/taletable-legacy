
package com.kispoko.tome.activity.sheet.widget.table;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.lib.ui.EditDialog;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.ImageViewBuilder;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.TableWidget;
import com.kispoko.tome.sheet.widget.WidgetType;
import com.kispoko.tome.sheet.widget.WidgetUnion;
import com.kispoko.tome.sheet.widget.table.cell.CellUnion;

import java.util.UUID;



/**
 * Table Action Dialog Fragment
 */
public class TableActionDialogFragment extends android.support.v4.app.DialogFragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    /**
     * Identifier of the target Table Widget
     */
    private UUID    tableWidgetId;

    /**
     * Identifier of the cell that was clicked (if a cell was clicked).
     */
    private UUID    cellId;

    /**
     * The name (human friendly label) of the clicked cell.
     */
    private String  cellName;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TableActionDialogFragment() { }


    public static TableActionDialogFragment newInstance(UUID tableWidgetId,
                                                        UUID cellId,
                                                        String cellName)
    {
        TableActionDialogFragment tableActionDialogFragment = new TableActionDialogFragment();

        Bundle args = new Bundle();
        args.putString("cell_name", cellName);
        args.putSerializable("table_widget_id", tableWidgetId);
        args.putSerializable("cell_id", cellId);
        tableActionDialogFragment.setArguments(args);

        return tableActionDialogFragment;
    }


    // DIALOG FRAGMENT
    // ------------------------------------------------------------------------------------------

    @Override @NonNull
    @SuppressWarnings("unchecked")
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        LinearLayout dialogLayout = EditDialog.layout(getContext());

        final Dialog dialog = new Dialog(getActivity());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setContentView(dialogLayout);

        int width = (int) getContext().getResources().getDimension(R.dimen.action_dialog_width);
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(width, height);

        // > Read State
        this.tableWidgetId  = (UUID) getArguments().getSerializable("table_widget_id");
        this.cellId         = (UUID) getArguments().getSerializable("cell_id");
        this.cellName       = getArguments().getString("cell_name");

        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater layoutInflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        return this.view(getContext());
    }


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    // > Open Cell Editor
    // -----------------------------------------------------------------------------------------

    private void openCellEditor(AppCompatActivity activity)
    {
        if (this.tableWidgetId != null && this.cellId != null)
        {
            // [1] Find Widget
            WidgetUnion widgetUnion = SheetManager.currentSheet().widgetWithId(this.tableWidgetId);

            if (widgetUnion.type() == WidgetType.TABLE)
            {
                // [2] Get Table Widget
                TableWidget tableWidget = widgetUnion.tableWidget();

                // [3] Get Table Cell
                CellUnion cellUnion = tableWidget.cellWithId(this.cellId);

                if (cellUnion != null)
                {
                    cellUnion.cell().openEditor(activity);
                    this.dismiss();
                }

            }
        }

    }


    // > Views
    // -----------------------------------------------------------------------------------------

    private View view(final Context context)
    {
        LinearLayout layout = this.viewLayout(context);

        // > Headers
        // -------------------------------------------------------------------------------------

        TextView cellHeader  = this.headerView(context.getString(R.string.cell), context);

        TextView rowHeader   = this.headerView(context.getString(R.string.row), context);

        TextView tableHeader = this.headerView(context.getString(R.string.table), context);

        // > Buttons
        // -------------------------------------------------------------------------------------

        // ** Cell Button
        // -------------------------------------------------------------------------------------

        String cellButtonLabel;
        if (this.cellName != null)
            cellButtonLabel = context.getString(R.string.edit) + " " + this.cellName;
        else
            cellButtonLabel = context.getString(R.string.edit_cell);

        LinearLayout cellButton = this.buttonView(R.drawable.ic_dialog_table_action_cell,
                                                  cellButtonLabel,
                                                  context);

        final AppCompatActivity activity = (AppCompatActivity) context;
        cellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCellEditor(activity);
            }
        });

        // ** Edit Row Button
        // -------------------------------------------------------------------------------------

        LinearLayout editRowButton = this.buttonView(R.drawable.ic_dialog_table_action_edit_row,
                                                     context.getString(R.string.edit_row),
                                                     context);

        // ** Add Row Before Button
        // -------------------------------------------------------------------------------------

        LinearLayout addRowBeforeButton =
                this.buttonView(R.drawable.ic_dialog_table_action_add_row_before,
                                context.getString(R.string.add_row_below),
                                context);

        // ** Add Row After Button
        // -------------------------------------------------------------------------------------

        LinearLayout addRowAfterButton =
                this.buttonView(R.drawable.ic_dialog_table_action_add_row_after,
                                context.getString(R.string.add_row_above),
                                context);

        // ** Delete Row Button
        // -------------------------------------------------------------------------------------

        LinearLayout deleteRowAfterButton =
                this.buttonView(R.drawable.ic_dialog_table_action_delete_row,
                                context.getString(R.string.delete_row),
                                context);

        // ** Edit Table Button
        // -------------------------------------------------------------------------------------

        LinearLayout editTableButton =
                this.buttonView(R.drawable.ic_dialog_table_action_table,
                                context.getString(R.string.edit_table),
                                context);

        // > Layout
        // -------------------------------------------------------------------------------------


        layout.addView(cellHeader);
        layout.addView(cellButton);

        layout.addView(rowHeader);
        layout.addView(editRowButton);
        layout.addView(addRowBeforeButton);
        layout.addView(addRowAfterButton);
        layout.addView(deleteRowAfterButton);

        layout.addView(tableHeader);
        layout.addView(editTableButton);

        return layout;
    }


    private LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT;

        layout.backgroundResource   = R.drawable.bg_dialog;
        layout.backgroundColor      = R.color.dark_blue_9;

        layout.padding.topDp        = 5f;
        layout.padding.bottomDp     = 15f;
        layout.padding.leftDp       = 10f;
        layout.padding.rightDp      = 10f;

        return layout.linearLayout(context);
    }


    private LinearLayout buttonView(int iconId, String labelString, Context context)
    {
        // [1] Declarations
        // ------------------------------------------------------------------------------------

        LinearLayoutBuilder layout = new LinearLayoutBuilder();
        ImageViewBuilder    icon   = new ImageViewBuilder();
        TextViewBuilder     label  = new TextViewBuilder();

        // [2] Layout
        // -------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.HORIZONTAL;

        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = 0;
        layout.weight               = 1f;

        layout.gravity              = Gravity.CENTER_VERTICAL;

        layout.backgroundResource   = R.drawable.bg_dialog_button;
        layout.backgroundColor      = R.color.dark_blue_11;

        layout.margin.topDp         = 3f;
        layout.margin.bottomDp      = 3f;

        layout.padding.topDp        = 12f;
        layout.padding.bottomDp     = 12f;
        layout.padding.leftDp       = 12f;
        layout.padding.rightDp      = 10f;

        layout.child(icon)
              .child(label);

        // [3 A] Icon
        // -------------------------------------------------------------------------------------

        icon.width                  = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height                 = LinearLayout.LayoutParams.WRAP_CONTENT;

        icon.image                  = iconId;
        icon.color                  = R.color.dark_blue_hl_1;

        icon.margin.rightDp         = 12f;

        // [3 B] Label
        // -------------------------------------------------------------------------------------

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT;
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        label.text                  = labelString;

        label.font                  = Font.serifFontRegular(context);
        label.color                 = R.color.dark_blue_hlx_6;
        label.sizeSp                = 17f;


        return layout.linearLayout(context);
    }


    private TextView headerView(String headerString, Context context)
    {
        TextViewBuilder header = new TextViewBuilder();

        header.width            = LinearLayout.LayoutParams.WRAP_CONTENT;
        header.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        header.text             = headerString.toUpperCase();

        header.font             = Font.serifFontBold(context);
        header.color            = R.color.dark_blue_hl_6;
        header.sizeSp           = 11f;

        header.margin.topDp     = 15f;
        header.margin.leftDp    = 2f;
        header.margin.bottomDp  = 2f;

        return header.textView(context);
    }

}
