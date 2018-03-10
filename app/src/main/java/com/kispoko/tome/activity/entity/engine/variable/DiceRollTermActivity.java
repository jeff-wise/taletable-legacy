
package com.kispoko.tome.activity.entity.engine.variable;


/**
 * Dice Roll Term Activity
 */
//public class DiceRollTermActivity extends AppCompatActivity
//{
//
//    // PROPERTEIS
//    // -----------------------------------------------------------------------------------------
//
////    private DiceRollTerm        diceRollTerm;
//
//    // > Form
//    // -----------------------------------------------------------------------------------------
//
//    private Map<String,Field>   fieldByName;
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
//        // [1] Set activity view
//        // --------------------------------------------------------------------------------------
//
//        setContentView(R.layout.activity_form_basic);
//
//        // [2] Read parameters
//        // --------------------------------------------------------------------------------------
//
//        this.diceRollTerm = null;
//        if (getIntent().hasExtra("dice_roll_term")) {
//            this.diceRollTerm = (DiceRollTerm) getIntent().getSerializableExtra("dice_roll_term");
//        }
//
//        // [3] Initialize UI components
//        // -------------------------------------------------------------------------------------
//
//        this.initializeToolbar();
//        this.initializeData();
//        this.initializeView();
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
//    // UI
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * Initialize the toolbar.
//     */
//    private void initializeToolbar()
//    {
//        UI.initializeToolbar(this, getString(R.string.dice_roll_term_editor));
//    }
//
//
//    /**
//     * Initialize the template list view.
//     */
//    private void initializeView()
//    {
//        ScrollView scrollView = (ScrollView) findViewById(R.id.content);
//        scrollView.addView(this.view(this));
//    }
//
//
//    private void initializeData()
//    {
//        // [1] Initialize indexes
//        // -------------------------------------------------------------------------------------
//
//        this.fieldByName = new HashMap<>();
//
//        // [2] Get & Index Fields
//        // -------------------------------------------------------------------------------------
//
//        if (this.diceRollTerm == null || this.diceRollTerm.termValue() == null)
//            return;
//
//        Collection<Field> fields = new ArrayList<>();
//
//        // GENERATE fields from Value Set
//        try {
//            fields.addAll(ProdType.fields(this.diceRollTerm, this));
//            fields.addAll(ProdType.fields(this.diceRollTerm.termValue(), this));
//        }
//        catch (FunctorException exception) {
//            ApplicationFailure.functor(exception);
//        }
//
//        // INDEX fields by name
//        for (Field field : fields) {
//            this.fieldByName.put(field.name(), field);
//        }
//    }
//
//
//    // VIEWS
//    // ------------------------------------------------------------------------------------------
//
//    private LinearLayout view(Context context)
//    {
//        LinearLayout layout = this.viewLayout(context);
//
//        // > Toolbar
//        layout.addView(Form.toolbarView(context));
//
//        // > Form
//        layout.addView(this.formView(context));
//
//        return layout;
//    }
//
//
//    private LinearLayout viewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation          = LinearLayout.VERTICAL;
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.backgroundColor      = R.color.dark_theme_primary_84;
//
//        return layout.linearLayout(context);
//    }
//
//
//    private LinearLayout formView(Context context)
//    {
//        LinearLayout layout = Form.layout(context);
//
//        // > Form Structure
//        // -------------------------------------------------------------------------------------
//
//        layout.addView(Form.headerView("Properties", context));
//
//        this.addFieldView("value_name", layout);
//        layout.addView(Form.dividerView(context));
//        this.addFieldView("type", layout);
//
//        // > Click Events
//        // -------------------------------------------------------------------------------------
//
//        this.setValueListeners();
//
//
//        return layout;
//    }
//
//
//    private void setValueListeners()
//    {
//        Field typeField = this.fieldByName.get("type");
//
//        if (typeField == null)
//            return;
//
//        // > Variable Reference
//        typeField.setCaseOnClickListener("variable", new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                Intent intent = new Intent(DiceRollTermActivity.this,
//                                           VariableReferenceActivity.class);
//
//                if (diceRollTerm != null &&
//                    diceRollTerm.termValue() != null &&
//                    diceRollTerm.termValue().variableReference() != null)
//                {
//                    intent.putExtra("variable_reference",
//                                    diceRollTerm.termValue().variableReference());
//                }
//
//                startActivity(intent);
//            }
//        });
//    }
//
//
//    private void addFieldView(String fieldName, LinearLayout layout)
//    {
//        Field field = this.fieldByName.get(fieldName);
//
//        if (field != null) {
//            LinearLayout fieldView = field.view(this);
//            layout.addView(fieldView);
//        }
//    }
//
//
//}
