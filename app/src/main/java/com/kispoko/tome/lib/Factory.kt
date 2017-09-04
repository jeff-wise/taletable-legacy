
package com.kispoko.tome.lib


import lulo.document.SchemaDoc
import lulo.value.ValueParser



/**
 * Factory Interface
 */
interface Factory<A>
{
    fun fromDocument(doc: SchemaDoc): ValueParser<A>
}
