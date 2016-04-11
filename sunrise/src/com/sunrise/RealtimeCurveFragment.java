package com.sunrise;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RealtimeCurveFragment extends Fragment {
	public static final String EXTRA_TITLE = "com.sunrise.android.realtimecurve.title";

	private static final String TAG = "Tag";

	private static CurveModel mCurveModel;
	private TextView mLoopTextView;
	private TextView mCurveTypeTextView;
	private TextView mMaxMinValueView;
	private TextView mCurvesSelectView;
	private LinearLayout mLinearLayout;
	private PopuItem[] mMostValueItem;
	private PopuJar mMostValuePopu;
	private String[] mYCRealData;

	public static final String LIST_SELECT_DIALOG = "select";
	public static final int REQUEST_LOOP = 0;
	public static final int REQUEST_CURVE_TYPE = 1;
	public static final int REQUEST_CURVES_SELECT = 2;

	public static Handler mHandler;
	public Timer timer = new Timer();
	public updateCurveTask updateCurve = new updateCurveTask();

	public XYMultipleSeriesDataset buildDataset(String[] titles,
			List<double[]> xValues, List<double[]> yValues) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		addXYSeries(dataset, titles, xValues, yValues, 0);
		return dataset;
	}

	public void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles,
			List<double[]> xValues, List<double[]> yValues, int scale) {
		if (titles == null)
			return;
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			XYSeries series = new XYSeries(titles[i], scale);
			double[] xV = xValues.get(i);
			double[] yV = yValues.get(i);
			int seriesLength = xV.length;
			series.clear();
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]);
			}
			dataset.removeSeries(series);
			dataset.addSeries(series);
		}
	}

	public void setChartSettings(XYMultipleSeriesRenderer renderer,
			String title, String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
	}

	public XYMultipleSeriesRenderer buildRenderer(int[] colors,
			PointStyle[] styles, String[] titles) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		setRenderer(renderer, colors, styles, titles);
		return renderer;
	}

	public void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors,
			PointStyle[] styles, String[] titles) {
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(30);
		renderer.setLabelsTextSize(20);
		renderer.setLegendTextSize(25);
		renderer.setPointSize(5f);
		renderer.setMargins(new int[] { 20, 50, 20, 20 });
		if (titles == null)
			return;
		int length = titles.length;
		for (int i = 0; i < colors.length; i++) {
			for (int j = 0; j < styles.length; j++) {
				if ((i * styles.length + j) == length)
					return;
				XYSeriesRenderer r = new XYSeriesRenderer();
				r.setLineWidth(3);
				r.setColor(colors[j]);
				r.setPointStyle(styles[i]);
				r.setDisplayChartValues(true);
				r.setChartValuesTextSize(24);
				r.setDisplayChartValuesDistance(80);
				renderer.addSeriesRenderer(r);
			}
		}
	}

	public static double[] getJsonToDoubleArray(JSONArray jsonArray) {
		double[] arr = new double[jsonArray.length()];
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				arr[i] = jsonArray.getDouble(i);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return arr;
	}

	public static void sendMessage(int code, Object obj) {
		Message msg = new Message();
		msg.what = code;
		msg.obj = obj;
		mHandler.sendMessage(msg);
	}

	public static void getPhpData(StringBuilder builder) {
		try {
			String path = mCurveModel.getPath();
			URL url = new URL(path);

			URLConnection Connection = url.openConnection();

			HttpURLConnection con = (HttpURLConnection) Connection;
			con.setConnectTimeout(6000);
			con.setRequestMethod("GET");
			con.connect();

			BufferedReader bufReader = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String line = "";

			while ((line = bufReader.readLine()) != null) {
				builder.append(line);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setLoopsPath() {
		Date date = new Date();
		long time = date.getTime();
		String path = "http://192.168.0.99/php_data/uiinterface.php?reqType=GetBkofSt&stid=4&time="
				+ time;
		mCurveModel.setPath(path);
	}

	public void getLoopsName() {
		new Thread() {

			@Override
			public void run() {
				setLoopsPath();
				StringBuilder builder = new StringBuilder();
				try {
					getPhpData(builder);
					JSONObject jsonObject = new JSONObject(builder.toString());
					String data = jsonObject.getString("data");
					JSONArray ja = new JSONArray(data);
					int length = ja.length();
					String[] names = new String[length];
					String[] ids = new String[length];
					for (int i = 0; i < length; i++) {
						JSONObject jsonObjectData = ja.getJSONObject(i);
						names[i] = jsonObjectData.getString("name");
						ids[i] = jsonObjectData.getString("id");
					}
					sendMessage(0, names);
					sendMessage(1, ids);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}.start();
	}

	static class updateCurveTask extends TimerTask {
		@Override
		public void run() {
			setCurveIdPath(mCurveModel.getCurveTypesAlias()[mCurveModel
					.getCurveTypesItem()]);
		}
	}

	public static void setCurveIdPath(String type) {
		if (mCurveModel.getLoopsId() == null) {
			return;
		}
		String bkId = mCurveModel.getLoopsId()[mCurveModel.getLoopItem()];
		Date date = new Date();
		long time = date.getTime();
		String sCurveIdPath = "http://192.168.0.99/php_data/uiinterface.php?reqType=GetRtBk&stid=4&bkid="
				+ bkId + "&metype=" + type + "&time=" + time;
		mCurveModel.setPath(sCurveIdPath);
		getCurveTypeIds();
	}

	public static void getCurveTypeIds() {
		new Thread() {

			@Override
			public void run() {
				StringBuilder builder = new StringBuilder();
				try {
					getPhpData(builder);
					sendMessage(2, builder);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}.start();
	}

	public void setCurvePath(String ycid) {
		Calendar ca = Calendar.getInstance();
		int year = ca.get(Calendar.YEAR);
		int month = ca.get(Calendar.MONTH) + 1;
		int day = ca.get(Calendar.DAY_OF_MONTH);
		String path = "http://192.168.0.99/php_data/uiinterface.php?reqType=GetBkSample&stid=4&year="
				+ year + "&month=" + month + "&day=" + day + "&ycid=" + ycid;
		mCurveModel.setPath(path);
	}

	public void getCurveData() {
		new Thread() {

			@Override
			public void run() {
				StringBuilder builder = new StringBuilder();
				try {
					getPhpData(builder);
					sendMessage(3, builder);
				} catch (Exception ex) {
					Log.e(TAG, "Url interactive failure");
				}
			}
		}.start();
	}

	protected void drawXYMultipleSeriesRenderer(String[] titles,
			List<double[]> values, int numberValue, double yMaxValue,
			double yMinValue) {
		List<double[]> x = new ArrayList<double[]>();
		double[] ycPoint = new double[numberValue];
		if (numberValue != 0) {
			for (int ycnum = 0; ycnum < numberValue; ycnum++) {
				ycPoint[ycnum] = ycnum;
			}

			for (int i = 0; i < titles.length; i++) {
				x.add(ycPoint);
			}
		}

		int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.CYAN,
				Color.YELLOW };
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE,
				PointStyle.DIAMOND, PointStyle.TRIANGLE, PointStyle.SQUARE };
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles,
				titles);
		int length = renderer.getSeriesRendererCount();
		for (int i = 0; i < length; i++) {
			((XYSeriesRenderer) renderer.getSeriesRendererAt(i))
					.setFillPoints(true);
		}
		int xMin = 0;
		int xMax = 72;
		if (numberValue > 72) {
			xMin = numberValue - 72;
			xMax = numberValue;
		}

		if (yMinValue == yMaxValue) {
			yMinValue = 0;
		}
		setChartSettings(renderer, "曲线显示", "", "", xMin, xMax, yMinValue,
				yMaxValue + (yMaxValue - yMinValue) * 0.1, Color.LTGRAY,
				Color.GREEN);

		renderer.setXLabels(0);
		for (int i = 0, j = 0, n = 0; i <= 48; i++) {
			n = i * 6;
			switch (i % 2) {
			case 0:
				renderer.addXTextLabel(n, j + ":" + "00");
				break;
			case 1:
				renderer.addXTextLabel(n, j + ":" + "30");
				j++;
				break;
			default:
				break;
			}
		}
		renderer.setYLabels(10);
		renderer.setShowGrid(true);
		renderer.setXLabelsAlign(Paint.Align.RIGHT);
		renderer.setYLabelsAlign(Paint.Align.RIGHT);
		renderer.setZoomButtonsVisible(true);
		renderer.setPanLimits(new double[] { -2, 290,
				yMinValue - (yMaxValue - yMinValue) * 0.1,
				yMaxValue + (yMaxValue - yMinValue) * 0.1 });
		renderer.setZoomLimits(new double[] { -2, 290,
				yMinValue - (yMaxValue - yMinValue) * 0.1,
				yMaxValue + (yMaxValue - yMinValue) * 0.1 });
		XYMultipleSeriesDataset dataset = buildDataset(titles, x, values);
		View view = ChartFactory.getLineChartView(getActivity(), dataset,
				renderer);
		view.setBackgroundColor(Color.BLACK);
		mLinearLayout.removeAllViews();
		mLinearLayout.addView(view);
	}

	@SuppressLint({ "HandlerLeak", "InflateParams" })
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_realtime_curve, container,
				false);
		mCurveModel = new CurveModel();
		mCurveModel.setSelect(false);

		mLoopTextView = (TextView) v.findViewById(R.id.realtime_curve_loop);
		mLoopTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mCurveModel.setTitle("回路");
				FragmentManager fm = getActivity().getSupportFragmentManager();
				SingleChoiceListDialogFragment dialog = SingleChoiceListDialogFragment
						.newInstance(mCurveModel);
				dialog.setTargetFragment(RealtimeCurveFragment.this,
						REQUEST_LOOP);
				dialog.show(fm, LIST_SELECT_DIALOG);
			}
		});

		mCurveTypeTextView = (TextView) v
				.findViewById(R.id.realtime_curve_type);
		mCurveTypeTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				mCurveModel.setTitle("曲线类型");
				SingleChoiceListDialogFragment dialog = SingleChoiceListDialogFragment
						.newInstance(mCurveModel);
				dialog.setTargetFragment(RealtimeCurveFragment.this,
						REQUEST_CURVE_TYPE);
				dialog.show(fm, LIST_SELECT_DIALOG);
			}
		});

		mMaxMinValueView = (TextView) v
				.findViewById(R.id.realtime_max_min_value_display);
		mMaxMinValueView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mMostValuePopu == null)
					return;
				mMostValuePopu.show(mMaxMinValueView);

			}
		});

		mCurvesSelectView = (TextView) v
				.findViewById(R.id.realtime_select_curves);
		mCurvesSelectView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				MultiChoiceListDialogFragment dialog = MultiChoiceListDialogFragment
						.newInstance(mCurveModel);
				dialog.setTargetFragment(RealtimeCurveFragment.this,
						REQUEST_CURVES_SELECT);
				dialog.show(fm, LIST_SELECT_DIALOG);
			}
		});

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					mCurveModel.setLoops((String[]) msg.obj);
					String loopName = mCurveModel.getLoops()[mCurveModel
							.getLoopItem()];
					mLoopTextView.setText("回路  " + "(" + loopName + ")");
					String curveTypeName = mCurveModel.getCurveTypes()[mCurveModel
							.getCurveTypesItem()];
					mCurveTypeTextView.setText("曲线类型  " + "(" + curveTypeName
							+ ")");
					break;
				case 1:
					mCurveModel.setLoopsId((String[]) msg.obj);
					if (mLinearLayout.getContext() != null) {
						mCurveModel.setCurveTypesItem(0);
						timer.schedule(updateCurve, 0, 60000);
					}
					break;
				case 2:
					try {
						JSONArray jaCt = new JSONArray(msg.obj.toString());
						int CtNumber = jaCt.length();
						String[] ycId = new String[CtNumber];
						mYCRealData = new String[CtNumber];
						String ycIdStr = new String();
						for (int i = 0; i < CtNumber; i++) {
							JSONObject jsonObjectData = jaCt.getJSONObject(i);
							ycId[i] = jsonObjectData.getString("id");
							if (i < CtNumber - 1) {
								ycIdStr = ycIdStr + ycId[i] + ",";
							} else {
								ycIdStr = ycIdStr + ycId[i];
							}
							mYCRealData[i] = jsonObjectData.getString("val");
						}

						setCurvePath(ycIdStr);
						getCurveData();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case 3:
					try {
						JSONArray jaYc = new JSONArray(msg.obj.toString());
						int numberValue = 0;
						int n = 0;
						int nMax = 0;
						int nMin = 0;
						boolean isTag = false;
						int ycNumber = jaYc.length();
						mMostValueItem = new PopuItem[ycNumber];
						String[] ycNames = new String[ycNumber];
						JSONArray[] ycDatas = new JSONArray[ycNumber];

						JSONObject jsonObjectData0 = jaYc.getJSONObject(0);
						ycNames[0] = jsonObjectData0.getString("ycname");
						ycDatas[0] = jsonObjectData0.getJSONArray("data");
						double[] data0 = getJsonToDoubleArray(ycDatas[0]);
						Calendar ca = Calendar.getInstance();
						int hour = ca.get(Calendar.HOUR_OF_DAY);
						int minute = ca.get(Calendar.MINUTE);
						numberValue = (hour * 60 + minute) / 5 + 1;
						double maxValue = data0[0];
						double minValue = data0[0];

						if (mCurveModel.isSelect()) {
							int curveNum = 0;
							for (int i = 0; i < ycNumber; i++) {
								if (Arrays.binarySearch(
										mCurveModel.getSelectCurves(), i) >= 0) {
									curveNum++;
								}
							}
							mMostValueItem = new PopuItem[curveNum];
							ycNames = new String[curveNum];
							ycDatas = new JSONArray[curveNum];
							jsonObjectData0 = jaYc.getJSONObject(mCurveModel
									.getSelectCurves()[0]);
							ycNames[0] = jsonObjectData0.getString("ycname");
							ycDatas[0] = jsonObjectData0.getJSONArray("data");
							data0 = getJsonToDoubleArray(ycDatas[0]);
							maxValue = data0[0];
							minValue = data0[0];
						}

						List<double[]> values = new ArrayList<double[]>();
						for (int i = 0, ycNamesNum = 0; i < ycNumber; i++) {
							isTag = false;
							if (mCurveModel.isSelect()) {
								if (Arrays.binarySearch(
										mCurveModel.getSelectCurves(), i) < 0) {
									isTag = true;
								}
							}

							if (isTag)
								continue;

							JSONObject jsonObjectData = jaYc.getJSONObject(i);
							ycNames[ycNamesNum] = jsonObjectData
									.getString("ycname");
							ycDatas[ycNamesNum] = jsonObjectData
									.getJSONArray("data");
							double[] data = getJsonToDoubleArray(ycDatas[ycNamesNum]);
							values.add(data);
							double displayMaxValue = data[0];
							double displayMinValue = data[0];
							for (int j = 0; j < numberValue; j++) {
								if (data[j] > displayMaxValue) {
									displayMaxValue = data[j];
									nMax = j;
								}
								if (data[j] < displayMinValue) {
									displayMinValue = data[j];
									nMin = j;
								}
							}

							for (int j = 0; j < numberValue; j++) {
								if (data[j] > maxValue) {
									maxValue = data[j];
								}
								if (data[j] < minValue) {
									minValue = data[j];
								}
							}
							mMostValueItem[n++] = new PopuItem(n,
									ycNames[ycNamesNum] + "\n    实时值："
											+ mYCRealData[i] + "\n    最大值："
											+ "（时间：" + nMax / 12 + ":"
											+ (nMax % 12) * 5 / 10 + ""
											+ ((nMax % 12) * 5) % 10 + "  值："
											+ displayMaxValue + "）"
											+ "\n    最小值：" + "（时间：" + nMin / 12
											+ ":" + (nMin % 12) * 5 / 10 + ""
											+ ((nMin % 12) * 5) % 10 + "  值："
											+ displayMinValue + "）", null);
							ycNamesNum++;
						}

						mMostValuePopu = new PopuJar(getActivity(),
								PopuJar.VERTICAL);
						for (int i = 0; i < n; i++) {
							mMostValuePopu.addPopuItem(mMostValueItem[i]);
						}
						if (!mCurveModel.isSelect()) {
							mCurveModel.setCurveNames(ycNames);
						}
						drawXYMultipleSeriesRenderer(ycNames, values,
								numberValue, maxValue, minValue);
					} catch (Exception e) {
						drawXYMultipleSeriesRenderer(null, null, 0, 0, 0);
						e.printStackTrace();
					}
					break;

				default:
					break;
				}
			}
		};

		getLoopsName();

		mLinearLayout = (LinearLayout) v
				.findViewById(R.id.realtime_curve_chart);

		return v;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		updateCurve.cancel();
		timer.cancel();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
		switch (requestCode) {
		case REQUEST_LOOP:
			mCurveModel = (CurveModel) data
					.getSerializableExtra(SingleChoiceListDialogFragment.EXTRA_CURVE_MODEL);
			mCurveModel.setSelect(false);
			String loopName = mCurveModel.getLoops()[mCurveModel.getLoopItem()];
			mLoopTextView.setText("回路  " + "(" + loopName + ")");
			setCurveIdPath(mCurveModel.getCurveTypesAlias()[mCurveModel
					.getCurveTypesItem()]);
			return;
		case REQUEST_CURVE_TYPE:
			mCurveModel = (CurveModel) data
					.getSerializableExtra(SingleChoiceListDialogFragment.EXTRA_CURVE_MODEL);
			mCurveModel.setSelect(false);
			int curveTypesItem = mCurveModel.getCurveTypesItem();
			String curveTypeName = mCurveModel.getCurveTypes()[curveTypesItem];
			mCurveTypeTextView.setText("曲线类型  " + "(" + curveTypeName + ")");
			setCurveIdPath(mCurveModel.getCurveTypesAlias()[curveTypesItem]);
			return;
		case REQUEST_CURVES_SELECT:
			mCurveModel = (CurveModel) data
					.getSerializableExtra(MultiChoiceListDialogFragment.EXTRA_CURVE_MODEL);
			mCurveModel.setSelect(true);
			setCurveIdPath(mCurveModel.getCurveTypesAlias()[mCurveModel
					.getCurveTypesItem()]);
			return;
		default:
			return;
		}
	}

}
