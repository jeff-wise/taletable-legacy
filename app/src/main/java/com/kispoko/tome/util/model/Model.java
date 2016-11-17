
package com.kispoko.tome.util.model;


import java.util.UUID;



/**
 * Model Interface
 */
public interface Model
{
    void onModelUpdate(String name);
    UUID getId();
    void setId(UUID id);

}
