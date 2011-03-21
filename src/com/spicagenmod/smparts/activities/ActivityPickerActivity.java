/**
 * @author AnderWeb <anderweb@gmail.com>
 *
 */
package com.spicagenmod.smparts.activities;

import com.spicagenmod.smparts.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ExpandableListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class ActivityPickerActivity extends ExpandableListActivity {
    private static int sIconWidth = -1;
    private static int sIconHeight = -1;

    private static final Paint sPaint = new Paint();
    private static final Rect sBounds = new Rect();
    private static final Rect sOldBounds = new Rect();
    private static Canvas sCanvas = new Canvas();

	private PackageManager mPackageManager;
    private final class LoadingTask extends AsyncTask<Void, Void, Void> {
    	List<PackageInfo> groups;
        @Override
        public void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }
        @Override
        public Void doInBackground(Void... params) {
            groups = mPackageManager.getInstalledPackages(0);
            Collections.sort(groups, new PackageInfoComparable());
            return null;
        }
        @Override
        public void onPostExecute(Void result) {
            setProgressBarIndeterminateVisibility(false);
            setListAdapter(new MyExpandableListAdapter(groups));
        }
    }

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_list);
        getExpandableListView().setTextFilterEnabled(true);
        mPackageManager = getPackageManager();
        // Start async loading the data
        new LoadingTask().execute();
    }
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
        ActivityInfo info = (ActivityInfo) getExpandableListAdapter().getChild(groupPosition, childPosition);
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(info.applicationInfo.packageName,
                info.name));
        Intent mReturnData = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        mReturnData.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

        // Set the name of the activity
        mReturnData.putExtra(Intent.EXTRA_SHORTCUT_NAME, info.loadLabel(mPackageManager));

		setResult(RESULT_OK,mReturnData);
		finish();
        return true;
	}
	/**
	 * ExpandableListAdapter to handle packages and activities
	 * @author adw
	 *
	 */
    public class MyExpandableListAdapter extends BaseExpandableListAdapter {
    	private final List<PackageInfo> groups;
    	private final AbsListView.LayoutParams lpGroup;
    	private final AbsListView.LayoutParams lpChild;
    	private final int leftPadding;
        public MyExpandableListAdapter(List<PackageInfo> g) {
			super();
			groups=g;
            leftPadding=getResources().getDimensionPixelSize(android.R.dimen.app_icon_size);

            lpGroup = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lpChild = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, leftPadding);
		}
        public ActivityInfo getChild(int groupPosition, int childPosition) {
        	//return groups.get(groupPosition).activities[childPosition];
        	PackageInfo tmp;
			try {
				tmp = mPackageManager.getPackageInfo(groups.get(groupPosition).packageName, PackageManager.GET_ACTIVITIES);
	        	if(tmp.activities!=null)
	        		return tmp.activities[childPosition];
	        	else
	        		return null;
			} catch (NameNotFoundException e) {
				return null;
			}

        }
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }
        public int getChildrenCount(int groupPosition) {
        	//return groups.get(groupPosition).activities.length;
        	PackageInfo tmp;
			try {
				tmp = mPackageManager.getPackageInfo(groups.get(groupPosition).packageName, PackageManager.GET_ACTIVITIES);
	        	if(tmp.activities!=null)
	        		return tmp.activities.length;
	        	else
	        		return 0;
			} catch (NameNotFoundException e) {
				return 0;
			}
        }
        public TextView getGenericView() {
            TextView textView = new TextView(ActivityPickerActivity.this);
            textView.setLayoutParams(lpGroup);
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            textView.setPadding(leftPadding, 0, 0, 0);
            return textView;
        }
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {
            TextView textView = getGenericView();
            ActivityInfo activity=getChild(groupPosition, childPosition);
            if(activity!=null){
	            String name=activity.name.replace(activity.packageName, "");
	        	textView.setText(activity.loadLabel(mPackageManager)+"("+name+")");
	        	textView.setLayoutParams(lpChild);
            }
            return textView;
        }
        public PackageInfo getGroup(int groupPosition) {
        	return groups.get(groupPosition);
        }
        public int getGroupCount() {
        	return groups.size();
        }
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                ViewGroup parent) {
            TextView textView = getGenericView();
            PackageInfo info=getGroup(groupPosition);
            textView.setText(info.applicationInfo.loadLabel(mPackageManager));
            textView.setCompoundDrawablesWithIntrinsicBounds(createIconThumbnail(info.applicationInfo.loadIcon(mPackageManager),ActivityPickerActivity.this), null, null, null);
            return textView;
        }
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
        public boolean hasStableIds() {
            return true;
        }

        public Drawable createIconThumbnail(Drawable icon, Context context) {
            if (sIconWidth == -1) {
                final Resources resources = context.getResources();
                sIconWidth = sIconHeight = (int) resources.getDimension(android.R.dimen.app_icon_size);
            }

            int width = sIconWidth;
            int height = sIconHeight;

            float scale = 1.0f;
            if (icon instanceof PaintDrawable) {
                PaintDrawable painter = (PaintDrawable) icon;
                painter.setIntrinsicWidth(width);
                painter.setIntrinsicHeight(height);
            } else if (icon instanceof BitmapDrawable) {
                // Ensure the bitmap has a density.
                BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if (bitmap.getDensity() == Bitmap.DENSITY_NONE) {
                    bitmapDrawable.setTargetDensity(context.getResources().getDisplayMetrics());
                }
            }
            int iconWidth = icon.getIntrinsicWidth();
            int iconHeight = icon.getIntrinsicHeight();

            if (width > 0 && height > 0) {
                if (width < iconWidth || height < iconHeight || scale != 1.0f) {
                    final float ratio = (float) iconWidth / iconHeight;

                    if (iconWidth > iconHeight) {
                        height = (int) (width / ratio);
                    } else if (iconHeight > iconWidth) {
                        width = (int) (height * ratio);
                    }

                    final Bitmap.Config c = icon.getOpacity() != PixelFormat.OPAQUE ?
                                Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
                    final Bitmap thumb = Bitmap.createBitmap(sIconWidth, sIconHeight, c);
                    final Canvas canvas = sCanvas;
                    canvas.setBitmap(thumb);
                    // Copy the old bounds to restore them later
                    // If we were to do oldBounds = icon.getBounds(),
                    // the call to setBounds() that follows would
                    // change the same instance and we would lose the
                    // old bounds
                    sOldBounds.set(icon.getBounds());
                    final int x = (sIconWidth - width) / 2;
                    final int y = (sIconHeight - height) / 2;
                    icon.setBounds(x, y, x + width, y + height);
                    icon.draw(canvas);
                    icon.setBounds(sOldBounds);
                    icon = new FastBitmapDrawable(thumb);
                } else if (iconWidth < width && iconHeight < height) {
                    final Bitmap.Config c = Bitmap.Config.ARGB_8888;
                    final Bitmap thumb = Bitmap.createBitmap(sIconWidth, sIconHeight, c);
                    final Canvas canvas = sCanvas;
                    canvas.setBitmap(thumb);
                    sOldBounds.set(icon.getBounds());
                    final int x = (width - iconWidth) / 2;
                    final int y = (height - iconHeight) / 2;
                    icon.setBounds(x, y, x + iconWidth, y + iconHeight);
                    icon.draw(canvas);
                    icon.setBounds(sOldBounds);
                    icon = new FastBitmapDrawable(thumb);
                }
            }

            return icon;
        }

    }
    public class PackageInfoComparable implements Comparator<PackageInfo>{
    	@Override
    	public int compare(PackageInfo o1, PackageInfo o2) {
    		return o1.applicationInfo.loadLabel(mPackageManager).toString().compareToIgnoreCase(o2.applicationInfo.loadLabel(mPackageManager).toString());
    	}
    }
    public class FastBitmapDrawable extends Drawable {
        private Bitmap mBitmap;

        FastBitmapDrawable(Bitmap b) {
            mBitmap = b;
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawBitmap(mBitmap, 0.0f, 0.0f, null);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public void setAlpha(int alpha) {
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
        }

        @Override
        public int getIntrinsicWidth() {
            return mBitmap.getWidth();
        }

        @Override
        public int getIntrinsicHeight() {
            return mBitmap.getHeight();
        }

        @Override
        public int getMinimumWidth() {
            return mBitmap.getWidth();
        }

        @Override
        public int getMinimumHeight() {
            return mBitmap.getHeight();
        }

        public Bitmap getBitmap() {
            return mBitmap;
        }
    }
}
