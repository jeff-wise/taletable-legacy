
package com.kispoko.tome.lib.orm


import android.util.Log
import android.util.Log.d
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.model.Model
import effect.Eff
import effect.Identity
import kotlin.reflect.full.declaredMemberProperties



typealias ORMEff<A> = Eff<ORMError,Identity,A>



object ORM
{



    fun <A : Model> functors(model : A) : List<Func<*>> =
            model.javaClass.kotlin.declaredMemberProperties
                 .filter({ it.returnType is Func<*> })
                 .map({ it.get(model) as Func<*> })



}




object ORMLog
{

    var logLevel : ORMLogLevel = ORMLogLevel.DEBUG



    fun event(event : ORMEvent)
    {
        when(event)
        {
            is FunctorIsMissingNameField ->
            {
                if (logLevel >= ORMLogLevel.VERBOSE)
                    Log.d("***ORM EVENT", event.prettyEventMessage())
            }
            is FunctorIsMissingValueClassField ->
            {
                if (logLevel >= ORMLogLevel.VERBOSE)
                    Log.d("***ORM EVENT", event.prettyEventMessage())
            }
            is GeneratedTableDefinition ->
            {
                if (logLevel >= ORMLogLevel.DEBUG)
                    Log.d("***ORM EVENT", event.prettyEventMessage())
            }
        }
    }


}


enum class ORMLogLevel
{
    NORMAL,
    VERBOSE,
    DEBUG
}
