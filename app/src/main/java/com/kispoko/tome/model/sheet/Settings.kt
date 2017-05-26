
package com.kispoko.tome.model.sheet


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.theme.Theme
import com.kispoko.tome.theme.ThemeType
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import lulo.value.valueResult
import java.util.*



/**
 * Sheet Settings
 */
data class Settings(override val id : UUID,
                    val themeType : Func<ThemeType>,
                    val customTheme : Func<Theme>) : Model
{

    companion object : Factory<Settings>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<Settings> = when (doc)
        {
            is DocDict ->
            {
                effApply3(::Settings,
                          // Model Id
                          valueResult(UUID.randomUUID()),
                          // Theme Type
                          effApply(::Prim, doc.enum<ThemeType>("theme_type")),
                          // Custom Theme?
                          split(doc.maybeAt("theme"),
                                valueResult<Func<Theme>>(Null()),
                                fun(doc : SpecDoc) : ValueParser<Func<Theme>> =
                                    effApply(::Comp, Theme.fromDocument(doc))
                          )
                )
            }
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}

//public class Settings extends Model
//                      implements ToYaml, Serializable
//{
//
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    // > Model
//    // -----------------------------------------------------------------------------------------
//
//    private UUID                                id;
//
//
//    // > Functors
//    // -----------------------------------------------------------------------------------------
//
//    private PrimitiveFunctor<SheetThemeType>    themeType;
//    private ModelFunctor<Theme>                 customTheme;
//
//
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    public Settings()
//    {
//        this.id             = null;
//
//        this.themeType      = new PrimitiveFunctor<>(null, SheetThemeType.class);
//        this.customTheme    = ModelFunctor.empty(Theme.class);
//    }
//
//
//    public Settings(UUID id, SheetThemeType themeType, Theme customTheme)
//    {
//        this.id             = id;
//
//        this.themeType      = new PrimitiveFunctor<>(themeType, SheetThemeType.class);
//        this.customTheme    = ModelFunctor.full(customTheme, Theme.class);
//    }
//
//
//    /**
//     * Create a Settings from its yaml representation.
//     * @param yaml The yaml parser.
//     * @return The parsed Settings
//     * @throws YamlParseException
//     */
//    public static Settings fromYaml(YamlParser yaml)
//                  throws YamlParseException
//    {
//        if (yaml.isNull())
//            return Settings.asDefault();
//
//        UUID           id          = UUID.randomUUID();
//
//        SheetThemeType themeType   = SheetThemeType.fromYaml(yaml.atKey("theme_type"));
//        Theme          customTheme = Theme.fromYaml(yaml.atMaybeKey("theme"));
//
//        return new Settings(id, themeType, customTheme);
//    }
//
//
//    private static Settings asDefault()
//    {
//        Settings settings = new Settings();
//
//        settings.setId(UUID.randomUUID());
//
//        settings.setThemeType(null);
//        settings.setCustomTheme(null);
//
//        return settings;
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
//                .putYaml("theme_type", this.themeType())
//                .putYaml("theme", this.customTheme.getValue());
//    }
//
//
//    // > State
//    // --------------------------------------------------------------------------------------
//
//    // ** Theme Type
//    // --------------------------------------------------------------------------------------
//
//    /**
//     * The type of sheet theme i.e. LIGHT, DARK, or CUSTOM.
//     * @return the theme type.
//     */
//    public SheetThemeType themeType()
//    {
//        return this.themeType.getValue();
//    }
//
//
//    /**
//     * Set the theme type. If null, defaults to DARK.
//     * @param themeType The theme type.
//     */
//    public void setThemeType(SheetThemeType themeType)
//    {
//        if (themeType != null)
//            this.themeType.setValue(themeType);
//        else
//            this.themeType.setValue(SheetThemeType.DARK);
//    }
//
//
//    // ** Theme
//    // --------------------------------------------------------------------------------------
//
//    /**
//     * The custom theme for the sheet. May be null.
//     * @return
//     */
//    public Theme customTheme()
//           throws NullValueException
//    {
//        if (this.customTheme.isNull())
//            throw new NullValueException();
//
//        return this.customTheme.getValue();
//    }
//
//
//    /**
//     * Set the custom sheet theme.
//     * @param theme The theme.
//     */
//    public void setCustomTheme(Theme theme)
//    {
//        this.customTheme.setValue(theme);
//    }
//
//
//}