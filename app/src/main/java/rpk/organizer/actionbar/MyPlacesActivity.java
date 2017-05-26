package rpk.organizer.actionbar;

import android.app.Dialog;
import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import rpk.organizer.actionbar.MyPlaces.Place;
import rpk.organizer.actionbar.MyPlaces.PlacesAdapter;
import rpk.organizer.actionbar.Utils.PlacesHandler;


public class MyPlacesActivity extends Fragment implements AdapterView.OnItemClickListener {
    private ListView PlacesListView;
    private Context mContext;
    private FloatingActionButton fab;
    private PlacesAdapter adapter;
    private int IsClickedFlag = 0;

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
        adapter = new PlacesAdapter(PlacesHandler.getPlaces(), mContext);
        PlacesHandler.setAdapter(adapter);
        PlacesListView = (ListView) getActivity().findViewById(R.id.lista);
        PlacesListView.post(new Runnable() {
            @Override
            public void run() {
                PlacesListView.requestLayout();
            }
        });
        PlacesListView.setAdapter(adapter);
        PlacesListView.setOnItemClickListener(this);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fapPlace);
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
                        EditText edit = (EditText) dialog.findViewById(R.id.placeName);
                        EditText edit2 = (EditText) dialog.findViewById(R.id.etDestination);
                        String czas = "0:00";
                        Place place;
                        if (edit.getText().toString().isEmpty()) {
                            if (edit2.getText().toString().isEmpty()) {
                                Toast.makeText(getContext(), "Place can not be empty", Toast.LENGTH_SHORT).show();
                            } else {
                                if (!PlacesHandler.isAlreadyAdded(edit2.getText().toString())) {
                                    place = new Place(edit2.getText().toString(), edit2.getText().toString(), czas);
                                    PlacesHandler.addPlace(place);
                                    adapter.notifyDataSetChanged();
                                    PlacesHandler.db.dodaj(place);
                                } else {
                                    Toast.makeText(getContext(), "Place already exists", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            if (edit2.getText().toString().isEmpty()) {
                                Toast.makeText(getContext(), "Place destination can not be empty", Toast.LENGTH_SHORT).show();
                            } else {
                                if (PlacesHandler.isAlreadyAdded(edit.getText().toString())) {
                                    place = new Place(edit.getText().toString(), edit2.getText().toString(), czas);
                                    PlacesHandler.addPlace(place);
                                    adapter.notifyDataSetChanged();
                                    PlacesHandler.db.dodaj(place);
                                } else {
                                    Toast.makeText(getContext(), "Place already exists", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        populate();

    }

    @Override
    public void onStart() {
        super.onStart();
        IsClickedFlag = 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        IsClickedFlag = 0;
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        if (IsClickedFlag == 0) {

            String place = PlacesHandler.getPlace((int) id).getPosition();
            MainActivity.AddNewFragmentOnTop(ShortestPathActivity.class, "SHORTEST_PATH", place);
//            final FragmentTransaction ft = getFragmentManager().beginTransaction();
//            Class frag = ShortestPathActivity.class;
//            Fragment fragment;
//            try {
//
//                fragment = (Fragment) frag.newInstance();
//                Bundle args = new Bundle();
//                args.putString("PLACE", place);
//                fragment.setArguments(args);
//                ft.replace(R.id.activity_main, fragment, "SHORTEST_PATH");
//                ft.addToBackStack(null);
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                ft.commit();
//            } catch (java.lang.InstantiationException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
        }
        IsClickedFlag = 1;
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

    }
}
