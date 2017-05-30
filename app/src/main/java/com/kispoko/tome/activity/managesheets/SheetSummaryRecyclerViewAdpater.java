
package com.kispoko.tome.activity.managesheets;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;


/**
 * Sheet Summary Recycler View Adapter
 */
//public class SheetSummaryRecyclerViewAdpater
//       extends RecyclerView.Adapter<SheetSummaryRecyclerViewAdpater.ViewHolder>
//{
//
//
//    // PROPERTIES
//    // -------------------------------------------------------------------------------------------
//
//    //private List<Summary>   summaryList;
//
//    private Context         context;
//
//
//    // CONSTRUCTORS
//    // -------------------------------------------------------------------------------------------
//
//    public SheetSummaryRecyclerViewAdpater(List<Summary> summaryList, Context context)
//    {
//        this.summaryList = summaryList;
//        this.context     = context;
//    }
//
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
//    {
//        View itemView = SummaryListItemView.view(parent.getContext());
//        return new ViewHolder(itemView);
//    }
//
//
//    @Override
//    public void onBindViewHolder(SheetSummaryRecyclerViewAdpater.ViewHolder viewHolder,
//                                 int position)
//    {
//        Summary summary = this.summaryList.get(position);
//
//        // > Name
//        viewHolder.setSheetName(summary.sheetName());
//
//        // > Date
//        viewHolder.setDate(summary.lastUsed());
////
////        // > On Click Listener
////        viewHolder.setOnClick(widgetUnion, this.context);
//    }
//
//
//    // The number of value sets to display
//    @Override
//    public int getItemCount()
//    {
//        return this.summaryList.size();
//    }
//
//
//    /**
//     * The View Holder caches a view for each item.
//     */
//    public class ViewHolder extends RecyclerView.ViewHolder
//    {
//
//        private LinearLayout    layoutView;
//
//        private TextView        nameView;
//        private TextView        dateView;
//
//
//        public ViewHolder(final View itemView)
//        {
//            super(itemView);
//
//            this.layoutView =
//                    (LinearLayout) itemView.findViewById(R.id.sheet_summary_list_item_layout);
//
//            this.dateView = (TextView) itemView.findViewById(R.id.sheet_summary_list_item_date);
//
//            this.nameView = (TextView) itemView.findViewById(R.id.sheet_summary_list_item_name);
//        }
//
//
//        public void setSheetName(String name)
//        {
//            this.nameView.setText(name);
//        }
//
//
//        public void setDate(Long ms)
//        {
//            // TODO locale?
//            SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
//            df.setTimeZone(TimeZone.getTimeZone("GMT"));
//            String dateString = df.format(ms);
//
//            this.dateView.setText(dateString);
//        }
//
//
//        public void setOnClick(final Summary summary, final Context context)
//        {
//            this.layoutView.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View view)
//                {
//                }
//            });
//        }
//
//    }
//
//
//}
