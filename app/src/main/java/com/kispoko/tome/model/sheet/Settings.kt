
package com.kispoko.tome.model.sheet


import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue3
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.theme.ThemeId
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Sheet Settings
 */
data class Settings(override val id : UUID,
                    val themeId : ThemeId,
                    val sheetName : SheetName,
                    val sheetSummary : SheetSummary)
                     : ProdType, ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(themeId : ThemeId,
                sheetName : SheetName,
                sheetSummary: SheetSummary)
        : this(UUID.randomUUID(),
               themeId,
               sheetName,
               sheetSummary)


    companion object : Factory<Settings>
    {

        private fun defaultThemeId()      = ThemeId.Dark
        private fun defaultSheetName()    = SheetName("")
        private fun defaultSheetSummary() = SheetSummary("")


        override fun fromDocument(doc: SchemaDoc): ValueParser<Settings> = when (doc)
        {
            is DocDict ->
            {
                apply(::Settings,
                      // Theme Id
                      split(doc.maybeAt("theme_id"),
                            effValue<ValueError,ThemeId>(defaultThemeId()),
                            { ThemeId.fromDocument(it) }),
                      // Sheet Name
                      split(doc.maybeAt("sheet_name"),
                            effValue(defaultSheetName()),
                            { SheetName.fromDocument(it) }),
                      // Sheet Summary
                      split(doc.maybeAt("sheet_summary"),
                            effValue(defaultSheetSummary()),
                            { SheetSummary.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = Settings(defaultThemeId(),
                                 defaultSheetName(),
                                 defaultSheetSummary())

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun themeId() : ThemeId = this.themeId


    fun sheetName() : String = this.sheetName.value


    fun sheetSummary() : String = this.sheetSummary.value


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "theme_id" to this.themeId().toDocument(),
        "sheet_name" to DocText(this.sheetName()),
        "sheet_summary" to DocText(this.sheetSummary())
    ))


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_SheetSettingsValue =
        RowValue3(sheetSettingsTable,
                   PrimValue(this.themeId),
                   PrimValue(this.sheetName),
                   PrimValue(this.sheetSummary))

}


/**
 * Sheet Name
 */
data class SheetName(val value : String) : SQLSerializable, Serializable
{

    companion object : Factory<SheetName>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<SheetName> = when (doc)
        {
            is DocText -> effValue(SheetName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Sheet Summary
 */
data class SheetSummary(val value : String) : SQLSerializable, Serializable
{

    companion object : Factory<SheetSummary>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<SheetSummary> = when (doc)
        {
            is DocText -> effValue(SheetSummary(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}



//public class Settings extends ProdType
//                      implements ToYaml, Serializable
//{
//
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    // > ProdType
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
//    // > ProdType
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
