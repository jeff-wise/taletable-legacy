
package com.kispoko.tome.lib.orm.sql



class Query()
{

    fun f() {

    }

}


fun query(init : Query.(x : Int) -> Unit) : Query {
    val q = Query()
    val y = 5
    q.init(y)
    return q
}


val x = query { x ->
    f()
    val y = x + 5
}


