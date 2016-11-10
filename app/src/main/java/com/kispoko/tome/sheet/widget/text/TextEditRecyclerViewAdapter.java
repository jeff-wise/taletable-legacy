
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
import com.kispoko.tome.type.ListType;



/**
 * Recycler View Adapter for ListType Types
 */
public class TextEditRecyclerViewAdapter
       extends RecyclerView.Adapter<TextEditRecyclerViewAdapter.ViewHolder>
{

    // > PROPERTIES
    // -------------------------------------------------------------------------------------------

    private TextWidget textWidget;
    private ListType listType;


    // > CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public TextEditRecyclerViewAdapter(TextWidget textWidget, ListType listType)
    {
        this.textWidget = textWidget;
        this.listType = listType;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = this.listType.getItemView(parent.getContext());
        return new ViewHolder(itemView, this.listType, this.textWidget);
    }


    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(TextEditRecyclerViewAdapter.ViewHolder viewHolder, int position)
    {
        View itemView = viewHolder.itemView;

        TextView textView = (TextView) itemView.findViewById(R.id.type_list_item_name);
        textView.setText(this.listType.getValue(position));

        if (this.listType.getValue(position).equals(this.textWidget.getValue())) {
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
        return this.listType.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private View itemView;

        public ViewHolder(final View itemView, final ListType listType, final TextWidget textWidget)
        {
            super(itemView);
            this.itemView = itemView;

            // On Click Listener
            final Activity activity = (Activity) itemView.getContext();
            final RecyclerView.ViewHolder thisViewHolder = this;
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newValue = listType.getValue(thisViewHolder.getAdapterPosition());

                    // Set chosen value as result of activity and finish
                    EditResult editResult = new EditResult(EditResult.ResultType.TEXT_VALUE,
                                                           textWidget.getId(), newValue);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("RESULT", editResult);
                    activity.setResult(Activity.RESULT_OK, resultIntent);
                    activity.finish();
                }
            });

        }

    }
}

