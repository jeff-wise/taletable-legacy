
package com.kispoko.tome.lib.functor


import com.kispoko.tome.lib.model.Model
import effect.*
import lulo.value.ValueError
import java.io.Serializable



/**
 * Functor
 */
sealed class Func<out A>(open val value : A?) : Serializable
{
    fun isNull() : Boolean = this.value == null
}


/**
 * Primitive Functor
 */
data class Prim<A : Any>(override var value : A) : Func<A>(value), Serializable


/**
 * Collection Functor
 */
data class Comp<A : Model> (override var value : A) : Func<A>(value), Serializable


/**
 * Collection Functor
 */
data class Coll<A>(val list : MutableList<A>) : Func<MutableList<A>>(list), Serializable


/**
 * Collection Functor
 */
data class CollS<A> (val list : MutableList<A>) : Serializable where A : Model, A : Comparable<A>
{
    init
    {
        this.list.sorted()
    }
}



/**
 * Mutable Collection Functor
 */
data class CollM<A : Model>(val list : MutableList<A>) : Func<MutableList<A>>(list), Serializable


/**
 * Set Functor
 */
data class Conj<A>(val set : MutableSet<A>) : Func<MutableSet<A>>(set), Serializable


/**
 * Mutable Set Functor
 */
//data class ConjM<A>(val set : MutableSet<A>) : Func<Set<A>>(set)



class Null<out A : Any> : Func<A>(null), Serializable
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



fun <A : Any> maybeLiftPrim(mValue : Maybe<A>) : Maybe<Prim<A>> = when(mValue)
{
    is Just -> maybeApply(::Prim, mValue)
    else    -> Nothing()
}


fun <A : Any> getMaybePrim(mPrim : Maybe<Prim<A>>) : A? = when (mPrim)
{
    is Just -> mPrim.value.value
    else    -> null
}
