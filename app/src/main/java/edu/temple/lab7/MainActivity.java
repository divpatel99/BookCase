package edu.temple.lab7;

import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TextView relative = (TextView) findViewById(R.id.TextV);

        setContentView(R.layout.activity_main);

        Resources res = getResources();
        String[] planets = res.getStringArray(R.array.planets_array);
        planets[0] = "Book 1";
        relative.setText(planets[0]);



    }
}
