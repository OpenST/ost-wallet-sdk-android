package ost.com.sampleostsdkapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import ost.com.sampleostsdkapplication.fragments.BaseFragment;
import ost.com.sampleostsdkapplication.fragments.LoginFragment;

import static ost.com.sampleostsdkapplication.Constants.OST_USER_ID;

public class MainActivity extends AppCompatActivity implements NavigationHost,
        BaseFragment.OnBaseFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mappy_main_activity);

        LogInUser logInUser = ((App) getApplicationContext()).getLoggedUser();
        if (null != logInUser) {
            String userId = logInUser.getOstUserId();

            Intent userListIntent = new Intent(getApplicationContext(), UsersListActivity.class);
            userListIntent.putExtra(OST_USER_ID, userId);
            userListIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getApplicationContext().startActivity(userListIntent);

        } else {
            if (savedInstanceState == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container, new LoginFragment())
                        .commit();
            }
        }
    }

    /**
     * Navigate to the given fragment.
     *
     * @param fragment       Fragment to navigate to.
     * @param addToBackstack Whether or not the current fragment should be added to the backstack.
     */
    @Override
    public void navigateTo(Fragment fragment, boolean addToBackstack) {
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment);

        if (addToBackstack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    @Override
    public void onBack() {

    }
}
