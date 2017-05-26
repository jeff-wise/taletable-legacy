
package com.kispoko.tome.activity.sheet.widget.table;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.activity.BooleanColumnActivity;
import com.kispoko.tome.activity.NumberColumnActivity;
import com.kispoko.tome.activity.TextColumnActivity;
import com.kispoko.tome.activity.sheet.grouprow.WidgetListItemView;
import com.kispoko.tome.error.UnknownVariantError;
import com.kispoko.tome.exception.UnionException;

import java.io.Serializable;
import java.util.List;



/**
 * Column List Recyler View Adapter
 */
public class ColumnListRecyclerViewAdapter
       extends RecyclerView.Adapter<ColumnListRecyclerViewAdapter.ViewHolder>
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    private List<ColumnUnion> columnUnions;

    private Context           context;


    // CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public ColumnListRecyclerViewAdapter(List<ColumnUnion> columnUnions, Context context)
    {
        this.columnUnions   = columnUnions;
        this.context        = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = WidgetListItemView.view(parent.getContext());
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ColumnListRecyclerViewAdapter.ViewHolder viewHolder,
                                 int position)
    {
        ColumnUnion columnUnion = this.columnUnions.get(position);

        // > Name
//        String label = widgetUnion.widget().data().format().label();
//        if (label == null)
//            label = "No Label";
//        viewHolder.setName(label);

        // > Type
        //viewHolder.setType(widgetUnion.type().name());

        // > On Click Listener
        //viewHolder.setOnClick(widgetUnion, this.context);
    }


    @Override
    public int getItemCount()
    {
        return this.columnUnions.size();
    }


    /**
     * The View Holder caches a view for each item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private RelativeLayout  layoutView;
        private TextView        nameView;
        private TextView        typeView;


        public ViewHolder(final View itemView)
        {
            super(itemView);

            this.layoutView = (RelativeLayout) itemView.findViewById(R.id.draggable_card_layout);
            this.nameView = (TextView) itemView.findViewById(R.id.draggable_card_title);
            this.typeView = (TextView) itemView.findViewById(R.id.column_list_item_type);
        }


        public void setName(String name)
        {
            this.nameView.setText(name);
        }


        public void setType(String type)
        {
            this.typeView.setText(type);
        }


        public void setOnClick(final ColumnUnion columnUnion)
        {
            this.layoutView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    switch (columnUnion.type())
                    {
                        case TEXT:
                            openColumnActivity(TextColumnActivity.class,
                                               columnUnion.textColumn());
                            break;
                        case NUMBER:
                            openColumnActivity(NumberColumnActivity.class,
                                               columnUnion.numberColumn());
                            break;
                        case BOOLEAN:
                            openColumnActivity(BooleanColumnActivity.class,
                                               columnUnion.booleanColumn());
                            break;
                        default:
                            ApplicationFailure.union(
                                    UnionException.unknownVariant(
                                            new UnknownVariantError(ColumnType.class.getName())));
                    }
                }
            });
        }


        private void openColumnActivity(Class<?> columnActivityClass, Serializable column)
        {
            Intent intent = new Intent(context, columnActivityClass);

            Bundle bundle = new Bundle();
            bundle.putSerializable("column", column);
            intent.putExtras(bundle);

            context.startActivity(intent);
        }

    }



}
