package com.ecarezone.android.patient.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.ecarezone.android.patient.DoctorActivity;
import com.ecarezone.android.patient.DoctorBioActivity;
import com.ecarezone.android.patient.MainActivity;
import com.ecarezone.android.patient.R;
import com.ecarezone.android.patient.SearchActivity;
import com.ecarezone.android.patient.adapter.DoctorsAdapter;
import com.ecarezone.android.patient.config.Constants;
import com.ecarezone.android.patient.config.LoginInfo;
import com.ecarezone.android.patient.model.Doctor;
import com.ecarezone.android.patient.model.database.ChatDbApi;
import com.ecarezone.android.patient.model.database.DoctorProfileDbApi;
import com.ecarezone.android.patient.model.rest.SearchDoctorsRequest;
import com.ecarezone.android.patient.model.rest.SearchDoctorsResponse;
import com.ecarezone.android.patient.utils.ProgressDialogUtil;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by CHAO WEI on 5/25/2015.
 */
public class DoctorListFragment extends EcareZoneBaseFragment {

    public static final String ADD_DOCTOR_DISABLE_CHECK = "addDocotrDisablecheck";

    private static final String TAG = DoctorListFragment.class.getSimpleName();
    private static final int HTTP_STATUS_OK = 200;
    private ListView mycareDoctorListView = null;
    private ListView recommendedDoctorListView = null;
    private ArrayList<Doctor> doctorList;
    private ArrayList<Doctor> recommendedDoctorList;
    private SearchView searchView;
    private DoctorsAdapter mycareDoctorAdapter;
    private DoctorsAdapter recommendedDoctorAdapter;
    View mycareDoctorContainer;
    View recommendedDoctorContainer;
    View doctorsDivider;
    private ProgressDialog progressDialog;
    private boolean checkProgress;
    private float padding = 16;

