
package com.kispoko.tome.util.functor;


import android.os.AsyncTask;

import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.orm.ORM;
import com.kispoko.tome.util.database.sql.OneToManyRelation;
import com.kispoko.tome.util.model.Model;

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

    private Class<A>                modelClass;

    private OnSaveListener          staticOnSaveListener;
    private OnLoadListener<A>       staticOnLoadListener;

    private boolean                 isLoaded;
    private boolean                 isSaved;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    private CollectionFunctor(List<A> value,
                              Class<A> modelClass,
                              boolean isSaved,
                              boolean isLoaded)
    {
        super(value);

        this.modelClass             = modelClass;

        this.staticOnSaveListener   = null;
        this.staticOnLoadListener   = null;

        this.isSaved                = isSaved;
        this.isLoaded               = isLoaded;
    }


    // > Full Functor
    // -----------------------------------------------------------------------------------------

    public static <A extends Model> CollectionFunctor<A> full(List<A> value,
                                                              Class<A> modelClass)
    {
        return new CollectionFunctor<>(value, modelClass, false, true);
    }


    // > Empty Functor
    // -----------------------------------------------------------------------------------------

    public static <A extends Model> CollectionFunctor<A> empty(Class<A> modelClass)
    {
        return new CollectionFunctor<>(null, modelClass, true, false);
    }


    // API
    // --------------------------------------------------------------------------------------

    // > State
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


    // > Asynchronous Operations
    // -----------------------------------------------------------------------------------------

    /**
     * Load an entire table of values.
     */
    public void load()
           throws DatabaseException
    {
        ORM.loadModelCollection(this.modelClass(), null);
    }


    public void load(final Model ownerModel)
           throws DatabaseException
    {
        OneToManyRelation oneToManyRelation = new OneToManyRelation(ORM.name(ownerModel),
                                                                    this.name(),
                                                                    ownerModel.getId());
        ORM.loadModelCollection(this.modelClass, oneToManyRelation);
    }


    public void save()
           throws DatabaseException
    {
        for (Model model : this.value)
        {
            ORM.saveModel(model, new ArrayList<OneToManyRelation>());
        }
    }


    public void save(List<OneToManyRelation> parentRelations)
           throws DatabaseException
    {
        for (Model model : this.value)
        {
            ORM.saveModel(model, parentRelations);
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
                    save();
                    return true;
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
                else
                {
                    if (staticOnSaveListener != null)
                        staticOnSaveListener.onSave();
                }
            }

        }.execute();

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
