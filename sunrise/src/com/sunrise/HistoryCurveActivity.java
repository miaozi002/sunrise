package com.sunrise;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;

public class HistoryCurveActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new HistoryCurveFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    super.onCreate(savedInstanceState);
	}

}
