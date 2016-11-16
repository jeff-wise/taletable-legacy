
package com.kispoko.tome.util.value;


import com.kispoko.tome.util.Model;
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

    private String   childModelName;
    private Class<A> modelClass;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public CollectionValue(List<A> value,
                           Model model,
                           String childModelName,
                           Class<A> modelClass)

    {
        super(value, model);
        this.childModelName = childModelName;
        this.modelClass     = modelClass;
    }


    // API
    // --------------------------------------------------------------------------------------

    // > State
    // --------------------------------------------------------------------------------------

    public String getChildModelName()
    {
        return this.childModelName;
    }


    public Class<A> getModelClass()
    {
        return this.modelClass;
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
