package com.sunrise;

import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.ListView;

public class MultiChoiceListDialogFragment extends DialogFragment {
	public static final String EXTRA_CURVE_MODEL = "com.sunrise.android.realtimecurve.curve";

	private CurveModel mCurveModel;
	private ListView lv;
	private boolean[] checkedItems;

	public static MultiChoiceListDialogFragment newInstance(
			CurveModel curveModel) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_CURVE_MODEL, curveModel);

		MultiChoiceListDialogFragment fragment = new MultiChoiceListDialogFragment();

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
		mCurveModel = (CurveModel) getArguments().getSerializable(
				EXTRA_CURVE_MODEL);

		final String[] curveNames = mCurveModel.getCurveNames();
		if (curveNames != null) {
			checkedItems = new boolean[curveNames.length];
			if (!mCurveModel.isSelect()) {
				for (int i = 0; i < curveNames.length; i++) {
					checkedItems[i] = true;
				}
			} else {
				for (int i = 0; i < curveNames.length; i++) {
					if (Arrays.binarySearch(mCurveModel.getSelectCurves(), i) >= 0) {
						checkedItems[i] = true;
					} else {
						checkedItems[i] = false;
					}
				}
			}
		} else {
			checkedItems = null;
		}

		AlertDialog builder = new AlertDialog.Builder(getActivity())
				.setTitle("请选择要显示的曲线：")
				.setMultiChoiceItems(curveNames, checkedItems,
						new OnMultiChoiceClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {

							}
						})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@SuppressLint("NewApi") @Override
					public void onClick(DialogInterface dialog, int which) {
						if (lv != null) {
							int[] positions = new int[lv.getCheckedItemCount()];
							for (int i = 0, j = 0; i < curveNames.length; i++) {
								if (lv.getCheckedItemPositions().get(i)) {
									positions[j++] = i;
								}
							}
							mCurveModel.setSelectCurves(positions);
							getArguments().putSerializable(EXTRA_CURVE_MODEL,
									mCurveModel);
							sendResult(Activity.RESULT_OK);
						}
					}
				}).setNegativeButton("取消", null).create();

		lv = builder.getListView();
		return builder;
	}

}
