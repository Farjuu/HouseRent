package com.example.nuhel.houserent.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.example.nuhel.houserent.Controller.GetFirebaseAuthInstance;
import com.example.nuhel.houserent.Controller.GetFirebaseInstance;
import com.example.nuhel.houserent.Controller.ProjectKeys;
import com.example.nuhel.houserent.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class OwnPostRecyclerViewAdapter extends RecyclerView.Adapter<OwnPostRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private RecyclerView recyclerView;
    private LinkedHashMap<String, HomeAddListDataModel> add_list;
    private DatabaseReference db;

    public OwnPostRecyclerViewAdapter(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.db = GetFirebaseInstance.GetInstance().getReference(ProjectKeys.ALLADSDIR);
        this.add_list = new LinkedHashMap<>();
        this.recyclerView.setAdapter(this);
        init();
    }

    private void init() {

        String id = GetFirebaseAuthInstance.getFirebaseAuthInstance().getCurrentUser().getUid();

        Query query = db.orderByChild(ProjectKeys.OWNERKEY).equalTo(id);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HomeAddListDataModel model = new SnapShotToDataModelParser().getModel(dataSnapshot, context);
                add_list.put(dataSnapshot.getKey(), model);
                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                HomeAddListDataModel model = new SnapShotToDataModelParser().getModel(dataSnapshot, context);
                if (model != null) {
                    add_list.put(key, model);
                    notifyItemChanged(new ArrayList<String>(add_list.keySet()).indexOf(key));
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                int position = new ArrayList<String>(add_list.keySet()).indexOf(key);

                if (position >= 0) {
                    add_list.remove(key);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, add_list.size());
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.main_ads_list_item, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.bindData((HomeAddListDataModel) (add_list.values().toArray()[position]), position);


    }

    @Override
    public int getItemCount() {
        return add_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        int pos;
        private ImageView imageView;
        private TextView area, room, type;
        private View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = itemView.findViewById(R.id.adsListImage);
            area = itemView.findViewById(R.id.adsListAreaText);
            room = itemView.findViewById(R.id.adsListRoomText);
            type = itemView.findViewById(R.id.adsListTypeText);
            area.setOnClickListener(this);
        }


        public void bindData(HomeAddListDataModel data, int position) {
            if (data != null) {
                pos = position;
                Glide.with(context)
                        .load(data.getImagelist().get(0))
                        .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                        .into(imageView);
                String areaText = data.getArea() == null ? "" : data.getArea();
                String roomsText = data.getArea() == null ? "" : data.getRoom();
                String typeText = data.getArea() == null ? "" : data.getType();
                area.setText("Area: " + areaText);
                room.setText("Rooms: " + roomsText);
                type.setText("Type: " + typeText);
            }
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(itemView.getContext(), "" + pos, Toast.LENGTH_SHORT).show();
        }
    }


}