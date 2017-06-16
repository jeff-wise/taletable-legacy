
package com.kispoko.tome.model.sheet.style


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.DocDict
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Spacing
 */
data class Spacing(override val id : UUID,
                   val left : Prim<Int>,
                   val top : Prim<Int>,
                   val right : Prim<Int>,
                   val bottom : Prim<Int>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(left : Int, top : Int, right : Int, bottom : Int)
            : this(UUID.randomUUID(), Prim(left), Prim(top), Prim(right), Prim(bottom))


    companion object : Factory<Spacing>
    {


        override fun fromDocument(doc : SpecDoc) : ValueParser<Spacing> = when (doc)
        {
            is DocDict -> effApply(::Spacing,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Left
                                   effApply(::Prim, doc.int("left")),
                                   // Top
                                   effApply(::Prim, doc.int("top")),
                                   // Right
                                   effApply(::Prim, doc.int("right")),
                                   // Bottom
                                   effApply(::Prim, doc.int("bottom")))
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        val default : Spacing = Spacing(0, 0, 0, 0)
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun left() : Int = this.left.value

    fun top() : Int = this.top.value

    fun right() : Int = this.right.value

    fun bottom() : Int = this.bottom.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

}

//
//public class Spacing extends Model
//                     implements ToYaml, Serializable
//{
//
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    // > Model
//    // -----------------------------------------------------------------------------------------
//
//    private UUID id;
//
//
//    // > Functors
//    // -----------------------------------------------------------------------------------------
//
//    private PrimitiveFunctor<Integer>   left;
//    private PrimitiveFunctor<Integer>   top;
//    private PrimitiveFunctor<Integer>   right;
//    private PrimitiveFunctor<Integer>   bottom;
//
//
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    public Spacing()
//    {
//        this.id     = null;
//
//        this.left   = new PrimitiveFunctor<>(null, Integer.class);
//        this.top    = new PrimitiveFunctor<>(null, Integer.class);
//        this.right  = new PrimitiveFunctor<>(null, Integer.class);
//        this.bottom = new PrimitiveFunctor<>(null, Integer.class);
//    }
//
//
//    public Spacing(UUID id,
//                   Integer left,
//                   Integer top,
//                   Integer right,
//                   Integer bottom)
//    {
//        this.id     = id;
//
//        this.left   = new PrimitiveFunctor<>(left, Integer.class);
//        this.top    = new PrimitiveFunctor<>(top, Integer.class);
//        this.right  = new PrimitiveFunctor<>(right, Integer.class);
//        this.bottom = new PrimitiveFunctor<>(bottom, Integer.class);
//
//        this.setLeft(left);
//        this.setTop(top);
//        this.setRight(right);
//        this.setBottom(bottom);
//    }
//
//
//    /**
//     * Create a Spacing from its yaml representation.
//     * @param yaml The yaml parser.
//     * @return The parsed Spacing.
//     * @throws YamlParseException
//     */
//    public static Spacing fromYaml(YamlParser yaml)
//                  throws YamlParseException
//    {
//        if (yaml.isNull())
//            return Spacing.asDefault();
//
//        UUID    id      = UUID.randomUUID();
//
//        Integer left    = yaml.atMaybeKey("left").getInteger();
//        Integer top     = yaml.atMaybeKey("top").getInteger();
//        Integer right   = yaml.atMaybeKey("right").getInteger();
//        Integer bottom  = yaml.atMaybeKey("bottom").getInteger();
//
//        return new Spacing(id, left, top, right, bottom);
//    }
//
//
//    /**
//     * Create a Spacing with default values.
//     * @return The default Spacing.
//     */
//    public static Spacing asDefault()
//    {
//        Spacing spacing = new Spacing();
//
//        spacing.setId(UUID.randomUUID());
//
//        spacing.setLeft(null);
//        spacing.setTop(null);
//        spacing.setRight(null);
//        spacing.setBottom(null);
//
//        return spacing;
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
//                .putInteger("left", this.left())
//                .putInteger("top", this.top())
//                .putInteger("right", this.right())
//                .putInteger("bottom", this.bottom());
//    }
//
//
//    // > State
//    // --------------------------------------------------------------------------------------
//
//    // ** Left
//    // --------------------------------------------------------------------------------------
//
//    /**
//     * The left spacing value.
//     * @return The left spacing.
//     */
//    public Integer left()
//    {
//        return this.left.getValue();
//    }
//
//
//    /**
//     * The left spacing in pixels.
//     * @return The left spacing in pixels.
//     */
//    public Integer leftPx()
//    {
//        return Util.dpToPixel(this.left());
//    }
//
//
//    /**
//     * Set the left spacing. If null, defaults to 0.
//     * @param spacing The left spacing.
//     */
//    public void setLeft(Integer spacing)
//    {
//        if (spacing != null)
//            this.left.setValue(spacing);
//        else
//            this.left.setValue(0);
//    }
//
//
//    // ** Top
//    // --------------------------------------------------------------------------------------
//
//    /**
//     * The top spacing value.
//     * @return The top spacing.
//     */
//    public Integer top()
//    {
//        return this.top.getValue();
//    }
//
//
//    /**
//     * The top spacing in pixels.
//     * @return The top spacing in pixels.
//     */
//    public Integer topPx()
//    {
//        return Util.dpToPixel(this.top());
//    }
//
//
//    /**
//     * Set the top spacing. If null, defaults to 0.
//     * @param spacing The top spacing.
//     */
//    public void setTop(Integer spacing)
//    {
//        if (spacing != null)
//            this.top.setValue(spacing);
//        else
//            this.top.setValue(0);
//    }
//
//
//    // ** Right
//    // --------------------------------------------------------------------------------------
//
//    /**
//     * The right spacing value.
//     * @return The right spacing.
//     */
//    public Integer right()
//    {
//        return this.right.getValue();
//    }
//
//
//    /**
//     * The right spacing in pixels.
//     * @return The right spacing in pixels.
//     */
//    public Integer rightPx()
//    {
//        return Util.dpToPixel(this.right());
//    }
//
//
//    /**
//     * Set the right spacing. If null, defaults to 0.
//     * @param spacing The right spacing.
//     */
//    public void setRight(Integer spacing)
//    {
//        if (spacing != null)
//            this.right.setValue(spacing);
//        else
//            this.right.setValue(0);
//    }
//
//
//    // ** Bottom
//    // --------------------------------------------------------------------------------------
//
//    /**
//     * The bottom spacing value.
//     * @return The bottom spacing.
//     */
//    public Integer bottom()
//    {
//        return this.bottom.getValue();
//    }
//
//
//    /**
//     * The bottom spacing in pixels.
//     * @return The bottom spacing in pixels.
//     */
//    public Integer bottomPx()
//    {
//        return Util.dpToPixel(this.bottom());
//    }
//
//
//    /**
//     * Set the bottom spacing. If null, defaults to 0.
//     * @param spacing The bottom spacing.
//     */
//    public void setBottom(Integer spacing)
//    {
//        if (spacing != null)
//            this.bottom.setValue(spacing);
//        else
//            this.bottom.setValue(0);
//    }
//
//}
