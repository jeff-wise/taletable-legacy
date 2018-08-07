
package com.taletable.android.activity.entity.book


import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.model.book.*
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialAppThemeLight
import com.taletable.android.rts.entity.book
import com.taletable.android.util.configureToolbar
import maybe.Just



/**
 * Book Activity
 */
class BookActivity : AppCompatActivity()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var currentBookReference : BookReference? = null
    private var currentBook          : Book? = null

    var referenceHistory     : MutableList<BookReference> = mutableListOf()


    // -----------------------------------------------------------------------------------------
    // ACTIVITY API
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        // (1) Set Content View
        // -------------------------------------------------------------------------------------

        setContentView(R.layout.activity_book)

        // (2) Read Parameters
        // -------------------------------------------------------------------------------------

        if (this.intent.hasExtra("book_reference"))
            this.currentBookReference = this.intent.getSerializableExtra("book_reference") as BookReference

        // (3) Fetch book
        // -------------------------------------------------------------------------------------

        this.currentBookReference?.let {
            val maybeBook = book(it.bookId)
            when (maybeBook) {
                is Just -> this.currentBook = maybeBook.value
            }
        }

        // (4) Configure View
        // -------------------------------------------------------------------------------------

        this.currentBook?.let {
            this.configureToolbar(it.bookInfo.title().value)

//            ThemeManager.theme(it.settings().themeId()) apDo {
//                this.applyTheme(it)
//            }
            this.applyTheme(com.taletable.android.model.theme.official.officialAppThemeLight)
        }

        val currentBookReference = this.currentBookReference
        val currentBook = this.currentBook

        if (currentBookReference != null && currentBook != null)
            this.setCurrentBookReference(currentBookReference)

    }


    override fun onCreateOptionsMenu(menu : Menu) : Boolean
    {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }


    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------

    private fun applyTheme(theme : Theme)
    {
        val uiColors = theme.uiColors()

        // STATUS BAR
        // -------------------------------------------------------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            val window = this.window

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

//            window.statusBarColor = theme.colorOrBlack(uiColors.toolbarBackgroundColorId())

            val statusBarColorTheme = ColorTheme(setOf(
                    ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_28")),
                    ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_8"))))
            window.statusBarColor = theme.colorOrBlack(statusBarColorTheme)
        }

        // TOOLBAR
        // -------------------------------------------------------------------------------------
        val toolbar = this.findViewById<Toolbar>(R.id.toolbar)

        // Toolbar > Background
        toolbar.setBackgroundColor(theme.colorOrBlack(uiColors.toolbarBackgroundColorId()))

        // Toolbar > Icons
        var iconColor = theme.colorOrBlack(uiColors.toolbarIconsColorId())

        val menuLeftButton = this.findViewById<ImageButton>(R.id.toolbar_main_button)
        menuLeftButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        val menuRightButton = this.findViewById<ImageButton>(R.id.toolbar_options_button)
        menuRightButton.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)

        // TITLE
        // -------------------------------------------------------------------------------------
        val titleView = this.findViewById<TextView>(R.id.toolbar_title)
        titleView.setTextColor(theme.colorOrBlack(uiColors.toolbarTitleColorId()))

    }


    fun setCurrentBookReference(bookReference : BookReference)
    {
        val contentView = this.findViewById<LinearLayout>(R.id.book_content)

        this.currentBook?.let { book ->
            when (bookReference)
            {
                is BookReferenceBook ->
                {
                    val bookUI = BookUI(book, this, officialAppThemeLight)
                    contentView.removeAllViews()
                    contentView.addView(bookUI.view())

                    this.referenceHistory.add(bookReference)

                }
                is BookReferenceChapter ->
                {
                    book.chapter(bookReference.chapterId).doMaybe { chapter ->
                        val chapterUI = ChapterUI(chapter, book, this, officialAppThemeLight)
                        contentView.removeAllViews()
                        contentView.addView(chapterUI.view())

                        this.referenceHistory.add(bookReference)
                    }
                }
                is BookReferenceSection ->
                {
                    book.section(bookReference.chapterId(), bookReference.sectionId()).doMaybe { section ->
                        val sectionUI = SectionUI(section, book, bookReference.chapterId(), this, officialAppThemeLight)
                        contentView.removeAllViews()
                        contentView.addView(sectionUI.view())

                        this.referenceHistory.add(bookReference)
                    }
                }
                is BookReferenceSubsection ->
                {
                    book.subsection(bookReference.chapterId(), bookReference.sectionId(), bookReference.subsectionId()).doMaybe { subsection ->
                        val subsectionUI = SubsectionUI(subsection, book, this, officialAppThemeLight)
                        contentView.removeAllViews()
                        contentView.addView(subsectionUI.view())

                        this.referenceHistory.add(bookReference)
                    }
                }
                is BookReferenceContent ->
                {
                    book.content(bookReference.contentId).doMaybe {
                        val cardUI = CardUI(book, it, this)
                        contentView.removeAllViews()
                        contentView.addView(cardUI.view())

                        this.referenceHistory.add(bookReference)
                    }
                }

            }
        }


    }


    fun setToPreviousReference()
    {
        Log.d("***BOOK ACTIVITY", "set previous")
        if (this.referenceHistory.size >= 2) {
            // Drop current
            this.referenceHistory.removeAt(this.referenceHistory.size - 1)
            // Set to last
            this.referenceHistory.takeLast(1).firstOrNull()?.let {
                this.setCurrentBookReference(it)
            }
        }
    }

}


