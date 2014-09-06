package com.xebia.xtime;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.SimpleAdapter;

import com.xebia.xtime.authenticator.Authenticator;
import com.xebia.xtime.dayoverview.DayOverviewActivity;
import com.xebia.xtime.monthoverview.MonthPagerFragment;
import com.xebia.xtime.shared.model.DayOverview;
import com.xebia.xtime.weekoverview.DailyHoursListFragment;
import com.xebia.xtime.weekoverview.WeekPagerFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OverviewActivity extends ActionBarActivity implements DailyHoursListFragment.Listener {

    private static final String TAG = "OverviewActivity";
    private AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAccountManager = AccountManager.get(this);
        if (hasXTimeAccount()) {
            setListNavigation();
        } else {
            Log.d(TAG, "Add account");
            AccountManagerCallback<Bundle> callback = new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> future) {
                    try {
                        Log.d(TAG, "AccountManagerCallback called");
                        if (future.isDone()) {
                            Bundle result = future.getResult();
                            String authToken = result.getString(AccountManager.KEY_AUTHTOKEN);
                            Log.i(TAG, "Auth token: '" + authToken + "'");
                            setListNavigation();
                        }
                    } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                        Log.e(TAG, "Failed to get auth token", e);
                    }
                }
            };
            mAccountManager.addAccount(Authenticator.ACCOUNT_TYPE, Authenticator.AUTH_TYPE, null,
                    null, this, callback, null);
        }
    }

    private boolean hasXTimeAccount() {
        Account[] accounts = mAccountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE);
        return accounts.length > 0;
    }

    private void setListNavigation() {

        final List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("title", getString(R.string.title_week_overview));
        map.put("fragment", Fragment.instantiate(this, WeekPagerFragment.class.getName()));
        data.add(map);
        map = new HashMap<>();
        map.put("title", getString(R.string.title_month_overview));
        map.put("fragment", Fragment.instantiate(this, MonthPagerFragment.class.getName()));
        data.add(map);
        SimpleAdapter adapter = new SimpleAdapter(this, data,
                R.layout.ab_nav_dropdown, new String[]{"title"},
                new int[]{android.R.id.text1});

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(adapter,
                new ActionBar.OnNavigationListener() {
                    @Override
                    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                        Map<String, Object> map = data.get(itemPosition);
                        Object o = map.get("fragment");
                        if (o instanceof Fragment) {
                            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
                            tx.replace(android.R.id.content, (Fragment) o);
                            tx.commit();
                        }
                        return true;
                    }
                }
        );
    }

    @Override
    public void onItemClicked(DayOverview overview) {
        Intent intent = new Intent(this, DayOverviewActivity.class);
        intent.putExtra(DayOverviewActivity.EXTRA_DAY_OVERVIEW, overview);
        startActivity(intent);
    }
}
