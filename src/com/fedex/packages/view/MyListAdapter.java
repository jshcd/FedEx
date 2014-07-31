package com.fedex.packages.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fedex.packages.R;
import com.fedex.packages.model.CustomListItem;

/**
 * Adapter to show <code>CustomListItem</code> objects on a
 * <code>ListView</code>.
 * 
 * @author Javier Sanchez Hernandez
 * @version 1.0
 */
public class MyListAdapter extends ArrayAdapter<CustomListItem> {

	public MyListAdapter(Context context, ArrayList<CustomListItem> countries) {
		super(context, R.layout.list_item, countries);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item for this position
		CustomListItem listItem = getItem(position);
		// Check if the list item is a header or not
		if (listItem.isHeader()) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.list_header, parent, false);
		} else {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.list_item, parent, false);
		}

		// Lookup view for data population
		TextView tvFedExPackageId = (TextView) convertView
				.findViewById(R.id.textViewPackageId);
		// Populate the data into the template view using the data object
		tvFedExPackageId.setText(listItem.getText());
		// Return the completed view to render on screen
		return convertView;
	}
}
