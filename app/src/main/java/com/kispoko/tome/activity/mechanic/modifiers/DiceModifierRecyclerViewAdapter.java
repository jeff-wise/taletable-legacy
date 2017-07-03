
package com.kispoko.tome.activity.mechanic.modifiers;


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
import com.kispoko.tome.model.game.engine.dice.RollModifier;

import java.util.List;



/**
 * Dice Modifier Recycler View Adapter
 */
public class DiceModifierRecyclerViewAdapter
        extends RecyclerView.Adapter<DiceModifierRecyclerViewAdapter.ViewHolder>
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    private List<RollModifier>  modifierList;

    private Context             context;


    // CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public DiceModifierRecyclerViewAdapter(List<RollModifier> modifiers, Context context)
    {
        this.modifierList   = modifiers;
        this.context        = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = this.itemView(parent.getContext());
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(DiceModifierRecyclerViewAdapter.ViewHolder viewHolder,
                                 int position)
    {
        RollModifier rollModifier = this.modifierList.get(position);
//
//        viewHolder.setModifier(rollModifier.valuePlusString());
//        viewHolder.setName(rollModifier.name());

        viewHolder.setOnClick(rollModifier, this.context);
    }


    @Override
    public int getItemCount()
    {
        return this.modifierList.size();
    }


    /**
     * The View Holder caches a view for each item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder
    {

        // PROPERTIES
        // -------------------------------------------------------------------------------------

        private LinearLayout layoutView;

        private TextView     modifierView;
        private TextView     nameView;


        // CONSTRUCTORS
        // -------------------------------------------------------------------------------------

        public ViewHolder(final View itemView)
        {
            super(itemView);

            this.layoutView  =
                    (LinearLayout) itemView.findViewById(R.id.roll_modifier_list_item_layout);
            this.modifierView  =
                    (TextView) itemView.findViewById(R.id.roll_modifier_list_item_modifier);
            this.nameView =
                    (TextView) itemView.findViewById(R.id.roll_modifier_list_item_name);
        }


        // API
        // -------------------------------------------------------------------------------------

        public void setModifier(String modifier)
        {
            this.modifierView.setText(modifier);
        }


        public void setName(String name)
        {
            this.nameView.setText(name);
        }


        public void setOnClick(final RollModifier rollModifier, final Context context)
        {
            this.layoutView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(context, RollModifierEditorActivity.class);
//                    intent.putExtra("roll_modifier", rollModifier);
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

        layout.addView(this.modifierView(context));
        layout.addView(this.nameView(context));

        return layout;
    }


    private LinearLayout itemViewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.id               = R.id.roll_modifier_list_item_layout;

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.orientation      = LinearLayout.VERTICAL;

        layout.padding.leftDp   = 12f;
        layout.padding.rightDp  = 12f;
        layout.padding.topDp    = 16f;
        layout.padding.bottomDp = 16f;

        return layout.linearLayout(context);
    }


    private TextView modifierView(Context context)
    {
        TextViewBuilder quantity = new TextViewBuilder();

        quantity.id                 = R.id.roll_modifier_list_item_modifier;

        quantity.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        quantity.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

//        quantity.font               = Font.serifFontRegular(context);
        quantity.color              = R.color.gold_light;
        quantity.sizeSp             = 16f;

        return quantity.textView(context);
    }


    private TextView nameView(Context context)
    {
        TextViewBuilder quantity = new TextViewBuilder();

        quantity.id                 = R.id.roll_modifier_list_item_name;

        quantity.width              = LinearLayout.LayoutParams.WRAP_CONTENT;
        quantity.height             = LinearLayout.LayoutParams.WRAP_CONTENT;

//        quantity.font               = Font.serifFontItalic(context);
        quantity.color              = R.color.dark_theme_primary_55;
        quantity.sizeSp             = 15f;

        quantity.margin.topDp       = 10f;

        return quantity.textView(context);
    }


}
