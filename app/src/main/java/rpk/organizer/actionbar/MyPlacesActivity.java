package rpk.organizer.actionbar;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import rpk.organizer.actionbar.Utils.PlacesHandler;


public class MyPlacesActivity extends Fragment {
    private ListView PlacesListView;
    private Context mContext;
    private FloatingActionButton fab;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_my_places,
                container, false);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        mContext = getContext();
        PlacesListView = (ListView)getActivity().findViewById(R.id.lista);
                PlacesListView.post(new Runnable() {
            @Override
            public void run() {
                PlacesListView.requestLayout();
            }
        });
        fab = (FloatingActionButton)getActivity().findViewById(R.id.fapPlace);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_addplace);
                // Custom Android Allert Dialog Title
                dialog.setTitle(R.string.dialog_title);

                Button dialogButtonCancel = (Button) dialog.findViewById(R.id.customDialogCancel);
                Button dialogButtonOk = (Button) dialog.findViewById(R.id.customDialogOk);
                // Click cancel to dismiss android custom dialog box
                dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                // Your android custom dialog ok action
                // Action for custom dialog ok button click
                dialogButtonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        //TODO adding list element

                    }
                });

                dialog.show();
            }
        });
        populate();
    }
    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        //Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
        // Then you start a new Activity via Intent
        //Intent intent = new Intent();
        //intent.setClass(this, ListItemDetail.class);
        //intent.putExtra("position", position);
        // Or / And
        //intent.putExtra("id", id);
        //startActivity(intent);
    }
    public void populate() {
                PlacesAdapter adapter = new PlacesAdapter(PlacesHandler.getPlaces(),mContext);
                PlacesListView.setAdapter(adapter);
    }
}
