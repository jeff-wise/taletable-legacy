
package com.kispoko.tome.activity.engine.value;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;

import lulo.ValueType;


/**
 * ValueSet RecyclerView Adapter
 */
//public class ValuesRecyclerViewAdapter
//       extends RecyclerView.Adapter<ValuesRecyclerViewAdapter.ViewHolder>
//{
//
//    // PROPERTIES
//    // -------------------------------------------------------------------------------------------
//
//    private BaseValueSet valueSet;
//
//
//    // CONSTRUCTORS
//    // -------------------------------------------------------------------------------------------
//
//    public ValuesRecyclerViewAdapter(BaseValueSet valueSet)
//    {
//        this.valueSet = valueSet;
//    }
//
//
//    // RECYCLER VIEW ADAPTER API
//    // -------------------------------------------------------------------------------------------
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
//    {
//        View itemView = ValueListItemView.view(parent.getContext());
//        return new ViewHolder(itemView, parent.getContext());
//    }
//
//
//    @Override
//    public void onBindViewHolder(ValuesRecyclerViewAdapter.ViewHolder viewHolder, int position)
//    {
//        if (this.valueSet == null)
//            return;
//
//        ValueUnion valueUnion = this.valueSet.values().get(position);
//
//        viewHolder.setValueText(valueUnion.value().valueString());
//        viewHolder.setDescriptionText(valueUnion.value().description());
//
//        ValueReference valueReference =
//                ValueReference.create(this.valueSet.name(), valueUnion.value().name());
//        viewHolder.setOnClickListener(valueUnion.type(), valueReference);
//    }
//
//
//    @Override
//    public int getItemCount()
//    {
//        if (this.valueSet != null)
//            return this.valueSet.values().size();
//
//        return 0;
//    }
//
//
//    // VIEW HOLDER
//    // -------------------------------------------------------------------------------------------
//
//    /**
//     * The View Holder caches a view for each item.
//     */
//    public class ViewHolder extends RecyclerView.ViewHolder
//    {
//
//        private Context      context;
//
//        private LinearLayout layout;
//        private TextView     valueView;
//        private TextView     descriptionView;
//
//
//        public ViewHolder(final View itemView, Context context)
//        {
//            super(itemView);
//
//            this.context = context;
//
//            this.layout = (LinearLayout) itemView.findViewById(R.id.value_list_item_layout);
//            this.valueView = (TextView) itemView.findViewById(R.id.value_list_item_value);
//            this.descriptionView =
//                    (TextView) itemView.findViewById(R.id.value_list_item_description);
//        }
//
//
//        public void setValueText(String valueText)
//        {
//            this.valueView.setText(valueText);
//        }
//
//
//        public void setDescriptionText(String text)
//        {
//            this.descriptionView.setText(text);
//        }
//
//
//        public void setOnClickListener(final ValueType valueType,
//                                       final ValueReference valueReference)
//        {
//            this.layout.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View view)
//                {
//                    switch (valueType)
//                    {
//                        case TEXT:
//                            Intent textIntent = new Intent(context, TextValueEditorActivity.class);
//                            textIntent.putExtra("value_set_name", valueReference.valueSetName());
//                            textIntent.putExtra("value_name", valueReference.valueName());
//                            context.startActivity(textIntent);
//                            break;
//                        case NUMBER:
//                            Intent numIntent = new Intent(context, TextValueEditorActivity.class);
//                            numIntent.putExtra("value_set_name", valueReference.valueSetName());
//                            numIntent.putExtra("value_name", valueReference.valueName());
//                            context.startActivity(numIntent);
//                            break;
//                    }
//                }
//            });
//        }
//
//    }
//
//}
