
package com.taletable.android.lib.orm.schema


import com.taletable.android.lib.orm.ProdType
import com.taletable.android.lib.orm.SumType
import com.taletable.android.lib.orm.sql.SQLSerializable
import maybe.Maybe
import java.io.Serializable



// ---------------------------------------------------------------------------------------------
// COLUMN VALUE
// ---------------------------------------------------------------------------------------------

sealed class ColumnValue


// Column Value: Primitive
// ---------------------------------------------------------------------------------------------

data class PrimValue<A : SQLSerializable>(var prim : A)
                : ColumnValue(), Serializable


data class MaybePrimValue<A : SQLSerializable>(var maybePrim : Maybe<A>)
                : ColumnValue(), Serializable


// Column Value: Product
// ---------------------------------------------------------------------------------------------

data class ProdValue<A : ProdType>(var product : A)
                : ColumnValue(), Serializable


data class MaybeProdValue<A : ProdType>(var maybeProd : Maybe<A>)
                : ColumnValue(), Serializable


// Column Value: Sum
// ---------------------------------------------------------------------------------------------

data class SumValue<A : SumType>(var sum : A)
                : ColumnValue(), Serializable


data class MaybeSumValue<A : SumType>(var maybeSum : Maybe<A>)
                : ColumnValue(), Serializable


// Column Value: Coll
// ---------------------------------------------------------------------------------------------

data class CollValue<A : ProdType>(var list : List<A>)
                : ColumnValue(), Serializable


// ---------------------------------------------------------------------------------------------
// QUERY
// ---------------------------------------------------------------------------------------------

sealed class ColumnQuery


// Update: Primitive
// ---------------------------------------------------------------------------------------------

class PrimQuery<A : SQLSerializable>() : ColumnQuery(), Serializable


class MaybePrimQuery<A : SQLSerializable>() :  ColumnQuery(), Serializable


// Update: Product
// ---------------------------------------------------------------------------------------------

class ProdQuery<A : ProdType>() :  ColumnQuery(), Serializable


class MaybeProdQuery<A : ProdType>() :  ColumnQuery(), Serializable


// Update: Sum
// ---------------------------------------------------------------------------------------------

class SumQuery<A : SumType>() : ColumnQuery(), Serializable


class MaybeSumQuery<A : SumType>() : ColumnQuery(), Serializable


// Update: Coll
// ---------------------------------------------------------------------------------------------

class CollQuery<A : ProdType>() : ColumnQuery(), Serializable


