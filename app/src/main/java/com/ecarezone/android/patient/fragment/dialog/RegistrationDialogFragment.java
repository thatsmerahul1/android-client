package com.ecarezone.android.patient.fragment.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.adapter.RegistrationAdapter;
import com.ecarezone.android.patient.config.Constants;

/**
 * Created by L&T Technology Services  on 2/18/2016.
 */
public class RegistrationDialogFragment extends DialogFragment implements View.OnClickListener {
    private TextView textview_ok;
    private TextView textview_cancel;
    private Fragment targetFragment;
    private ListView listview_registration;
    private RegistrationAdapter adapter;
    private String item;
    private String itemCode;
    private boolean isSelectionMade = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View v = inflater.inflate(R.layout.dia_regestration, container, false);
        targetFragment = getTargetFragment();
        TextView title = (TextView) v.findViewById(R.id.textview_title);

        textview_cancel = (TextView) v.findViewById(R.id.button_cancel);
        textview_cancel.setOnClickListener(this);

        textview_ok = (TextView) v.findViewById(R.id.button_ok);
        textview_ok.setOnClickListener(this);
        textview_ok.setTextColor(getResources().getColor(R.color.ecarezone_green_light));


        if (getArguments().getString(Constants.TYPE).equalsIgnoreCase(Constants.COUNTRY)) {
            listview_registration = (ListView) v.findViewById(R.id.lisview_registration);
            String[] contries = getResources().getStringArray(R.array.country_array);
            String[] contryCodes = getResources().getStringArray(R.array.country_code_array);
            adapter = new RegistrationAdapter(getActivity(), R.layout.country_spinner_item, contries, contryCodes, true, getArguments().getString(Constants.COUNTRY));
          } else {
            title.setText(R.string.settings_language);
            listview_registration = (ListView) v.findViewById(R.id.lisview_registration);
            String[] languages = getResources().getStringArray(R.array.language_array);
            String[] languageCodes = getResources().getStringArray(R.array.language_local_array);
            adapter = new RegistrationAdapter(getActivity(), R.layout.country_spinner_item, languages, languageCodes, false, getArguments().getString(Constants.LANGUAGE));
            if(languageCodes != null){
                isSelectionMade = true;
                textview_ok.setTextColor(getResources().getColor(R.color.ecarezone_green_dark));
            }
        }

        listview_registration.setAdapter(adapter);
        listview_registration.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < parent.getCount(); i++) {
                    View v = parent.getChildAt(i);
                    TextView tv = (TextView) v.findViewById(R.id.text1);
                    if (i == position) {
                        tv.setBackgroundResource(R.drawable.circle_blue_complete);
                        item = tv.getText().toString();
                        itemCode = (String) tv.getTag();
                        continue;
                    }

                    tv.setBackgroundResource(R.drawable.circle_blue);
                    isSelectionMade = true;
                    textview_ok.setTextColor(getResources().getColor(R.color.ecarezone_green_dark));
                }

                System.out.println(" Registration Dialog itemCode " + itemCode);
            }
        });
        return v;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.button_ok) {
            if(isSelectionMade) {
                Intent i = new Intent();
                i.putExtra(Constants.ITEM, item);
                i.putExtra(Constants.ITEM_CODE, itemCode);

                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                dismiss();
            }
        } else {
            dismiss();
        }

    }
}
