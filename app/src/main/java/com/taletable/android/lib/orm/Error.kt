
package com.taletable.android.lib.orm



/**
 * ORM Error
 */
sealed class ORMError
{
    abstract fun errorMessage() : String

    abstract fun prettyErrorMessage() : String
}



object ModelClassDoesNotHaveName : ORMError()
{
    override fun errorMessage() : String = "ORM Error: ProdType class does not have a name."

    override fun prettyErrorMessage() : String = this.errorMessage()
}

