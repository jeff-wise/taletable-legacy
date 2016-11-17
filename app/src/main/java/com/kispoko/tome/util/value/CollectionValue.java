
package com.kispoko.tome.util.value;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.promise.CollectionValuePromise;
import com.kispoko.tome.util.promise.SaveValuePromise;

import java.util.List;



/**
 * Collection Value
 */
public class CollectionValue<A extends Model> extends Value<List<A>>
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------

    private List<Class<A>> modelClasses;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public CollectionValue(List<A> value,
                           Model model,
                           List<Class<A>> modelClasses)
    {
        super(value, model);
        this.modelClasses = modelClasses;
    }


    // API
    // --------------------------------------------------------------------------------------

    // > State
    // --------------------------------------------------------------------------------------

    public List<Class<A>> getModelClasses()
    {
        return this.modelClasses;
    }


    // > Asynchronous Operations
    // --------------------------------------------------------------------------------------

    public void loadValue(final CollectionValuePromise<A> promise)
    {
        promise.run(new CollectionValuePromise.OnReady<A>() {
            @Override
            public void run(List<A> result) {
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
