
package com.kispoko.tome.lib.functor


import com.kispoko.tome.lib.model.Model
import effect.Eff
import effect.Identity
import effect.effValue
import lulo.value.ValueError



/**
 * Functor
 */
sealed class Func<out A>(open val value : A?)
{
    fun isNull() : Boolean = this.value == null
}


/**
 * Primitive Functor
 */
data class Prim<A : Any>(override var value : A) : Func<A>(value)


/**
 * Collection Functor
 */
data class Comp<A : Model> (override var value : A) : Func<A>(value)


/**
 * Collection Functor
 */
data class Coll<A : Model>(val list : MutableList<A>) : Func<MutableList<A>>(list)


/**
 * Collection Functor
 */
data class CollS<A> (val list : MutableList<A>) where A : Model, A : Comparable<A>
{
    init
    {
        this.list.sorted()
    }
}



/**
 * Mutable Collection Functor
 */
data class CollM<A : Model>(val list : MutableList<A>) : Func<MutableList<A>>(list)


/**
 * Set Functor
 */
data class Conj<A>(val set : MutableSet<A>) : Func<MutableSet<A>>(set)


/**
 * Mutable Set Functor
 */
//data class ConjM<A>(val set : MutableSet<A>) : Func<Set<A>>(set)



class Null<out A : Any> : Func<A>(null)
{

    override fun equals(other: Any?) : Boolean
    {
        if (other is Null<*>)
            return true

        return super.equals(other)
    }

    override fun hashCode() : Int
    {
        return 99999999
    }

}


fun <A : Any> nullEff() : Eff<ValueError, Identity,Func<A>> = effValue(Null())
