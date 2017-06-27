
package com.kispoko.tome.lib.functor


import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.model.SumModel
import com.kispoko.tome.lib.orm.ORM
import com.kispoko.tome.lib.orm.Schema
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLValue
import effect.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.run
import java.io.Serializable
import kotlin.reflect.KClass



/**
 * Functor
 */
sealed class Func<out A>(open val value : A,
                         open var name : String? = null) : Serializable
{

    fun isNull() : Boolean = this.value == null
}


/**
 * Primitive Functor
 */
data class Prim<A : SQLSerializable>(override var value : A, override var name : String?)
            : Func<A>(value), Serializable, SQLSerializable
{

    private var isDefault : Boolean = false

    constructor(value : A) : this(value, null)

    companion object
    {
        fun <A : SQLSerializable> default(defaultValue : A) : Prim<A>
        {
            val prim = Prim(defaultValue)
            prim.isDefault = true
            return prim
        }
    }

    fun isDefault() : Boolean = this.isDefault

    override fun asSQLValue() : SQLValue = this.value.asSQLValue()

}


data class Sum<A : SumModel>(override var value : A, override var name : String?)
            : Func<A>(value, name)
{

    constructor(value : A) : this(value, null)

}


/**
 * Product Functor
 */
data class Comp<A : Model> (override var value : A,
                            override var name : String?)
        : Func<A>(value), Serializable
{

    private var isDefault : Boolean = false

    constructor(value : A) : this(value, null)

    companion object
    {
        fun <A : Model> default(defaultValue : A) : Comp<A>
        {
            val comp = Comp(defaultValue)
            comp.isDefault = true
            return comp
        }
    }

    fun isDefault() : Boolean = this.isDefault


    suspend fun saveAsync(recursive : Boolean, isTransaction : Boolean = false) = run(CommonPool, {
        this.save(recursive, isTransaction)
    })


    fun save(recursive : Boolean, isTransaction : Boolean = false)
    {
        ORM.saveModel(this.value, null, recursive, isTransaction)
    }

}


/**
 * Collection Functor
 */
data class Coll<A>(val list : MutableList<A>) : Func<MutableList<A>>(list), Serializable
        where A : Model
{

    fun save(recursive : Boolean, isTransaction : Boolean = false)
    {
        this.list.forEach {
            ORM.saveModel(it, null, recursive, isTransaction)
        }

    }
}


/**
 * Collection Functor
 */
data class CollS<A> (val list : MutableList<A>, val valueClass : KClass<A>? = null)
    : Func<MutableList<A>>(list), Serializable where A : Model, A : Comparable<A>
{

    init
    {
        this.list.sorted()
    }


    fun save(recursive : Boolean, isTransaction : Boolean = false)
    {
        this.list.forEach {
            ORM.saveModel(it, null, recursive, isTransaction)
        }

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



fun <A : SQLSerializable> maybeLiftPrim(mValue : Maybe<A>) : Maybe<Prim<A>> = when(mValue)
{
    is Just -> maybeApply(::Prim, mValue)
    else    -> Nothing()
}


fun <A : SumModel> maybeLiftSum(mValue : Maybe<A>) : Maybe<Sum<A>> = when(mValue)
{
    is Just -> maybeApply(::Sum, mValue)
    else    -> Nothing()
}


fun <A : Model> maybeLiftComp(mValue : Maybe<A>) : Maybe<Comp<A>> = when(mValue)
{
    is Just -> maybeApply(::Comp, mValue)
    else    -> Nothing()
}


fun <A : SQLSerializable> getMaybePrim(mPrim : Maybe<Prim<A>>) : A? = when (mPrim)
{
    is Just -> mPrim.value.value
    else    -> null
}


fun <A : Model> getMaybeComp(mComp : Maybe<Comp<A>>) : Maybe<A> = when (mComp)
{
    is Just -> Just(mComp.value.value)
    else    -> Nothing()
}


fun <A : SumModel> getMaybeSum(mSum : Maybe<Sum<A>>) : Maybe<A> = when (mSum)
{
    is Just -> Just(mSum.value.value)
    else    -> Nothing()
}
