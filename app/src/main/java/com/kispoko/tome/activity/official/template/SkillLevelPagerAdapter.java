
package com.kispoko.tome.activity.official.template;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kispoko.tome.R;
import com.kispoko.tome.official.template.Game;
import com.kispoko.tome.official.template.SkillLevel;
import com.kispoko.tome.official.template.Template;
import com.kispoko.tome.official.template.TemplateIndex;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;



/**
 * Skill Level Pager Adapter
 *
 * Switches between official templates for Novice, Intermediate etc.. players
 */
public class SkillLevelPagerAdapter extends FragmentPagerAdapter
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private final int NUM_ITEMS = 3;

    private Context         context;
    private Game            game;
    private TemplateIndex   templateIndex;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public SkillLevelPagerAdapter(FragmentManager fragmentManager,
                                  Game game,
                                  TemplateIndex templateIndex,
                                  Context context)
    {
        super(fragmentManager);

        this.game           = game;
        this.templateIndex  = templateIndex;
        this.context        = context;
    }


    // FRAGMENT PAGER ADAPTER API
    // -----------------------------------------------------------------------------------------

    @Override
    public int getCount()
    {
        return NUM_ITEMS;
    }


    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
                return this.context.getString(R.string.novice);
            case 1:
                return this.context.getString(R.string.experienced);
            case 2:
                return this.context.getString(R.string.advanced);
            default:
                return "";
        }
    }


    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                List<Template> noviceTemplateSet =
                        this.templateIndex.templatesWithSkillLevel(this.game, SkillLevel.NOVICE);
                return TemplateListFragment.newInstance(new ArrayList<>(noviceTemplateSet));

            case 1:
                List<Template> middleTemplateSet =
                      this.templateIndex.templatesWithSkillLevel(this.game, SkillLevel.EXPERIENCED);
                return TemplateListFragment.newInstance(new ArrayList<>(middleTemplateSet));
            case 2:
                List<Template> advancedTemplateSet =
                        this.templateIndex.templatesWithSkillLevel(this.game, SkillLevel.ADVANCED);
                return TemplateListFragment.newInstance(new ArrayList<>(advancedTemplateSet));
            default:
                List<Template> defaultTemplateSet =
                        this.templateIndex.templatesWithSkillLevel(this.game, SkillLevel.NOVICE);
                return TemplateListFragment.newInstance(new ArrayList<>(defaultTemplateSet));
        }
    }

}
