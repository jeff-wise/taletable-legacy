
package com.taletable.android.activity.home


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.taletable.android.activity.entity.feed.FeedUI
import com.taletable.android.app.ApplicationLog
import com.taletable.android.load.TomeDoc
import com.taletable.android.model.AppActionOpenSession
import com.taletable.android.model.engine.variable.TextVariable
import com.taletable.android.model.engine.variable.TextVariableLiteralValue
import com.taletable.android.model.engine.variable.Variable
import com.taletable.android.model.engine.variable.VariableId
import com.taletable.android.model.feed.*
import com.taletable.android.model.sheet.group.*
import com.taletable.android.model.sheet.style.*
import com.taletable.android.model.sheet.widget.TextWidget
import com.taletable.android.model.sheet.widget.TextWidgetFormat
import com.taletable.android.model.sheet.widget.WidgetFormat
import com.taletable.android.model.theme.ColorId
import com.taletable.android.model.theme.ColorTheme
import com.taletable.android.model.theme.ThemeColorId
import com.taletable.android.model.theme.ThemeId
import com.taletable.android.model.theme.official.officialAppThemeLight
import com.taletable.android.rts.entity.addFeed
import com.taletable.android.rts.session.SessionId
import effect.Err
import effect.Val
import maybe.Just
import maybe.Nothing
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

//            val tabDivider = homeActivity.findViewById<LinearLayout>(R.id.tab_divider)
//            tabDivider?.setBackgroundColor(Color.parseColor("#DDDDDD"))

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
            val feedLoader = TomeDoc.loadFeed("feed/news.yaml", context)

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
                                          .withLeftMargin(12.0)
                                          .withRightMargin(12.0))

        val row2Format = GroupRowFormat(ElementFormat.default()
                                          .withHorizontalAlignment(Alignment.Left)
                                          .withLeftMargin(12.0)
                                          .withRightMargin(12.0))

        val groupRows = listOf(
                GroupRow(row1Format,
                         GroupRowIndex(0),
                         mutableListOf(titleTextWidget)),
                GroupRow(row2Format,
                         GroupRowIndex(0),
                         mutableListOf(descriptionTextWidget))
        )
        val groupElementFormat = ElementFormat.default()
                                              .withTopPadding(0.0)
                                              .withBottomPadding(12.0)
        val groupFormat = GroupFormat(groupElementFormat, Nothing())
        val groups = listOf(Group(groupFormat, groupRows))

        val card = Card(CardTitle("Featured Session"),
                        CardReason("Recommended"),
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
                                          .withLeftMargin(12.0)
                                          .withRightMargin(12.0))

        val row2Format = GroupRowFormat(ElementFormat.default()
                                          .withHorizontalAlignment(Alignment.Left)
                                          .withLeftMargin(12.0)
                                          .withRightMargin(12.0))

        val groupRows = listOf(
                GroupRow(row1Format,
                         GroupRowIndex(0),
                         mutableListOf(titleTextWidget)),
                GroupRow(row2Format,
                         GroupRowIndex(0),
                         mutableListOf(descriptionTextWidget))
        )
        val groupElementFormat = ElementFormat.default()
                                              .withTopPadding(0.0)
                                              .withBottomPadding(12.0)
        val groupFormat = GroupFormat(groupElementFormat, Nothing())
        val groups = listOf(Group(groupFormat, groupRows))

        val card = Card(CardTitle("Featured Session"),
                        CardReason("Recommended"),
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