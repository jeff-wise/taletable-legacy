
package com.kispoko.tome.engine.programming.program;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Program Index
 */
public class ProgramIndex implements Model, Serializable
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


    public ProgramIndex(UUID id, List<Program> programs)
    {
        this.id = id;

        List<Class<? extends Program>> programClasses = new ArrayList<>();
        programClasses.add(Program.class);
        this.programs = CollectionValue.full(programs, programClasses);

        this.programByName = new HashMap<>();

        indexPrograms();
    }


    public static ProgramIndex fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID id = UUID.randomUUID();

        List<Program> programs = yaml.forEach(new Yaml.ForEach<Program>() {
            @Override
            public Program forEach(Yaml yaml, int index) throws YamlException {
                return Program.fromYaml(yaml);
            }
        });

        return new ProgramIndex(id, programs);
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


    // ** On Load
    // ------------------------------------------------------------------------------------------

    /**
     * This method is called when the Program Index is completely loaded for the first time.
     */
    public void onLoad()
    {
        // The programs are loaded into the collection, but are not automatically indexed.
        // Index all of the programs once they are all loaded.
        indexPrograms();
    }


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


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private void indexPrograms()
    {
        for (Program program : this.programs.getValue()) {
            this.programByName.put(program.getName(), program);
        }
    }




}

