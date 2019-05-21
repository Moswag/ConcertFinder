package cytex.co.zw.concertfinder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;


import java.util.ArrayList;
import java.util.List;
public class ViewReport extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);


        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(findViewById(R.id.progress_bar));

        Pie pie = AnyChart.pie();

        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                Toast.makeText(ViewReport.this, event.getData().get("x") + ":" + event.getData().get("value"), Toast.LENGTH_SHORT).show();
            }
        });

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Num 1", 6371664));
        data.add(new ValueDataEntry("Num 2", 789622));
        data.add(new ValueDataEntry("Num 3", 7216301));
        data.add(new ValueDataEntry("Num 4", 1486621));
        data.add(new ValueDataEntry("Num 5", 1200000));

        pie.data(data);

        pie.title("Best Parties (According to likes)");

        pie.labels().position("outside");

        pie.legend().title().enabled(true);
        pie.legend().title()
                .text("Party Rankings")
                .padding(0d, 0d, 10d, 0d);


        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);

        anyChartView.setChart(pie);
    }
}
