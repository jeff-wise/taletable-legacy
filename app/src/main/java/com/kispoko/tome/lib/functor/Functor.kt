
package com.kispoko.tome.lib.functor


import com.kispoko.tome.lib.model.Model



sealed class Func<A>(open val value : A?)



/**
 * Primitive Functor
 */
data class Prim<A : Any>(override val value : A) : Func<A>(value)
{
}


/**
 * Collection Functor
 */
data class Comp<A : Model> (override val value : A) : Func<A>(value)
{

}


/**
 * Collection Functor
 */
data class Coll<A : Model>(override val value : List<A>) : Func<List<A>>(value)
{

}


class Null<A : Any> : Func<A>(null)
