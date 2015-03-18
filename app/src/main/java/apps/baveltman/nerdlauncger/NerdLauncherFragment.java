package apps.baveltman.nerdlauncger;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class NerdLauncherFragment extends ListFragment {

    private static final String TAG = "NerdLauncherFragment";

    private PackageManager mPackageManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get list of activities to launch
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        mPackageManager = getActivity().getPackageManager();
        List<ResolveInfo> activities = mPackageManager.queryIntentActivities(startupIntent, 0);
        Log.i(TAG, "I've found " + activities.size() + " activities.");

        //sort collection of returned results
        Collections.sort(activities, new Comparator<ResolveInfo>() {
            public int compare(ResolveInfo a, ResolveInfo b) {
                PackageManager pm = getActivity().getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(
                        a.loadLabel(pm).toString(),
                        b.loadLabel(pm).toString());
            }
        });

        //set the adapter for the list
        ArrayAdapter<ResolveInfo> adapter = new IntentListAdapter(activities);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        ResolveInfo resolveInfo = (ResolveInfo)l.getAdapter().getItem(position);
        ActivityInfo activityInfo = resolveInfo.activityInfo;

        if (activityInfo == null) return;

        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setClassName(activityInfo.applicationInfo.packageName, activityInfo.name);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private class IntentListAdapter extends ArrayAdapter<ResolveInfo> {

        public IntentListAdapter(List<ResolveInfo> intents) {
            super(getActivity(), 0, intents);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // If we weren't given a view, inflate one
            if(convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(android.R.layout.simple_list_item_1, null);
            }

            // Documentation says that simple_list_item_1 is a TextView,
            // so cast it so that you can set its text value
            TextView tv = (TextView)convertView;
            ResolveInfo ri = getItem(position);
            tv.setText(ri.loadLabel(mPackageManager));
            return convertView;
        }
    }

}
