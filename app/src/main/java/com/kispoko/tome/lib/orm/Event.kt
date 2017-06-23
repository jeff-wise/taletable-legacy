package com.kispoko.tome.lib.orm



/**
 * ORM Event
 */
sealed class ORMEvent
{
    abstract fun eventMessage() : String

    abstract fun prettyEventMessage() : String
}


data class FunctorIsMissingNameField(val parentModelName : String) : ORMEvent()
{
    override fun eventMessage() : String = "Functor Will Not Be Serialized: " +
                                           "Missing name: " +
                                           "(Parent Model : $parentModelName)"

    override fun prettyEventMessage() : String = """
            Functor Will Not Be Serialized:
                 Missing Name Field
                 Parent Model Name: $parentModelName
            """
}


data class FunctorIsMissingValueClassField(val parentModelName : String) : ORMEvent()
{
    override fun eventMessage() : String = "Functor Will Not Be Serialized: " +
                                           "Missing Value Class: " +
                                           "(Parent Model : $parentModelName)"

    override fun prettyEventMessage() : String = """
            Functor Will Not Be Serialized:
                 Missing Value Class Field
                 Parent Model Name: $parentModelName
            """
}


data class GeneratedTableDefinition(val tableDefString : String) : ORMEvent()
{
    override fun eventMessage() : String = "Generated Table Definition: " + tableDefString

    override fun prettyEventMessage() : String = """
            Generated Table Definition:
                $tableDefString
            """
}
