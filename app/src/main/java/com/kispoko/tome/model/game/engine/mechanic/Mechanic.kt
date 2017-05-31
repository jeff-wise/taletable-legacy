
package com.kispoko.tome.model.game.engine.mechanic


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.game.engine.variable.Variable
import com.kispoko.tome.model.game.engine.variable.VariableName
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.util.*



/**
 * Mechanic
 */
data class Mechanic(override val id : UUID,
                    val name : Func<MechanicName>,
                    val label : Func<MechanicLabel>,
                    val description : Func<MechanicDescription>,
                    val summary : Func<MechanicSummary>,
                    val category : Func<MechanicCategory>,
                    val requirements : Func<List<VariableName>>,
                    val variables : Coll<Variable>) : Model
{

    companion object : Factory<Mechanic>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Mechanic>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::Mechanic,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Name
                         doc.at("name") ap {
                             effApply(::Prim, MechanicName.fromDocument(it))
                         },
                         // Label
                         split(doc.maybeAt("label"),
                               nullEff<MechanicLabel>(),
                               { effApply(::Prim, MechanicLabel.fromDocument(it))}),
                         // Description
                         split(doc.maybeAt("description"),
                               nullEff<MechanicDescription>(),
                               { effApply(::Prim, MechanicDescription.fromDocument(it)) }),
                         // Summary
                         split(doc.maybeAt("summary"),
                               nullEff<MechanicSummary>(),
                               { effApply(::Prim, MechanicSummary.fromDocument(it)) }),
                         // Category
                         split(doc.maybeAt("category"),
                               nullEff<MechanicCategory>(),
                               { effApply(::Prim, MechanicCategory.fromDocument(it)) }),
                         // Requirements
                         doc.list("requirements") ap { docList ->
                             effApply(::Prim,
                                docList.map { VariableName.fromDocument(it) })
                         },
                         // Variables
                         doc.list("variables") ap { docList ->
                             effApply(::Coll,
                                     docList.map { Variable.fromDocument(it) })
                         })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

}


/**
 * Mechanic Name
 */
data class MechanicName(val value : String)
{

    companion object : Factory<MechanicName>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<MechanicName> = when (doc)
        {
            is DocText -> effValue(MechanicName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}


/**
 * Mechanic Label
 */
data class MechanicLabel(val value : String)
{

    companion object : Factory<MechanicLabel>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<MechanicLabel> = when (doc)
        {
            is DocText -> effValue(MechanicLabel(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}


/**
 * Mechanic Description
 */
data class MechanicDescription(val value : String)
{

    companion object : Factory<MechanicDescription>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<MechanicDescription> = when (doc)
        {
            is DocText -> effValue(MechanicDescription(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}


/**
 * Mechanic Summary
 */
data class MechanicSummary(val value : String)
{

    companion object : Factory<MechanicSummary>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<MechanicSummary> = when (doc)
        {
            is DocText -> effValue(MechanicSummary(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}


/**
 * Mechanic Category
 */
data class MechanicCategory(val value : String)
{

    companion object : Factory<MechanicCategory>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<MechanicCategory> = when (doc)
        {
            is DocText -> effValue(MechanicCategory(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
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
