package com.twinly.eyebb.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.twinly.eyebb.R;
import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.utils.SharePrefsUtils;

/**
 * @author eyebb team
 * 
 * @category AboutActivity
 * 
 *           this activity is in options activity (The eleventh layer), changing
 *           the password whit is its main function.
 */
public class AboutActivity extends Activity {
	private ImageView logo;
	private String version;
	private TextView versionTxt;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		// check logo
		logo = (ImageView) findViewById(R.id.logo_img);
		// version
		try {
			version = getVersionName();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		versionTxt = (TextView) findViewById(R.id.version);
		versionTxt.setText(version);
		setLogo();

		setTitle(getString(R.string.text_about));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(android.R.color.transparent);

	}

	private void setLogo() {
		switch (SharePrefsUtils.getLanguage(this)) {
		case Constants.LOCALE_TW:
		case Constants.LOCALE_HK:
		case Constants.LOCALE_CN:
			logo.setBackground(getResources().getDrawable(R.drawable.logo_cht));
			break;
		default:
			logo.setBackground(getResources().getDrawable(R.drawable.logo_en));
			break;
		}
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

	/**
	 * version
	 */
	private String getVersionName() throws Exception {
		// get package
		PackageManager packageManager = getPackageManager();
		// get package name
		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),
				0);
		String version = packInfo.versionName;
		return version;
	}
}
