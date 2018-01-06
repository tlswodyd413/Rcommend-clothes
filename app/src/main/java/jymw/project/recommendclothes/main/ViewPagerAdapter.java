package jymw.project.recommendclothes.main;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import jymw.project.recommendclothes.R;

public class ViewPagerAdapter extends PagerAdapter {
    private LayoutInflater inflater;
    private ArrayList<WeatherInfoItem> itemList;

    public ViewPagerAdapter(final Context context, ArrayList<WeatherInfoItem> itemList) {
        Log.e("ViewPagerAdapter", "super()");
        this.itemList = itemList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        Log.e("ViewPagerAdapter", "isViewFromObject()");
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        Log.e("ViewPagerAdapter", "instantiateItem()");
        View view = inflater.inflate(R.layout.model_weatherinfo, null);

        WeatherInfoItem item = itemList.get(position);
        ((TextView) view.findViewById(R.id.tvThoroughfare)).setText(item.thoroughfare);
        ((TextView) view.findViewById(R.id.tvWeather)).setText(item.weather);
        ((TextView) view.findViewById(R.id.tvTempMin)).setText(item.tempMIn.concat("˚"));
        ((TextView) view.findViewById(R.id.tvTempNow)).setText(item.tempNow.concat("˚"));
        ((TextView) view.findViewById(R.id.tvTempMax)).setText(item.tempMax.concat("˚"));
        ((TextView) view.findViewById(R.id.tvEvaluation)).setText(item.evaluation);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.e("ViewPagerAdapter", "destroyItem()");
        container.removeView((View) object);
    }
}