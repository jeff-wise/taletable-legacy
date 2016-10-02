
package com.kispoko.tome.fragment.roleplay;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.kispoko.tome.ChooseImageAction;
import com.kispoko.tome.R;
import com.kispoko.tome.component.Image;
import com.kispoko.tome.sheet.Profile;



/**
 * ProfileFragment Fragment.
 *
 * UI Component for general player information such as class, race, and religion.
 */
public class ProfileFragment extends Fragment
{
    // Saved property names
    private static final String PROFILE_FORMAT = "profile";

    private Profile profile;

    private EventListener mListener;


    public ProfileFragment() {
        // Required empty public constructor
    }


    /**
     * Create a new instance of the fragment representing the character profile data.
     * @param profile The format description of the profile.
     * @return A new instance of ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(Profile profile)
    {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(PROFILE_FORMAT, profile);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            profile = (Profile) getArguments().getSerializable(PROFILE_FORMAT);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        ScrollView fragmentView = (ScrollView) inflater.inflate(R.layout.fragment_profile,
                                                                container, false);

        View profileView = this.profile.getView(getContext());
        fragmentView.addView(profileView);

        return fragmentView;

        /*
        RecyclerView recyclerView = (RecyclerView) fView.findViewById(R.id.profile_components);

        ComponentListAdapter componentAdapter =
               new ComponentListAdapter(getContext(), this.profile.getComponentList());

        int verticalItemSpace = (int) getResources().getDimension(R.dimen.component_item_space);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(verticalItemSpace));

        recyclerView.setAdapter(componentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return recyclerView; */
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EventListener) {
            mListener = (EventListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement EventListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface EventListener
    {
        // TODO: Update argument type and name
        void setChooseImageAction(ChooseImageAction chooseImageAction);
    }
}
