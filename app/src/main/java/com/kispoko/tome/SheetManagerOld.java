
package com.kispoko.tome;


/**
 * Sheet Manager
 */
//public class SheetManagerOld
//{
//
//    // PROPERTIES
//    // ------------------------------------------------------------------------------------------
//
//    // > Sheet State
//    private static ModelFunctor<Sheet> currentSheet;
//
//    private static Context             currentSheetContext;
//
//    //private static Sheet.OnSheetListener sheetListener;
//
//
//    private static boolean sheetReady           = false;
//    private static boolean campaignIndexReady   = false;
//
//
//    // API
//    // ------------------------------------------------------------------------------------------
//
//    public static Sheet currentSheet()
//    {
//        return currentSheet.getValue();
//    }
//
//
//    public static Context currentSheetContext()
//    {
//        return currentSheetContext;
//    }
//
//
//    public static void campaignIndexReady()
//    {
//        campaignIndexReady = true;
//
//        if (campaignIndexReady && sheetReady)
//            initializeSheet();
//    }
//
//
//    public static void sheetReady()
//    {
//        sheetReady = true;
//
//        if (campaignIndexReady && sheetReady)
//            initializeSheet();
//    }
//
//
//    private static void initializeSheet()
//    {
//        currentSheet().initialize(currentSheetContext());
//
//        sheetListener.onSheet(currentSheet());
//    }
//
//
//    /**
//     * Create a sheet from a sheet template file.
//     * @param sheetListener The listener for the new sheet.
//     * @param context The context object, for looking up the assets.
//     * @param templateId The ID of the template yaml file to load.
//     */
//    public static void goToTemplate(final Sheet.OnSheetListener sheetListener,
//                                    String templateId,
//                                    final Context context)
//    {
//        final String templateFileName = "template/" + templateId + ".yaml";
//
//        Log.d("***SHEETMANAGER", "template file name " + templateFileName);
//
//        new AsyncTask<Void,Void,Object>()
//        {
//
//            protected Object doInBackground(Void... args)
//            {
//                Sheet sheet;
//                try
//                {
//                    InputStream yamlIS = context.getAssets().open(templateFileName);
//                    YamlParser yaml = YamlParser.fromFile(yamlIS);
//                    sheet = Sheet.fromYaml(yaml);
//                }
//                catch (YamlParseException exception)
//                {
//                    return exception;
//                }
//                catch (IOException exception)
//                {
//                    return new TemplateFileException(
//                                new TemplateFileReadError(templateFileName),
//                            TemplateFileException.ErrorType.TEMPLATE_FILE_READ);
//                }
//                catch (Exception exception)
//                {
//                    return exception;
//                }
//
//                return sheet;
//            }
//
//            protected void onPostExecute(Object maybeSheet)
//            {
//                if (maybeSheet instanceof TemplateFileException)
//                {
//                    ApplicationFailure.templateFile((TemplateFileException) maybeSheet);
//                }
//                else if (maybeSheet instanceof YamlParseException)
//                {
//                    ApplicationFailure.yaml((YamlParseException) maybeSheet);
//                }
//                else if (maybeSheet instanceof Exception)
//                {
//                    Log.d("***SHEETMANAGER", "template load exception", (Exception) maybeSheet);
//                }
//                else if (maybeSheet instanceof Sheet)
//                {
//                    Sheet templateSheet = (Sheet) maybeSheet;
//
//                    currentSheet = ModelFunctor.full(templateSheet, Sheet.class);
//                    currentSheetContext = context;
//
//                    SheetManagerOld.sheetListener = sheetListener;
//
//                    SheetManagerOld.sheetReady();
//
//                    ModelFunctor.OnSaveListener onSaveListener = new ModelFunctor.OnSaveListener()
//                    {
//                        @Override
//                        public void onSave()
//                        {
//                        }
//
//                        @Override
//                        public void onSaveDBError(DatabaseException exception)
//                        {
//                            ApplicationFailure.database(exception);
//                        }
//
//                        @Override
//                        public void onSaveError(Exception exception)
//                        {
//                            Log.d("***SHEET MANAGER", "other exception", exception);
//                        }
//                    };
//
//                    currentSheet.setOnSaveListener(onSaveListener);
//                    currentSheet.saveAsync();
//                }
//            }
//
//        }.execute();
//    }
//
//
//    public static void goToMostRecent(final Sheet.OnSheetListener listener, Context context)
//    {
//        ModelFunctor.OnLoadListener<Sheet> onLoadListener = new ModelFunctor.OnLoadListener<Sheet>()
//        {
//            @Override
//            public void onLoad(Sheet value)
//            {
//                Log.d("***SHEET MANAGER", "on load sheet");
//
//                // value.initialize();
//
//                SheetManagerOld.sheetListener = listener;
//
//                SheetManagerOld.sheetReady();
//
//                listener.onSheet(value);
//            }
//
//            @Override
//            public void onLoadDBError(DatabaseException exception)
//            {
//                ApplicationFailure.database(exception);
//            }
//
//            @Override
//            public void onLoadError(Exception exception)
//            {
//                Log.d("***SHEET MANAGER", "other exception", exception);
//            }
//        };
//
//        currentSheet        = ModelFunctor.empty(Sheet.class);
//        currentSheet.setOnLoadListener(onLoadListener);
//        currentSheetContext = context;
//
//        // Construct query
//        List<OrderBy.Field> fields = new ArrayList<>();
//        // TODO make this derived, not hardcoded
//        fields.add(new OrderBy.Field("lastused", Function.DATETIME));
//        OrderBy orderBy = new OrderBy(fields, OrderBy.Order.DESC);
//
//        ModelQueryParameters.TopResult topResultQuery =
//                new ModelQueryParameters.TopResult(orderBy);
//        ModelQueryParameters queryParameters =
//                new ModelQueryParameters(topResultQuery, ModelQueryParameters.Type.TOP_RESULT);
//
//        currentSheet.loadAsync(queryParameters);
//    }
//
//
//    // > Sheet State
//    // ------------------------------------------------------------------------------------------
//
//    public static Dictionary dictionary()
//    {
//        if (currentSheet() != null) {
//            Engine engine = currentSheet().engine();
//            if (engine != null) {
//                return engine.dictionary();
//            }
//        }
//
//        return null;
//    }
//
//
//    public static MechanicIndex mechanicIndex()
//    {
//        if (currentSheet() != null) {
//            Engine engine = currentSheet().engine();
//            if (engine != null) {
//                return engine.mechanicIndex();
//            }
//        }
//
//        return null;
//    }
//
//
//    public static ProgramIndex programIndex()
//    {
//        if (currentSheet() != null) {
//            Engine engine = currentSheet().engine();
//            if (engine != null) {
//                return engine.programIndex();
//            }
//        }
//
//        return null;
//    }
//}
