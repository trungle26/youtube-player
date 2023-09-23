package com.trungcoder.youtubeforcar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trungcoder.youtubeforcar.databinding.VideoItemBinding;

import java.util.List;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.VideoItemViewHolder> {
    private final List<VideoItem> videoItems;

    public QueueAdapter(List<VideoItem> videoItems) {
        this.videoItems = videoItems;
    }

    public class VideoItemViewHolder extends RecyclerView.ViewHolder{
        private final VideoItemBinding binding;
        public VideoItemViewHolder(@NonNull VideoItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public VideoItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        VideoItemBinding binding = VideoItemBinding.inflate(inflater,parent,false);
        return new VideoItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoItemViewHolder holder, int position) {
        // get element from your dataset at this position
        // replace the contents of the view with that element
        final VideoItem singleVideo = videoItems.get(position);

        // replace the default text with id, title and description with setText method
        holder.binding.videoId.setText("Video ID: "+singleVideo.getId()+" ");
        holder.binding.videoTitle.setText(singleVideo.getTitle());
        holder.binding.videoDescription.setText(singleVideo.getDescription());
        holder.binding.videoThumbnail.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.currentVideoQueueIndex = position;
                MainActivity.youTubePlayerView.getYouTubePlayerWhenReady(youTubePlayer -> youTubePlayer.loadVideo(singleVideo.getId(),0));
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                MainActivity.queue.remove(position);
                notifyItemRemoved(position);
                if(MainActivity.currentVideoQueueIndex > MainActivity.queue.size()){
                    MainActivity.currentVideoQueueIndex = MainActivity.queue.size()-1;
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoItems.size();
    }
}
