
package com.kispoko.tome.util.value;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.model.Modeler;
import com.kispoko.tome.util.promise.ValuePromise;
import com.kispoko.tome.util.promise.SaveValuePromise;



/**
 * Modeler Value
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
        return Modeler.name(this.getValue().getClass()) + "_id";
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


    public void save()
    {
        SaveValuePromise promise = Modeler.saveValuePromise(this.getValue());
        promise.run(new SaveValuePromise.OnReady() {
            @Override
            public void run() {
                setIsSaved(true);
            }
        });
    }


}
