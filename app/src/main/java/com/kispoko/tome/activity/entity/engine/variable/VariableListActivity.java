
package com.kispoko.tome.activity.entity.engine.variable;


/**
 * Variable List Activity
 */
//public class VariableListActivity extends AppCompatActivity
//{
//
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    private List<VariableUnion>             variableList;
//
//    private VariableListRecyclerViewAdapter recyclerViewAdapter;
//
//
//    // ACTIVITY LIFECYCLE EVENTS
//    // -----------------------------------------------------------------------------------------
//
//    @Override
//    @SuppressWarnings("unchecked")
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//
//        // [1] Set Content View
//        // -------------------------------------------------------------------------------------
//
//        setContentView(R.layout.activity_variable_list);
//
//        // [2] Get Parameters
//        // -------------------------------------------------------------------------------------
//
//        this.variableList = new ArrayList<>();
//        if (getIntent().hasExtra("variables")) {
//            this.variableList = (List<VariableUnion>) getIntent().getSerializableExtra("variables");
//        }
//
//        // [3] Initialize UI
//        // -------------------------------------------------------------------------------------
//
//        initializeToolbar();
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
////        if (this.valuesAdapter != null) {
////            this.valueSet.sortAscByLabel();
////            this.valuesAdapter.notifyDataSetChanged();
////        }
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
//        UI.initializeToolbar(this, getString(R.string.variable_list_editor));
//    }
//
//
//    private void initializeView()
//    {
//        // [1] Initalize RecyclerView
//        // -------------------------------------------------------------------------------------
//
//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.variable_list_view);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        SimpleDividerItemDecoration dividerItemDecoration =
//                        new SimpleDividerItemDecoration(this, R.color.dark_theme_primary_86);
//        recyclerView.addItemDecoration(dividerItemDecoration);
//
//        this.recyclerViewAdapter = new VariableListRecyclerViewAdapter(this.variableList, this);
//        recyclerView.setAdapter(this.recyclerViewAdapter);
//
//        // [2] Initalize Floating Action Button
//        // -------------------------------------------------------------------------------------
////        FloatingActionButton addNewValueButton =
////                (FloatingActionButton) findViewById(R.id.button_new_variable);
//    }
//
//}
