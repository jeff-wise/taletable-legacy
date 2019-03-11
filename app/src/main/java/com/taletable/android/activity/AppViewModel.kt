
package com.taletable.android.activity


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taletable.android.model.user.User
import com.taletable.android.model.user.UserName
import com.taletable.android.model.user.catalog.BookmarkCollection
import com.taletable.android.model.user.catalog.BookmarkCollectionName
import com.taletable.android.model.user.catalog.Catalog



class AppViewModel : ViewModel()
{

//    private val users: MutableLiveData<List<User>> by lazy {
//        MutableLiveData().also {
//            loadUsers()
//        }
//    }
//
//    fun getUsers(): LiveData<List<User>> {
//        return users
//    }

    private fun loadUsers() {
        // Do an asynchronous operation to fetch users.
    }


    private val collection1 = BookmarkCollection(BookmarkCollectionName("Combat Rules"), listOf())
    private val collection2 = BookmarkCollection(BookmarkCollectionName("Garak the Crusher"), listOf())
    private val collection3 = BookmarkCollection(BookmarkCollectionName("Custom Weapons"), listOf())
    private val testCatalog = Catalog(listOf(collection1, collection2, collection3))

    private val user = User(UserName("Bob"), testCatalog)

    val activeUser : MutableLiveData<User> = MutableLiveData()

    init {
        activeUser.value = user
    }

}
