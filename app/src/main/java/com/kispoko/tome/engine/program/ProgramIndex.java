
package com.kispoko.tome.engine.program;


import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.CollectionFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Program Index
 */
public class ProgramIndex extends Model
                          implements ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private CollectionFunctor<Program> programs;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Map<String,Program> programByName;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ProgramIndex()
    {
        this.id            = null;

        this.programs      = CollectionFunctor.empty(Program.class);

        this.programByName = new HashMap<>();
    }


    public ProgramIndex(UUID id, List<Program> programs)
    {
        this.id = id;

        this.programs = CollectionFunctor.full(programs, Program.class);

        this.programByName = new HashMap<>();

        indexPrograms();
    }


    public static ProgramIndex fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID id = UUID.randomUUID();

        List<Program> programs = yaml.forEach(new YamlParser.ForEach<Program>() {
            @Override
            public Program forEach(YamlParser yaml, int index) throws YamlParseException {
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


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.list(this.programs());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * The programs in the index.
     * @return The Program List.
     */
    public List<Program> programs()
    {
        return this.programs.getValue();
    }


    /**
     * Add a new program to the index. If it has the same name as another program currently in
     * the index, it will not be added.
     * @param program The program to add to the index.
     */
    public void addProgram(Program program)
    {
        if (!this.programByName.containsKey(program.name())) {
            this.programByName.put(program.name(), program);
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
            this.programByName.put(program.name(), program);
        }
    }




}

