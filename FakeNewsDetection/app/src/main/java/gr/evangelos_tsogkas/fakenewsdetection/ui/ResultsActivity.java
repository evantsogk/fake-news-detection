package gr.evangelos_tsogkas.fakenewsdetection.ui;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import app.futured.donut.DonutProgressView;
import com.squareup.picasso.Picasso;
import gr.evangelos_tsogkas.fakenewsdetection.R;
import gr.evangelos_tsogkas.fakenewsdetection.domain.Evaluation;
import gr.evangelos_tsogkas.fakenewsdetection.domain.News;
import gr.evangelos_tsogkas.fakenewsdetection.domain.NewsFromContent;
import gr.evangelos_tsogkas.fakenewsdetection.domain.NewsFromUrl;


public class ResultsActivity extends AppCompatActivity {
    private News news;

    private static final int color_fake = Color.parseColor("#B71C1C");
    private static final int color_fake_bg = Color.parseColor("#4DB71C1C");
    private static final int color_real = Color.parseColor("#689F38");
    private static final int color_real_bg = Color.parseColor("#4D689F38");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        }

        news = (News) getIntent().getSerializableExtra("news");
        initialize_donuts();

        ImageView imageView = findViewById(R.id.news_image);
        Uri imageUri = getIntent().getParcelableExtra("image_uri");
        if (news instanceof NewsFromContent && imageUri != null)
            Picasso.get().load(imageUri).into(imageView);
        else if (news instanceof NewsFromUrl && ((NewsFromUrl) news).getNews_image()!=null)
            Picasso.get().load(((NewsFromUrl) news).getNews_image()).into(imageView);
        imageView.setVisibility(View.VISIBLE);

        TextView textView = findViewById(R.id.news_text);
        textView.setText(news.getText());
        textView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void initialize_donuts() {
        Evaluation evaluation = news.getEvaluation();
        int label_politifact = evaluation.getLabel_politifact();
        float score_politifact = evaluation.percentage(evaluation.getScore_politifact());
        int label_gossipcop = evaluation.getLabel_gossipcop();
        float score_gossipcop = evaluation.percentage(evaluation.getScore_gossipcop());


        // evaluation score views
        DonutProgressView donut_politifact = findViewById(R.id.donut_politifact);
        DonutProgressView donut_gossipcop = findViewById(R.id.donut_gossipcop);
        TextView txt_politifact_label = findViewById(R.id.politifact_label);
        TextView txt_gossipcop_label = findViewById(R.id.gossipcop_label);
        TextView txt_politifact_score = findViewById(R.id.politifact_score);
        TextView txt_gossipcop_score = findViewById(R.id.gossipcop_score);

        if (label_politifact == 0) {
            donut_politifact.setBgLineColor(color_fake_bg);
            donut_politifact.addAmount("", score_politifact, color_fake);
            txt_politifact_label.setText(R.string.fake);
            txt_politifact_label.setTextColor(color_fake);
        }
        else {
            donut_politifact.setBgLineColor(color_real_bg);
            donut_politifact.addAmount("", score_politifact, color_real);
            txt_politifact_label.setText(R.string.real);
            txt_politifact_label.setTextColor(color_real);
        }

        if (label_gossipcop == 0) {
            donut_gossipcop.setBgLineColor(color_fake_bg);
            donut_gossipcop.addAmount("", score_gossipcop , color_fake);
            txt_gossipcop_label.setText(R.string.fake);
            txt_gossipcop_label.setTextColor(color_fake);
        }
        else {
            donut_gossipcop.setBgLineColor(color_real_bg);
            donut_gossipcop.addAmount("", score_gossipcop , color_real);
            txt_gossipcop_label.setText(R.string.real);
            txt_gossipcop_label.setTextColor(color_real);
        }
        String politifact_percentage = Integer.toString((int) score_politifact) + '%';
        String gossipcop_percentage = Integer.toString((int) score_gossipcop) + '%';
        txt_politifact_score.setText(politifact_percentage);
        txt_gossipcop_score.setText(gossipcop_percentage);
    }
}
