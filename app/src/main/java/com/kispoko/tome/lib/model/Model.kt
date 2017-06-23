
package com.kispoko.tome.lib.model


import com.kispoko.tome.lib.functor.Func
import java.util.*



/**
 * Model Interface
 */

interface Model
{
    val id : UUID

    val name : String

    val modelObject : Model

    fun onLoad()
}


interface SumModel
{
    fun functor() : Func<*>

    val sumModelObject : SumModel
}
