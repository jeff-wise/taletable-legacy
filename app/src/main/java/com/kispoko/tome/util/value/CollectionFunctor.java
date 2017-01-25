
package com.kispoko.tome.util.value;


import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.OneToManyRelation;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.model.ModelLib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



/**
 * Collection Value
 */
public class CollectionFunctor<A extends Model> extends Functor<List<A>>
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

    private CollectionFunctor(List<A> value,
                              List<Class<? extends A>> modelClasses,
                              boolean isSaved,
                              boolean isLoaded)
    {
        super(value);

        this.modelClasses         = modelClasses;

        this.staticOnSaveListener = null;
        this.staticOnLoadListener = null;

        this.isSaved              = isSaved;
        this.isLoaded             = isLoaded;
    }


    // > Full Functor
    // -----------------------------------------------------------------------------------------

    public static <A extends Model> CollectionFunctor<A> full(List<A> value,
                                                              List<Class<? extends A>> modelClasses)
    {
        return new CollectionFunctor<>(value, modelClasses, false, true);
    }


    public static <A extends Model> CollectionFunctor<A> full(List<A> value,
                                                              Class<? extends A> modelClass)
    {
        List<Class<? extends A>> modelClasses = new ArrayList<>();
        modelClasses.add(modelClass);
        return new CollectionFunctor<>(value, modelClasses, false, true);
    }


    // > Empty Functor
    // -----------------------------------------------------------------------------------------

    public static <A extends Model> CollectionFunctor<A> empty(List<Class<? extends A>> modelClasses)
    {
        return new CollectionFunctor<>(null, modelClasses, true, false);
    }


    public static <A extends Model> CollectionFunctor<A> empty(Class<? extends A> modelClass)
    {
        List<Class<? extends A>> modelClasses = new ArrayList<>();
        modelClasses.add(modelClass);
        return new CollectionFunctor<>(null, modelClasses, true, false);
    }


    // API
    // --------------------------------------------------------------------------------------

    // > State
    // --------------------------------------------------------------------------------------

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


    public List<Class<? extends A>> getModelClasses()
    {
        return this.modelClasses;
    }


    // > Asynchronous Operations
    // -----------------------------------------------------------------------------------------

    /**
     * Load an entire table of values.
     */
    public void load()
    {
        ModelLib.modelCollectionFromDatabase(null, this.modelClasses, this.onLoadListener(null));
    }


    @SuppressWarnings("unchecked")
    public void load(final Model ownerModel,
                     final OnLoadListener dynamicOnLoadListener)
    {
        OneToManyRelation oneToManyRelation =
                                new OneToManyRelation(ModelLib.name(ownerModel),
                                                      this.name(),
                                                      ownerModel.getId());
        ModelLib.<A>modelCollectionFromDatabase(oneToManyRelation,
                                                this.modelClasses,
                                                this.onLoadListener(dynamicOnLoadListener));
    }


    @SuppressWarnings("unchecked")
    public void save(List<OneToManyRelation> parentRelations,
                     OnSaveListener dynamicOnSaveListener)
    {
        ModelLib.modelCollectionToDatabase((List<Model>) this.getValue(),
                                           parentRelations,
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
            public void onLoad(List<A> loadedValues)
            {
                setValue(loadedValues);

                setIsLoaded(true);

                for (Model loadedModel : loadedValues)
                {
                    loadedModel.onLoad();
                }

                if (staticOnLoadListener != null)
                    staticOnLoadListener.onLoad(loadedValues);

                if (dynamicOnLoadListener != null)
                    dynamicOnLoadListener.onLoad(loadedValues);
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
        void onLoad(List<A> value);
        void onLoadDBError(DatabaseException exception);
        void onLoadError(Exception exception);
    }

}
