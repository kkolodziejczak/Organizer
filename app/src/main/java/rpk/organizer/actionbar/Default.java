package rpk.organizer.actionbar;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import rpk.organizer.actionbar.R;

public class Default extends Fragment implements View.OnClickListener{
    public Button res1, res2,res3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_default,
                container, false);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        res1 = (Button) getActivity().findViewById(R.id.button5);
        res1.setOnClickListener(this);
        res2 = (Button) getActivity().findViewById(R.id.button6);
        res2.setOnClickListener(this);
        res3 = (Button) getActivity().findViewById(R.id.button7);
        res3.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.button5:
                intent = new Intent(getActivity(), ShortestWayActivity.class);
                startActivity(intent);
                break;
            case R.id.button6:
                intent = new Intent(getActivity(), Calendar.class);
                startActivity(intent);
                break;
            case R.id.button7:

                break;
        }
    }
}
