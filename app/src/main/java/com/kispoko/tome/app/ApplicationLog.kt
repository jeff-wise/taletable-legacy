
package com.kispoko.tome.app


import android.util.Log



/**
 * Application Error
 */


object ApplicationLog
{


    fun error(error : ApplicationError)
    {
        Log.d("***TOME LOG", error.userMessage())
    }

}
