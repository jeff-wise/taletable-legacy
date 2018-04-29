
package com.kispoko.tome.activity.entity.book


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
import com.kispoko.tome.R
import com.kispoko.tome.model.book.Book
import com.kispoko.tome.model.book.BookReference
import com.kispoko.tome.model.book.BookReferenceContent
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.rts.entity.book
import com.kispoko.tome.rts.entity.theme.ThemeManager
import com.kispoko.tome.util.configureToolbar
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

            ThemeManager.theme(it.settings().themeId()) apDo {
                this.applyTheme(it)
            }
        }

        val currentBookReference = this.currentBookReference
        val currentBook = this.currentBook

        if (currentBookReference != null && currentBook != null)
            this.initializeView(currentBookReference, currentBook)
    }


    override fun onCreateOptionsMenu(menu : Menu) : Boolean
    {
        menuInflater.inflate(R.menu.empty, menu)
        return true
    }


    // -----------------------------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------------------------

    private fun initializeView(bookReference : BookReference, book : Book)
    {
        val contentView = this.findViewById<LinearLayout>(R.id.book_content)

        when (bookReference)
        {
            is BookReferenceContent ->
            {
                book.content(bookReference.contentId).doMaybe {
                    val cardUI = CardUI(book, it, this)
                    contentView.addView(cardUI.view())
                }
            }

        }

    }


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

            window.statusBarColor = theme.colorOrBlack(uiColors.toolbarBackgroundColorId())
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

}


