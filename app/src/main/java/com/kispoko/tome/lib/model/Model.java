
package com.kispoko.tome.lib.model;


import java.util.UUID;



/**
 * Model Interface
 */
public interface Model
{
    UUID getId();
    void setId(UUID id);


    // Name???

    void onLoad();
    // void onSave();
}
