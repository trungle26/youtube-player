package com.trungcoder.youtubeforcar.fragment.page3;

import static com.trungcoder.youtubeforcar.MainActivity.youTubePlayerView;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.trungcoder.youtubeforcar.R;
import com.trungcoder.youtubeforcar.VideoItem;
import com.trungcoder.youtubeforcar.connector.playlistConnector;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link page3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class page3 extends Fragment {

    //YoutubeAdapter class that serves as a adapter for filling the
    //RecyclerView by the CardView (video_item.xml) that is created in layout folder
    private YoutubeAdapter3 youtubeAdapter3;

    //RecyclerView manages a long list by recycling the portion of view
    //that is currently visible on screen
    private RecyclerView mRecyclerView3;

    //ProgressDialog can be shown while downloading data from the internet
    //which indicates that the query is being processed
    private ProgressDialog mProgressDialog;

    //Handler to run a thread which could fill the list after downloading data
    //from the internet and inflating the images, title and description
    private Handler handler;

    //results list of type VideoItem to store the results so that each item
    //int the array list has id, title, description and thumbnail url
    private List<VideoItem> searchResults3;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public page3() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment page2.
     */
    // TODO: Rename and change types and number of parameters
    public static page3 newInstance(String param1, String param2) {
        page3 fragment = new page3();
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
        View rootView = inflater.inflate(R.layout.fragment_page3, container, false);

        //initailising the objects with their respective view in activity_main.xml file
        mProgressDialog = new ProgressDialog(rootView.getContext());
        mRecyclerView3 = (RecyclerView) rootView.findViewById(R.id.videos_recycler_view_3);

        //setting title and and style for progress dialog so that users can understand
        //what is happening currently
        mProgressDialog.setTitle("Đang tải...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        //Fixing the size of recycler view which means that the size of the view
        //should not change if adapter or children size changes
        mRecyclerView3.setHasFixedSize(true);
        //give RecyclerView a layout manager to set its orientation to vertical
        //by default it is vertical
        mRecyclerView3.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        handler = new Handler();
        //TODO: init videos when open app
        mProgressDialog.show();
        init("PLx75dt_ldXNKrlqg8lkwP_JO4Wpp3cbAB");
        return rootView;
    }

    //custom search method which takes argument as the keyword for which videos is to be searched
    private void init(String keywords){

        //A thread that will execute the searching and inflating the RecyclerView as and when
        //results are found
        new Thread(){

            //implementing run method
            public void run(){

                //create our YoutubeConnector class's object with Activity context as argument
                playlistConnector yc = new playlistConnector(getActivity(),keywords);

                //calling the YoutubeConnector's search method by entered keyword
                //and saving the results in list of type VideoItem class
                searchResults3 = yc.init();

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
        youtubeAdapter3 = new YoutubeAdapter3(getActivity().getApplicationContext(),searchResults3);

        //setAdapter to RecyclerView
        mRecyclerView3.setAdapter(youtubeAdapter3);

        //notify the Adapter that the data has been downloaded so that list can be updapted
        youtubeAdapter3.notifyDataSetChanged();
    }

}