
package com.kispoko.tome.rules.programming.program;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Program Index
 */
public class ProgramIndex implements Model
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;

    private CollectionValue<Program> programs;


    // > Internal
    private Map<String,Program> programByName;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ProgramIndex()
    {
        this.id            = null;

        List<Class<? extends Program>> programClasses = new ArrayList<>();
        programClasses.add(Program.class);
        this.programs      = CollectionValue.empty(programClasses);

        this.programByName = new HashMap<>();
    }


    public ProgramIndex(UUID id)
    {
        this.id = id;

        List<Class<? extends Program>> programClasses = new ArrayList<>();
        programClasses.add(Program.class);
        this.programs = CollectionValue.full(new ArrayList<Program>(), programClasses);

        this.programByName = new HashMap<>();
    }


    public static ProgramIndex fromYaml(Yaml yaml)
                  throws YamlException
    {
        final ProgramIndex programIndex = new ProgramIndex(UUID.randomUUID());

        List<Program> programs = yaml.forEach(new Yaml.ForEach<Program>() {
            @Override
            public Program forEach(Yaml yaml, int index) throws YamlException {
                return Program.fromYaml(yaml);
            }
        });

        for (Program program : programs) {
            programIndex.addProgram(program);
        }

        return programIndex;
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    /**
     * Get the model identifier.
     * @return The model UUID.
     */
    public UUID getId()
    {
        return this.id;
    }


    /**
     * Set the model identifier.
     * @param id The new model UUID.
     */
    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Update
    // ------------------------------------------------------------------------------------------

    public void onValueUpdate(String valueName) { }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Add a new program to the index. If it has the same name as another program currently in
     * the index, it will not be added.
     * @param program The program to add to the index.
     */
    public void addProgram(Program program)
    {
        if (!this.programByName.containsKey(program.getName())) {
            this.programByName.put(program.getName(), program);
            this.programs.getValue().add(program);
        }
    }


    /**
     * Get the program in the index with the given name. Names are unique, so only one match
     * may be found.
     * @param name The name of the program to lookup.
     * @return The Program with the given name, or null if no program with that name exists
     *         in the index.
     */
    public Program programWithName(String name)
    {
        return this.programByName.get(name);
    }




}

