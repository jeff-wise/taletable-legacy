
package com.kispoko.tome.lib.orm


import com.kispoko.tome.lib.orm.schema.ColumnValue
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.run
import java.util.*



/**
 * ProdType Interface
 */

interface ProdType
{
    val id : UUID

    val prodTypeObject : ProdType


    fun onLoad()

    fun rowValue() : RowValue


    /**
     * Saves this product type. It runs non-recursive, saving the row only. It creates a
     * transaction for the update operation. It runs asynchronoulsy
     */
    suspend fun save() = run(CommonPool, {
        saveProdType(this, listOf(), false, true)
    })


    /**
     * Save recrusive
     */
    suspend fun saveAll() = run(CommonPool, {
        saveProdType(this, listOf(), true, true)
    })


//    fun oneToManyRelation(fieldName : String) : OneToManyRelation =
//        OneToManyRelation(this.tableName(), fieldName, this.id)

//    fun tableColumns() : List<String> = persistentFunctors().mapNotNull { it.name }
//
//
//    fun persistentFunctorsNamed() : List<Form<*>> =
//        this.persistentFunctors().filter { it.name != null }

//    fun persistentPrimFunctorsNamed() : List<Form<*>> =
//            this.persistentFunctors()
//                    .filter { it.name != null }
//                    .filter { if }
//

//    companion object
//    {
//
//        private val functorsCache : MutableMap<String,List<Form<*>>> = mutableMapOf()
//
//
//        fun <A : ProdType> functors(prodType : A) : List<Form<*>>
//        {
//            val startTime = System.nanoTime()
//            val functors = prodType.javaClass.kotlin.declaredMemberProperties
//                                .filter({ it.returnType.jvmErasure.isSubclassOf(Form::class) })
//                                .map({ it.get(prodType) as Form<*> })
//
//            val endTime = System.nanoTime()
//            ORMLog.timeSpentInReflection += (endTime - startTime)
//            //Log.d("***MODEL", "functors time for ${prodType.name}: ${Util.timeDifferenceString(startTime, endTime)}")
//
//            return functors
//        }
//    }

}


interface SumType
{
    // fun functor() : Val<*>

    fun columnValue() : ColumnValue

    fun case() : String

    val sumModelObject : SumType
}

