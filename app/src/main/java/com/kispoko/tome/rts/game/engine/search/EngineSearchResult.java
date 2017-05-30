
package com.kispoko.tome.rts.game.engine.search;



/**
 * Engine Search Result Interface
 */
public interface EngineSearchResult
{
    float ranking();
    void addToRanking(float f);
}
