package com.blacpythoz.musik.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blacpythoz.musik.R;
import com.blacpythoz.musik.adapters.AlbumAdapter;
import com.blacpythoz.musik.models.AlbumModel;
import com.blacpythoz.musik.services.MusicService;

import java.util.ArrayList;

/**
 * Created by deadsec on 9/3/17.
 */

public class AlbumListFragment extends Fragment {

    MusicService musicService;
    Intent playIntent;
    boolean serviceBound=false;
    ArrayList<AlbumModel> albums;
    RecyclerView recyclerView;
    AlbumAdapter adapter;
    ServiceConnection serviceConnection;
    CardView albumCardView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                MusicService.MusicBinder binder = (MusicService.MusicBinder) iBinder;
                musicService = binder.getService();
                musicService.toBackground();
                serviceBound = true;
                initPlayer();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                serviceBound = false;
            }
        };
    }

   public void initPlayer() {
       albums = musicService.getAlbums();
       adapter = new AlbumAdapter(albums,getContext());
       recyclerView.setAdapter(adapter);
       handleAllListener();
   }

    @Override
    public void onStart() {
        super.onStart();
        playIntent = new Intent(getActivity(),MusicService.class);
        playIntent.setAction("");
        getActivity().bindService(playIntent,serviceConnection, Context.BIND_AUTO_CREATE);
        getActivity().startService(playIntent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_album_list,container,false);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.rv_album_list);
        albums = new ArrayList<>();
        adapter = new AlbumAdapter(albums,getContext());
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        albumCardView = (CardView)rootView.findViewById(R.id.cv_item_album);
        recyclerView.setAdapter(adapter);
        if(serviceBound) { initPlayer(); }
        return rootView;
    }

    public void handleAllListener() {
        adapter.setAlbumClickListener(new AlbumAdapter.AlbumClickListener() {
            @Override
            public void OnAlbumClickListener(View v, AlbumModel album, int pos) {
                Log.i("Album clicked is: ",album.getArtistName());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(serviceBound) {
            getActivity().unbindService(serviceConnection);
        }
    }
}
