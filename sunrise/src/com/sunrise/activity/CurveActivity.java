package com.sunrise.activity;


import com.sunrise.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CurveActivity extends Activity {

	private TextView mRealTextView;
	private TextView mHistoryTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_curve);

		mRealTextView = (TextView) findViewById(R.id.realtime_curve);
		mRealTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(CurveActivity.this, RealtimeCurveActivity.class);
				startActivity(i);
			}
		});

		mHistoryTextView = (TextView) findViewById(R.id.history_curve);
		mHistoryTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(CurveActivity.this, HistoryCurveActivity.class);
				startActivity(i);
			}
		});
	}

}
