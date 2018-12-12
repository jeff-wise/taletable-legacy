
package com.taletable.android.activity.sheet.dialog


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.*
import android.widget.LinearLayout
import com.taletable.android.R
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.model.book.BookReference
import com.taletable.android.rts.entity.EntityId



/**
 * Book Excerpt Dialog
 */
class BookExcerptDialog : BottomSheetDialogFragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var bookId        : EntityId?      = null
    private var bookReference : BookReference? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(bookId : EntityId,
                        bookReference : BookReference) : BookExcerptDialog
        {
            val dialog = BookExcerptDialog()

            val args = Bundle()
            args.putSerializable("book_id", bookId)
            args.putSerializable("book_reference", bookReference)
            dialog.arguments = args

            return dialog
        }
    }


    // -----------------------------------------------------------------------------------------
    // DIALOG FRAGMENT
    // -----------------------------------------------------------------------------------------

    override fun onCreateDialog(savedInstanceState : Bundle?) : Dialog
    {
        // (1) Read State
        // -------------------------------------------------------------------------------------

        this.bookId        = arguments?.getSerializable("book_id") as EntityId
        this.bookReference = arguments?.getSerializable("book_reference") as BookReference


        // (2) Initialize UI
        // -------------------------------------------------------------------------------------

        val dialog = Dialog(context)

        val dialogLayout = this.dialogLayout()

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.window.attributes.windowAnimations = R.style.DialogAnimation

        dialog.setContentView(dialogLayout)

        val window = dialog.window
        val wlp = window.attributes

        wlp.gravity = Gravity.BOTTOM
        window.attributes = wlp

        val width  = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT

        dialog.window.setLayout(width, height)

        return dialog
    }


    override fun onCreateView(inflater : LayoutInflater,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
        val bookId = this.bookId
        val bookReference = this.bookReference
        val context = this.context

        return if (bookId != null && context != null && bookReference != null)
        {
            super.onCreateView(inflater, container, savedInstanceState)
        }
        else
        {
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }


    // -----------------------------------------------------------------------------------------
    // DIALOG LAYOUT
    // -----------------------------------------------------------------------------------------

    fun dialogLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        return layout.linearLayout(context)
    }

}

