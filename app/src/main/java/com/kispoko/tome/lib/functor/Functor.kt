
package com.kispoko.tome.lib.functor


import com.kispoko.tome.lib.model.Model
import effect.Eff
import effect.Identity
import effect.effValue
import lulo.value.ValueError



/**
 * Functor
 */
sealed class Func<A>(open val value : A?)


/**
 * Primitive Functor
 */
data class Prim<A : Any>(override var value : A) : Func<A>(value)
{
}


/**
 * Collection Functor
 */
data class Comp<A : Model> (override var value : A) : Func<A>(value)
{

}


/**
 * Collection Functor
 */
data class Coll<A : Model>(var list: List<A>) : Func<List<A>>(list)
{

}


class Null<A : Any> : Func<A>(null)


fun <A : Any> nullEff() : Eff<ValueError, Identity,Func<A>> = effValue(Null())
