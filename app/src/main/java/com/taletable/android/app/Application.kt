
package com.taletable.android.app


import android.util.Log
import effect.Eff
import effect.Identity



// ---------------------------------------------------------------------------------------------
// LOG
// ---------------------------------------------------------------------------------------------

object ApplicationLog
{

    fun error(error : ApplicationError)
    {
        Log.d("***TOME LOG", error.debugMessage())
    }


    fun event(event : ApplicationEvent)
    {
        when (event.eventType()) {
            is EventTypeLoad -> {
                Log.d("***TOME LOG", event.debugMessage())
            }
            is EventTypeDatabase -> {
                Log.d("***TOME LOG", event.debugMessage())
            }
        }
    }

}


// ---------------------------------------------------------------------------------------------
// EFFECT
// ---------------------------------------------------------------------------------------------

typealias AppEff<A> = Eff<AppError, Identity, A>


