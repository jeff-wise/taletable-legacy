
package com.kispoko.tome.lib.functor;


import android.content.Context;
import android.widget.LinearLayout;

import com.kispoko.tome.lib.database.DatabaseException;
import com.kispoko.tome.lib.functor.error.FunctorAccessError;
import com.kispoko.tome.lib.functor.error.UninitializedFunctorError;
import com.kispoko.tome.lib.functor.form.Field;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.util.tuple.Tuple4;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



/**
 * Value
 */
public abstract class Functor<A> implements Serializable
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    protected A                 value;

    /**
     * A name for the functor. Used as a column name.
     */
    private   String            name;

    /**
     * The name of the functor in human-readable form. Used as the label for fields.
     */
    private   String            label;
    private   Integer           labelId;

    /**
     * A description of the functor's purpose. Used in fields.
     */
    private   String            description;
    private   Integer           descriptionId;

    private   OnUpdateListener  onUpdateListener;

    private   boolean           isDefault;

    private   boolean           isSaved;
    private   boolean           isRequired;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public Functor(A value)
    {
        this.value          = value;

        this.name           = null;

        this.label          = null;
        this.labelId        = null;

        this.description    = null;
        this.descriptionId  = null;

        this.isDefault      = false;
        this.isSaved        = false;
        this.isRequired     = false;
    }


    public Functor(A value, boolean isRequired)
    {
        this.value          = value;

        this.name           = null;

        this.label          = null;
        this.labelId        = null;

        this.description    = null;
        this.descriptionId  = null;

        this.isDefault      = false;
        this.isSaved        = false;
        this.isRequired     = isRequired;
    }


    // API
    // --------------------------------------------------------------------------------------

    // > State
    // --------------------------------------------------------------------------------------

    // ** Value
    // --------------------------------------------------------------------------------------

    public A getValue()
    {
        return this.value;
    }


    public void setValue(A newValue)
    {
        if (newValue != null)
        {
            this.value = newValue;

            if (this.onUpdateListener != null)
                this.onUpdateListener.onUpdate();
        }
    }


    // ** Is Default
    // --------------------------------------------------------------------------------------

    /**
     * If true, then the value in the functor is a default value (not set by user).
     * @return Is default?
     */
    public boolean isDefault()
    {
        return this.isDefault;
    }

    /**
     * Set whether or not the value in the functor is a default value.
     * @param isDefault Is default?
     */
    public void setIsDefault(boolean isDefault)
    {
        this.isDefault = isDefault;
    }


    // ** Update Listener
    // --------------------------------------------------------------------------------------

    public void setOnUpdateListener(OnUpdateListener onUpdateListener)
    {
        this.onUpdateListener = onUpdateListener;
    }


    // > Data
    // --------------------------------------------------------------------------------------

    // ** Is Null
    // --------------------------------------------------------------------------------------

    /**
     * Returns true if the data inside the value is null.
     * @return True if the data in the value is null, or false otherwise.
     */
    public boolean isNull()
    {
        return this.value == null;
    }


    // ** Name
    // --------------------------------------------------------------------------------------

    /**
     * The functor name.
     * @return the name.
     */
    public String name()
    {
        return this.name;
    }


    /**
     * Set the functor's name.
     * @param name The name.
     */
    public void setName(String name)
    {
        this.name = name;
    }


    // ** Label
    // -----------------------------------------------------------------------------------------

    /**
     * The functor label.
     * @return The label
     */
    public String label()
    {
        return this.label;
    }


    /**
     * Set the functor's label.
     * @param label The label.
     */
    public void setLabel(String label)
    {
        this.label = label;
    }


    // ** Label Id
    // -----------------------------------------------------------------------------------------

    /**
     * The label string resource id.
     * @return The id.
     */
    public Integer labelId()
    {
        return this.labelId;
    }


    /**
     * Set the label string resource id.
     * @param labelId The string resource id.
     */
    public void setLabelId(int labelId)
    {
        this.labelId = labelId;
    }


    // ** Description
    // -----------------------------------------------------------------------------------------

    /**
     * The functor's description.
     * @return The description.
     */
    public String description()
    {
        return this.description;
    }


    /**
     * Set the functor's description.
     * @param description The description.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }


    // ** Description Id
    // -----------------------------------------------------------------------------------------

    /**
     * The description string resource id.
     * @return The id.
     */
    public Integer descriptionId()
    {
        return this.descriptionId;
    }


    /**
     * Set the description string resource id.
     * @param descriptionId The string resource id.
     */
    public void setDescriptionId(int descriptionId)
    {
        this.descriptionId = descriptionId;
    }


    // ** Is Required
    // --------------------------------------------------------------------------------------

    public boolean isRequired()
    {
        return this.isRequired;
    }


    /**
     * Set the functor's isRequired flag.
     * @param isRequired Is required?
     */
    public void setIsRequired(boolean isRequired)
    {
        this.isRequired = isRequired;
    }


    // FUNCTOR PROPERTIES
    // ------------------------------------------------------------------------------------------

    /**
     * Collect all of the ModelLib's values and return them in data structures suitable for
     * further analysis.
     * @param model A ModelLib instance to get the values of.
     * @param <A> The model type.
     * @return The ModelLib's values, sorted.
     * @throws DatabaseException
     */
    public static <A> Tuple4<List<PrimitiveFunctor<?>>,
                             List<OptionFunctor<?>>,
                             List<ModelFunctor<?>>,
                             List<CollectionFunctor<?>>> propertyFunctors(A model)
                       throws FunctorException
    {
        // [1] Get all of the class's Value fields
        // --------------------------------------------------------------------------------------
        List<java.lang.reflect.Field> valueFields = new ArrayList<>();

        List<java.lang.reflect.Field> allFields = FieldUtils.getAllFieldsList(model.getClass());
        for (java.lang.reflect.Field field : allFields)
        {
            if (Functor.class.isAssignableFrom(field.getType()))
                valueFields.add(field);
        }

        // [2] Store the value fields by type and map to columns
        // --------------------------------------------------------------------------------------
        List<PrimitiveFunctor<?>>                primitiveFunctors  = new ArrayList<>();
        List<OptionFunctor<?>>                   optionFunctors     = new ArrayList<>();
        List<ModelFunctor<? extends Model>>      modelFunctors      = new ArrayList<>();
        List<CollectionFunctor<? extends Model>> collectionFunctors = new ArrayList<>();

        try
        {
            for (java.lang.reflect.Field field : valueFields)
            {
                Functor<?> functor = (Functor<?>) FieldUtils.readField(field, model, true);

                if (functor == null) {
                    throw FunctorException.uninitializedFunctor(
                            new UninitializedFunctorError(model.getClass().getName(),
                                                          field.getName()));
                }

                // Sort values by database value type
                if (PrimitiveFunctor.class.isAssignableFrom(field.getType()))
                {
                    PrimitiveFunctor primitiveValue = (PrimitiveFunctor) functor;

                    if (primitiveValue.name() == null)
                        primitiveValue.setName(field.getName());

                    primitiveFunctors.add(primitiveValue);
                }
                else if (OptionFunctor.class.isAssignableFrom(field.getType()))
                {
                    OptionFunctor optionFunctor = (OptionFunctor) functor;

                    if (optionFunctor.name() == null)
                        optionFunctor.setName(field.getName());

                    optionFunctors.add(optionFunctor);

                }
                else if (ModelFunctor.class.isAssignableFrom(field.getType()))
                {
                    ModelFunctor<? extends Model> modelFunctor =
                                                    (ModelFunctor<? extends Model>) functor;

                    if (modelFunctor.name() == null)
                        modelFunctor.setName(field.getName());

                    modelFunctors.add(modelFunctor);
                }
                else if (CollectionFunctor.class.isAssignableFrom(field.getType()))
                {
                    CollectionFunctor<? extends Model> collectionFunctor =
                                                     (CollectionFunctor<? extends Model>) functor;

                    if (collectionFunctor.name() == null)
                        collectionFunctor.setName(field.getName());

                    collectionFunctors.add(collectionFunctor);
                }
            }
        }
        catch (IllegalAccessException e)
        {
            throw FunctorException.functorAccess(
                    new FunctorAccessError(model.getClass().getName(), e));
        }

        return new Tuple4<>(primitiveFunctors, optionFunctors, modelFunctors, collectionFunctors);
    }


    // FORM
    // -----------------------------------------------------------------------------------------

    public static <A extends Model> List<Field> fields(A model,
                                                       boolean isEditMode,
                                                       Context context)
                  throws FunctorException
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        Tuple4<List<PrimitiveFunctor<?>>,
               List<OptionFunctor<?>>,
               List<ModelFunctor<?>>,
               List<CollectionFunctor<?>>> functorsTuple = Functor.propertyFunctors(model);


        List<Field> fields = new ArrayList<>();


        // [2 A] Add PRIMITIVE functor fields
        // -------------------------------------------------------------------------------------

        for (PrimitiveFunctor primitiveFunctor : functorsTuple.getItem1())
        {
            if (isEditMode || primitiveFunctor.isRequired()) {
                fields.add(primitiveFunctor.field(model.getId(), isEditMode, context));
            }
        }

        // [2 B] Add COLLECTION functor fields
        // -------------------------------------------------------------------------------------

        for (CollectionFunctor collectionFunctor : functorsTuple.getItem4())
        {
            if (isEditMode || collectionFunctor.isRequired()) {
                fields.add(collectionFunctor.field(model.getId(), isEditMode, context));
            }
        }

        return fields;
    }


    // LISTENERS
    // ------------------------------------------------------------------------------------------

    public interface OnUpdateListener extends Serializable {
        void onUpdate();
    }


}
