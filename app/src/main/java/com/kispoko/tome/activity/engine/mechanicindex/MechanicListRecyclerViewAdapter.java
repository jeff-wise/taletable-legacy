
package com.kispoko.tome.activity.engine.mechanicindex;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.engine.mechanic.MechanicActivity;
import com.kispoko.tome.activity.sheet.widget.dialog.ChooseValueDialogFragment;
import com.kispoko.tome.engine.mechanic.Mechanic;
import com.kispoko.tome.engine.mechanic.MechanicIndex;
import com.kispoko.tome.sheet.SheetManager;

import java.util.List;

import static com.kispoko.tome.R.string.mechanic;


/**
 * Mechanic List Recycler View Adapter
 */
public class MechanicListRecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    private List<Object>    items;

    private Context         context;


    private final int HEADER_VIEW = 0;
    private final int MECHANIC_VIEW = 1;


    // CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public MechanicListRecyclerViewAdapter(List<Object> items, Context context)
    {
        this.items      = items;
        this.context    = context;
    }


    // RECYCLER VIEW API
    // -------------------------------------------------------------------------------------------

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case HEADER_VIEW:
                View headerView = MechanicListItemView.header(parent.getContext());
                return new HeaderViewHolder(headerView);
            case MECHANIC_VIEW:
                View mechanicView = MechanicListItemView.mechanic(parent.getContext());
                return new MechanicViewHolder(mechanicView);
        }

        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        Object item = this.items.get(position);

        if (item instanceof String)
        {
            String category = (String) item;

            HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
            headerViewHolder.setCategory(category);
        }
        else if (item instanceof Mechanic)
        {
            Mechanic mechanic = (Mechanic) item;

            MechanicViewHolder mechanicViewHolder = (MechanicViewHolder) viewHolder;
            mechanicViewHolder.setName(mechanic.label());
            mechanicViewHolder.setSummary(mechanic.summary());
            mechanicViewHolder.setOnClick(mechanic.name(), this.context);

            MechanicIndex mechanicIndex = SheetManager.mechanicIndex();
            if (mechanicIndex != null)
            {
                boolean isActive = mechanicIndex.mechanicIsActive(mechanic.name());

                if (isActive)
                    mechanicViewHolder.setActive();
            }
        }
    }


    // The number of value sets to display
    @Override
    public int getItemCount()
    {
        return this.items.size();
    }


    //Returns the view type of the item at position for the purposes of view recycling.
    @Override
    public int getItemViewType(int position)
    {
        Object item = items.get(position);

        if (item instanceof String)
            return HEADER_VIEW;
        else if (item instanceof Mechanic)
            return MECHANIC_VIEW;

        return -1;
    }


    // HEADER VIEW HOLDER
    // -----------------------------------------------------------------------------------------

    /**
     * The View Holder caches a view for each item.
     */
    public class HeaderViewHolder extends RecyclerView.ViewHolder
    {

        // PROPERTIES
        // -----------------------------------------------------------------------------------------

        private TextView categoryView;


        // CONSTRUCTORS
        // -----------------------------------------------------------------------------------------

        public HeaderViewHolder(final View itemView)
        {
            super(itemView);

            this.categoryView = (TextView) itemView.findViewById(R.id.mechanic_category_header);
        }


        // API
        // -----------------------------------------------------------------------------------------

        public void setCategory(String category)
        {
            this.categoryView.setText(category);
       }

    }


    // MECHANIC VIEW HOLDER
    // -------------------------------------------------------------------------------------------

    /**
     * The View Holder caches a view for each item.
     */
    public class MechanicViewHolder extends RecyclerView.ViewHolder
    {

        private LinearLayout    layoutView;

        private TextView        nameView;
        private TextView        statusView;
        private TextView        summaryView;


        public MechanicViewHolder(final View itemView)
        {
            super(itemView);

            this.layoutView = (LinearLayout) itemView.findViewById(R.id.mechanic_list_item_layout);

            this.nameView = (TextView) itemView.findViewById(R.id.mechanic_name);
            this.statusView = (TextView) itemView.findViewById(R.id.mechanic_status);
            this.summaryView = (TextView) itemView.findViewById(R.id.mechanic_summary);
        }


        public void setName(String name)
        {
            this.nameView.setText(name);
        }


        public void setActive()
        {
            this.statusView.setVisibility(View.VISIBLE);
        }


        public void setSummary(String summary)
        {
            if (summary != null)
                this.summaryView.setText(summary);
            else
                this.summaryView.setVisibility(View.GONE);
        }


        public void setOnClick(final String mechanicName, final Context context)
        {
            this.layoutView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(context, MechanicActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("mechanic_name", mechanicName);
                    intent.putExtras(bundle);

                    context.startActivity(intent);
                }
            });
        }

    }

}
