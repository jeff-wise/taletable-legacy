
package com.kispoko.tome.activity.home


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import com.kispoko.tome.R
import com.kispoko.tome.activity.entity.feed.FeedUI
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.app.assetInputStream
import com.kispoko.tome.load.TomeDoc
import com.kispoko.tome.model.AppActionOpenSession
import com.kispoko.tome.model.engine.variable.TextVariable
import com.kispoko.tome.model.engine.variable.TextVariableLiteralValue
import com.kispoko.tome.model.engine.variable.Variable
import com.kispoko.tome.model.engine.variable.VariableId
import com.kispoko.tome.model.feed.*
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.sheet.group.*
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.sheet.widget.TextWidget
import com.kispoko.tome.model.sheet.widget.TextWidgetFormat
import com.kispoko.tome.model.sheet.widget.WidgetFormat
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.model.theme.official.officialAppThemeLight
import com.kispoko.tome.rts.entity.addFeed
import com.kispoko.tome.rts.session.SessionId
import effect.Err
import effect.Val
import maybe.Just
import java.io.IOException
import java.util.*



class FeedFragment : Fragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance() : FeedFragment
        {
            val feedFragment = FeedFragment()

//            val args = Bundle()
//            args.putSerializable("sheet_id", sheetId)
//            taskFragment.arguments = args

            return feedFragment
        }
    }


    // -----------------------------------------------------------------------------------------
    // FRAGMENT
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        val homeActivity = activity
        return if (homeActivity != null)
        {
//            val contentView = homeActivity.findViewById<LinearLayout>(R.id.content)
            val feed = this.feed(homeActivity)
            addFeed(feed)

            val tabDivider = homeActivity.findViewById<LinearLayout>(R.id.tab_divider)
            tabDivider?.setBackgroundColor(Color.parseColor("#DDDDDD"))

            val feedUI = FeedUI(feed, officialAppThemeLight, homeActivity)
            feedUI.view()

        }
        else
        {
            null
        }
    }


    // -----------------------------------------------------------------------------------------
    // INTERNAL
    // -----------------------------------------------------------------------------------------

