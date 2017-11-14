
package com.kispoko.tome.lib.functor


import com.kispoko.tome.lib.model.ProdType
import com.kispoko.tome.lib.model.SumType
import com.kispoko.tome.lib.orm.OneToManyRelationRow
import com.kispoko.tome.lib.orm.saveProdType
import com.kispoko.tome.lib.orm.sql.SQLNull
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLValue
import effect.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.run
import java.io.Serializable



/**
 * Functor
 */
sealed class Val<A> : Serializable // (open val name : String) : Serializable
{

    //fun isNull() : Boolean = this.value == null
}


/**
 * Primitive Functor
 */
data class Prim<A : SQLSerializable>(var value : A)
            : Val<A>(), Serializable, SQLSerializable
{

    private var isDefault : Boolean = false

    //constructor(value : A) : this(value, null)

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


//    fun save(parentProdType : ProdType) {
//        this.name?.let {
//            savePrim(this.asSQLValue(), it, parentProdType)
//        }
//    }

}


/**
 * Maybe Prim
 */
data class MaybePrim<A : SQLSerializable>(var value : Maybe<A>)
            : Val<A>(), Serializable, SQLSerializable
{

    private var isDefault : Boolean = false

//    companion object
//    {
//        fun <A : SQLSerializable> default(defaultValue : A) : Prim<A>
//        {
//            val prim = Prim(defaultValue)
//            prim.isDefault = true
//            return prim
//        }
//    }

    fun isDefault() : Boolean = this.isDefault

    override fun asSQLValue() : SQLValue
    {
        val currentValue = this.value
        return when (currentValue) {
            is Just    -> currentValue.value.asSQLValue()
            else -> SQLNull
        }
    }

}



data class Sum<A : SumType>(var value : A)
            : Val<A>()
{

    //constructor(value : A) : this(value, null)

}

data class MaybeSum<A : SumType>(var value : Maybe<A>)
            : Val<A>()
{

    //constructor(value : A) : this(value, null)

}



/**
 * Product Functor
 */
data class Prod<A : ProdType> (var value : A)
        : Val<A>(), Serializable
{

    private var isDefault : Boolean = false

//    constructor(value : A) : this(value, null)

    companion object
    {
        fun <A : ProdType> default(defaultValue : A) : Prod<A>
        {
            val comp = Prod(defaultValue)
            comp.isDefault = true
            return comp
        }
    }

    fun isDefault() : Boolean = this.isDefault


    suspend fun saveAsync(recursive : Boolean, isTransaction : Boolean = false) = run(CommonPool, {
        this.save(recursive, isTransaction)
    })


    fun save(recursive : Boolean, isTransaction : Boolean = false) {
        saveProdType(this.value, listOf(), recursive, isTransaction)
    }

}



/**
 * Maybe Product Functor
 */
data class MaybeProd<A : ProdType> (var value : Maybe<A>)
        : Val<A>(), Serializable
{

    private var isDefault : Boolean = false

//    constructor(value : A) : this(value, null)

//    companion object
//    {
//        fun <A : ProdType> default(defaultValue : A) : Prod<A>
//        {
//            val comp = Prod(defaultValue)
//            comp.isDefault = true
//            return comp
//        }
//    }

    fun isDefault() : Boolean = this.isDefault


    suspend fun saveAsync(recursive : Boolean, isTransaction : Boolean = false) = run(CommonPool, {
        this.save(recursive, isTransaction)
    })


    fun save(recursive : Boolean, isTransaction : Boolean = false)
    {
        //saveProdType(this.value, setOf(), recursive, isTransaction)
    }

}




//interface CollFunc
//{
//    fun save(recursive : Boolean, oneToManyRelation : OneToManyRelation)
//
//    fun save(recursive : Boolean, parentProdType : ProdType)
//
//}


/**
 * Collection Functor
 */
