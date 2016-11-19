
package com.kispoko.tome.sheet.widget.text;


import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kispoko.tome.activity.EditResult;
import com.kispoko.tome.R;
import com.kispoko.tome.sheet.widget.TextWidget;
import com.kispoko.tome.rules.refinement.MemberOf;


/**
 * Recycler View Adapter for MemberOf RefinementIndex
 */
public class TextEditRecyclerViewAdapter
       extends RecyclerView.Adapter<TextEditRecyclerViewAdapter.ViewHolder>
{

    // > PROPERTIES
    // -------------------------------------------------------------------------------------------

    private TextWidget textWidget;
    private MemberOf memberOf;


    // > CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public TextEditRecyclerViewAdapter(TextWidget textWidget, MemberOf memberOf)
    {
        this.textWidget = textWidget;
        this.memberOf = memberOf;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = this.memberOf.getItemView(parent.getContext());
        return new ViewHolder(itemView, this.memberOf, this.textWidget);
    }


    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(TextEditRecyclerViewAdapter.ViewHolder viewHolder, int position)
    {
        View itemView = viewHolder.itemView;

        TextView textView = (TextView) itemView.findViewById(R.id.type_list_item_name);
        textView.setText(this.memberOf.getValue(position));

        if (this.memberOf.getValue(position).equals(this.textWidget.getValue())) {
            ImageView iconView = (ImageView) itemView.findViewById(R.id.type_list_item_icon);
            iconView.setImageDrawable(
                ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_list_item_selected));


            int selectedColor = ContextCompat.getColor(itemView.getContext(), R.color.red_medium);
            iconView.setColorFilter(selectedColor);
            textView.setTextColor(selectedColor);

        }
    }


    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return this.memberOf.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private View itemView;

        public ViewHolder(final View itemView, final MemberOf memberOf, final TextWidget textWidget)
        {
            super(itemView);
            this.itemView = itemView;

            // On Click Listener
            final Activity activity = (Activity) itemView.getContext();
            final RecyclerView.ViewHolder thisViewHolder = this;
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newValue = memberOf.getValue(thisViewHolder.getAdapterPosition());

                    // Set chosen value as result of activity and finish
                    EditResult editResult = new EditResult(EditResult.ResultType.TEXT_VALUE,
                                                           textWidget.getName(), newValue);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("RESULT", editResult);
                    activity.setResult(Activity.RESULT_OK, resultIntent);
                    activity.finish();
                }
            });

        }

    }
}

