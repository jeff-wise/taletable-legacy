
package com.kispoko.tome.lib.orm.schema




// ---------------------------------------------------------------------------------------------
// TABLE
// ---------------------------------------------------------------------------------------------

sealed class Table(open val tableName : String)


// Table 1
// ---------------------------------------------------------------------------------------------

data class Table1(override val tableName : String,
                  val column1Name : String)
                   : Table(tableName)


// Table 2
// ---------------------------------------------------------------------------------------------

data class Table2(override val tableName : String,
                  val column1Name : String,
                  val column2Name : String)
                   : Table(tableName)


// Table 3
// ---------------------------------------------------------------------------------------------

data class Table3(override val tableName : String,
                  val column1Name : String,
                  val column2Name : String,
                  val column3Name : String)
                   : Table(tableName)


// Table 4
// ---------------------------------------------------------------------------------------------

data class Table4(override val tableName : String,
                  val column1Name : String,
                  val column2Name : String,
                  val column3Name : String,
                  val column4Name : String)
                   : Table(tableName)


// Table 5
// ---------------------------------------------------------------------------------------------

data class Table5(override val tableName : String,
                  val column1Name : String,
                  val column2Name : String,
                  val column3Name : String,
                  val column4Name : String,
                  val column5Name : String)
                   : Table(tableName)


// Table 6
// ---------------------------------------------------------------------------------------------

data class Table6(override val tableName : String,
                  val column1Name : String,
                  val column2Name : String,
                  val column3Name : String,
                  val column4Name : String,
                  val column5Name : String,
                  val column6Name : String)
                   : Table(tableName)


// Table 7
// ---------------------------------------------------------------------------------------------

data class Table7(override val tableName : String,
                  val column1Name : String,
                  val column2Name : String,
                  val column3Name : String,
                  val column4Name : String,
                  val column5Name : String,
                  val column6Name : String,
                  val column7Name : String)
                   : Table(tableName)


// Table 8
// ---------------------------------------------------------------------------------------------

data class Table8(override val tableName : String,
                  val column1Name : String,
                  val column2Name : String,
                  val column3Name : String,
                  val column4Name : String,
                  val column5Name : String,
                  val column6Name : String,
                  val column7Name : String,
                  val column8Name : String)
                   : Table(tableName)


// Table 9
// ---------------------------------------------------------------------------------------------

data class Table9(override val tableName : String,
                  val column1Name : String,
                  val column2Name : String,
                  val column3Name : String,
                  val column4Name : String,
                  val column5Name : String,
                  val column6Name : String,
                  val column7Name : String,
                  val column8Name : String,
                  val column9Name : String)
                   : Table(tableName)


// Table 10
// ---------------------------------------------------------------------------------------------

data class Table10(override val tableName : String,
                   val column1Name : String,
                   val column2Name : String,
                   val column3Name : String,
                   val column4Name : String,
                   val column5Name : String,
                   val column6Name : String,
                   val column7Name : String,
                   val column8Name : String,
                   val column9Name : String,
                   val column10Name : String)
                    : Table(tableName)

