package gr.evangelos_tsogkas.fakenewsdetection.ui;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import java.util.ArrayList;

import gr.evangelos_tsogkas.fakenewsdetection.R;


public class ResearchActivity extends AppCompatActivity {
    public static final float GROUP_SPACE = 0.13f;
    public static final float BAR_SPACE = 0.02f;
    public static final float BAR_WIDTH = 0.27f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_research);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        }

        initializePolitifactBarchart();
        initializeGossipCopBarchart();

    }

    public void initializePolitifactBarchart() {
        BarChart barchart_politifact = findViewById(R.id.chart_politifact);
        barchart_politifact.getDescription().setEnabled(false);
        barchart_politifact.setPinchZoom(false);
        barchart_politifact.setDrawBarShadow(false);
        barchart_politifact.setDrawGridBackground(false);
        barchart_politifact.getAxisLeft().setTextColor(ContextCompat.getColor(this, R.color.text));
        barchart_politifact.getXAxis().setTextColor(ContextCompat.getColor(this, R.color.text));
        barchart_politifact.getLegend().setTextColor(ContextCompat.getColor(this, R.color.text));
        barchart_politifact.animateY(1500);

        Legend l = barchart_politifact.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
        l.setXEntrySpace(15f);
        l.setTextSize(11f);

        XAxis xAxis = barchart_politifact.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Accuracy","Fake News F1","Real News F1"}));

        YAxis leftAxis = barchart_politifact.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0.5f);
        leftAxis.setAxisMaximum(0.91f);
        leftAxis.setLabelCount(9);
        barchart_politifact.getAxisRight().setEnabled(false);


        // fill data
        ArrayList<BarEntry> values1 = new ArrayList<>();
        ArrayList<BarEntry> values2 = new ArrayList<>();
        ArrayList<BarEntry> values3 = new ArrayList<>();
        values1.add(new BarEntry(0, 0.814f));
        values2.add(new BarEntry(0, 0.804f));
        values3.add(new BarEntry(0, 0.853f));
        values1.add(new BarEntry(1, 0.796f));
        values2.add(new BarEntry(1, 0.787f));
        values3.add(new BarEntry(1, 0.831f));
        values1.add(new BarEntry(2, 0.829f));
        values2.add(new BarEntry(2, 0.818f));
        values3.add(new BarEntry(2, 0.870f));

        BarDataSet set1, set2, set3;
        set1 = new BarDataSet(values1, "Text");
        set1.setColor(Color.parseColor("#5975A4"));
        set1.setDrawValues(false);
        set2 = new BarDataSet(values2, "Vis");
        set2.setColor(Color.parseColor("#CC8963"));
        set2.setDrawValues(false);
        set3 = new BarDataSet(values3, "Text+Vis");
        set3.setColor(Color.parseColor("#5C9E6E"));
        set3.setDrawValues(false);

        BarData data = new BarData(set1, set2, set3);
        data.setValueFormatter(new LargeValueFormatter());
        barchart_politifact.setData(data);
        barchart_politifact.getBarData().setBarWidth(BAR_WIDTH);
        barchart_politifact.getXAxis().setAxisMinimum(0);
        barchart_politifact.getXAxis().setAxisMaximum(0 + barchart_politifact.getBarData().getGroupWidth(GROUP_SPACE, BAR_SPACE) * 3);
        barchart_politifact.groupBars(0, GROUP_SPACE, BAR_SPACE);
        barchart_politifact.getData().setHighlightEnabled(!barchart_politifact.getData().isHighlightEnabled());
        barchart_politifact.invalidate();
    }

    public void initializeGossipCopBarchart() {
        BarChart barchart_gossipcop = findViewById(R.id.chart_gossipcop);
        barchart_gossipcop.getDescription().setEnabled(false);
        barchart_gossipcop.setPinchZoom(false);
        barchart_gossipcop.setDrawBarShadow(false);
        barchart_gossipcop.setDrawGridBackground(false);
        barchart_gossipcop.getAxisLeft().setTextColor(ContextCompat.getColor(this, R.color.text));
        barchart_gossipcop.getXAxis().setTextColor(ContextCompat.getColor(this, R.color.text));
        barchart_gossipcop.getLegend().setTextColor(ContextCompat.getColor(this, R.color.text));
        barchart_gossipcop.animateY(1500);

        Legend l = barchart_gossipcop.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
        l.setXEntrySpace(15f);
        l.setTextSize(11f);

        XAxis xAxis = barchart_gossipcop.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Accuracy","Fake News F1","Real News F1"}));

        YAxis leftAxis = barchart_gossipcop.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0.5f);
        leftAxis.setAxisMaximum(0.91f);
        leftAxis.setLabelCount(9);
        barchart_gossipcop.getAxisRight().setEnabled(false);


        // fill data
        ArrayList<BarEntry> values1 = new ArrayList<>();
        ArrayList<BarEntry> values2 = new ArrayList<>();
        ArrayList<BarEntry> values3 = new ArrayList<>();
        values1.add(new BarEntry(0, 0.769f));
        values2.add(new BarEntry(0, 0.633f));
        values3.add(new BarEntry(0, 0.781f));
        values1.add(new BarEntry(1, 0.759f));
        values2.add(new BarEntry(1, 0.638f));
        values3.add(new BarEntry(1, 0.769f));
        values1.add(new BarEntry(2, 0.778f));
        values2.add(new BarEntry(2, 0.629f));
        values3.add(new BarEntry(2, 0.791f));

        BarDataSet set1, set2, set3;
        set1 = new BarDataSet(values1, "Text");
        set1.setColor(Color.parseColor("#5975A4"));
        set1.setDrawValues(false);
        set2 = new BarDataSet(values2, "Vis");
        set2.setColor(Color.parseColor("#CC8963"));
        set2.setDrawValues(false);
        set3 = new BarDataSet(values3, "Text+Vis");
        set3.setColor(Color.parseColor("#5C9E6E"));
        set3.setDrawValues(false);

        BarData data = new BarData(set1, set2, set3);
        data.setValueFormatter(new LargeValueFormatter());
        barchart_gossipcop.setData(data);
        barchart_gossipcop.getBarData().setBarWidth(BAR_WIDTH);
        barchart_gossipcop.getXAxis().setAxisMinimum(0);
        barchart_gossipcop.getXAxis().setAxisMaximum(0 + barchart_gossipcop.getBarData().getGroupWidth(GROUP_SPACE, BAR_SPACE) * 3);
        barchart_gossipcop.groupBars(0, GROUP_SPACE, BAR_SPACE);
        barchart_gossipcop.getData().setHighlightEnabled(!barchart_gossipcop.getData().isHighlightEnabled());
        barchart_gossipcop.invalidate();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
