
package com.kispoko.tome.lib

import lulo.document.SpecDoc
import lulo.value.ValueParser


/**
 * Factory Interface
 */
interface Factory<A>
{
    fun fromDocument(doc : SpecDoc) : ValueParser<A>
}
