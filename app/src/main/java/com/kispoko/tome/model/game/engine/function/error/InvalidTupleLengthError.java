
package com.kispoko.tome.model.game.engine.function.error;



/**
 * Function Error: Invalid Tuple Length
 */
public class InvalidTupleLengthError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private int tupleIndex;
    private int expectedLength;
    private int actualLength;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public InvalidTupleLengthError(int tupleIndex, int expectedLength, int actualLength)
    {
        this.tupleIndex     = tupleIndex;
        this.expectedLength = expectedLength;
        this.actualLength    = actualLength;
    }


    // API
    // --------------------------------------------------------------------------------------

    /**
     * Get the index of the tuple with the invalid length.
     * @return The tuple index.
     */
    public int getTupleIndex()
    {
        return this.tupleIndex;
    }


    /**
     * Get the expected length of the invalid tuple.
     * @return The tuple's expected length;
     */
    public int getExpectedLength()
    {
        return this.expectedLength;
    }


    /**
     * Get the actualy length of the invalid tuple
     * @return The tuple's actual length.
     */
    public int getActualLength()
    {
        return this.actualLength;
    }
}
