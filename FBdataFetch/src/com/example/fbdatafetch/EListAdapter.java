package com.example.fbdatafetch;

import java.util.ArrayList;

import android.content.Context;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class EListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private ArrayList<Group> groups;

	public EListAdapter(Context context, ArrayList<Group> groups) {
		this.context = context;
		this.groups = groups;
	}

	public Object getChild(int groupPosition, int childPosition) {
		groups.get(groupPosition).getChildItem(childPosition);
		return null;
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public int getChildrenCount(int groupPosition) {
		return groups.get(groupPosition).getChildrenCount();
	}

	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	public int getGroupCount() {
		return groups.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public boolean hasStableIds() {
		return false;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		Group group = (Group) getGroup(groupPosition);

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.group_layout, null);
		}

		TextView tv = (TextView) convertView.findViewById(R.id.tvGroup);
		tv.setText(group.getTitle());

		CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.chbGroup);
		checkBox.setChecked(group.getChecked());

		checkBox.setOnClickListener((android.view.View.OnClickListener) new Group_CheckBox_Click(
				Integer.valueOf(groupPosition)));

		return convertView;
	}

	class Group_CheckBox_Click implements OnClickListener {
		private int groupPosition;

		Group_CheckBox_Click(int groupPosition) {
			this.groupPosition = groupPosition;
		}

		@Override
		public void onClick(View v) {
			groups.get(groupPosition).toggle();

			int childrenCount = groups.get(groupPosition).getChildrenCount();
			boolean groupIsChecked = groups.get(groupPosition).getChecked();
			for (int i = 0; i < childrenCount; i++)
				groups.get(groupPosition).getChildItem(i)
						.setChecked(groupIsChecked);

			notifyDataSetChanged();
		}

	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		Child child = groups.get(groupPosition).getChildItem(childPosition);

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.child_layout, null);
		}

		TextView tv = (TextView) convertView.findViewById(R.id.tvChild);
		tv.setText(child.getFullname());

		CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.chbChild);
		checkBox.setChecked(child.getChecked());

		checkBox.setOnClickListener((android.view.View.OnClickListener) new Child_CheckBox_Click(
				Integer.valueOf(groupPosition), Integer.valueOf(childPosition)));

		return convertView;
	}

	class Child_CheckBox_Click implements OnClickListener {
		private int groupPosition;
		private int childPosition;

		Child_CheckBox_Click(int groupPosition, int childPosition) {
			this.groupPosition = groupPosition;
			this.childPosition = childPosition;
		}

		public void onClick(View v) {
			groups.get(groupPosition).getChildItem(childPosition).toggle();

			int childrenCount = groups.get(groupPosition).getChildrenCount();
			boolean childrenNoneIsChecked = false;
			for (int i = 0; i < childrenCount; i++) {
				if (groups.get(groupPosition).getChildItem(i).getChecked())
					childrenNoneIsChecked = true;
			}

			groups.get(groupPosition).setChecked(childrenNoneIsChecked);

			notifyDataSetChanged();
		}
	}

}