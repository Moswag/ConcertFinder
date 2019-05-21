package cytex.co.zw.concertfinder.fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


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
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

import cytex.co.zw.concertfinder.R;
import cytex.co.zw.concertfinder.adapter.ViewConcertsAdapter;
import cytex.co.zw.concertfinder.models.Comment;
import cytex.co.zw.concertfinder.models.Concert;
import cytex.co.zw.concertfinder.models.Like;
import cytex.co.zw.concertfinder.models.Report;
import cytex.co.zw.concertfinder.utils.Constants;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportFragment extends Fragment {
    // Creating DatabaseReference.

    List<Report> listReports;
    private final static String TAG="ReportFragment";


    public ReportFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ReportFragment(List<Report> listReports) {
        this.listReports=listReports;
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_report, container, false);
        getActivity().setTitle("View Report");




        FirebaseApp.initializeApp(getActivity());

        AnyChartView anyChartView = view.findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(view.findViewById(R.id.progress_bar));





        Pie pie = AnyChart.pie();

        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                Toast.makeText(getActivity(), event.getData().get("x") + ":" + event.getData().get("value"), Toast.LENGTH_SHORT).show();
            }
        });

        List<DataEntry> data = new ArrayList<>();
        //listReports.add(new Report("Wagwan",3));
        Log.d(TAG,listReports.toString());


        for(Report report:listReports){
            data.add(new ValueDataEntry(report.getConcert(), report.getLikes()));
        }

        pie.data(data);

        pie.title("Best Parties (According to likes)");

        pie.labels().position("outside");

        pie.legend().title().enabled(true);
        pie.legend().title()
                .text("Party Rankings")
                .padding(0d, 0d, 10d, 0d);


        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.VERTICAL)
                .align(Align.CENTER);

        anyChartView.setChart(pie);


        return view;
    }
}
