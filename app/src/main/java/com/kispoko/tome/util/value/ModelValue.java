
package com.kispoko.tome.util.value;


import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.query.ModelQueryParameters;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.model.ModelLib;
import com.kispoko.tome.util.promise.AsyncFunction;

import java.io.Serializable;



/**
 * ModelLib Value
 */
public class ModelValue<A extends Model> extends Value<A>
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

    /**
     * This constructor should be used when the ModelValue is to be loaded asynchronously. The other
     * constructors set isLoaded to TRUE by default. This constructor also allows static listeners
     * for when the model is saved and loaded.
     * @param value
     * @param modelClass
     * @param onSaveListener
     * @param onLoadListener
     */
    public ModelValue(A value,
                      Class<A> modelClass,
                      OnSaveListener onSaveListener,
                      OnLoadListener onLoadListener)
    {
        super(value);

        this.modelClass           = modelClass;

        this.staticOnSaveListener = onSaveListener;
        this.staticOnLoadListener = onLoadListener;

        this.isLoaded             = false;
        this.isSaved              = false;
    }


    public ModelValue(A value,
                      Class<A> modelClass)
    {
        super(value);

        this.modelClass           = modelClass;

        this.staticOnSaveListener = null;
        this.staticOnLoadListener = null;

        this.isLoaded             = true;
        this.isSaved              = false;
    }


    // API
    // --------------------------------------------------------------------------------------

    // > State
    // --------------------------------------------------------------------------------------

    public Class<A> getModelClass()
    {
        return this.modelClass;
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
                                   onLoadListener(dynamicOnLoadListener));
    }


    public void save(final OnSaveListener dynamicOnSaveListener)
    {
        ModelLib.modelToDatabase(this.getValue(), this.onSaveListener(dynamicOnSaveListener));
    }


    public void save()
    {
        this.save(null);
    }


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
            public void onLoad(A value) {
                setValue(value);
                setIsLoaded(true);
                staticOnLoadListener.onLoad(value);
                dynamicOnLoadListener.onLoad(value);
            }

            @Override
            public void onLoadError(DatabaseException exception) {
                staticOnLoadListener.onLoadError(exception);
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
            public void onSave() {
                setIsSaved(true);
                staticOnSaveListener.onSave();
                dynamicOnSaveListener.onSave();
            }

            @Override
            public void onSaveError(DatabaseException exception) {
                staticOnSaveListener.onSave();
                dynamicOnSaveListener.onSave();
            }
        };
    }


    // LISTENERS
    // --------------------------------------------------------------------------------------

    public interface OnSaveListener {
        void onSave();
        void onSaveError(DatabaseException exception);
    }


    public interface OnLoadListener<A> {
        void onLoad(A value);
        void onLoadError(DatabaseException exception);
    }


}
