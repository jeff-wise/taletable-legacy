
package com.kispoko.tome.activity;


import android.content.Context;

import com.kispoko.tome.sheet.Component;
import com.kispoko.tome.sheet.component.Text;
import com.kispoko.tome.sheet.Sheet;

import java.io.Serializable;

/**
 * Edit Result
 *
 * This class holds a value returned from the EditActivity which represents a change in
 * a component.
 */

public class EditResult implements Serializable
{

    // > RESULT TYPE
    // ------------------------------------------------------------------------------------------

    public static enum ResultType {
        TEXT_VALUE
    }


    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private ResultType resultType;
    private String componentName;
    private Object result;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public EditResult(ResultType resultType, String componentName, Object result)
    {
        this.resultType    = resultType;
        this.componentName = componentName;
        this.result        = result;
    }


    // > API
    // ------------------------------------------------------------------------------------------

    public void applyResult(Context context, Sheet sheet)
    {
        Component component = sheet.getComponent(this.componentName);

        switch (this.resultType)
        {
            case TEXT_VALUE:
                ((Text) component).setValue((String) this.result, context);
                break;
        }
    }

}
