
package com.kispoko.tome.rules;


import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Program Index
 */
public class ProgramIndex
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Map<String,Program> programByName;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ProgramIndex(List<Program> programs)
    {
        programByName = new HashMap<>();
        for (Program program : programs)
        {
            programByName.put(program.getName(), program);
        }
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > State
    // ------------------------------------------------------------------------------------------

    public boolean hasProgram(String programName) {
        return this.programByName.containsKey(programName);
    }


    public Program getProgram(String programName) {
        return this.programByName.get(programName);
    }


    // > Database
    // ------------------------------------------------------------------------------------------

    public void load()
    {

    }


    public void save()
    {

    }
}

