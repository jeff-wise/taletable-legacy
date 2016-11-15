
package com.kispoko.tome.util.yaml;





/**
 * Yaml Error
 */
public class YamlError
{

    /**
     * Yaml Map does not have a key that was expected.
     */
    public static class MissingKey
    {

        // PROPERTIES
        // --------------------------------------------------------------------------------------

        private String key;


        // CONSTRUCTORS
        // --------------------------------------------------------------------------------------

        public MissingKey(String key)
        {
            this.key = key;
        }

    }


    /**
     * Yaml structure was of a different type than expected.
     */
    public static class UnexpectedType
    {

        // PROPERTIES
        // --------------------------------------------------------------------------------------

        private Yaml.ObjectType expectedType;


        // CONSTRUCTORS
        // --------------------------------------------------------------------------------------

        public UnexpectedType(Yaml.ObjectType expectedType)
        {
            this.expectedType = expectedType;
        }


        // API
        // --------------------------------------------------------------------------------------

        public Yaml.ObjectType getExpectedType()
        {
            return this.expectedType;
        }

    }
}
