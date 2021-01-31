

# Tale Table (legacy)

This repository was the result of about two years of work developing an Android application with a powerful engine for building and playing Tabletop Roleplaying Games.

## Architecture

It was originally written in Java and then later refactored to Kotlin. 

It utilizes some custom Kotlin libraries:
 * **Do** Provides the equivalent of an [ExceptT Monad](https://hackage.haskell.org/package/mtl-2.2.2/docs/Control-Monad-Except.html), but designed to integrate seamlessly with native Kotlin code: https://github.com/jeff-wise/do
 * **Lulo** IDL with support for sum types: https://github.com/jeff-wise/lulo-haskell
 * **Culebra** Yaml parsing library that utilizes Do: https://github.com/jeff-wise/culebra
