package com.mmnn.bonn036.zoo.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.mmnn.bonn036.zoo.R;
import com.mmnn.bonn036.zoo.utils.HanziUtils;
import com.mmnn.bonn036.zoo.view.AlphabetFastIndexer;
import com.mmnn.bonn036.zoo.view.widget.FlexibleListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NameListActivityEx extends Activity implements View.OnClickListener {
	public static final String TAG = "NameListActivityEx";

	public static final String EXTRA_FLAG_DATA = "extra_flag_data";
	public static final String EXTRA_FLAG_NAME = "extra_flag_name";

	private FlexibleListView mListView;
	private NameListAdapter mAdapter;
	private AlphabetFastIndexer mFastIndexer;
	protected String mPreviousThumb;

	private String[] mAdapterData;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		try {
			mAdapterData = intent.getStringArrayExtra(EXTRA_FLAG_DATA);
		} catch (Exception e) {
			e.printStackTrace();
		}
		initView();
	}

	private void initView() {
		setContentView(R.layout.activity_name_list);
		ActionBar actionBar = getActionBar();
		String titleString = getString(R.string.back);
		actionBar.setTitle(titleString);

		mListView = (FlexibleListView) findViewById(R.id.main_list);
		mListView.setCanLoadMore(false);
		mListView.setCanPullDown(false);

		mAdapter = new NameListAdapter(this, this);
		mListView.setAdapter(mAdapter);
		mAdapter.setData(mAdapterData);
		//
		ListView listView = mListView.getListView();
		if (listView != null) {
			mFastIndexer = (AlphabetFastIndexer) findViewById(R.id.listview_indexer);
			mFastIndexer.setVerticalPosition(true);
			mFastIndexer.attatch(listView);
		    listView.setOnScrollListener(mFastIndexer.decorateScrollListener(
					new OnScrollListener() {
						@Override
						public void onScrollStateChanged(AbsListView view, int scrollState) {
						}

						@Override
						public void onScroll(AbsListView view, int firstVisibleItem,
											 int visibleItemCount,
											 int totalItemCount) {
						/*
						 * 在列表滑动时显示当前Section的首字母
						 */
							String thumb = mAdapter.getSectionTitleForPostion(firstVisibleItem);
							if ((thumb != null) && (!TextUtils.equals(thumb, mPreviousThumb))) {
								mFastIndexer.drawThumb(thumb);
								mPreviousThumb = thumb;
							}
						}
					}));
		}
	}

	@Override
	public void onClick(View v) {
		try {
			int position = (Integer) v.getTag();
			Intent intent = new Intent();
			intent.putExtra(EXTRA_FLAG_NAME, mAdapter.getName(position));
			setResult(RESULT_OK, intent);
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class NameListAdapter extends BaseAdapter implements SectionIndexer {

		private Context mContext;
		private View.OnClickListener mOnClickListener;
		private List<NameItem> mShowList = new ArrayList<>();
		private String[] mSections;

		public NameListAdapter(Context context, View.OnClickListener onClickListener) {
			mContext = context.getApplicationContext();
			mOnClickListener = onClickListener;
		}

		public void setData(String[] nameList) {
			mShowList.clear();
			if (nameList != null) {
				for (String name : nameList) {
					NameItem item = new NameItem(name);
					item.setPhoneticize(HanziUtils.hanZiToPhoneticize(name));
					mShowList.add(item);
				}
/*			try {
				Collections.sort(mAllBrandList, new SortByLocaleKey());
			} catch (IllegalArgumentException ex) {
				ex.printStackTrace();
			}*/
			}
			notifyDataSetChanged();
		}

		public String getSectionTitleForPostion(int position) {
			NameItem item = mShowList.get(position);
//		String title = item.getDisplayName();
			String title = item.getPhoneticize();
			if (title != null && title.length() > 0) {
				try {
					return title.substring(0, 1).toUpperCase();
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
			return "";
		}

		@Override
		public int getCount() {
			return mShowList.size();
		}

		public String getName(int position) {
			if (position < 0 || position >= mShowList.size()) {
				return null;
			}
			return mShowList.get(position).getValue();
		}

		@Override
		public Object getItem(int position) {
			if (position < 0 || position >= mShowList.size()) {
				return null;
			}
			return mShowList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(mContext, R.layout.name_list_item, null);
				holder = new ViewHolder();
				holder.cnName = (TextView) convertView.findViewById(R.id.item_cnname);
				holder.content = convertView.findViewById(R.id.content_group);
				holder.content.setOnClickListener(mOnClickListener);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.content.setTag(position);

			NameItem item = mShowList.get(position);
			holder.cnName.setText(item.getValue());

			return convertView;
		}

		@Override
		public Object[] getSections() {
			if (mSections == null) {
				createSections();
			}
			return mSections;
		}

		@Override
		public int getPositionForSection(int section) {
			if (mShowList == null || mShowList.isEmpty()) {
				return -1;
			}
			if (section < 0) {
				return -1;
			}
			if (mSections == null) {
				createSections();
			}
			if (section >= mSections.length) {
				return -1;
			}
			String sectionStr = mSections[section];
			for (int i = 0; i < mShowList.size(); i++) {
				NameItem item = mShowList.get(i);
				String tmp = item.getAlpha();
				if (tmp.equals(sectionStr)) {
					return i;
				}
			}
			return -1;
		}

		@Override
		public int getSectionForPosition(int position) {
			if (mShowList == null || mShowList.isEmpty()) {
				return -1;
			}
			if (position < 0 || position >= mShowList.size()) {
				return -1;
			}
			if (mSections == null) {
				createSections();
			}
			NameItem item = mShowList.get(position);
			Character ch = null;
			if (item.getAlpha() != null) {
				ch = item.getAlpha().charAt(0);
			}
			if (ch == null || !Character.isLetter(ch)) {
				ch = '#';
			}
			return findSectionPosition(ch.toString());
		}

		private int findSectionPosition(String section) {
			for (int i = 0; i < mSections.length; i++) {
				if (TextUtils.equals(mSections[i], section)) {
					return i;
				}
			}
			return -1;
		}

		private void createSections() {
			ArrayList<String> sections = new ArrayList<>();
			Character lastSection = null;
			if (mShowList != null && !mShowList.isEmpty()) {
				for (NameItem item : mShowList) {
					if (item.getAlpha() == null) {
						continue;
					}
					Character section = item.getAlpha().charAt(0);
					if (!Character.isLetter(section)) {
						section = '#';
					}
					if (!section.equals(lastSection)) {
						lastSection = section;
						if (!sections.contains(section.toString())) {
							sections.add(section.toString());
						}
					}
				}
			}
			Object[] tmpSections = sections.toArray();
			mSections = new String[tmpSections.length];
			System.arraycopy(tmpSections, 0, mSections, 0, mSections.length);
		}

		private class NameItem {
			private String mValue;
			private String mPhoneticize;

			public NameItem(String value) {
				mValue = value;
			}

			public void setPhoneticize(String phoneticize) {
				mPhoneticize = phoneticize;
			}

			public String getPhoneticize() {
				return mPhoneticize;
			}

			public String getValue() {
				return mValue;
			}

			public String getAlpha() {
				String alpha = "#";
				if (mPhoneticize != null && !mPhoneticize.isEmpty()) {
					char alphaTempt = mPhoneticize.charAt(0);
					// ASCII码 A= 65,a = 97
					if( (alphaTempt >= 65 && alphaTempt <= 90) ||(alphaTempt >= 97 && alphaTempt <= 122)){
						alpha = String.valueOf(alphaTempt).toUpperCase(Locale.getDefault());
					} else {
						alpha = "#";
					}
				}
				return alpha;
			}
		}

		private class ViewHolder {
			View content;
			TextView cnName;
		}
	}
}
