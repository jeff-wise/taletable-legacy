
package com.kispoko.tome.util.value;


import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.model.Modeler;
import com.kispoko.tome.util.promise.AsyncFunction;
import com.kispoko.tome.util.promise.CollectionValuePromise;
import com.kispoko.tome.util.promise.SaveValuePromise;

import java.util.List;
import java.util.UUID;


/**
 * Collection Value
 */
public class CollectionValue<A extends Model> extends Value<List<A>>
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------

    private List<Class<? extends A>> modelClasses;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public CollectionValue(List<A> value,
                           Model model,
                           List<Class<? extends A>> modelClasses)
    {
        super(value, model);
        this.modelClasses = modelClasses;
    }


    // API
    // --------------------------------------------------------------------------------------

    // > State
    // --------------------------------------------------------------------------------------

    public List<Class<? extends A>> getModelClasses()
    {
        return this.modelClasses;
    }


    // > Asynchronous Operations
    // --------------------------------------------------------------------------------------

    public void loadValue(final String parentModelName, final UUID parentModelId)
    {
        new AsyncFunction<>(new AsyncFunction.Action<List<A>>()
        {
            @Override
            public List<A> run()
            {
                List<A> loadedCollection = null;
                try {
                    loadedCollection = Modeler.collectionFromDatabase(parentModelName,
                                                                      parentModelId,
                                                                      modelClasses);
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
                return loadedCollection;
            }
        })
        .run(new AsyncFunction.OnReady<List<A>>()
        {
            @Override
            public void run(List<A> result)
            {
                setValue(result);
            }
        });
    }


    public void saveValue(final SaveValuePromise promise)
    {
        promise.run(new SaveValuePromise.OnReady() {
            @Override
            public void run() {
                setIsSaved(true);
            }
        });
    }

}
