
package com.taletable.android.official


import com.taletable.android.ApplicationAssets



// ---------------------------------------------------------------------------------------------
// THEME
// ---------------------------------------------------------------------------------------------

sealed class OfficialTheme
{


    abstract val filePath : String


    object Light: OfficialTheme()
    {
        override val filePath = ApplicationAssets.officialThemeDirectoryPath + "/light.yaml"

        override fun toString() : String = "Light Theme"

    }

    object Dark: OfficialTheme()
    {
        override val filePath = ApplicationAssets.officialThemeDirectoryPath + "/dark.yaml"

        override fun toString() : String = "Dark Theme"

    }


    override fun toString() : String = when (this)
    {
        is Light -> "Light"
        is Dark  -> "Dark"
    }
}


// ---------------------------------------------------------------------------------------------
// FILE DIRECTORIES
// ---------------------------------------------------------------------------------------------

val officialDirectoryPath = "official"



//fun officialManifestPath(gameId : GameId, entityTypeId : String) =
//   "$officialDirectoryPath/${gameId.value}/${entityTypeId}_manifest.yaml"



