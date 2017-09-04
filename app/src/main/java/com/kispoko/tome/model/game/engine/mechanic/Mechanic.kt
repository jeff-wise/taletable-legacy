
package com.kispoko.tome.model.game.engine.mechanic


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLBlob
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.engine.variable.Variable
import com.kispoko.tome.model.game.engine.variable.VariableId
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList



/**
 * Mechanic
 */
data class Mechanic(override val id : UUID,
                    val mechanicId : Prim<MechanicId>,
                    val label : Prim<MechanicLabel>,
                    val description : Prim<MechanicDescription>,
                    val summary : Prim<MechanicSummary>,
                    val categoryId : Prim<MechanicCategoryId>,
                    val requirements : Prim<MechanicRequirements>,
                    val variables : Conj<Variable>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INITIALIZATION
    // -----------------------------------------------------------------------------------------

    init
    {
        this.mechanicId.name                    = "mechanic_name"
        this.label.name                         = "label"
        this.description.name                   = "description"
        this.summary.name                       = "summary"
        this.categoryId.name                    = "category_id"
        this.requirements.name                  = "requirements"
        this.variables.name                     = "variables"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(mechanicId: MechanicId,
                label : MechanicLabel,
                description : MechanicDescription,
                summary : MechanicSummary,
                categoryId : MechanicCategoryId,
                requirements : MechanicRequirements,
                variables : MutableSet<Variable>)
        : this(UUID.randomUUID(),
               Prim(mechanicId),
               Prim(label),
               Prim(description),
               Prim(summary),
               Prim(categoryId),
               Prim(requirements),
               Conj(variables))


    companion object : Factory<Mechanic>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<Mechanic> = when (doc)
        {
            is DocDict ->
            {
                effApply(::Mechanic,
                         // Mechanic Id
                         doc.at("id") ap { MechanicId.fromDocument(it) },
                         // Label
                         doc.at("label") ap { MechanicLabel.fromDocument(it) },
                         // Description
                         doc.at("description") ap { MechanicDescription.fromDocument(it) },
                         // Summary
                         doc.at("summary") ap { MechanicSummary.fromDocument(it) },
                         // Category Id
                         doc.at("category_id") ap { MechanicCategoryId.fromDocument(it) },
                         // Requirements
                         doc.at("requirements") ap { MechanicRequirements.fromDocument(it) },
                         // Variables
                         doc.list("variables") ap { docList ->
                             docList.mapSetMut { Variable.fromDocument(it) }
                         })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun mechanicId() : MechanicId = this.mechanicId.value

    fun label() : String = this.label.value.value

    fun description() : String = this.description.value.value

    fun summary() : String = this.summary.value.value

    fun categoryId() : MechanicCategoryId = this.categoryId.value

    fun requirements() : Set<VariableId> = this.requirements.value.variables.toSet()

    fun variables() : Set<Variable> = this.variables.set


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "mechanic"

    override val modelObject = this

}


/**
 * Mechanic Id
 */
data class MechanicId(val value : String) : SQLSerializable, Serializable
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
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Mechanic Label
 */
data class MechanicLabel(val value : String) : SQLSerializable, Serializable
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
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})
}


/**
 * Mechanic Description
 */
data class MechanicDescription(val value : String) : SQLSerializable, Serializable
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
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})
}


/**
 * Mechanic Summary
 */
data class MechanicSummary(val value : String) : SQLSerializable, Serializable
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
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})
}


/**
 * Mechanic Category
 */
data class MechanicCategory(override val id : UUID,
                            val categoryId : Prim<MechanicCategoryId>,
                            val label : Prim<MechanicCategoryLabel>,
                            val description : Prim<MechanicCategoryDescription>)
                             : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INITIALIZATION
    // -----------------------------------------------------------------------------------------

    init
    {
        this.categoryId.name    = "category_id"
        this.label.name         = "label"
        this.description.name   = "description"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(categoryId : MechanicCategoryId,
                label : MechanicCategoryLabel,
                description : MechanicCategoryDescription)
        : this(UUID.randomUUID(),
               Prim(categoryId),
               Prim(label),
               Prim(description))


    companion object : Factory<MechanicCategory>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<MechanicCategory> = when (doc)
        {
            is DocDict ->
            {
                effApply(::MechanicCategory,
                         // Category Id
                         doc.at("id") ap { MechanicCategoryId.fromDocument(it) },
                         // Label
                         doc.at("label") ap { MechanicCategoryLabel.fromDocument(it) },
                         // Description
                         doc.at("description") ap { MechanicCategoryDescription.fromDocument(it) }
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun categoryId() : MechanicCategoryId = this.categoryId.value

    fun label() : String = this.label.value.value

    fun description() : String = this.description.value.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "mechanic_category"

    override val modelObject = this

}


/**
 * Mechanic Category Id
 */
data class MechanicCategoryId(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<MechanicCategoryId>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<MechanicCategoryId> = when (doc)
        {
            is DocText -> effValue(MechanicCategoryId(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})
}


/**
 * Mechanic Category Label
 */
data class MechanicCategoryLabel(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<MechanicCategoryLabel>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<MechanicCategoryLabel> = when (doc)
        {
            is DocText -> effValue(MechanicCategoryLabel(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})
}


/**
 * Mechanic Category Description
 */
data class MechanicCategoryDescription(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<MechanicCategoryDescription>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<MechanicCategoryDescription> = when (doc)
        {
            is DocText -> effValue(MechanicCategoryDescription(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})
}


/**
 * Mechanic Requirements
 */
data class MechanicRequirements(val variables : ArrayList<VariableId>)
                : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<MechanicRequirements>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<MechanicRequirements> = when (doc)
        {
            is DocList -> effApply(::MechanicRequirements,
                                   doc.mapArrayList { VariableId.fromDocument(it) })
            else       -> effError(UnexpectedType(DocType.LIST, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({SerializationUtils.serialize(this.variables)})

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
