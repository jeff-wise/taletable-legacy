
package com.kispoko.tome.sheet.widget;


import com.kispoko.tome.sheet.widget.format.Format;
import com.kispoko.tome.type.Type;
import com.kispoko.tome.util.Model;
import com.kispoko.tome.util.value.Value;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Widget Data
 */
public abstract class WidgetData implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private   Value<UUID>         id;
    private   Value<String>       name;
    private   Value<UUID>         groupId;
    private   Value<Type.Id>      typeId;
    private   Value<List<String>> actions;
    protected Value<Format>       format;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public WidgetData(UUID id,
                      String name,
                      UUID groupId,
                      Type.Id typeId,
                      Format format,
                      List<String> actions)
    {
        this.id      = new Value<>("id", id, this);
        this.name    = new Value<>("name", name, this);
        this.groupId = new Value<>("groupId", groupId, this);
        this.typeId  = new Value<>("typeId", typeId, this);
        this.format  = new Value<>("format", format, this);
        this.actions = new Value<>("actions", actions, this);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > State
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    public UUID getId()
    {
        return this.id.getValue();
    }


    public void setId(UUID id)
    {
        this.id.setValue(id);
    }


    // ** Name
    // ------------------------------------------------------------------------------------------

    public String getName()
    {
        return this.name.getValue();
    }


    public void setName(String name)
    {
        this.name.setValue(name);
    }


    // ** Group Id
    // ------------------------------------------------------------------------------------------

    public UUID getGroupId()
    {
        return this.groupId.getValue();
    }

    public void setGroupId(UUID groupId)
    {
        this.groupId.setValue(groupId);
    }


    // ** Type Id
    // ------------------------------------------------------------------------------------------

    public Type.Id getTypeId()
    {
        return this.typeId.getValue();
    }


    public void setTypeId(Type.Id typeId)
    {
        this.typeId.setValue(typeId);
    }


    // ** Format
    // ------------------------------------------------------------------------------------------

    public Format getFormat()
    {
        return this.format.getValue();
    }


    public void setFormat(Format format)
    {
        this.format.setValue(format);
    }


    // ** Actions
    // ------------------------------------------------------------------------------------------

    public List<String> getActions() {
        return this.actions.getValue();
    }


    public void setActions(List actions)
    {
        if (actions != null)
            this.actions.setValue(actions);
        else
            this.actions.setValue(new ArrayList<String>());
    }

}
