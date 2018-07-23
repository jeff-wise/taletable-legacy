
package com.taletable.android.activity.sheet.grouprow;


/**
 * Widget List Recycler View Adapter
 */
//public class WidgetListRecyclerViewAdapter
//       extends RecyclerView.Adapter<WidgetListRecyclerViewAdapter.ViewHolder>
//{
//
//    // PROPERTIES
//    // -------------------------------------------------------------------------------------------
//
//    //private List<WidgetUnion>   widgetList;
//
//    private Context             context;
//
//
//    // CONSTRUCTORS
//    // -------------------------------------------------------------------------------------------
//
//    public WidgetListRecyclerViewAdapter(List<WidgetUnion> widgetList, Context context)
//    {
//        this.widgetList = widgetList;
//        this.context    = context;
//    }
//
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
//    {
//        View itemView = WidgetListItemView.view(parent.getContext());
//        return new ViewHolder(itemView);
//    }
//
//
//    @Override
//    public void onBindViewHolder(WidgetListRecyclerViewAdapter.ViewHolder viewHolder,
//                                 int position)
//    {
//        WidgetUnion widgetUnion = this.widgetList.get(position);
//
//        // > Name
//        String label = widgetUnion.widget().data().format().label();
//        if (label == null)
//            label = "No Label";
//        viewHolder.setName(label);
//
//        // > Type
//        viewHolder.setType(widgetUnion.type().name());
//
//        // > On Click Listener
//        viewHolder.setOnClick(widgetUnion, this.context);
//    }
//
//
//    // The number of value sets to display
//    @Override
//    public int getItemCount()
//    {
//        return this.widgetList.size();
//    }
//
//
//    /**
//     * The View Holder caches a view for each item.
//     */
//    public class ViewHolder extends RecyclerView.ViewHolder
//    {
//
//        private RelativeLayout  layoutView;
//        private TextView        nameView;
//        private TextView        typeView;
//
//
//        public ViewHolder(final View itemView)
//        {
//            super(itemView);
//
//            this.layoutView = (RelativeLayout) itemView.findViewById(R.id.draggable_card_layout);
//            this.nameView = (TextView) itemView.findViewById(R.id.draggable_card_title);
//            this.typeView = (TextView) itemView.findViewById(R.id.widget_list_item_type);
//        }
//
//
//        public void setName(String name)
//        {
//            this.nameView.setText(name);
//        }
//
//
//        public void setType(String type)
//        {
//            this.typeView.setText(type);
//        }
//
//
//        public void setOnClick(final WidgetUnion widgetUnion, final Context context)
//        {
//            this.layoutView.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View view)
//                {
//                    switch (widgetUnion.type())
//                    {
//                        case ACTION:
//                            openWidgetActivity(ActionWidgetActivity.class,
//                                               widgetUnion.actionWidget());
//                            break;
//                        case BOOLEAN:
//                            openWidgetActivity(BooleanWidgetActivity.class,
//                                               widgetUnion.booleanWidget());
//                            break;
//                        case IMAGE:
//                            openWidgetActivity(ImageWidgetActivity.class,
//                                               widgetUnion.imageWidget());
//                            break;
//                        case NUMBER:
//                            openWidgetActivity(NumberWidgetActivity.class,
//                                               widgetUnion.numberWidget());
//                            break;
//                        case TABLE:
//                            openWidgetActivity(TableWidgetActivity.class,
//                                               widgetUnion.tableWidget());
//                            break;
//                        case TEXT:
//                            openWidgetActivity(TextWidgetActivity.class,
//                                               widgetUnion.textWidget());
//                            break;
//                        default:
//                            ApplicationFailure.union(
//                                    UnionException.unknownVariant(
//                                            new UnknownVariantError(WidgetType.class.getName())));
//                    }
//                }
//            });
//        }
//
//
//        private void openWidgetActivity(Class<?> widgetActivityClass, Serializable widget)
//        {
//            Intent intent = new Intent(context, widgetActivityClass);
//
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("widget", widget);
//            intent.putExtras(bundle);
//
//            context.startActivity(intent);
//        }
//
//    }
//
//
//
//}
