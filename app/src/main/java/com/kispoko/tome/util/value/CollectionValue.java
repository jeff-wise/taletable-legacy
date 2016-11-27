
package com.kispoko.tome.util.value;


import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.model.ModelLib;

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

    private OnSaveListener           staticOnSaveListener;
    private OnLoadListener<A>        staticOnLoadListener;

    private boolean                  isLoaded;
    private boolean                  isSaved;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public CollectionValue(List<A> value,
                           List<Class<? extends A>> modelClasses,
                           OnSaveListener onSaveListener,
                           OnLoadListener onLoadListener)
    {
        super(value);

        this.modelClasses         = modelClasses;

        this.staticOnSaveListener = onSaveListener;
        this.staticOnLoadListener = onLoadListener;

        this.isLoaded             = false;
    }


    public CollectionValue(List<A> value,
                           List<Class<? extends A>> modelClasses)
    {
        super(value);

        this.modelClasses         = modelClasses;

        this.staticOnSaveListener = null;
        this.staticOnLoadListener = null;

        this.isLoaded             = true;
    }


    // API
    // --------------------------------------------------------------------------------------

    // > State
    // --------------------------------------------------------------------------------------

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


    // > Asynchronous Operations
    // --------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public void load(final String parentModelName,
                     final UUID parentModelId,
                     final OnLoadListener dynamicOnLoadListener)
    {
        ModelLib.<A>modelCollectionFromDatabase(parentModelName,
                                             parentModelId,
                                             modelClasses,
                                             onLoadListener(dynamicOnLoadListener));
    }


    @SuppressWarnings("unchecked")
    public void save(final OnSaveListener dynamicOnSaveListener)
    {
        ModelLib.modelCollectionToDatabase((List<Model>) this.getValue(),
                                           this.onSaveListener(dynamicOnSaveListener));
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
            public void onLoad(List<A> values) {
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
                staticOnSaveListener.onSaveError(exception);
                dynamicOnSaveListener.onSaveError(exception);
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
        void onLoad(List<A> value);
        void onLoadError(DatabaseException exception);
    }


}
