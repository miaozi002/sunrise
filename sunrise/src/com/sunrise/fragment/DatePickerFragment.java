package com.sunrise.fragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.sunrise.R;
import com.sunrise.model.CurveModel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

public class DatePickerFragment extends DialogFragment {
	public static final String EXTRA_DATE = "com.sunrise.android.criminalintent.date";

	private CurveModel mCurveModel;
	private String mDatePickerTitle;

	public static DatePickerFragment newInstance(CurveModel curveModel) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_DATE, curveModel);

		DatePickerFragment fragment = new DatePickerFragment();
		fragment.setArguments(args);

		return fragment;
	}

	private void sendResult(int resultCode) {
		if (getTargetFragment() == null)
			return;
		Intent i = new Intent();
		i.putExtra(EXTRA_DATE, mCurveModel);

		getTargetFragment().onActivityResult(getTargetRequestCode(),
				resultCode, i);
	}

	@SuppressLint({ "InflateParams", "NewApi" })
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mCurveModel = (CurveModel) getArguments().getSerializable(EXTRA_DATE);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mCurveModel.getDate());
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		View v = getActivity().getLayoutInflater().inflate(
				R.layout.dialog_date, null);

		DatePicker datePicker = (DatePicker) v
				.findViewById(R.id.dialog_date_datePicker);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			Date maxDate = new Date();
			datePicker.setMaxDate(maxDate.getTime());
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			calendar.set(2011, 0, 1);
			Date minDate = calendar.getTime();
			datePicker.setMinDate(minDate.getTime());
		}

		datePicker.init(year, month, day, new OnDateChangedListener() {

			@Override
			public void onDateChanged(DatePicker arg0, int arg1, int arg2,
					int arg3) {
				Date newDate = new GregorianCalendar(arg1, arg2, arg3)
						.getTime();
				mCurveModel.setDate(newDate);
				getArguments().putSerializable(EXTRA_DATE, mCurveModel);
			}
		});
		
		int dateTag = mCurveModel.getDateTag();
		switch (dateTag) {
		case 0:
			mDatePickerTitle = "日期（年-月-日）";
			break;
		case 1:
			((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0))
					.getChildAt(2).setVisibility(View.GONE);
			mDatePickerTitle = "日期（年-月）";
			break;
		case 2:
			((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0))
					.getChildAt(1).setVisibility(View.GONE);
			((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0))
					.getChildAt(2).setVisibility(View.GONE);
			mDatePickerTitle = "日期（年）";
			break;
		default:
			break;
		}

		return new AlertDialog.Builder(getActivity())
				.setView(v)
				.setTitle(mDatePickerTitle)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								sendResult(Activity.RESULT_OK);
							}
						}).create();
	}

}
