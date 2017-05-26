
package com.kispoko.tome.lib.model;


import android.content.Context;
import android.util.Log;

import com.kispoko.tome.lib.functor.CollectionFunctor;
import com.kispoko.tome.lib.functor.Functor;
import com.kispoko.tome.lib.functor.FunctorException;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.functor.OptionFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.functor.error.FunctorAccessError;
import com.kispoko.tome.lib.functor.error.UninitializedFunctorError;
import com.kispoko.tome.util.tuple.Tuple4;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Model Interface
 */
//public abstract class Model
//{
//
//    abstract public UUID getId();
//    abstract public void setId(UUID id);
//
//    abstract public void onLoad();
//
//
//    // PROPERTIES PROPERTIES
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Collect all of the ModelLib's values and return them in data structures suitable for
//     * further analysis.
//     * @param model A ModelLib instance to get the values of.
//     * @param <A> The model type.
//     * @return The ModelLib's values, sorted.
//     * @throws FunctorException
//     */
//    public static <A> Tuple4<List<PrimitiveFunctor<?>>,
//                                 List<OptionFunctor<?>>,
//                                 List<ModelFunctor<?>>,
//                                 List<CollectionFunctor<?>>> propertyFunctors(A model)
//                       throws FunctorException
//    {
//        // [1] Get all of the class's Value fields
//        // --------------------------------------------------------------------------------------
//        List<Field> valueFields = new ArrayList<>();
//
//        List<java.lang.reflect.Field> allFields = FieldUtils.getAllFieldsList(model.getClass());
//        for (java.lang.reflect.Field field : allFields)
//        {
//            if (Functor.class.isAssignableFrom(field.getType()))
//                valueFields.add(field);
//        }
//
//        // [2] Store the value fields by type and map to columns
//        // --------------------------------------------------------------------------------------
//        List<PrimitiveFunctor<?>>                primitiveFunctors  = new ArrayList<>();
//        List<OptionFunctor<?>>                   optionFunctors     = new ArrayList<>();
//        List<ModelFunctor<? extends Model>>      modelFunctors      = new ArrayList<>();
//        List<CollectionFunctor<? extends Model>> collectionFunctors = new ArrayList<>();
//
//        try
//        {
//            for (java.lang.reflect.Field field : valueFields)
//            {
//                Functor<?> functor = (Functor<?>) FieldUtils.readField(field, model, true);
//
//                if (functor == null) {
//                    throw FunctorException.uninitializedFunctor(
//                            new UninitializedFunctorError(model.getClass().getName(),
//                                                          field.getName()));
//                }
//
//                // Sort values by database value type
//                if (PrimitiveFunctor.class.isAssignableFrom(field.getType()))
//                {
//                    PrimitiveFunctor primitiveValue = (PrimitiveFunctor) functor;
//
//                    if (primitiveValue.name() == null)
//                        primitiveValue.setName(field.getName());
//
//                    primitiveFunctors.add(primitiveValue);
//                }
//                else if (OptionFunctor.class.isAssignableFrom(field.getType()))
//                {
//                    OptionFunctor optionFunctor = (OptionFunctor) functor;
//
//                    if (optionFunctor.name() == null)
//                        optionFunctor.setName(field.getName());
//
//                    optionFunctors.add(optionFunctor);
//
//                }
//                else if (ModelFunctor.class.isAssignableFrom(field.getType()))
//                {
//                    ModelFunctor<? extends Model> modelFunctor =
//                                                    (ModelFunctor<? extends Model>) functor;
//
//                    if (modelFunctor.name() == null)
//                        modelFunctor.setName(field.getName());
//
//                    modelFunctors.add(modelFunctor);
//                }
//                else if (CollectionFunctor.class.isAssignableFrom(field.getType()))
//                {
//                    CollectionFunctor<? extends Model> collectionFunctor =
//                                                     (CollectionFunctor<? extends Model>) functor;
//
//                    if (collectionFunctor.name() == null)
//                        collectionFunctor.setName(field.getName());
//
//                    collectionFunctors.add(collectionFunctor);
//                }
//            }
//        }
//        catch (IllegalAccessException e)
//        {
//            throw FunctorException.functorAccess(
//                    new FunctorAccessError(model.getClass().getName(), e));
//        }
//
//        return new Tuple4<>(primitiveFunctors, optionFunctors, modelFunctors, collectionFunctors);
//    }
//
//
//    // FORM
//    // -----------------------------------------------------------------------------------------
//
//    public static <A extends Model> Collection<com.kispoko.tome.lib.model.form.Field>
//                                        fields(A model,
//                                               Context context)
//                  throws FunctorException
//    {
//        // [1] Declarations
//        // -------------------------------------------------------------------------------------
//
//        Tuple4<List<PrimitiveFunctor<?>>,
//               List<OptionFunctor<?>>,
//               List<ModelFunctor<?>>,
//               List<CollectionFunctor<?>>> functorsTuple = Model.propertyFunctors(model);
//
//        List<Functor<?>> functors = new ArrayList<>();
//        functors.addAll(functorsTuple.getItem1());
//        functors.addAll(functorsTuple.getItem3());
//        functors.addAll(functorsTuple.getItem4());
//
//        Map<String, com.kispoko.tome.lib.model.form.Field> fieldByName = new HashMap<>();
//
//        Map<String, List<String>> caseMap = new HashMap<>();
//
//        // [2 A] Add PRIMITIVE functor fields
//        // -------------------------------------------------------------------------------------
//
//        for (PrimitiveFunctor primitiveFunctor : functorsTuple.getItem1())
//        {
//            com.kispoko.tome.lib.model.form.Field field =
//                    primitiveFunctor.field(model.getId(), context);
//            fieldByName.put(field.name(), field);
//        }
//
//        // [2 B] Add OPTION functor fields
//        // -------------------------------------------------------------------------------------
//
//        for (OptionFunctor optionFunctor : functorsTuple.getItem2())
//        {
//            com.kispoko.tome.lib.model.form.Field field =
//                                                optionFunctor.field(model.getId(), context);
//            fieldByName.put(field.name(), field);
//        }
//
//        // [2 C] Add MODEL functor fields
//        // -------------------------------------------------------------------------------------
//
//        for (ModelFunctor modelFunctor : functorsTuple.getItem3())
//        {
//            com.kispoko.tome.lib.model.form.Field field =
//                                            modelFunctor.field(model.getId(), context);
//            fieldByName.put(field.name(), field);
//        }
//
//        // [2 D] Add COLLECTION functor fields
//        // -------------------------------------------------------------------------------------
//
//        for (CollectionFunctor collectionFunctor : functorsTuple.getItem4())
//        {
//            com.kispoko.tome.lib.model.form.Field field =
//                                            collectionFunctor.field(model.getId(), context);
//            fieldByName.put(field.name(), field);
//        }
//
//
//        // [3] Process cases
//        // -------------------------------------------------------------------------------------
//
//        for (Functor functor : functors)
//        {
//            if (functor.isCaseType())
//            {
//                if (!caseMap.containsKey(functor.parentTypeName()))
//                    caseMap.put(functor.parentTypeName(), new ArrayList<String>());
//
//                List<String> cases = caseMap.get(functor.parentTypeName());
//                cases.add(functor.caseName());
//            }
//        }
//
//        for (String parentTypeName : caseMap.keySet())
//        {
//            com.kispoko.tome.lib.model.form.Field parentField = fieldByName.get(parentTypeName);
//
//            if (parentField == null)
//                continue;
//
//            List<String> cases = caseMap.get(parentTypeName);
//
//            for (String _case : cases)
//            {
//                Log.d("***MODEL", "case " + _case);
//                String fieldName = parentTypeName + "_" + _case;
//                com.kispoko.tome.lib.model.form.Field field = fieldByName.get(fieldName);
//
//                if (field == null)
//                    continue;
//
//                Log.d("***MODEL", "add case field");
//
//                parentField.addCaseField(_case, field);
//
//                fieldByName.remove(_case);
//            }
//        }
//
//
//        return fieldByName.values();
//    }
//
//    // UPDATE
//    // -----------------------------------------------------------------------------------------
//
//
//    @SuppressWarnings("unchecked")
//    public static <A extends Model> void updateProperty(A model,
//                                                        String propertyName,
//                                                        Object newValue)
//                  throws FunctorException
//    {
//        Tuple4<List<PrimitiveFunctor<?>>,
//                List<OptionFunctor<?>>,
//                List<ModelFunctor<?>>,
//                List<CollectionFunctor<?>>> functorsTuple = Model.propertyFunctors(model);
//
//        for (PrimitiveFunctor primitiveFunctor : functorsTuple.getItem1())
//        {
//            if (primitiveFunctor.name().equals(propertyName))
//            {
//                try {
//                    primitiveFunctor.setValue(newValue);
//                }
//                catch (Exception exception) {
//
//                }
//            }
//        }
//
//    }
//
//}
