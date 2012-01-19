package com.coctelmental.android.project1886.taxis;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.coctelmental.android.project1886.R;
import com.coctelmental.android.project1886.common.TaxiDriver;
import com.coctelmental.android.project1886.helpers.ServiceRequestsHelper;
import com.coctelmental.android.project1886.helpers.UsersHelper;
import com.coctelmental.android.project1886.model.ResultBundle;
import com.coctelmental.android.project1886.util.JsonHandler;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class TaxiItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	private Context mContext;
	private ArrayList<OverlayItem> overlayList;

	private TaxiDriverInfoAsyncTask taxiDriverInfoAsyncTask;

	private AlertDialog overlayDialog;
	private View layoutInformationOverlay;

	public TaxiItemizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		overlayList = new ArrayList<OverlayItem>();
		mContext = context;

		// get custom overlay layout
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInformationOverlay = (View) inflater.inflate(
				R.layout.user_taxi_information_overlay, null);
		Button bSelectTaxiDriver = (Button) layoutInformationOverlay
				.findViewById(R.id.buttonSelectTaxi);

		// setup button
		bSelectTaxiDriver.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				overlayDialog.dismiss();
				Intent intent = new Intent(mContext,
						UserTaxiRequestConfirmation.class);
				mContext.startActivity(intent);
			}
		});

		// call populate to fix a bug (Issue 2035)
		populate();

		// setup overlay alert dialog
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
		dialogBuilder.setTitle(R.string.taxiDriverInformation);
		dialogBuilder.setView(layoutInformationOverlay);
		overlayDialog = dialogBuilder.create();
	}

	public void addOverlay(OverlayItem overlay) {
		overlayList.add(overlay);
	}

	@Override
	protected OverlayItem createItem(int i) {
		return overlayList.get(i);
	}

	@Override
	public int size() {
		return overlayList.size();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (!shadow) {
			super.draw(canvas, mapView, false);
		}
	}

	@Override
	protected boolean onTap(int index) {
		OverlayItem item = overlayList.get(index);
		String TaxiDriverId = item.getTitle();

		// retrieve taxi driver UUID from overlay snippet text
		String selectedTaxiDriverUUID = item.getSnippet();
		ServiceRequestsHelper.getServiceRequest().setTaxiDriverUUID(
				selectedTaxiDriverUUID);

		// show overlay dialog
		overlayDialog.show();

		// cancel previous launched async task if it's running
		if (taxiDriverInfoAsyncTask != null)
			taxiDriverInfoAsyncTask.cancel(true);

		taxiDriverInfoAsyncTask = new TaxiDriverInfoAsyncTask();
		// launch async task adding taxi driver id as param
		taxiDriverInfoAsyncTask.execute(TaxiDriverId);
		return true;
	}

	public void populateNow() {
		populate();
	}

	public void clear() {
		// clear array
		overlayList = new ArrayList<OverlayItem>();
		populate();
	}

	public boolean isOverlayDialogVisible() {
		if (overlayDialog != null && overlayDialog.isShowing())
			return true;
		return false;
	}

	private class TaxiDriverInfoAsyncTask extends
			AsyncTask<String, Void, ResultBundle> {

		protected void onPreExecute() {
			// hide other layout if they are being shown
			layoutInformationOverlay.findViewById(R.id.labelsContainer)
					.setVisibility(View.GONE);
			layoutInformationOverlay.findViewById(R.id.labelNoInfoTaxi)
					.setVisibility(View.GONE);
			// show progress bar
			layoutInformationOverlay.findViewById(R.id.labelLoadingBar)
					.setVisibility(View.VISIBLE);
		}

		protected ResultBundle doInBackground(String... params) {
			// send request to server and return response code
			return tryGetTaxiDriverInfo(params[0]);
		}

		protected void onPostExecute(ResultBundle rb) {
			// check result
			if (rb.getResultCode() == HttpURLConnection.HTTP_OK) {
				String jsonUser = rb.getContent();
				TaxiDriver taxiDriver = JsonHandler.fromJson(jsonUser,
						TaxiDriver.class);

				// set selected taxi driver ID
				ServiceRequestsHelper.getServiceRequest().setTaxiDriverID(
						taxiDriver.getDni());
				// set selected taxi driver name
				ServiceRequestsHelper.getServiceRequest()
						.setTaxiDriverFullName(taxiDriver.getFullName());
				// set selected taxi driver car brand
				ServiceRequestsHelper.getServiceRequest()
						.setTaxiDriverCarBrand(taxiDriver.getCarBrand());
				// set selected taxi driver car model
				ServiceRequestsHelper.getServiceRequest()
						.setTaxiDriverCarModel(taxiDriver.getCarModel());

				// get overlay textviews
				TextView tvTaxiDriverName = (TextView) layoutInformationOverlay
						.findViewById(R.id.labelTaxiDriverName);
				TextView tvCarName = (TextView) layoutInformationOverlay
						.findViewById(R.id.labelTaxiDriverCar);
				// fill textviews
				tvTaxiDriverName.setText(taxiDriver.getFullName());
				tvCarName.setText(taxiDriver.getCarBrand() + " "
						+ taxiDriver.getCarModel());

				// hide progress bar
				layoutInformationOverlay.findViewById(R.id.labelLoadingBar)
						.setVisibility(View.GONE);
				// show taxi driver info
				layoutInformationOverlay.findViewById(R.id.labelsContainer)
						.setVisibility(View.VISIBLE);
			} else {
				// hide progress bar
				layoutInformationOverlay.findViewById(R.id.labelLoadingBar)
						.setVisibility(View.GONE);
				// show error message
				layoutInformationOverlay.findViewById(R.id.labelNoInfoTaxi)
						.setVisibility(View.VISIBLE);
			}
		}
	}

	private ResultBundle tryGetTaxiDriverInfo(String taxiDriverID) {
		ResultBundle rb = null;
		rb = UsersHelper.getTaxiDriver(taxiDriverID);
		return rb;
	}

}
