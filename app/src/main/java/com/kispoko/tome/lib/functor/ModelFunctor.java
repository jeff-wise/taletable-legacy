
package com.kispoko.tome.lib.functor;


import android.content.Context;
import android.os.AsyncTask;

import com.kispoko.tome.lib.database.DatabaseException;
import com.kispoko.tome.lib.database.orm.ORM;
import com.kispoko.tome.lib.database.query.ModelQueryParameters;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.model.form.Field;

import java.io.Serializable;
import java.util.UUID;


/**
 * ModelLib Value
 */
public class ModelFunctor<A extends Model> extends Functor<A>
                                         implements Serializable
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------

    private Class<A>        modelClass;

    private OnSaveListener  staticOnSaveListener;
    private OnLoadListener  staticOnLoadListener;

    private boolean         isLoaded;
    private boolean         isSaved;

    private boolean         isSaving;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    private ModelFunctor(A value, Class<A> modelClass, boolean isSaved, boolean isLoaded)
    {
        super(value);

        this.modelClass             = modelClass;

        this.isLoaded               = isLoaded;
        this.isSaved                = isSaved;

        this.staticOnSaveListener   = null;
        this.staticOnLoadListener   = null;

        this.isSaving               = false;
    }


    public static <A extends Model> ModelFunctor<A> full(A value, Class<A> modelClass)
    {
        return new ModelFunctor<>(value, modelClass, false, true);
    }


    public static <A extends Model> ModelFunctor<A> empty(Class<A> modelClass)
    {
        return new ModelFunctor<>(null, modelClass, true, false);
    }


    // API
    // --------------------------------------------------------------------------------------

    // ** Model Class
    // ------------------------------------------------------------------------------------------

    public Class<A> modelClass()
    {
        return this.modelClass;
    }


    // ** Listeners
    // ------------------------------------------------------------------------------------------

    public void setOnSaveListener(OnSaveListener onSaveListener)
    {
        this.staticOnSaveListener = onSaveListener;
    }


    public void setOnLoadListener(OnLoadListener<A> onLoadListener)
    {
        this.staticOnLoadListener = onLoadListener;
    }


    // ** Is Loaded
    // ------------------------------------------------------------------------------------------

    public boolean getIsLoaded()
    {
        return this.isLoaded;
    }


    public void setIsLoaded(boolean isLoaded)
    {
        this.isLoaded = isLoaded;
    }


    // ** Is Saved
    // ------------------------------------------------------------------------------------------

    public boolean getIsSaved()
    {
        return this.isSaved;
    }


    public void setIsSaved(boolean isSaved)
    {
        this.isSaved = isSaved;
    }


    // > SQL Column Name
    // ------------------------------------------------------------------------------------------

    public String sqlColumnName()
    {
        return this.name() + "_" + ORM.name(modelClass) + "_id";
    }


    // > Load
    // ------------------------------------------------------------------------------------------

    public void load(final ModelQueryParameters queryParameters)
           throws DatabaseException
    {
        A model = ORM.loadModel(this.modelClass(), queryParameters);

        this.setValue(model);
        this.setIsLoaded(true);
    }


    @SuppressWarnings("unchecked")
    public void loadAsync(final ModelQueryParameters queryParameters)
    {
        new AsyncTask<Void,Void,Object>()
        {

            @Override
            protected Object doInBackground(Void... args)
            {
                try
                {
                    return ORM.loadModel(modelClass(), queryParameters);
                }
                catch (DatabaseException exception)
                {
                    return exception;
                }
                catch (Exception exception)
                {
                    return exception;
                }
            }

            @Override
            protected void onPostExecute(Object result)
            {
                if (result instanceof DatabaseException)
                {
                    if (staticOnLoadListener != null)
                        staticOnLoadListener.onLoadDBError((DatabaseException) result);
                }
                else if (result instanceof Exception)
                {
                    if (staticOnLoadListener != null)
                        staticOnLoadListener.onLoadError((Exception) result);
                }
                else
                {
                    A loadedModel = (A) result;

                    setValue(loadedModel);
                    setIsLoaded(true);

                    if (staticOnLoadListener != null)
                        staticOnLoadListener.onLoad(loadedModel);
                }
            }

        }.execute();

    }


    // > Save
    // ------------------------------------------------------------------------------------------

    public void save()
           throws DatabaseException
    {
        if (this.getValue() != null && !this.isSaving)
        {
            this.isSaving = true;

            ORM.saveModel(this.getValue());

            this.isSaving = false;
        }
    }


    public void saveAsync()
    {

        new AsyncTask<Void,Void,Object>()
        {

            @Override
            protected Object doInBackground(Void... args)
            {
                try
                {
                    if (getValue() != null && !isSaving)
                    {
                        isSaving = true;

                        ORM.saveModel(getValue(), true);

                        return true;
                    }
                    else
                    {
                        return false;
                    }

                }
                catch (DatabaseException exception)
                {
                    return exception;
                }
                catch (Exception exception)
                {
                    return exception;
                }
            }

            @Override
            protected void onPostExecute(Object result)
            {
                if (result instanceof DatabaseException)
                {
                    if (staticOnSaveListener != null)
                        staticOnSaveListener.onSaveDBError((DatabaseException) result);
                }
                else if (result instanceof Exception)
                {
                    if (staticOnSaveListener != null)
                        staticOnSaveListener.onSaveError((Exception) result);
                }
                else if (result instanceof Boolean)
                {
                    Boolean savedModel = (Boolean) result;

                    if (savedModel)
                    {
                        if (staticOnSaveListener != null)
                            staticOnSaveListener.onSave();

                        // > Toggle Is Saving State
                        // --------------------------------------------
                        isSaving = false;
                        isSaved  = true;
                    }

                }
            }

        }.execute();
    }


    // FORM
    // --------------------------------------------------------------------------------------

    public Field field(UUID modelId, Context context)
    {
        // ** Name
        String fieldName = this.name();

        // ** Label
        String fieldLabel = "";
        if (this.label() != null)
            fieldLabel = this.label();
        else if (this.labelId() != null)
            fieldLabel = context.getString(this.labelId());

        String fieldDescription = "";
        if (this.description() != null)
            fieldDescription = this.description();
        else if (this.descriptionId() != null)
            fieldDescription = context.getString(this.descriptionId());


        return Field.model(modelId, fieldName, fieldLabel, fieldDescription);
    }


    // LISTENERS
    // ----------------------------------------------------------------------------------------

    public interface OnSaveListener extends Serializable
    {
        void onSave();
        void onSaveDBError(DatabaseException exception);
        void onSaveError(Exception exception);
    }


    public interface OnLoadListener<A> extends Serializable
    {
        void onLoad(A value);
        void onLoadDBError(DatabaseException exception);
        void onLoadError(Exception exception);
    }


}
