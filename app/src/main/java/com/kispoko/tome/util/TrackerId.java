
package com.kispoko.tome.util;


import java.util.UUID;



/**
 * Tracker ID
 */
public class TrackerId
{

    public static enum Target {
        SHEET,
        ROLEPLAY,
        PAGE,
        GROUP,
        TABLE,
        CELL,
        RULES,
        TYPES,
        FUNCTION_INDEX,
        PROGRAM_INDEX,
    }

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID code;
    private Target target;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TrackerId(UUID code, Target target) {
        this.code = code;
        this.target = target;
    }


    // > API
    // ------------------------------------------------------------------------------------------

    public UUID getCode() {
        return this.code;
    }

    public Target getTarget() {
        return this.target;
    }

}