//    fun view() : ScrollView
//    {
//        val scrollView = ScrollView(context)
//
//        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                                                     LinearLayout.LayoutParams.MATCH_PARENT)
//
//        scrollView.layoutParams = layoutParams
//
//        return scrollView
//    }
//


    // -----------------------------------------------------------------------------------------
    // FEED
    // -----------------------------------------------------------------------------------------

    private fun feed(context : Context) : Feed
    {
        val feed = newsFeed(context) ?: Feed.empty()

        feed.appendCard(this.rulebook5eSessionCard())
        feed.appendCard(this.casmeySessionCard())

        return feed
    }


    private fun newsFeed(context : Context) : Feed?
    {
        return try {
            val feedLoader = assetInputStream(context, "feed/news.yaml")
                               .apply { TomeDoc.loadFeed(it, "Home Feed", context) }

            when (feedLoader)
            {
                is Val -> {
                    feedLoader.value
                }
                is Err -> {
                    ApplicationLog.error(feedLoader.error)
                    null
                }
            }
        }
        catch (e : IOException) {
            Log.d("***FEED ACTIVITY", "io exception loading news feed")
            null
        }
    }


    private fun rulebook5eSessionCard() : CardItem
    {

        val titleTextFormat =
                TextFormat.default()
                   .withColorTheme(ColorTheme(setOf(
                       ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8")))))
                   .withSize(TextSize(19f))
                   .withFont(TextFont.RobotoCondensed)
                   .withFontStyle(TextFontStyle.Bold)
                   .withElementFormat(ElementFormat.default().withHorizontalAlignment(Alignment.Left))
        val titleTextWidgetFormat =
                TextWidgetFormat(
                        WidgetFormat(ElementFormat.default().withHorizontalAlignment(Alignment.Left)),
                        titleTextFormat)
        val titleTextWidget = TextWidget(titleTextWidgetFormat,
                                         VariableId("session_book_5esrd_title"))
        val descriptionTextFormat =
                TextFormat.default()
                   .withColorTheme(ColorTheme(setOf(
                       ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8")))))
                   .withSize(TextSize(17f))
                    .withFont(TextFont.RobotoCondensed)
                    .withFontStyle(TextFontStyle.Regular)
                   .withElementFormat(ElementFormat.default().withHorizontalAlignment(Alignment.Left))
        val descriptionTextWidgetFormat =
                    TextWidgetFormat(
                        WidgetFormat(ElementFormat.default().withHorizontalAlignment(Alignment.Left)),
                        descriptionTextFormat)
        val descriptionTextWidget = TextWidget(descriptionTextWidgetFormat,
                                               VariableId("session_book_5esrd_description"))

        val row1Format = GroupRowFormat(ElementFormat.default()
                                          .withHorizontalAlignment(Alignment.Left)
                                          .withLeftMargin(8.0)
                                          .withRightMargin(8.0))

        val row2Format = GroupRowFormat(ElementFormat.default()
                                          .withHorizontalAlignment(Alignment.Left)
                                          .withLeftMargin(8.0)
                                          .withRightMargin(8.0))

        val groupRows = listOf(
                GroupRow(row1Format,
                         GroupRowIndex(0),
                         listOf(titleTextWidget)),
                GroupRow(row2Format,
                         GroupRowIndex(0),
                         listOf(descriptionTextWidget))
        )
        val groupElementFormat = ElementFormat.default()
                                              .withTopMargin(6.0)
                                              .withTopPadding(6.0)
                                              .withBottomPadding(8.0)
        val groupTopBorder = Border.top(BorderEdge(
                                ColorTheme(setOf(
                                        ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_4")))),
                                BorderThickness(1)
                             ))
        val groupFormat = GroupFormat(groupElementFormat, Just(groupTopBorder))
        val groups = listOf(Group(groupFormat, groupRows))

        val card = Card(CardTitle("Featured Session"),
                        CardIsPinned(false),
                        Just(AppActionOpenSession(SessionId(UUID.randomUUID()))),
                        Just(CardActionLabel("Open Session")),
                        groups.map { GroupReferenceLiteral(it) })


        val title = "5E SRD Rulebook"
        val description = "All of the rules from the 5th Edition System Reference Document."
        val variables = listOf<Variable>(
                TextVariable(VariableId("session_book_5esrd_title"),
                        TextVariableLiteralValue(title)),
                TextVariable(VariableId("session_book_5esrd_description"),
                             TextVariableLiteralValue(description))
        )

        return CardItem(card, variables)

    }


    private fun casmeySessionCard() : CardItem
    {

        val titleTextFormat =
                TextFormat.default()
                   .withColorTheme(ColorTheme(setOf(
                       ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8")))))
                   .withSize(TextSize(19f))
                   .withFont(TextFont.RobotoCondensed)
                   .withFontStyle(TextFontStyle.Bold)
                   .withElementFormat(ElementFormat.default().withHorizontalAlignment(Alignment.Left))
        val titleTextWidgetFormat =
                TextWidgetFormat(
                        WidgetFormat(ElementFormat.default().withHorizontalAlignment(Alignment.Left)),
                        titleTextFormat)
        val titleTextWidget = TextWidget(titleTextWidgetFormat,
                                         VariableId("session_casmey_title"))
        val descriptionTextFormat =
                TextFormat.default()
                   .withColorTheme(ColorTheme(setOf(
                       ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8")))))
                   .withSize(TextSize(17f))
                   .withFont(TextFont.RobotoCondensed)
                   .withFontStyle(TextFontStyle.Regular)
                   .withElementFormat(ElementFormat.default().withHorizontalAlignment(Alignment.Left))
        val descriptionTextWidgetFormat =
                    TextWidgetFormat(
                        WidgetFormat(ElementFormat.default().withHorizontalAlignment(Alignment.Left)),
                        descriptionTextFormat)
        val descriptionTextWidget = TextWidget(descriptionTextWidgetFormat,
                                               VariableId("session_casmey_description"))

        val row1Format = GroupRowFormat(ElementFormat.default()
                                          .withHorizontalAlignment(Alignment.Left)
                                          .withLeftMargin(8.0)
                                          .withRightMargin(8.0))

        val row2Format = GroupRowFormat(ElementFormat.default()
                                          .withHorizontalAlignment(Alignment.Left)
                                          .withLeftMargin(8.0)
                                          .withRightMargin(8.0))

        val groupRows = listOf(
                GroupRow(row1Format,
                         GroupRowIndex(0),
                         listOf(titleTextWidget)),
                GroupRow(row2Format,
                         GroupRowIndex(0),
                         listOf(descriptionTextWidget))
        )
        val groupElementFormat = ElementFormat.default()
                                              .withTopMargin(6.0)
                                              .withTopPadding(6.0)
                                              .withBottomPadding(8.0)
        val groupTopBorder = Border.top(BorderEdge(
                                ColorTheme(setOf(
                                        ThemeColorId(ThemeId.Light, ColorId.Theme("light_grey_4")))),
                                BorderThickness(1)
                             ))
        val groupFormat = GroupFormat(groupElementFormat, Just(groupTopBorder))
        val groups = listOf(Group(groupFormat, groupRows))

        val card = Card(CardTitle("Featured Session"),
                        CardIsPinned(false),
                        Just(AppActionOpenSession(SessionId(UUID.randomUUID()))),
                        Just(CardActionLabel("Open Session")),
                        groups.map { GroupReferenceLiteral(it) })


        val title = "Casmey, Level 1 Human Rogue"
        val description = "A circus performer as a kid and a pirate as a teengaer, " +
                          "Casmey is now a reluctant adult looking for a new adventure"
        val variables = listOf<Variable>(
                TextVariable(VariableId("session_casmey_title"),
                        TextVariableLiteralValue(title)),
                TextVariable(VariableId("session_casmey_description"),
                             TextVariableLiteralValue(description))
        )

        return CardItem(card, variables)

    }



}