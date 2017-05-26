
package com.kispoko.tome.model.sheet.group;


import com.kispoko.tome.model.sheet.BackgroundColor;



/**
 * Group Parent
 *
 * Interface for a group that is a parent of some descendant UI object
 *  e.g. Group Row, Widget, etc..
 */
public interface GroupParent
{
    BackgroundColor background();
}
