
package com.kispoko.tome.activity.engine;

import android.os.Bundle;
import android.support.v4.app.Fragment;



/**
 * Engine Fragment: State
 */
public class StateFragment extends Fragment
{

    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public StateFragment() {
        // Required empty public constructor
    }


    /**
     * @return A new instance of ProfileFragment.
     */
    public static StateFragment newInstance()
    {
        return new StateFragment();
    }


    // API
    // ------------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState)
//    {
//        ScrollView fragmentView = this.scrollView(getContext());
//
//        View pageView = this.page.view();
//        fragmentView.addView(pageView);
//
//        return fragmentView;
//    }

}
