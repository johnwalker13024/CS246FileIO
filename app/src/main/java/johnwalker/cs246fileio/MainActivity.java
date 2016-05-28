package johnwalker.cs246fileio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public final static String FILENAME = "numbers.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void create(View view) {
        System.out.println("create buttton working!");
    }

    public void load(View view) {
        System.out.println("load buttton working!");
    }

    public void clear(View view) {
        System.out.println("clear buttton working!");
    }
}