class Coll<A>(val list : List<A>) : Val<A>(), Serializable
        where A : ProdType
{

    private val mutableList : MutableList<A> = list.toMutableList()


    fun save(recursive : Boolean, oneToManyRelationRow : OneToManyRelationRow) {
        this.list.forEach {
            saveProdType(it, listOf(oneToManyRelationRow), recursive, false)
        }
    }


    //constructor(list : List<A>) : this(list.toMutableList())


    fun save(recursive : Boolean, parentProdType : ProdType) {
//        val name = this.name
//        if (name != null) {
//            val relation = OneToManyRelation(parentProdType.tableName(), name, parentProdType.id)
//            this.save(recursive, relation)
//        }
    }

}


val coll : Coll<*> = Coll(listOf())
//val x = coll.li


/**
 * Collection Functor
 */
//data class CollS<A> (val list : MutableList<A>, val valueClass : KClass<A>? = null)
//    : Val<MutableList<A>>(list), Serializable, CollFunc where A : ProdType, A : Comparable<A>
//{
//
//    init
//    {
//        this.list.sorted()
//    }
//
//
//    override fun save(recursive : Boolean, oneToManyRelation : OneToManyRelation)
//    {
//        this.list.forEach {
//            saveProdType(it, setOf(oneToManyRelation), recursive, false)
//        }
//    }
//
//
//    override fun save(recursive: Boolean, parentProdType : ProdType) {
//        val name = this.name
//        if (name != null) {
//            val relation = OneToManyRelation(parentProdType.tableName(), name, parentProdType.id)
//            this.save(recursive, relation)
//        }
//    }
//
//}



/**
 * Mutable Collection Functor
 */
//data class CollM<A : ProdType>(val list : MutableList<A>) : Val<MutableList<A>>(list), Serializable


/**
 * Set Functor
 */
//data class Conj<A>(val set : MutableSet<A>) : Form<MutableSet<A>>(set), Serializable

//data class Conj<A>(val set : MutableSet<A>) : Val<MutableSet<A>>(set), Serializable, CollFunc
//        where A : ProdType
//{
//
//    override fun save(recursive : Boolean, oneToManyRelation : OneToManyRelation)
//    {
//        this.set.forEach {
//            saveProdType(it, setOf(oneToManyRelation), recursive, false)
//        }
//    }
//
//
//    override fun save(recursive: Boolean, parentProdType : ProdType) {
//        val name = this.name
//        if (name != null) {
//            val relation = OneToManyRelation(parentProdType.tableName(), name, parentProdType.id)
//            this.save(recursive, relation)
//        }
//    }
//
//}



fun <A : SQLSerializable> maybeLiftPrim(mValue : Maybe<A>) : Maybe<Prim<A>> = when(mValue)
{
    is Just -> apply(::Prim, mValue)
    else    -> Nothing()
}


fun <A : SumType> maybeLiftSum(mValue : Maybe<A>) : Maybe<Sum<A>> = when(mValue)
{
    is Just -> apply(::Sum, mValue)
    else    -> Nothing()
}


fun <A : ProdType> maybeLiftComp(mValue : Maybe<A>) : Maybe<Prod<A>> = when(mValue)
{
    is Just -> apply(::Prod, mValue)
    else    -> Nothing()
}


fun <A : SQLSerializable> getMaybePrim(mPrim : Maybe<Prim<A>>) : A? = when (mPrim)
{
    is Just -> mPrim.value.value
    else    -> null
}


fun <A : SQLSerializable> _getMaybePrim(mPrim : Maybe<Prim<A>>) : Maybe<A> = when (mPrim)
{
    is Just -> Just(mPrim.value.value)
    else    -> Nothing()
}


fun <A : ProdType> getMaybeComp(mComp : Maybe<Prod<A>>) : Maybe<A> = when (mComp)
{
    is Just -> Just(mComp.value.value)
    else    -> Nothing()
}


fun <A : SumType> getMaybeSum(mSum : Maybe<Sum<A>>) : Maybe<A> = when (mSum)
{
    is Just -> Just(mSum.value.value)
    else    -> Nothing()
}
