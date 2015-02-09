package com.twinly.eyebb.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ShareActionProvider;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.ActivityConstants;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.model.ActivityInfo;
import com.twinly.eyebb.model.Notifications;
import com.twinly.eyebb.utils.SharePrefsUtils;

/**
 * @author eyebb team
 * 
 * @category WebViewActivity
 * 
 *           WebViewActivity will show some web pages that comes from the
 *           server.
 * 
 */
public class WebViewActivity extends Activity {
	private WebView webViewDetails;
	private String webViewDetailsURL;
	private String actionBarTitle;

	@SuppressLint("SetJavaScriptEnabled")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

		Bundle bundle = getIntent().getExtras();
		switch (bundle.getInt("from")) {
		case ActivityConstants.FRAGMENT_REPORT_ACTIVITY:
			ActivityInfo activityInfo = (ActivityInfo) bundle
					.getSerializable("activityInfo");
			switch (SharePrefsUtils.getLanguage(this)) {
			case Constants.LOCALE_TW:
			case Constants.LOCALE_HK:
			case Constants.LOCALE_CN:
				webViewDetailsURL = activityInfo.getUrlTc();
				actionBarTitle = activityInfo.getTitleTc();
				break;
			default:
				webViewDetailsURL = activityInfo.getUrl();
				actionBarTitle = activityInfo.getTitle();
				break;
			}
			break;
		case ActivityConstants.FRAGMENT_PROFILE:
			Notifications notification = (Notifications) bundle
					.getSerializable("notifications");
			switch (SharePrefsUtils.getLanguage(this)) {
			case Constants.LOCALE_TW:
			case Constants.LOCALE_HK:
			case Constants.LOCALE_CN:
				webViewDetailsURL = notification.getUrlTc();
				actionBarTitle = notification.getTitleTc();
				break;
			default:
				webViewDetailsURL = notification.getUrl();
				actionBarTitle = notification.getTitle();
				break;
			}
			break;
		}

		setTitle(actionBarTitle);
		webViewDetails = (WebView) findViewById(R.id.webview_show);
		// get the size of current display
		WebSettings webSettings = webViewDetails.getSettings();
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		// set the function to support zoom
		webViewDetails.getSettings().setSupportZoom(true);
		// Set Zoom tool appears
		webViewDetails.getSettings().setBuiltInZoomControls(true);
		// Expand the proportion of scaling
		// webViewDetails.getSettings().setUseWideViewPort(true);
		// Scrolling and zooming
		webViewDetails.setVerticalScrollBarEnabled(false);
		webViewDetails.setHorizontalScrollBarEnabled(false);
		webViewDetails.getSettings().setDomStorageEnabled(true);
		webViewDetails.getSettings().setJavaScriptEnabled(true);

		webViewDetails.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				setTitle(R.string.text_loading);
				setProgress(progress * 100);
				if (progress == 100)
					setTitle(actionBarTitle);
			}
		});
		webViewDetails.setWebViewClient(new WebViewClient() {

			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// Handle the error

			}

			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

		});

		webViewDetails.loadUrl(webViewDetailsURL);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.share_action_provider, menu);

		MenuItem actionItem = menu
				.findItem(R.id.menu_item_share_action_provider_action_bar);
		ShareActionProvider actionProvider = (ShareActionProvider) actionItem
				.getActionProvider();
		actionProvider
				.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
		actionProvider.setShareIntent(createShareIntent());

		return true;
	}

	/**
	 * Creates a sharing {@link Intent}.
	 * 
	 * @return The sharing intent.
	 */
	private Intent createShareIntent() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, "Welcome to "
				+ webViewDetailsURL);
		return shareIntent;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else if (item.getItemId() == 0) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
