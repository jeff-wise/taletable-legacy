
package com.kispoko.tome.activity;


import com.kispoko.tome.sheet.widget.WidgetData;
import com.kispoko.tome.sheet.widget.TextWidget;
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
        WidgetData widgetData = sheet.componentWithId(this.componentId);

        switch (this.resultType)
        {
            case TEXT_VALUE:
                ((TextWidget) widgetData).setValue((String) this.result, sheetActivity);
                break;
        }
    }

}
