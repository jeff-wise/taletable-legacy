
package com.kispoko.tome.model.theme


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import effect.Err
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.util.*



/**
 * Theme
 */
data class Theme(override val id : UUID,
                 val name : Func<ThemeName>) : Model
{

    companion object : Factory<Theme>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<Theme> = when (doc)
        {
            is DocDict -> effApply(::Theme,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Campaign Name
                                   doc.at("name") ap {
                                       effApply(::Prim, ThemeName.fromDocument(it))
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

}



/**
 * Section Name
 */
data class ThemeName(val name : String)
{
    companion object : Factory<ThemeName>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<ThemeName> = when (doc)
        {
            is DocText -> effValue(ThemeName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}


enum class ThemeType
{
    LIGHT,
    DARK,
    CUSTOM;

}


//public class Theme extends Model
//                   implements ToYaml, Serializable
//{
//
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    // > Model
//    // -----------------------------------------------------------------------------------------
//
//    private UUID                            id;
//
//
//    // > Functors
//    // -----------------------------------------------------------------------------------------
//
//    private PrimitiveFunctor<String>        name;
//    private CollectionFunctor<ThemeColor>   colors;
//
//
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    public Theme()
//    {
//        this.id     = null;
//
//        this.name   = new PrimitiveFunctor<>(null, String.class);
//        this.colors = CollectionFunctor.empty(ThemeColor.class);
//    }
//
//
//    public Theme(UUID id, String name, List<ThemeColor> colors)
//    {
//        this.id     = id;
//
//        this.name   = new PrimitiveFunctor<>(name, String.class);
//        this.colors = CollectionFunctor.full(colors, ThemeColor.class);
//    }
//
//
//    /**
//     * Create a Theme from its yaml representation.
//     * @param yaml The yaml parser.
//     * @return The parsed Theme.
//     * @throws YamlParseException
//     */
//    public static Theme fromYaml(YamlParser yaml)
//                  throws YamlParseException
//    {
//        UUID             id     = UUID.randomUUID();
//
//        String           name   = yaml.atKey("name").getString();
//
//        List<ThemeColor> colors = yaml.atKey("colors").forEach(new YamlParser.ForEach<ThemeColor>()
//        {
//            @Override
//            public ThemeColor forEach(YamlParser yaml, int index) throws YamlParseException
//            {
//                return ThemeColor.fromYaml(yaml);
//            }
//        }, true);
//
//        return new Theme(id, name, colors);
//    }
//
//
//    // API
//    // -----------------------------------------------------------------------------------------
//
//    // > Model
//    // --------------------------------------------------------------------------------------
//
//    // ** Id
//    // --------------------------------------------------------------------------------------
//
//    public UUID getId()
//    {
//        return this.id;
//    }
//
//
//    public void setId(UUID id)
//    {
//        this.id = id;
//    }
//
//
//    // ** On Load
//    // --------------------------------------------------------------------------------------
//
//    /**
//     * Called when the Spacing is completely loaded.
//     */
//    public void onLoad() { }
//
//
//    // > To Yaml
//    // --------------------------------------------------------------------------------------
//
//    public YamlBuilder toYaml()
//    {
//        return YamlBuilder.map()
//                .putString("name", this.name())
//                .putList("colors", this.colors());
//    }
//
//
//    // > State
//    // --------------------------------------------------------------------------------------
//
//    // ** Name
//    // --------------------------------------------------------------------------------------
//
//    /**
//     * The theme name.
//     * @return The name
//     */
//    public String name()
//    {
//        return this.name.getValue();
//    }
//
//
//    // ** Colors
//    // --------------------------------------------------------------------------------------
//
//    /**
//     * The theme color palette.
//     * @return The color list.
//     */
//    public List<ThemeColor> colors()
//    {
//        if (this.colors.isNull())
//            return new ArrayList<>();
//
//        return this.colors.getValue();
//    }
//
//
//}
//
//

