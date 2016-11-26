
package com.kispoko.tome.util.value;


import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.query.ModelQueryParameters;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.model.Modeler;
import com.kispoko.tome.util.promise.AsyncFunction;

import java.io.Serializable;



/**
 * Modeler Value
 */
public class ModelValue<A extends Model> extends Value<A>
                                         implements Serializable
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------

    private Class<A>            modelClass;

    private OnUpdateListener<A> onUpdateListener;
    private OnSaveListener      onSaveListener;
    private OnLoadListener<A>   onLoadListener;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public ModelValue(A value,
                      OnUpdateListener<A> onUpdateListener,
                      Class<A> modelClass,
                      OnSaveListener onSaveListener,
                      OnLoadListener onLoadListener)
    {
        super(value);

        this.modelClass       = modelClass;

        this.onUpdateListener = onUpdateListener;
        this.onSaveListener   = onSaveListener;
        this.onLoadListener   = onLoadListener;
    }


    public ModelValue(A value,
                      OnUpdateListener<A> onUpdateListener,
                      Class<A> modelClass)
    {
        super(value);

        this.modelClass       = modelClass;

        this.onUpdateListener = onUpdateListener;
        this.onSaveListener   = null;
        this.onLoadListener   = null;
    }


    public ModelValue(A value,
                      Class<A> modelClass)
    {
        super(value);

        this.modelClass       = modelClass;

        this.onUpdateListener = null;
        this.onSaveListener   = null;
        this.onLoadListener   = null;
    }



    // API
    // --------------------------------------------------------------------------------------

    // > Set Value
    // --------------------------------------------------------------------------------------

    @Override
    public void setValue(A newValue)
    {
        if (newValue != null) {
            this.value = newValue;
            this.onUpdateListener.onUpdate(newValue);
        }
    }


    // > State
    // --------------------------------------------------------------------------------------

    public Class<A> getModelClass()
    {
        return this.modelClass;
    }


    // > Helpers
    // ------------------------------------------------------------------------------------------

    public String sqlColumnName()
    {
        return this.name() + "_" + Modeler.name(modelClass) + "_id";
    }


    // > Asynchronous Operations
    // ------------------------------------------------------------------------------------------

    public void load(final ModelQueryParameters queryParameters)
    {
        new AsyncFunction<>(new AsyncFunction.Action<Object>()
        {
            @Override
            public Object run()
            {
                try {
                    return Modeler.fromDatabase(getModelClass(), queryParameters);
                } catch (DatabaseException exception) {
                    return exception;
                }
            }
        })
        .run(new AsyncFunction.OnReady<Object>()
        {
            @Override
            public void run(Object result)
            {
                if (result instanceof DatabaseException) {
                    if (onLoadListener != null)
                        onLoadListener.onLoadError((DatabaseException) result);
                }
                else if (getModelClass().isAssignableFrom(result.getClass())) {
                    A value = getModelClass().cast(result);
                    setValue(value);
                    if (onLoadListener != null)
                        onLoadListener.onLoad(value);
                }

            }
        });
    }


    public void save(final OnSaveListener oneTimeListener)
    {
        new AsyncFunction<>(new AsyncFunction.Action<Object>()
        {
            @Override
            public Object run()
            {
                try {
                    Modeler.toDatabase(getValue());
                    return null;
                } catch (DatabaseException e) {
                    return e;
                }
            }
        })
        .run(new AsyncFunction.OnReady<Object>()
        {
            @Override
            public void run(Object result)
            {
                if (result instanceof DatabaseException) {
                    if (oneTimeListener != null)
                        oneTimeListener.onSaveError((DatabaseException) result);
                    if (onSaveListener != null)
                        onSaveListener.onSaveError((DatabaseException) result);
                }
                else {
                    setIsSaved(true);
                    if (oneTimeListener != null)
                        oneTimeListener.onSave();
                    if (onSaveListener != null)
                        onSaveListener.onSave();
                }
            }
        });
    }


    public void save()
    {
        this.save(null);
    }


    // LISTENERS
    // --------------------------------------------------------------------------------------

    public interface OnUpdateListener<A> {
        void onUpdate(A value);
    }


    public interface OnSaveListener {
        void onSave();
        void onSaveError(DatabaseException exception);
    }


    public interface OnLoadListener<A> {
        void onLoad(A value);
        void onLoadError(DatabaseException exception);
    }

}
