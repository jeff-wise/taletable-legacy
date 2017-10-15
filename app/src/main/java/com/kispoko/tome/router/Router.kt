
package com.kispoko.tome.router

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject





/**
 * Router
 *
 * Route updates to appropriate activity.
 */
object Router
{

    private val bus = PublishSubject.create<Any>().toSerialized()



    fun send(message : Message) {
        bus.onNext(message)
    }


    fun <T> listen(eventType : Class<T>): Observable<T> = bus.ofType(eventType)



}