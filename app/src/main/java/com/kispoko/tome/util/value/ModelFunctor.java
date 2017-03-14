
package com.kispoko.tome.util.value;


import android.util.Log;

import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.query.ModelQueryParameters;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.model.ModelLib;

import java.io.Serializable;



/**
 * ModelLib Value
 */
public class ModelFunctor<A extends Model> extends Functor<A>
                                         implements Serializable
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------

    private Class<A>       modelClass;

    private OnSaveListener staticOnSaveListener;
    private OnLoadListener staticOnLoadListener;

    private boolean        isLoaded;
    private boolean        isSaved;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    private ModelFunctor(A value, Class<A> modelClass, boolean isSaved, boolean isLoaded)
    {
        super(value);

        this.modelClass           = modelClass;

        this.isLoaded             = isLoaded;
        this.isSaved              = isSaved;

        this.staticOnSaveListener = null;
        this.staticOnLoadListener = null;
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

    // > State
    // --------------------------------------------------------------------------------------

    // ** Model Class
    // ------------------------------------------------------------------------------------------

    public Class<A> getModelClass()
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
        return this.name() + "_" + ModelLib.name(modelClass) + "_id";
    }


    // > Asynchronous Operations
    // ------------------------------------------------------------------------------------------

    public void load(final ModelQueryParameters queryParameters,
                     final OnLoadListener<A> dynamicOnLoadListener)
    {
        ModelLib.modelFromDatabase(getModelClass(),
                                   queryParameters,
                                   this.onLoadListener(dynamicOnLoadListener));
    }


    public void save(final OnSaveListener dynamicOnSaveListener)
    {
        if (this.getValue() != null) {
            ModelLib.modelToDatabase(this.getValue(),
                                     this.onSaveListener(dynamicOnSaveListener));
        }
    }


    public void save()
    {
        this.save(null);
    }



    // INTERNAL
    // ------------------------------------------------------------------------------------------

    // > Listeners
    // ------------------------------------------------------------------------------------------

    /**
     * Wrap the dynamic and static listeners into one listener. This also sets the model value
     * by default when the listener is called.
     * @return
     */
    private OnLoadListener<A> onLoadListener(final OnLoadListener<A> dynamicOnLoadListener)
    {
        return new OnLoadListener<A>()
        {
            @Override
            public void onLoad(A loadedValue)
            {
                setValue(loadedValue);

                setIsLoaded(true);

                loadedValue.onLoad();

                if (staticOnLoadListener != null)
                    staticOnLoadListener.onLoad(loadedValue);

                if (dynamicOnLoadListener != null)
                    dynamicOnLoadListener.onLoad(loadedValue);
            }

            @Override
            public void onLoadDBError(DatabaseException exception)
            {
                if (staticOnLoadListener != null)
                    staticOnLoadListener.onLoadDBError(exception);

                if (dynamicOnLoadListener != null)
                    dynamicOnLoadListener.onLoadDBError(exception);
            }

            @Override
            public void onLoadError(Exception exception)
            {
                if (staticOnLoadListener != null)
                    staticOnLoadListener.onLoadError(exception);

                if (dynamicOnLoadListener != null)
                    dynamicOnLoadListener.onLoadError(exception);
            }
        };
    }


    /**
     * Wrap the dynamic and static listeners into one listener. This also sets the model value
     * by default when the listener is called.
     * @return
     */
    private OnSaveListener onSaveListener(final OnSaveListener dynamicOnSaveListener)
    {
        return new OnSaveListener()
        {
            @Override
            public void onSave()
            {
                setIsSaved(true);

                // getValue().onSave();

                Log.d("***MODELFUNCTOR", "on save  " + modelClass.getName());

                if (staticOnSaveListener != null)
                    staticOnSaveListener.onSave();

                if (dynamicOnSaveListener != null)
                    dynamicOnSaveListener.onSave();
            }

            @Override
            public void onSaveDBError(DatabaseException exception)
            {
                if (staticOnSaveListener != null)
                    staticOnSaveListener.onSaveDBError(exception);

                if (dynamicOnSaveListener != null)
                    dynamicOnSaveListener.onSaveDBError(exception);
            }

            @Override
            public void onSaveError(Exception exception)
            {
                if (staticOnSaveListener != null)
                    staticOnSaveListener.onSaveError(exception);

                if (dynamicOnSaveListener != null)
                    dynamicOnSaveListener.onSaveError(exception);
            }
        };
    }


    // LISTENERS
    // --------------------------------------------------------------------------------------

    public interface OnSaveListener extends Serializable {
        void onSave();
        void onSaveDBError(DatabaseException exception);
        void onSaveError(Exception exception);
    }


    public interface OnLoadListener<A> extends Serializable {
        void onLoad(A value);
        void onLoadDBError(DatabaseException exception);
        void onLoadError(Exception exception);
    }


}
