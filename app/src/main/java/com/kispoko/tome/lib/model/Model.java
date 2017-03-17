
package com.kispoko.tome.lib.model;


import java.util.UUID;



/**
 * Model Interface
 */
public interface Model
{
    UUID getId();
    void setId(UUID id);

    void onLoad();
    // void onSave();
}
