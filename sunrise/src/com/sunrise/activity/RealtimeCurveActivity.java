package com.sunrise.activity;

import com.sunrise.fragment.RealtimeCurveFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;


public class RealtimeCurveActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new RealtimeCurveFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    super.onCreate(savedInstanceState);
	}
}
