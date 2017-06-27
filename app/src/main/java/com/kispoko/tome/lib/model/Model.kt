
package com.kispoko.tome.lib.model


import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.yaml.YamlBuilder.map
import org.apache.commons.collections4.CollectionUtils.filter
import java.util.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure


/**
 * Model Interface
 */

interface Model
{
    val id : UUID

    val name : String

    val modelObject : Model

    fun onLoad()


    companion object
    {

        // Getting the properties of the same class over and over again turned out to be
        // *very* expensive. As soon as I added this cache, the time for saving a basic one-page
        // sheet went from 24 seconds to 3 seconds.
        private val functorsCache : MutableMap<String,List<Func<*>>> = mutableMapOf()


        fun <A : Model> functors(model : A) : List<Func<*>>
        {
            if (functorsCache.containsKey(model.name))
            {
                return functorsCache[model.name]!!
            }
            else
            {
                val functors = model.javaClass.kotlin.declaredMemberProperties
                                    .filter({ it.returnType.jvmErasure.isSubclassOf(Func::class) })
                                    .map({ it.get(model) as Func<*> })
                this.functorsCache[model.name] = functors
                return functors
            }
        }
    }


}


interface SumModel
{
    fun functor() : Func<*>

    val sumModelObject : SumModel
}