    @Override
    protected String getCallerName() {
        return DoctorListFragment.class.getSimpleName();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setHasOptionsMenu(true);
        } catch (Exception e) {
        }
        ((MainActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getString(R.string.main_side_menu_doctors));
        pullDBFromdevice();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.menu_action_search);
        Log.i(TAG, "searchMenuItem = " + searchMenuItem);
        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setIconifiedByDefault(true);
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setQueryHint(getResources().getString(R.string.doctor_search_hint_text));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryString) {
                performDoctorSearch(queryString);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        View searchEditFrame = searchView.findViewById(R.id.search_edit_frame);
        searchEditFrame.setBackgroundResource(R.drawable.search_edittext_border);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == null) return false;

        final int itemId = item.getItemId();
        if (itemId == R.id.menu_action_search) {
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_doctor_list_2, container, false);

        mycareDoctorListView = (ListView) view.findViewById(R.id.mycare_doctors_list);

        recommendedDoctorListView = (ListView) view.findViewById(R.id.recommended_doctors_list);

        padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, getResources().getDisplayMetrics());

        mycareDoctorContainer = view.findViewById(R.id.mycare_doctors_container);
        recommendedDoctorContainer = view.findViewById(R.id.recommended_doctors_container);
        doctorsDivider = view.findViewById(R.id.doctors_divider);
        checkProgress = true;
        progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(),
                getText(R.string.progress_dialog_loading).toString());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(message,
                new IntentFilter("send"));
        populateMyCareDoctorList();
        populateRecommendedDoctorList();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(message);
    }

    private void populateMyCareDoctorList() {
        SearchDoctorsRequest request =
                new SearchDoctorsRequest(LoginInfo.userId, null, null, null, null, null);
        getSpiceManager().execute(request, new PopulateMyCareDoctorListRequestListener());
    }

    private void populateRecommendedDoctorList() {
        SearchDoctorsRequest request =
                new SearchDoctorsRequest(null, null, null, null, null, null);
        getSpiceManager().execute(request, new RecommendeDoctorListRequestListener());
    }

    private void performDoctorSearch(String queryString) {
        progressDialog = ProgressDialogUtil.getProgressDialog(getActivity(),
                getText(R.string.progress_dialog_search).toString());
        if (TextUtils.isEmpty(queryString)) {
            queryString = " ";
        }
        SearchDoctorsRequest request =
                new SearchDoctorsRequest(null, LoginInfo.userName,
                        LoginInfo.hashedPassword, Constants.API_KEY, Constants.deviceUnique, queryString);
        getSpiceManager().execute(request, new DoSearchRequestListener());
    }

    public final class PopulateMyCareDoctorListRequestListener implements RequestListener<SearchDoctorsResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            if (checkProgress) {
                checkProgress = false;
            } else {
                progressDialog.dismiss();
            }
        }

        @Override
        public void onRequestSuccess(SearchDoctorsResponse getDoctorsResponse) {
            if (getDoctorsResponse.status.code == HTTP_STATUS_OK) {
                doctorList = (ArrayList<Doctor>) getDoctorsResponse.data;
                ListIterator<Doctor> iter = doctorList.listIterator();
                Doctor doctor = null;
                while (iter.hasNext()) {
                    doctor = iter.next();
                    DoctorProfileDbApi doctorProfileDbApi = DoctorProfileDbApi.getInstance(getActivity());
                    Doctor id = doctorProfileDbApi.getProfile(doctor.emailId);
                    if (id == null || doctor.doctorId != id.doctorId) {
                        doctorProfileDbApi.saveProfile(doctor.doctorId, doctor);
                    } else {
                        doctorProfileDbApi.updateProfile(String.valueOf(doctor.doctorId), doctor);
                    }
                }

                if (doctorList.size() == 0) {
                    mycareDoctorContainer.setVisibility(View.GONE);
                    doctorsDivider.setVisibility(View.GONE);
                    if (recommendedDoctorList != null && recommendedDoctorList.size() > 0) {
                        recommendedDoctorContainer.setVisibility(View.VISIBLE);
                    } else {
                        recommendedDoctorContainer.setVisibility(View.GONE);
                    }
                } else if (doctorList.size() > 0) {

                    mycareDoctorAdapter = new DoctorsAdapter(getActivity(), doctorList);
                    mycareDoctorListView.setAdapter(mycareDoctorAdapter);

                    mycareDoctorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Log.i(TAG, "position = " + position);
                            Bundle data = new Bundle();
                            data.putParcelable(Constants.DOCTOR_DETAIL, doctorList.get(position));
                            data.putBoolean(Constants.DOCTOR_ALEADY_ADDED, true);
                            final Activity activity = getActivity();
                            if (activity != null) {
                                Intent showDoctorIntent = new Intent(activity.getApplicationContext(), DoctorActivity.class);
                                showDoctorIntent.putExtra(Constants.DOCTOR_DETAIL, data);
                                showDoctorIntent.putExtra(ADD_DOCTOR_DISABLE_CHECK, true);
                                activity.startActivity(showDoctorIntent);
                                activity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                            }
                        }
                    });

                    mycareDoctorContainer.setVisibility(View.VISIBLE);
                    doctorsDivider.setVisibility(View.VISIBLE);

                    if (recommendedDoctorList != null && recommendedDoctorList.size() > 0) {
                        recommendedDoctorContainer.setVisibility(View.VISIBLE);
                    } else {
                        recommendedDoctorContainer.setVisibility(View.GONE);
                    }
                }
            } else {
            }
            if (checkProgress) {
                checkProgress = false;
            } else {
                progressDialog.dismiss();
            }
        }
    }

    public final class DoSearchRequestListener implements RequestListener<SearchDoctorsResponse> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            progressDialog.dismiss();
        }

        @Override
        public void onRequestSuccess(SearchDoctorsResponse searchDoctorsResponse) {
            if (searchDoctorsResponse.status.code == HTTP_STATUS_OK) {
                ArrayList<Doctor> doctorList = (ArrayList<Doctor>) searchDoctorsResponse.data;
                Bundle data = new Bundle();
                data.putParcelableArrayList(Constants.DOCTOR_LIST, doctorList);
                final Activity activity = getActivity();
                if (activity != null) {
                    Intent searchIntent = new Intent(activity.getApplicationContext(), SearchActivity.class);
                    searchIntent.putExtra(Constants.DOCTOR_LIST, data);
                    activity.startActivity(searchIntent);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Failed to search: " + searchDoctorsResponse.status.message, Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        }
    }

    public final class RecommendeDoctorListRequestListener implements RequestListener<SearchDoctorsResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {

            if (checkProgress) {
                checkProgress = false;
            } else {
                progressDialog.dismiss();
            }
        }

        @Override
        public void onRequestSuccess(SearchDoctorsResponse getRecommendedDoctorsResponse) {
            if (getRecommendedDoctorsResponse.status.code == HTTP_STATUS_OK) {
                recommendedDoctorList = (ArrayList<Doctor>) getRecommendedDoctorsResponse.data;
                if (recommendedDoctorList.size() > 0) {
                    recommendedDoctorAdapter = new DoctorsAdapter(getActivity(), recommendedDoctorList);
                    recommendedDoctorListView.setAdapter(recommendedDoctorAdapter);
                    recommendedDoctorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                         @Override
                         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                             Log.i(TAG, "position = " + position);
                             Bundle data = new Bundle();
                             data.putParcelable(Constants.DOCTOR_DETAIL, recommendedDoctorList.get(position));

                             final Activity activity = getActivity();
                             if (activity != null) {
                                 Intent showDoctorIntent = new Intent(activity.getApplicationContext(), DoctorBioActivity.class);

                                 if (checkDocotorExist(position)) {
                                     data.putBoolean(ADD_DOCTOR_DISABLE_CHECK, true);
                                 } else {
                                     data.putBoolean(ADD_DOCTOR_DISABLE_CHECK, false);
                                 }
                                 showDoctorIntent.putExtra(Constants.DOCTOR_BIO_DETAIL, data);
                                 activity.startActivity(showDoctorIntent);
                                 activity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                             }
                         }
                     }

                    );

                    recommendedDoctorContainer.setVisibility(View.VISIBLE);

                    if (doctorList != null && doctorList.size() > 0)

                    {
                        mycareDoctorContainer.setVisibility(View.VISIBLE);
                        doctorsDivider.setVisibility(View.VISIBLE);
                    } else

                    {
                        mycareDoctorContainer.setVisibility(View.GONE);
                        doctorsDivider.setVisibility(View.GONE);
                    }
                } else if (recommendedDoctorList.size() == 0) {
                    recommendedDoctorContainer.setVisibility(View.GONE);
                    if (doctorList != null && doctorList.size() > 0) {
                        mycareDoctorContainer.setVisibility(View.VISIBLE);
                        doctorsDivider.setVisibility(View.VISIBLE);
                    } else {
                        mycareDoctorContainer.setVisibility(View.GONE);
                        doctorsDivider.setVisibility(View.GONE);
                    }
                }
                Utility.setListViewHeightBasedOnChildren(mycareDoctorListView, padding);
                Utility.setListViewHeightBasedOnChildren(recommendedDoctorListView, padding);
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get doctors: " + getRecommendedDoctorsResponse.status.message, Toast.LENGTH_LONG).show();
            }
            if (checkProgress) {
                checkProgress = false;
            } else {
                progressDialog.dismiss();
            }

        }
    }

    private boolean checkDocotorExist(int position) {
        if (recommendedDoctorList != null && doctorList != null) {
            Long id = ((Doctor) recommendedDoctorList.get(position)).doctorId;
            for (Doctor doctor : doctorList) {
                if (doctor.doctorId.equals(id)) {
                    return true;
                }
            }
        }
        return false;

    }

    @SuppressWarnings("resource")
    private void pullDBFromdevice() {
        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {

                String currentDBPath = getApplicationContext().getDatabasePath("ecarezone.db").toString();/*"/data/" + getApplicationContext().getPackageName() + "/databases/ecarezone"*/

                File currentDB = new File(currentDBPath);

                String backupDBPath = "ecarezone.db";
                File backupDB = new File(sd, "/Download/" + backupDBPath);
                if (!backupDB.exists()) {
                    backupDB.createNewFile();
                }

                FileChannel src = new FileInputStream(currentDB)
                        .getChannel();
                FileChannel dst = new FileOutputStream(backupDB)
                        .getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

            }
        } catch (Exception e) {
            Log.e("", e.toString());
        }
    }

//    BroadcastReceiver message = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            updateUnreadMessageCount(ChatDbApi.getInstance(context).getUnReadChatCount());
//        }
//    };

    BroadcastReceiver message = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(mycareDoctorAdapter != null) {
                mycareDoctorAdapter.notifyDataSetChanged();
            }
            if(recommendedDoctorAdapter != null) {
                recommendedDoctorAdapter.notifyDataSetChanged();
            }
        }
    };

}

class Utility {

    public static void setListViewHeightBasedOnChildren(ListView listView, float padding) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        totalHeight += padding;//for padding at the bottom

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + 20;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

}
