package com.example.fbdatafetch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ExpandableListView;

public class MainActivity extends FragmentActivity {

	private MainFragment mainFragment;
	private TCPClient mTcpClient;
	private ArrayList<Group> groups;
	private ExpandableListView listView;
	private EListAdapter adapter;
	private String UserInfo;
	private String Map;
	private DrawingView dv;
	private double[] x, y;
	int User_id = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			// Add the fragment on initial activity setup
			mainFragment = new MainFragment();
			getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, mainFragment).commit();
		} else {
			// Or set the fragment from restored state info
			mainFragment = (MainFragment) getSupportFragmentManager()
					.findFragmentById(android.R.id.content);
		}

		// dv = (DrawingView) findViewById(R.id.drawview);
		new connectTask().execute("");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onClick_Refresh_Click(View view) {
		UserInfo = mainFragment.getUserDate() + ";0;0;0;" + "%false";
		writeToFile(UserInfo);
		if (mTcpClient != null) {
			mTcpClient.sendMessage(UserInfo);
		}

		dv = (DrawingView) findViewById(R.id.drawview);
		// x += 10;
		// y += 20;
		// dv.SetPos(x, y);
		// dv.invalidate();

		Log.e("Refresh", UserInfo);
	}

	public void onClick_Pop_Click(View view) {
		setContentView(R.layout.pop_layout);

		int[] num_child = { 5, 5, 5 };

		groups = new ArrayList<Group>();
		for (int i = 0; i < 3; i++) {
			Group group;
			if (i == 0) {
				group = new Group(Integer.toString(i), "Like action movie?");
			} else if (i == 1) {
				group = new Group(Integer.toString(i), "Like contry music?");
			} else {
				group = new Group(Integer.toString(i), "Like Prof. Lin?");
			}
			for (int j = 0; j < num_child[i]; j++) {
				Child child = new Child(Integer.toString(j), "Star"
						+ Integer.toString(j + 1), Integer.toString(i));
				group.addChildrenItem(child);
			}
			groups.add(group);
		}

		listView = (ExpandableListView) findViewById(R.id.listView);
		adapter = new EListAdapter(this, groups);
		listView.setAdapter(adapter);
	}

	public void onClick_Back_Click(View view) {
		setContentView(R.layout.activity_main);
	}

	public void onClick_Send_Click(View view) {
		String[] PopStar = { "0", "0", "0" };
		for (int i = 0; i < groups.size(); i++) {
			for (int j = 0; j < groups.get(i).getChildrenCount(); j++) {
				if (groups.get(i).getChildItem(j).getChecked())
					// Log.e("ListView", "enable.bScene0" + i + "Test0" + j +
					// "= true\n\n");
					PopStar[i] = String.valueOf(j + 1);
			}
		}
		UserInfo = mainFragment.getUserDate() + ";" + PopStar[0] + ";"
				+ PopStar[1] + ";" + PopStar[2] + ";%true";
		writeToFile(UserInfo);
		if (mTcpClient != null) {
			mTcpClient.sendMessage(UserInfo);
		}
		Log.e("ListView", UserInfo);
	}

	public void writeToFile(String data) {
		File sdCard = Environment.getExternalStorageDirectory();
		File file = new File(sdCard, "mydatafile.txt");

		if (file.exists()) {
			try {
				FileOutputStream fOut = new FileOutputStream(file);
				OutputStreamWriter osw = new OutputStreamWriter(fOut);
				osw.write(data);
				// osw.flush();
				osw.close();
				fOut.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void visualization() {
		String[] Map_Info = Map.split(";");
		int num = Integer.parseInt(Map_Info[0]);
		String[] Users_Name = new String[num];
		x = new double[num];
		y = new double[num];
		for (int i = 0; i < num; i++) {
			String[] User_pos = Map_Info[i + 1].split(",");
			Users_Name[i] = User_pos[0];
			x[i] = Double.parseDouble(User_pos[2]);
			y[i] = Double.parseDouble(User_pos[3]);
		}

		List<String> Pairs = new ArrayList<String>();
		for (int i = 1 + num; i < Map_Info.length; i++) {
			Pairs.add(Map_Info[i]);
		}

		int[][] A = new int[num][num];
		for (int i = 0; i < num - 1; i++) {
			for (int j = i + 1; j < num; j++) {
				A[i][j] = 0;
			}
		}
		for (int i = 0; i < Pairs.size(); i++) {
			String[] Pairs_Info = Pairs.get(i).split(",");
			A[Integer.parseInt(Pairs_Info[0])][Integer.parseInt(Pairs_Info[1])] = Integer
					.parseInt(Pairs_Info[2]);
		}

		//int[] x_int = new int[x.length];
		//int[] y_int = new int[y.length];

		int Z = 1000;
		while (Z > 0) {
			directedForce(num, x, y, A);

			//for (int i = 0; i < x.length; i++) {
			//	x_int[i] = (int) x[i];
			//	y_int[i] = (int) y[i];
			//}
			Z--;
			//Log.e("show pos", "Hello0: " + x[0] + ", " + y[0]);
			//Log.e("show pos", "Hello1: " + x[1] + ", " + y[1]);
		}

		dv.SetPos(x, y);
		dv.SetA(A, num, User_id);
		dv.SetUsersName(Users_Name);
		dv.invalidate();

		for (int i = 0; i < num; i++) {
			Log.e("show pos", "Hello" + i + ": " + x[i] + ", " + y[i]);
		}

	}

	public void directedForce(int num, double[] x, double[] y, int[][] A) {
		double kuv1 = -0.01;
		double kuv2 = 100;
		double lenuv = 3;
		double[] fx = new double[num];
		double[] fy = new double[num];
		for (int i = 0; i < num; i++) {
			fx[i] = 0;
			fy[i] = 0;
			for (int j = 0; j < num; j++) {
				if (i != j) {
					double Dis = Math.sqrt(Math.pow(x[i] - x[j], 2)
							+ Math.pow(y[i] - y[j], 2));
					if (A[i][j] > 0) {
						// add weight effect
						fx[i] += (kuv1 * (Math.pow(A[i][j], 2)/10) * (Dis - lenuv) * (x[i] - x[j]))
								/ Dis;
						fy[i] += (kuv1 * (Math.pow(A[i][j], 2)/10) * (Dis - lenuv) * (y[i] - y[j]))
								/ Dis;
					}
					fx[i] += (kuv2 * (x[i] - x[j])) / Math.pow(Dis, 3);
					fy[i] += (kuv2 * (y[i] - y[j])) / Math.pow(Dis, 3);
				}
			}
		}
		double k = 0.05;
		for (int i = 0; i < num; i++) {
			x[i] += k * fx[i];
			y[i] += k * fy[i];
		}

	}

	public class connectTask extends AsyncTask<String, String, TCPClient> {

		@Override
		protected TCPClient doInBackground(String... message) {

			// we create a TCPClient object and
			mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
				@Override
				// here the messageReceived method is implemented
				public void messageReceived(String message) {
					// this method calls the onProgressUpdate
					publishProgress(message);
				}
			});
			mTcpClient.run();

			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);

			// in the arrayList we add the messaged received from server
			// arrayList.add(values[0]);
			// notify the adapter that the data set has changed. This means that
			// new message received
			// from server was added to the list
			// mAdapter.notifyDataSetChanged();

			Log.e("TCPClient", "Get msg:" + values[0]);
			Map = values[0];
			String[] User_data = UserInfo.split(";");
			String[] All_data = Map.split(";");
			for (int i = 0; i < All_data.length; i++) {
				String[] Per_data = All_data[i].split(",");
				for (int j = 0; j < Per_data.length; j++) {
					if (User_data[0].compareTo(Per_data[j]) == 0)
						User_id = Integer.parseInt(Per_data[j+1]);
				}
			}
			visualization();
		}
	}

}
