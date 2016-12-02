
package com.kispoko.tome.sheet;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.error.TemplateFileReadError;
import com.kispoko.tome.exception.TemplateFileException;
import com.kispoko.tome.rules.programming.variable.Variable;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.query.ModelQueryParameters;
import com.kispoko.tome.util.database.sql.Function;
import com.kispoko.tome.util.database.sql.OrderBy;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.value;


/**
 * Sheet Manager
 */
public class SheetManager
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Sheet State
    private static ModelValue<Sheet> currentSheet;

    private static Context           currentSheetContext;


    // > Data Queues
    private static List<Variable>    variableQueue;


    // API
    // ------------------------------------------------------------------------------------------

    public static Sheet currentSheet()
    {
        return currentSheet.getValue();
    }


    public static Context currentSheetContext()
    {
        return currentSheetContext;
    }


    /**
     * Create a sheet from a sheet template file.
     * @param sheetListener The listener for the new sheet.
     * @param context The context object, for looking up the assets.
     * @param templateId The ID of the template yaml file to load.
     */
    public static void goToTemplate(final Sheet.OnSheetListener sheetListener,
                                    String templateId,
                                    final Context context)
    {
        final String templateFileName = "template/" + templateId + ".yaml";

        new AsyncTask<Void,Void,Object>()
        {

            protected Object doInBackground(Void... args)
            {
                Sheet sheet;
                try {
                    InputStream yamlIS = context.getAssets().open(templateFileName);
                    Yaml yaml = Yaml.fromFile(yamlIS);
                    sheet = Sheet.fromYaml(yaml);
                } catch (IOException e) {
                    return new TemplateFileException(
                                new TemplateFileReadError(templateFileName),
                            TemplateFileException.ErrorType.TEMPLATE_FILE_READ);
                } catch (YamlException e) {
                    return e;
                }

                return sheet;
            }

            protected void onPostExecute(Object maybeSheet)
            {
                if (maybeSheet instanceof TemplateFileException)
                {
                    ApplicationFailure.templateFile((TemplateFileException) maybeSheet);
                }
                else if (maybeSheet instanceof YamlException)
                {
                    ApplicationFailure.yaml((YamlException) maybeSheet);
                }
                else if (maybeSheet instanceof Sheet)
                {
                    Sheet templateSheet = (Sheet) maybeSheet;

                    currentSheet = ModelValue.full(templateSheet, Sheet.class);
                    addVariablesToSheet(currentSheet.getValue());
                    currentSheetContext = context;

                    currentSheet.save(new ModelValue.OnSaveListener()
                    {
                        @Override
                        public void onSave() {

                        }

                        @Override
                        public void onSaveError(DatabaseException exception) {
                            Log.d("***SHEET MANAGER", "save error");
                            ApplicationFailure.database(exception);
                        }
                    });

                    sheetListener.onSheet(templateSheet);
                }
            }

        }.execute();
    }


    public static void goToMostRecent(final Sheet.OnSheetListener listener, Context context)
    {
        ModelValue.OnLoadListener<Sheet> onLoadListener = new ModelValue.OnLoadListener<Sheet>()
        {
            @Override
            public void onLoad(Sheet value)
            {
                Log.d("***SHEET MANAGER", "on load sheet");

                addVariablesToSheet(value);
                listener.onSheet(value);
            }

            @Override
            public void onLoadDBError(DatabaseException exception)
            {
                ApplicationFailure.database(exception);
            }

            @Override
            public void onLoadError(Exception exception)
            {
                Log.d("***SHEET MANAGER", "other exception", exception);
            }
        };

        currentSheet        = ModelValue.empty(Sheet.class);
        currentSheet.setOnLoadListener(onLoadListener);
        currentSheetContext = context;

        // Construct query
        List<OrderBy.Field> fields = new ArrayList<>();
        // TODO make this derived, not hardcoded
        fields.add(new OrderBy.Field("last_used", Function.DATETIME));
        OrderBy orderBy = new OrderBy(fields, OrderBy.Order.DESC);

        ModelQueryParameters.TopResult topResultQuery =
                new ModelQueryParameters.TopResult(orderBy);
        ModelQueryParameters queryParameters =
                new ModelQueryParameters(topResultQuery, ModelQueryParameters.Type.TOP_RESULT);

        currentSheet.load(queryParameters, null);
    }


    /**
     * This method is used to track variables when a sheet is loaded asynchronously. As variables
     * are loaded, they register themselves here. When everything is completely loaded, we can
     * synchronously add all of the variables to the Variable Index.
     * @param variable The variable to register.
     */
    public static void registerVariable(Variable variable)
    {
        variableQueue.add(variable);
    }


    /**
     * Prepare the Sheet Manager for use.
     */
    public static void initialize()
    {
        variableQueue = new ArrayList<>();
    }



    private static void addVariablesToSheet(Sheet sheet)
    {
        // Add all of the variables
        for (Variable variable : variableQueue) {
            sheet.getRules().getVariableIndex().addVariable(variable);
        }
        variableQueue = new ArrayList<>();
    }
}
