package com.trungcoder.youtubeforcar.fragment.BrowseFragment;

import static com.trungcoder.youtubeforcar.MainActivity.youTubePlayerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.trungcoder.youtubeforcar.R;
import com.trungcoder.youtubeforcar.VideoItem;
import com.trungcoder.youtubeforcar.connector.searchConnector;
import com.trungcoder.youtubeforcar.connector.trendingConnector;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BrowseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BrowseFragment extends Fragment {

    //EditText for input search keywords
    private EditText searchInput;

    //YoutubeAdapter class that serves as a adapter for filling the
    //RecyclerView by the CardView (video_item.xml) that is created in layout folder
    private YoutubeAdapter1 youtubeAdapter;

    //RecyclerView manages a long list by recycling the portion of view
    //that is currently visible on screen
    private RecyclerView mRecyclerView;

    //ProgressDialog can be shown while downloading data from the internet
    //which indicates that the query is being processed
    private ProgressDialog mProgressDialog;

    //Handler to run a thread which could fill the list after downloading data
    //from the internet and inflating the images, title and description
    private Handler handler;

    //results list of type VideoItem to store the results so that each item
    //int the array list has id, title, description and thumbnail url
    private List<VideoItem> searchResults;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BrowseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment page1.
     */
    // TODO: Rename and change types and number of parameters
    public static BrowseFragment newInstance(String param1, String param2) {
        BrowseFragment fragment = new BrowseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_browse, container, false);

        //initailising the objects with their respective view in activity_main.xml file
        mProgressDialog = new ProgressDialog(rootView.getContext());
        searchInput = (EditText)rootView.findViewById(R.id.search_input);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.videos_recycler_view);


        //setting title and and style for progress dialog so that users can understand
        //what is happening currently
        mProgressDialog.setTitle("Đang tải...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        //Fixing the size of recycler view which means that the size of the view
        //should not change if adapter or children size changes
        mRecyclerView.setHasFixedSize(true);
        //give RecyclerView a layout manager to set its orientation to vertical
        //by default it is vertical
        mRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        handler = new Handler();
        //TODO: init videos when open app
        mProgressDialog.show();
        initYoutube();


        //add listener to the EditText view which listens to changes that occurs when
        //users changes the text or deletes the text
        //passing object of Textview's EditorActionListener to this method
        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            //onEditorAction method called when user clicks ok button or any custom
            //button set on the bottom right of keyboard
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                //actionId of the respective action is returned as integer which can
                //be checked with our set custom search button in keyboard
                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    //setting progress message so that users can understand what is happening
                    mProgressDialog.setMessage("Finding videos for "+v.getText().toString());

                    //displaying the progress dialog on the top of activity for two reasons
                    //1.user can see what is going on
                    //2.User cannot click anything on screen for time being
                    mProgressDialog.show();

                    //calling our search method created below with input keyword entered by user
                    //by getText method which returns Editable type, get string by toString method
                    searchOnYoutube(v.getText().toString());

                    //getting instance of the keyboard or any other input from which user types
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    //hiding the keyboard once search button is clicked
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);

                    return false;
                }
                return true;
            }
        });

        return rootView;
    }

    private void initYoutube(){
        //A thread that will execute the searching and inflating the RecyclerView as and when
        //results are found
        new Thread(){

            //implementing run method
            public void run(){

                //create our YoutubeConnector class's object with Activity context as argument
                trendingConnector yc = new trendingConnector(getActivity());

                //calling the YoutubeConnector's search method by entered keyword
                //and saving the results in list of type VideoItem class
                searchResults = yc.init();

                //handler's method used for doing changes in the UI
                handler.post(new Runnable(){

                    //implementing run method of Runnable
                    public void run(){

                        //call method to create Adapter for RecyclerView and filling the list
                        //with thumbnail, title, id and description
                        fillYoutubeVideos();

                        //after the above has been done hiding the ProgressDialog
                        mProgressDialog.dismiss();
                    }
                });
            }
            //starting the thread
        }.start();
    }

    //custom search method which takes argument as the keyword for which videos is to be searched
    private void searchOnYoutube(final String keywords){

        //A thread that will execute the searching and inflating the RecyclerView as and when
        //results are found
        new Thread(){

            //implementing run method
            public void run(){

                //create our YoutubeConnector class's object with Activity context as argument
                searchConnector yc = new searchConnector(getActivity());

                //calling the YoutubeConnector's search method by entered keyword
                //and saving the results in list of type VideoItem class
                searchResults = yc.search(keywords);

                //handler's method used for doing changes in the UI
                handler.post(new Runnable(){

                    //implementing run method of Runnable
                    public void run(){

                        //call method to create Adapter for RecyclerView and filling the list
                        //with thumbnail, title, id and description
                        fillYoutubeVideos();

                        //after the above has been done hiding the ProgressDialog
                        mProgressDialog.dismiss();
                    }
                });
            }
            //starting the thread
        }.start();
    }

    //method for creating adapter and setting it to recycler view
    private void fillYoutubeVideos(){

        //object of YoutubeAdapter which will fill the RecyclerView
        youtubeAdapter = new YoutubeAdapter1(youTubePlayerView.getContext(),searchResults);

        //setAdapter to RecyclerView
        mRecyclerView.setAdapter(youtubeAdapter);

        //notify the Adapter that the data has been downloaded so that list can be updated
        youtubeAdapter.notifyDataSetChanged();

//        youTubePlayerView.getYouTubePlayerWhenReady(new YouTubePlayerCallback() {
//            @Override
//            public void onYouTubePlayer(@NonNull YouTubePlayer youTubePlayer) {
//                youTubePlayer.loadVideo(searchResults.get(0).getId(),0);
//            }
//        });
    }

    // method for getting

}