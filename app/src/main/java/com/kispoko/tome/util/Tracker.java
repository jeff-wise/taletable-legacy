
package com.kispoko.tome.util;


import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Tracker
 */
public class Tracker
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Map<String,Boolean> statusMap;

    private OnReady onReady;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Tracker(List<String> keys, OnReady onReady)
    {
        this.onReady = onReady;

        statusMap = new HashMap<>();
        for (String key : keys) {
            statusMap.put(key, false);
        }

        if (this.isReady()) whenReady();
    }


    // API
    // ------------------------------------------------------------------------------------------

    public void setKey(String key) {
        this.statusMap.put(key, true);
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private boolean isReady() {
        for (Boolean flag : statusMap.values()) {
            if (!flag) return false;
        }
        return true;
    }


    private void whenReady() {
        onReady.go();
        // TODO cleanup
    }


    // NESTED DEFINITIONS
    // ------------------------------------------------------------------------------------------


    public static abstract class OnReady
    {

        abstract protected void go();

    }

}
