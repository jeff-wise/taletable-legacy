
package com.kispoko.tome.activity.mechanic.quantity;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;
import com.kispoko.tome.model.game.engine.dice.DiceQuantity;

import java.util.List;



/**
 * Dice Quantity Recycler View Adapter
 */
public class DiceQuantityRecyclerViewAdapter
            extends RecyclerView.Adapter<DiceQuantityRecyclerViewAdapter.ViewHolder>
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    private List<DiceQuantity>  quantities;

    private Context             context;


    // CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public DiceQuantityRecyclerViewAdapter(List<DiceQuantity> quantities, Context context)
    {
        this.quantities = quantities;
        this.context    = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = this.itemView(parent.getContext());
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(DiceQuantityRecyclerViewAdapter.ViewHolder viewHolder,
                                 int position)
    {
        DiceQuantity diceQuantity = this.quantities.get(position);

        viewHolder.setQuantity(diceQuantity.toString());
//        viewHolder.setDescription(diceQuantity.description());

        viewHolder.setOnClick(diceQuantity, this.context);
    }


    @Override
    public int getItemCount()
    {
        return this.quantities.size();
    }


    /**
     * The View Holder caches a view for each item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder
    {

        // PROPERTIES
        // -------------------------------------------------------------------------------------

        private LinearLayout layoutView;

        private TextView     quantityView;
        private TextView     descriptionView;


        // CONSTRUCTORS
        // -------------------------------------------------------------------------------------

        public ViewHolder(final View itemView)
        {
            super(itemView);

            this.layoutView  =
                    (LinearLayout) itemView.findViewById(R.id.dice_quantity_list_item_layout);
            this.quantityView  =
                    (TextView) itemView.findViewById(R.id.dice_quantity_list_item_quantity);
            this.descriptionView =
                    (TextView) itemView.findViewById(R.id.dice_quantity_list_item_description);
        }


        // API
        // -------------------------------------------------------------------------------------

        public void setQuantity(String quantity)
        {
            this.quantityView.setText(quantity);
        }


        public void setDescription(String description)
        {
            this.descriptionView.setText(description);
        }


        public void setOnClick(final DiceQuantity diceQuantity, final Context context)
        {
            this.layoutView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(context, DiceQuantityEditorActivity.class);
//                    intent.putExtra("dice_quantity", diceQuantity);
                    context.startActivity(intent);
                }
            });
        }

    }


    // ITEM VIEW
    // -------------------------------------------------------------------------------------------

    private LinearLayout itemView(Context context)
    {
        LinearLayout layout = this.itemViewLayout(context);

        layout.addView(this.quantityView(context));
        layout.addView(this.descriptionView(context));

        return layout;
    }


    private LinearLayout itemViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.id               = R.id.dice_quantity_list_item_layout;

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.orientation      = LinearLayout.VERTICAL;

        layout.padding.leftDp   = 12f;
        layout.padding.rightDp  = 12f;
        layout.padding.topDp    = 16f;
        layout.padding.bottomDp = 16f;

        return layout.linearLayout(context);
    }


    private TextView quantityView(Context context)
    {
        TextViewBuilder quantity = new TextViewBuilder();

        quantity.id                 = R.id.dice_quantity_list_item_quantity;

        quantity.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        quantity.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        quantity.font               = Font.serifFontRegular(context);
        quantity.color              = R.color.gold_light;
        quantity.sizeSp             = 16f;

        return quantity.textView(context);
    }


    private TextView descriptionView(Context context)
    {
        TextViewBuilder quantity = new TextViewBuilder();

        quantity.id                 = R.id.dice_quantity_list_item_description;

        quantity.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        quantity.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

        quantity.font               = Font.serifFontItalic(context);
        quantity.color              = R.color.dark_theme_primary_55;
        quantity.sizeSp             = 15f;

        quantity.margin.topDp       = 10f;

        return quantity.textView(context);
    }



}
