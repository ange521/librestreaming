package me.lake.librestreaming.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.lake.librestreaming.filter.hardvideofilter.BaseHardVideoFilter;
import me.lake.librestreaming.filter.hardvideofilter.HardVideoGroupFilter;
import me.lake.librestreaming.filter.hardvideofilter.OriginalHardVideoFilter;
import me.lake.librestreaming.model.RESConfig;
import me.lake.librestreaming.sample.hardfilter.DifferenceBlendFilterHard;
import me.lake.librestreaming.sample.hardfilter.SkinBlurHardVideoFilter;
import me.lake.librestreaming.sample.hardfilter.FishEyeFilterHard;
import me.lake.librestreaming.sample.hardfilter.SobelEdgeDetectionHardVideoFilter;
import me.lake.librestreaming.sample.hardfilter.TowInputFilterHard;
import me.lake.librestreaming.sample.hardfilter.WhiteningHardVideoFilter;
import me.lake.librestreaming.tools.GLESTools;

/**
 * Created by lake on 16-5-31.
 */
public class HardStreamingActivity extends BaseStreamingActivity {
    protected FilterAdapter filterAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        filtermode = RESConfig.FilterMode.HARD;
        super.onCreate(savedInstanceState);
        /**
         * filters just for demo
         */
        ArrayList<FilterItem> filterItems = new ArrayList<>();
        filterItems.add(new FilterItem("NoFilter", null));
        filterItems.add(new FilterItem("DoNothing", new OriginalHardVideoFilter(null, null)));
        filterItems.add(new FilterItem("FishEye", new FishEyeFilterHard()));
        filterItems.add(new FilterItem("SkinBlur", new SkinBlurHardVideoFilter(GLESTools.readTextFile(getResources(), R.raw.skinblurgl))));
        filterItems.add(new FilterItem("Whitening", new WhiteningHardVideoFilter()));
        LinkedList<BaseHardVideoFilter> filters = new LinkedList<>();
        filters.add(new SkinBlurHardVideoFilter(GLESTools.readTextFile(getResources(), R.raw.skinblurgl)));
        filters.add(new WhiteningHardVideoFilter());
        filterItems.add(new FilterItem("FacialUp", new HardVideoGroupFilter(filters)));
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        filterItems.add(new FilterItem("NormalBlend", new TowInputFilterHard(null, null, bitmap)));
        filters = new LinkedList<>();
        filters.add(new SobelEdgeDetectionHardVideoFilter());
        filters.add(new FishEyeFilterHard());
        filterItems.add(new FilterItem("GroupFilter", new HardVideoGroupFilter(filters)));
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        filterItems.add(new FilterItem("DifferenceBlend", new DifferenceBlendFilterHard(bitmap)));
        filterItems.add(new FilterItem("SobelEdgeDetection", new SobelEdgeDetectionHardVideoFilter()));
        filterAdapter = new FilterAdapter();
        filterAdapter.updateFilters(filterItems);
        lv_filter.setAdapter(filterAdapter);
        lv_filter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (filterAdapter.selectItem(position)) {
                    //changeFilter
                    BaseHardVideoFilter filter = ((FilterItem) filterAdapter.getItem(position)).filter;
                    resClient.setHardVideoFilter(filter);
                }
            }
        });
        sb_attr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                BaseHardVideoFilter filter = resClient.acquireHardVideoFilter();
                resClient.releaseHardVideoFilter();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb_zoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                resClient.setZoomByPercent(progress / 100.0f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    class FilterItem {
        String name;
        BaseHardVideoFilter filter;

        public FilterItem(String name, BaseHardVideoFilter filter) {
            this.name = name;
            this.filter = filter;
        }
    }

    class FilterAdapter extends BaseAdapter {
        private List<FilterItem> filters;
        private int selectIndex = 0;

        FilterAdapter() {
            filters = new ArrayList<>(0);
        }

        public boolean selectItem(int index) {
            if (selectIndex == index) {
                return false;
            }
            selectIndex = index;
            notifyDataSetChanged();
            return true;
        }

        public void updateFilters(List<FilterItem> filters) {
            this.filters = filters == null ? new ArrayList<FilterItem>(0) : filters;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return filters.size();
        }

        @Override
        public Object getItem(int position) {
            return filters.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(HardStreamingActivity.this).inflate(R.layout.item_filter, parent, false);
                convertView.setTag(new ViewHolder(convertView));
            }
            if (selectIndex == position) {
                ((ViewHolder) convertView.getTag()).iv_star.setImageResource(android.R.drawable.btn_star_big_on);
            } else {
                ((ViewHolder) convertView.getTag()).iv_star.setImageResource(android.R.drawable.btn_star_big_off);
            }

            ((ViewHolder) convertView.getTag()).tv_name.setText(filters.get(position).name);
            return convertView;
        }

        class ViewHolder {
            TextView tv_name;
            ImageView iv_star;

            public ViewHolder(View v) {
                this.tv_name = (TextView) v.findViewById(R.id.tv_name);
                this.iv_star = (ImageView) v.findViewById(R.id.iv_star);
            }
        }
    }
}
