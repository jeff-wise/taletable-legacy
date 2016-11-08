
package com.kispoko.tome.activity;


import com.kispoko.tome.sheet.component.Component;
import com.kispoko.tome.sheet.component.type.Text;
import com.kispoko.tome.sheet.Sheet;

import java.io.Serializable;
import java.util.UUID;

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

    public enum ResultType {
        TEXT_VALUE
    }


    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private ResultType resultType;
    private UUID componentId;
    private Object result;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public EditResult(ResultType resultType, UUID componentId, Object result)
    {
        this.resultType    = resultType;
        this.componentId   = componentId;
        this.result        = result;
    }


    // > API
    // ------------------------------------------------------------------------------------------

    public void applyResult(SheetActivity sheetActivity, Sheet sheet)
    {
        Component component = sheet.componentWithId(this.componentId);

        switch (this.resultType)
        {
            case TEXT_VALUE:
                ((Text) component).setValue((String) this.result, sheetActivity);
                break;
        }
    }

}
