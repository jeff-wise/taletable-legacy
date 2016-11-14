
package com.kispoko.tome.util.value;


import com.kispoko.tome.util.Model;
import com.kispoko.tome.util.database.ColumnProperties;
import com.kispoko.tome.util.promise.LoadValuePromise;
import com.kispoko.tome.util.promise.SaveValuePromise;


/**
 * Model Value
 */
public class ModelValue<A extends Model> extends Value<A>
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------

    private ColumnProperties columnProperties;
    private Class<A>         modelClass;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public ModelValue(A value, Model model, ColumnProperties columnProperties, Class<A> modelClass)
    {
        super(value, model);
        this.columnProperties = columnProperties;
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


    public ColumnProperties getColumnProperties()
    {
        return this.columnProperties;
    }


    // > Asynchronous Operations
    // --------------------------------------------------------------------------------------

    public void loadValue(final LoadValuePromise<A> promise)
    {
        promise.run(new LoadValuePromise.OnReady<A>() {
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
