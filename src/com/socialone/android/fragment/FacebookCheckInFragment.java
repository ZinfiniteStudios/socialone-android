package com.socialone.android.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphPlace;
import com.google.analytics.tracking.android.EasyTracker;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.socialone.android.R;

import java.util.List;

/**
 * Created by david.hodge on 12/25/13.
 */
public class FacebookCheckInFragment extends SherlockFragment{

    View view;
    ListView listView;
    private UiLifecycleHelper uiHelper;
    Session session;
    Location location;
    LocationManager locationManager;
    GoogleCardsAdapter googleCardsAdapter;

    Dialog dialog;
    EditText checkinMessge;
    Button cancelCheckinBtn;
    Button checkinBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(getSherlockActivity(), new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                onSessionStateChange(session, state, exception);
            }
        });
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.social_checkin_list, container, false);
        listView = (ListView) view.findViewById(R.id.activity_googlecards_listview);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getFacebookLocations();
    }

    private void getFacebookLocations(){
        locationManager = (LocationManager) getSherlockActivity().getSystemService(getSherlockActivity().LOCATION_SERVICE);
        String bestProvider = locationManager.getBestProvider(new Criteria(), false);
        location = locationManager.getLastKnownLocation(bestProvider);
        session = ensureFacebookSessionFromCache(getSherlockActivity());
//        Request locRequest = new Request.newPlacesSearchRequest(session, location, 100, 25, null, new Request.GraphPlaceListCallback())
        double lat = location.getLatitude(),lon=location.getLongitude();
        Bundle params = new Bundle();
        params.putString("type", "place");
        params.putString("center", lat + "," + lon);
        params.putString("distance", "5000");
        params.putString("limit", "40");

        Request.GraphPlaceListCallback graphPlaceListCallback = new Request.GraphPlaceListCallback() {
            @Override
            public void onCompleted(List<GraphPlace> places, Response response) {
                googleCardsAdapter = new GoogleCardsAdapter(getSherlockActivity(), places);
                SwingBottomInAnimationAdapter swingBottomInAnimationAdapter =  new SwingBottomInAnimationAdapter(googleCardsAdapter);
                swingBottomInAnimationAdapter.setInitialDelayMillis(300);
                swingBottomInAnimationAdapter.setAbsListView(listView);
                listView.setAdapter(swingBottomInAnimationAdapter);
                googleCardsAdapter.setData(places);
            }
        };
        Request request = new Request().newPlacesSearchRequest(session, location, 500, 40, null, graphPlaceListCallback);

        Request.Callback callback = new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                Log.d("places", response.toString());
            }
        };

        request.executeAsync();
        Request request1 = new Request(session, "/search", params,
                HttpMethod.GET, callback);
        request1.executeAsync();

    }


    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
//            Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show();
//            getUserInfo();
        } else if (state.isClosed()) {
            Toast.makeText(getSherlockActivity(), "error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(getSherlockActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(getSherlockActivity());
    }

    public static Session ensureFacebookSessionFromCache(Context context){
        Session activeSession = Session.getActiveSession();
        if (activeSession == null || !activeSession.getState().isOpened()) {
            activeSession = Session.openActiveSessionFromCache(context);
        }
        return activeSession;
    }

    public void checkinDialog(GraphPlace place){
        final String placeName = place.getName();
        final String placeId = place.getId();

        dialog = new Dialog(getSherlockActivity());
        dialog.setContentView(R.layout.checkin_dialog);
        checkinMessge =  (EditText) dialog.findViewById(R.id.checkin_message);
        cancelCheckinBtn = (Button) dialog.findViewById(R.id.checkin_message_cancel);
        checkinBtn = (Button) dialog.findViewById(R.id.checkin_message_checkin);

        cancelCheckinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        checkinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //just text post
                Bundle postParams = new Bundle();
                postParams.putString("type", "checkin");
                postParams.putString("message", checkinMessge.getText().toString());
                postParams.putString("place", placeId);
                Request.Callback callback = new Request.Callback() {
                    public void onCompleted(Response response) {

                        FacebookRequestError error = response.getError();
                        if (error != null) {
                            dialog.dismiss();
                            Toast.makeText(getSherlockActivity(),
                                    error.getErrorMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            Toast.makeText(getSherlockActivity(),
                                    "Facebook Share completed",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                };
                Request request = new Request(session, "me/feed", postParams,
                        HttpMethod.POST, callback);

                RequestAsyncTask task = new RequestAsyncTask(request);
                task.execute();

            }
        });

        dialog.setTitle(placeName);
        dialog.show();
    }

    public class GoogleCardsAdapter extends BaseAdapter {

        private Context mContext;
        private List<GraphPlace> mAppPlace;
        private boolean mShouldReturnEmpty = true;

        public GoogleCardsAdapter(Context context, List<GraphPlace> appPlace) {
            mContext = context;
            mAppPlace = appPlace;
        }

        public void setData(List<GraphPlace> appPlace){
            mAppPlace = appPlace;
        }

        @Override
        public boolean isEmpty() {
            return mShouldReturnEmpty && super.isEmpty();
        }

        @Override
        public int getCount() {
            return mAppPlace.size();
        }

        @Override
        public GraphPlace getItem(int position) {
            return mAppPlace.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.social_checkin_list_item, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) view.findViewById(R.id.social_checkin_name);
                viewHolder.checkBox = (Button) view.findViewById(R.id.social_checkin_checkbox);

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            final GraphPlace place = getItem(position);
            viewHolder.textView.setText(place.getName());
            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkinDialog(place);
                }
            });
//            setImageView(viewHolder, position);

            return view;
        }

        public class ViewHolder {
            TextView textView;
            Button checkBox;
        }
    }
}
