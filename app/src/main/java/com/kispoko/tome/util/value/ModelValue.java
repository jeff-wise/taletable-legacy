
package com.kispoko.tome.util.value;


import com.kispoko.tome.util.Model;
import com.kispoko.tome.util.promise.ValuePromise;
import com.kispoko.tome.util.promise.SaveValuePromise;


/**
 * Model Value
 */
public class ModelValue<A extends Model> extends Value<A>
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------

    private Class<A>         modelClass;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public ModelValue(A value, Model model, Class<A> modelClass)
    {
        super(value, model);
        this.modelClass       = modelClass;
    }


    // API
    // --------------------------------------------------------------------------------------

    // > State
    // --------------------------------------------------------------------------------------

    public Class<A> getModelClass()
    {
        return this.modelClass;
    }


    // > Helpers
    // --------------------------------------------------------------------------------------


    public String sqlColumnName()
    {
        return this.getValue().getName() + "_id";
    }


    // > Asynchronous Operations
    // --------------------------------------------------------------------------------------

    public void loadValue(final ValuePromise<A> promise)
    {
        promise.run(new ValuePromise.OnReady<A>() {
            @Override
            public void run(A result) {
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
