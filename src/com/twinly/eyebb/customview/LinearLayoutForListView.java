package com.twinly.eyebb.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.twinly.eyebb.utils.DensityUtil;

/**
 * ȡ��ListView��LinearLayout��ʹ֮�ܹ��ɹ�Ƕ����ScrollView��
 * 
 * @author terry_��
 */
public class LinearLayoutForListView extends LinearLayout {

	private BaseAdapter adapter;
	private OnClickListener onClickListener = null;

	/**
	 * �󶨲���
	 */
	public void bindLinearLayout() {
		int count = adapter.getCount();
		this.removeAllViews();
		for (int i = 0; i < count; i++) {
			View v = adapter.getView(i, null, null);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(0, DensityUtil.dip2px(getContext(), 8), 0, 0);
			v.setLayoutParams(lp);
			v.setOnClickListener(this.onClickListener);
			addView(v, i);
		}
		Log.v("countTAG", "" + count);
	}

	public LinearLayoutForListView(Context context) {
		super(context);

	}

	public LinearLayoutForListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

	}

	/**
	 * ��ȡAdapter
	 * 
	 * @return adapter
	 */
	public BaseAdapter getAdpater() {
		return adapter;
	}

	/**
	 * �������
	 * 
	 * @param adpater
	 */
	public void setAdapter(BaseAdapter adpater) {
		this.adapter = adpater;
		bindLinearLayout();
	}

	/**
	 * ��ȡ����¼�
	 * 
	 * @return
	 */
	public OnClickListener getOnclickListner() {
		return onClickListener;
	}

	/**
	 * ���õ���¼�
	 * 
	 * @param onClickListener
	 */
	public void setOnclickLinstener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

}