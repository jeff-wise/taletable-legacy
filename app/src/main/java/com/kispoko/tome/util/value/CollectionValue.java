
package com.kispoko.tome.util.value;


import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.model.Modeler;
import com.kispoko.tome.util.promise.AsyncFunction;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;



/**
 * Collection Value
 */
public class CollectionValue<A extends Model> extends Value<List<A>>
                                              implements Serializable
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------

    private List<Class<? extends A>> modelClasses;

    private OnUpdateListener<A> onUpdateListener;
    private OnSaveListener      onSaveListener;
    private OnLoadListener      onLoadListener;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public CollectionValue(List<A> value,
                           OnUpdateListener<A> onUpdateListener,
                           List<Class<? extends A>> modelClasses,
                           OnSaveListener onSaveListener,
                           OnLoadListener onLoadListener)
    {
        super(value);

        this.modelClasses     = modelClasses;

        this.onUpdateListener = onUpdateListener;
        this.onSaveListener   = onSaveListener;
        this.onLoadListener   = onLoadListener;
    }


    public CollectionValue(List<A> value,
                           List<Class<? extends A>> modelClasses,
                           OnUpdateListener<A> onUpdateListener)
    {
        super(value);

        this.modelClasses     = modelClasses;

        this.onUpdateListener = onUpdateListener;
        this.onSaveListener   = null;
        this.onLoadListener   = null;
    }


    public CollectionValue(List<A> value,
                           List<Class<? extends A>> modelClasses)
    {
        super(value);

        this.modelClasses     = modelClasses;

        this.onUpdateListener = null;
        this.onSaveListener   = null;
        this.onLoadListener   = null;
    }



    // API
    // --------------------------------------------------------------------------------------

    // > Set Value
    // --------------------------------------------------------------------------------------

    @Override
    public void setValue(List<A> newValues)
    {
        if (newValues != null) {
            this.value = newValues;
            this.onUpdateListener.onUpdate(newValues);
        }
    }


    // > State
    // --------------------------------------------------------------------------------------

    public List<Class<? extends A>> getModelClasses()
    {
        return this.modelClasses;
    }


    // > Asynchronous Operations
    // --------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public void load(final String parentModelName, final UUID parentModelId)
    {
        new AsyncFunction<>(new AsyncFunction.Action<Object>()
        {
            @Override
            public Object run()
            {
                try {
                    return Modeler.collectionFromDatabase(parentModelName,
                                                          parentModelId,
                                                          modelClasses);
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
                else {
                    List<A> valueCollection = (List<A>) result;
                    setValue(valueCollection);
                    if (onLoadListener != null)
                        onLoadListener.onLoad(valueCollection);
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
                    for (Model model : getValue()) {
                        Modeler.toDatabase(model);
                    }
                    return null;
                }
                catch (DatabaseException exception) {
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
        void onUpdate(List<A> values);
    }


    public interface OnSaveListener {
        void onSave();
        void onSaveError(DatabaseException exception);
    }


    public interface OnLoadListener<A> {
        void onLoad(List<A> value);
        void onLoadError(DatabaseException exception);
    }

}
