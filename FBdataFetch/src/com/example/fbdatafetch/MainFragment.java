package com.example.fbdatafetch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import com.facebook.SessionState;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

public class MainFragment extends Fragment {

	private static final String TAG = "MainFragment";
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	private UiLifecycleHelper uiHelper;

	//private TextView userInfoTextView;
	
	private String UserData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_main, container, false);

		LoginButton authButton = (LoginButton) view
				.findViewById(R.id.authButton);

		authButton.setFragment(this);
		authButton.setReadPermissions(Arrays.asList("user_location",
				"user_birthday", "user_likes"));

		//userInfoTextView = (TextView) view.findViewById(R.id.userInfoTextView);

		return view;
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			//userInfoTextView.setVisibility(View.VISIBLE);

			// Request user data and show the results
			Request.newMeRequest(session, new Request.GraphUserCallback() {

				@Override
				public void onCompleted(GraphUser user, Response response) {
					if (user != null) {
						// Display the parsed user info
						UserData = buildUserInfoDisplay(user);
						//userInfoTextView.setText(UserData);
						//Log.e("aaa", UserData);

					}
				}
			}).executeAsync();

		} else if (state.isClosed()) {
			//userInfoTextView.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		// For scenarios where the main activity is launched and user
		// session is not null, the session state change notification
		// may not be triggered. Trigger it if it's open/closed.
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}

		uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
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
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	private String buildUserInfoDisplay(GraphUser user) {
		StringBuilder userInfo = new StringBuilder("");
		StringBuilder userInfoForServer = new StringBuilder("");

		// Example: typed access (name)
		// - no special permissions required
		//userInfo.append(String.format("Name: %s\n", user.getName()));
		userInfo.append(String.format("Name: %s\n", user.getName()));
		userInfoForServer.append(String.format("%s", user.getName()));
		
		// Example: typed access (birthday)
		// - requires user_birthday permission
		userInfo.append(String.format("Birthday: %s\n", user.getBirthday()));
		userInfoForServer.append(String.format(";%s", user.getBirthday()));
		
		// Example: partially typed access, to location field,
		// name key (location)
		// - requires user_location permission
		userInfo.append(String.format("Location: %s\n", user.getLocation()
				.getProperty("name")));
		userInfoForServer.append(String.format(";%s", user.getLocation().getProperty("name")));
		
		// Example: access via property name (locale)
		// - no special permissions required
		userInfo.append(String.format("Locale: %s\n",
				user.getProperty("locale")));
		userInfoForServer.append(String.format(";%s", user.getProperty("locale")));
		
		// Example: access via key for array (languages)
		// - requires user_likes permission
		JSONArray languages = (JSONArray) user.getProperty("languages");
		if (languages.length() > 0) {
			ArrayList<String> languageNames = new ArrayList<String>();
			for (int i = 0; i < languages.length(); i++) {
				JSONObject language = languages.optJSONObject(i);
				// Add the language name to a list. Use JSON
				// methods to get access to the name field.
				languageNames.add(language.optString("name"));
			}
			userInfo.append(String.format("Languages: %s\n",
					languageNames.toString()));
			userInfoForServer.append(String.format(";%s", languageNames.toString()));
		}

		return userInfoForServer.toString();
	}

	public String getUserDate(){
		return UserData;
	}

}
