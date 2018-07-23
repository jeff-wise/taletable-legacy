
package com.taletable.android.rts.entity.engine.search;



/**
 * Engine Search Result Interface
 */
public interface EngineSearchResult
{
    float ranking();
    void addToRanking(float f);
}
