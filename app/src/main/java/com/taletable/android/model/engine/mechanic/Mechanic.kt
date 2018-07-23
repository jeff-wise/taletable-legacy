
package com.taletable.android.model.engine.mechanic


import com.taletable.android.db.*
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.ProdType
import com.taletable.android.lib.orm.RowValue9
import com.taletable.android.lib.orm.schema.CollValue
import com.taletable.android.lib.orm.schema.MaybePrimValue
import com.taletable.android.lib.orm.schema.PrimValue
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.model.engine.variable.Variable
import com.taletable.android.model.engine.variable.VariableId
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable
import java.util.*



/**
 * Mechanic
 */
data class Mechanic(override val id : UUID,
                    val mechanicId : MechanicId,
                    val label : MechanicLabel,
                    val description : MechanicDescription,
                    val summary : MechanicSummary,
                    val annotation : Maybe<MechanicAnnotation>,
                    val categoryId : MechanicCategoryId,
                    val mechanicType : MechanicType,
                    val requirements : MutableList<VariableId>,
                    val variables : MutableList<Variable>)
                     : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(mechanicId: MechanicId,
                label : MechanicLabel,
                description : MechanicDescription,
                summary : MechanicSummary,
                annotation : Maybe<MechanicAnnotation>,
                categoryId : MechanicCategoryId,
                mechanicType : MechanicType,
                requirements : List<VariableId>,
                variables : List<Variable>)
        : this(UUID.randomUUID(),
               mechanicId,
               label,
               description,
               summary,
               annotation,
               categoryId,
               mechanicType,
               requirements.toMutableList(),
               variables.toMutableList())


    companion object : Factory<Mechanic>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<Mechanic> = when (doc)
        {
            is DocDict ->
            {
                apply(::Mechanic,
                      // Mechanic Id
                      doc.at("id") ap { MechanicId.fromDocument(it) },
                      // Label
                      doc.at("label") ap { MechanicLabel.fromDocument(it) },
                      // Description
                      doc.at("description") ap { MechanicDescription.fromDocument(it) },
                      // Summary
                      doc.at("summary") ap { MechanicSummary.fromDocument(it) },
                      // Annotation
                      split(doc.maybeAt("annotation"),
                            effValue<ValueError,Maybe<MechanicAnnotation>>(Nothing()),
                            { apply(::Just, MechanicAnnotation.fromDocument(it)) }),
                      // Category Id
                      doc.at("category_id") ap { MechanicCategoryId.fromDocument(it) },
                      // Category Id
                      split(doc.maybeAt("mechanic_type"),
                            effValue<ValueError,MechanicType>(MechanicType.Auto),
                            { MechanicType.fromDocument(it) }),
                      // Requirements
                      split(doc.maybeList("requirements"),
                            effValue(listOf()),
                            { docList -> docList.map { VariableId.fromDocument(it) }}),
                      // Variables
                      doc.list("variables") ap { docList ->
                          docList.map { Variable.fromDocument(it) }
                      })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.mechanicId().toDocument(),
        "label" to this.label().toDocument(),
        "description" to this.description().toDocument(),
        "summary" to this.summary().toDocument(),
        "category_id" to this.categoryId().toDocument(),
        "mechanic_type" to this.mechanicType().toDocument(),
        "requirements" to DocList(this.requirements().map { it.toDocument() }),
        "variables" to DocList(this.variables().map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun mechanicId() : MechanicId = this.mechanicId


    fun label() : MechanicLabel = this.label


    fun labelString() : String = this.label.value


    fun description() : MechanicDescription = this.description


    fun descriptionString() : String = this.description.value


    fun summary() : MechanicSummary = this.summary


    fun summaryString() : String = this.summary.value


    fun annotation() : Maybe<MechanicAnnotation> = this.annotation


    fun categoryId() : MechanicCategoryId = this.categoryId


    fun mechanicType() : MechanicType = this.mechanicType


    fun requirements() : List<VariableId> = this.requirements


    fun variables() : List<Variable> = this.variables


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_MechanicValue =
        RowValue9(mechanicTable,
                  PrimValue(this.mechanicId),
                  PrimValue(this.label),
                  PrimValue(this.description),
                  PrimValue(this.summary),
                  MaybePrimValue(this.annotation),
                  PrimValue(this.categoryId),
                  PrimValue(this.mechanicType),
                  PrimValue(MechanicRequirements(this.requirements)),
                  CollValue(this.variables))

}


/**
 * Mechanic Id
 */
data class MechanicId(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<MechanicId>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<MechanicId> = when (doc)
        {
            is DocText -> effValue(MechanicId(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Mechanic Label
 */
data class MechanicLabel(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<MechanicLabel>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<MechanicLabel> = when (doc)
        {
            is DocText -> effValue(MechanicLabel(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})
}


/**
 * Mechanic Description
 */
data class MechanicDescription(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<MechanicDescription>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<MechanicDescription> = when (doc)
        {
            is DocText -> effValue(MechanicDescription(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})
}


/**
 * Mechanic Type
 */
sealed class MechanicType : ToDocument, SQLSerializable, Serializable
{

    object Auto : MechanicType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "auto" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("auto")
    }


    object Option : MechanicType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "option" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("option")
    }


    object OptionSelected : MechanicType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "option_selected" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("option_selected")
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<MechanicType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "auto"            -> effValue<ValueError,MechanicType>(MechanicType.Auto)
                "option"          -> effValue<ValueError,MechanicType>(MechanicType.Option)
                "option_selected" -> effValue<ValueError,MechanicType>(MechanicType.OptionSelected)
                else              -> effError<ValueError,MechanicType>(
                                         UnexpectedValue("MechanicType", doc.text, doc.path))
            }
            else                  -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}


/**
 * Mechanic Summary
 */
data class MechanicSummary(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<MechanicSummary>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<MechanicSummary> = when (doc)
        {
            is DocText -> effValue(MechanicSummary(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Mechanic Annotation
 */
data class MechanicAnnotation(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<MechanicAnnotation>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<MechanicAnnotation> = when (doc)
        {
            is DocText -> effValue(MechanicAnnotation(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}



/**
 * Mechanic Requirements
 */
data class MechanicRequirements(val variableIds : List<VariableId>)
                : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<MechanicRequirements>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<MechanicRequirements> = when (doc)
        {
            is DocList -> apply(::MechanicRequirements,
                                   doc.map { VariableId.fromDocument(it) })
            else       -> effError(UnexpectedType(DocType.LIST, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue =
            SQLText({ variableIds.map { it.toString() }.joinToString("")  })

}



//
//
//
//    private void initializeFunctors()
//    {
//
//        // Name
//        this.name.setName("name");
//        this.name.setLabelId(R.string.mechanic_field_name_label);
//        this.name.setDescriptionId(R.string.mechanic_field_name_description);
//
//        // Label
//        this.label.setName("label");
//        this.label.setLabelId(R.string.mechanic_field_label_label);
//        this.label.setDescriptionId(R.string.mechanic_field_label_description);
//
//        // Summary
//        this.summary.setName("summary");
//        this.summary.setLabelId(R.string.mechanic_field_summary_label);
//        this.summary.setDescriptionId(R.string.mechanic_field_summary_description);
//
//        // Description
//        this.description.setName("description");
//        this.description.setLabelId(R.string.mechanic_field_description_label);
//        this.description.setDescriptionId(R.string.mechanic_field_description_description);
//
//        // Category
//        this.category.setName("category");
//        this.category.setLabelId(R.string.mechanic_field_category_label);
//        this.category.setDescriptionId(R.string.mechanic_field_category_description);
//
//        // Requirements
//        this.requirements.setName("requirements");
//        this.requirements.setLabelId(R.string.mechanic_field_reqs_label);
//        this.requirements.setDescriptionId(R.string.mechanic_field_reqs_description);
//
//        // Variables
//        this.variables.setName("variables");
//        this.variables.setLabelId(R.string.mechanic_field_variables_label);
//        this.variables.setDescriptionId(R.string.mechanic_field_variables_description);
//    }
//
//
//    /**
//     * Called when there is an update to one of the mechanic's requirement variables. Checks to
//     * see if the active status of the mechanic has changed.
//     */
//    public UpdateStatus onRequirementUpdate()
//    {
//        boolean isActive = true;
//
//        for (String requirement : this.requirements())
//        {
//            if (State.hasVariable(requirement))
//            {
//                VariableUnion variableUnion = State.variableWithName(requirement);
//
//                if (variableUnion.type() != VariableType.BOOLEAN) {
//                    ApplicationFailure.mechanic(
//                            MechanicException.nonBooleanRequirement(
//                                    new NonBooleanRequirementError(this.name(), requirement)));
//                    // TODO add to user programming errors
//                    continue;
//                }
//
//                if (!variableUnion.booleanVariable().value()) {
//                    isActive = false;
//                    break;
//                }
//            }
//            else
//            {
//                isActive = false;
//                break;
//            }
//
//        }
//
//        // If was active and is now inactive
//        if (this.active && !isActive)
//        {
//            this.removeFromState();
//            return UpdateStatus.REMOVED_FROM_STATE;
//        }
//        // If was not active and is now active
//        else if (!this.active && isActive)
//        {
//            this.addToState();
//            return UpdateStatus.ADDED_TO_STATE;
//        }
//
//        return UpdateStatus.NO_CHANGE;
//    }
//
//
//    /**
//     * Add every variable in the mechanic to the state (if the mechanic is active).
//     */
//    private void addToState()
//    {
//        this.active = true;
//
//        for (VariableUnion variableUnion : this.variables()) {
//            State.addVariable(variableUnion);
//        }
//    }
//
//
//    /**
//     * Remove the mechanic from the state. Remove each mechanic variable from the state.
//     */
//    private void removeFromState()
//    {
//        this.active = false;
//
//        for (VariableUnion variableUnion : this.variables()) {
//            State.removeVariable(variableUnion.variable().name());
//        }
//    }
//
//
//    /**
//     * Check the requirement variables to make sure they are valid.
//     */
//    private void validateRequirements()
//    {
//        // TODO need way to analyze variable before added to state.
//    }
//
//
//    // UPDATE STATUS
//    // ------------------------------------------------------------------------------------------
//
//    public enum UpdateStatus
//    {
//        ADDED_TO_STATE,
//        REMOVED_FROM_STATE,
//        NO_CHANGE
//    }
//
//}
