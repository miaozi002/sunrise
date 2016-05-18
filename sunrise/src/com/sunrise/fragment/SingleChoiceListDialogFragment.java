package com.sunrise.fragment;

import com.sunrise.model.CurveModel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

public class SingleChoiceListDialogFragment extends DialogFragment {
	public static final String EXTRA_SELECT = "com.sunrise.android.realtimecurve.select";
	public static final String EXTRA_CURVE_MODEL = "com.sunrise.android.realtimecurve.curve";

	private CurveModel mCurveModel;
	private String mTitle;
	private static int mItem;

	public static SingleChoiceListDialogFragment newInstance(CurveModel curveModel) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_CURVE_MODEL, curveModel);

		SingleChoiceListDialogFragment fragment = new SingleChoiceListDialogFragment();
		
		fragment.setArguments(args);

		return fragment;
	}

	private void sendResult(int resultCode) {
		if (getTargetFragment() == null)
			return;
		Intent i = new Intent();
		i.putExtra(EXTRA_CURVE_MODEL, mCurveModel);

		getTargetFragment().onActivityResult(getTargetRequestCode(),
				resultCode, i);
	}

	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final String[] listItems;
		mCurveModel = (CurveModel) getArguments().getSerializable(EXTRA_CURVE_MODEL);
		mTitle = mCurveModel.getTitle();
		if (mTitle.equals("回路")) {
			listItems = mCurveModel.getLoops();
			mItem = mCurveModel.getLoopItem();
		} else {
			listItems = mCurveModel.getCurveTypes();
			mItem = mCurveModel.getCurveTypesItem();
		}
		return new AlertDialog.Builder(getActivity())
				.setTitle(mTitle)
				.setSingleChoiceItems(listItems, mItem,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int item) {
								if (mTitle.equals("回路")) {
									mCurveModel.setLoopItem(item);
								} else {
									mCurveModel.setCurveTypesItem(item);
								}
								getArguments().putSerializable(EXTRA_CURVE_MODEL, mCurveModel);
								sendResult(Activity.RESULT_OK);
								dialog.cancel();
							}
						}).create();
	}
}
