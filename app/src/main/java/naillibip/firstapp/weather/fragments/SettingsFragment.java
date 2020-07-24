package naillibip.firstapp.weather.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;

import java.util.Locale;
import java.util.Objects;

import naillibip.firstapp.weather.App;
import naillibip.firstapp.weather.R;
import naillibip.firstapp.weather.activity.CitiesListActivity;

public class SettingsFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 1;

    private GoogleSignInClient googleSignInClient;
    private TextView tvGoogleAccount;
    private SignInButton signInButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvGoogleAccount = view.findViewById(R.id.tv_google_user_account);
        signInButton = view.findViewById(R.id.signInButton);

        Switch darkThemeSwitch = view.findViewById(R.id.switch_dark_theme);
        darkThemeSwitch.setChecked(App.getDarkModeStatus());
        darkThemeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            App.setDarkMode(isChecked);
            startActivity(new Intent(getActivity(), CitiesListActivity.class));
            getActivity().finish();
        });
        view.findViewById(R.id.layout_switch_dark_theme).setOnClickListener(v -> {
            darkThemeSwitch.setChecked(!darkThemeSwitch.isChecked());
        });

        //check existing Google login
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(Objects.requireNonNull(getActivity()));
        updateUI(account);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(getActivity()), gso);
        signInButton.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "Connection failed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            Log.w("myLogs", "signInResult:failed code=" + e.getStatusCode() + "; " + e.getMessage());
            Toast.makeText(getActivity(), "Failed to login", Toast.LENGTH_LONG).show();
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (tvGoogleAccount == null) return;
        if (account == null) {
            tvGoogleAccount.setVisibility(View.GONE);
            signInButton.setVisibility(View.VISIBLE);
        } else {
            signInButton.setVisibility(View.GONE);
            tvGoogleAccount.setVisibility(View.VISIBLE);
            String placeholder = "%1$s (%2$s)";
            tvGoogleAccount.setText(String.format(Locale.getDefault(), placeholder, account.getDisplayName(), account.getEmail()));
        }
    }
}
