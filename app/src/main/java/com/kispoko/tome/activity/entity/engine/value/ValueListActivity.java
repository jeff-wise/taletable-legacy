
package com.kispoko.tome.activity.entity.engine.value;


/**
 * Value List Activity
 */
//public class ValueListActivity extends AppCompatActivity
//{
//
//    // PROPERTIES
//    // ------------------------------------------------------------------------------------------
//
////    private BaseValueSet                valueSet;
////
////    private ValuesRecyclerViewAdapter   valuesAdapter;
//
//
//    // ACTIVITY LIFECYCLE EVENTS
//    // ------------------------------------------------------------------------------------------
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_value_list);
//
//        // > Read Parameters
//        String valueSetName = null;
//        if (getIntent().hasExtra("value_set_name")) {
//            valueSetName = getIntent().getStringExtra("value_set_name");
//        }
//
//        // > Lookup ValueSet
//        // -------------------------------------------------------------------------------------
//
//        Dictionary dictionary = SheetManagerOld.dictionary();
//
//        if (dictionary != null)
//        {
//            ValueSetUnion valueSetUnion = dictionary.lookup(valueSetName);
//            if (valueSetUnion.type() == ValueSetType.BASE)
//                this.valueSet = valueSetUnion.base();
//        }
//
//        initializeToolbar();
//
//        initializeView();
//    }
//
//
//    @Override
//    public void onBackPressed()
//    {
//        super.onBackPressed();
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.empty, menu);
//        return true;
//    }
//
//
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus)
//    {
//        super.onWindowFocusChanged(hasFocus);
//
//        if (this.valuesAdapter != null) {
//            this.valueSet.sortAscByLabel();
//            this.valuesAdapter.notifyDataSetChanged();
//        }
//    }
//
//
//    // INTERNAL
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Initialize the toolbar.
//     */
//    private void initializeToolbar()
//    {
//        // > Initalize the toolbar
//        // -------------------------------------------------------------------------------------
//        UI.initializeToolbar(this, getString(R.string.values_editor));
//
//        // > Set the Value Set Name
//        // -------------------------------------------------------------------------------------
//        TextView nameView = (TextView) findViewById(R.id.value_set_name);
//        nameView.setTypeface(Font.serifFontBold(this));
//
//        if (this.valueSet != null)
//            nameView.setText(this.valueSet.label());
//        else
//            nameView.setText(R.string.not_available);
//
//    }
//
//
//    private void initializeView()
//    {
//        // [1] Initalize RecyclerView
//        // -------------------------------------------------------------------------------------
//
//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.value_list_view);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        SimpleDividerItemDecoration dividerItemDecoration =
//                new SimpleDividerItemDecoration(this, R.color.dark_theme_primary_85);
//        recyclerView.addItemDecoration(dividerItemDecoration);
//
//        if (this.valueSet != null)
//        {
//            this.valueSet.sortAscByLabel();
//            this.valuesAdapter = new ValuesRecyclerViewAdapter(this.valueSet);
//            recyclerView.setAdapter(this.valuesAdapter);
//        }
//
//        // [2] Initalize Floating Action Button
//        // -------------------------------------------------------------------------------------
//        FloatingActionButton addNewValueButton =
//                (FloatingActionButton) findViewById(R.id.button_new_value);
//
//        final ValueListActivity valueListActivity = this;
//        addNewValueButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                switch (valueSet.valueType())
//                {
//                    case TEXT:
//                        Intent textIntent = new Intent(ValueListActivity.this,
//                                                       TextValueEditorActivity.class);
//                        textIntent.putExtra("value_set_name", valueSet.name());
//                        valueListActivity.startActivity(textIntent);
//                        break;
//                    case NUMBER:
//                        Intent numberIntent = new Intent(ValueListActivity.this,
//                                                         NumberValueEditorActivity.class);
//                        numberIntent.putExtra("value_set_name", valueSet.name());
//                        valueListActivity.startActivity(numberIntent);
//                        break;
//                }
//            }
//        });
//    }
//
//}
