package johnwalker.cs246fileio;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final static String FILENAME = "numbers.txt";
    private File file;
    private ListView listView;
    private ProgressBar progressBar;
    private Button createButton;
    private Button loadButton;
    private Button clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView     = (ListView)    findViewById(R.id.listView);
        createButton = (Button)      findViewById(R.id.buttonCreate);
        loadButton   = (Button)      findViewById(R.id.buttonLoad);
        clearButton  = (Button)      findViewById(R.id.buttonClear);
        progressBar  = (ProgressBar) findViewById(R.id.progressBar);

        // these buttons won't be accessible until the user has tapped the Create button and the
        //   process finishes
        loadButton.setEnabled(false);
        clearButton.setEnabled(false);
    }

    public void create(View view) {
        new FileCreatorTask().execute();
    }

    public void load(View view) {
        new FileLoaderTask().execute(file);
    }

    public void clear(View view) {
        listView.setAdapter(null);
    }

    private void disableButtons() {
        createButton.setEnabled(false);
        loadButton.setEnabled(false);
        clearButton.setEnabled(false);
    }

    private void enableButtons() {
        createButton.setEnabled(true);
        loadButton.setEnabled(true);
        clearButton.setEnabled(true);
    }

    // Sets the current percentage of progress for the progress bar
    private void setProgressPercent(Integer progress) {
        progressBar.setProgress(progress);
    }

    private void resetProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.setProgress(0);
    }

    private class FileCreatorTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                FileOutputStream fos = new FileOutputStream(file);
                for (int i = 1; i <= 10; i++) {
                    fos.write(Integer.toString(i).getBytes());
                    fos.write("\n".getBytes());
                    publishProgress(i);
                    Thread.sleep(250);
                }
                fos.close();
            } catch (FileNotFoundException ex) {
                System.out.println("File not found...");
                ex.printStackTrace();
            } catch (IOException ex) {
                System.out.println("FileIO Exception...");
                ex.printStackTrace();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            disableButtons();
            file = new File(getFilesDir(), FILENAME);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            resetProgressBar();
            enableButtons();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            setProgressPercent(values[0] * 10);
        }
    }

    private class FileLoaderTask extends AsyncTask<File, Integer, Void> {
        private List<String> contents;
        private int numLinesInFile;
        public List<String> getFileContents() { return contents; }

        @Override
        protected Void doInBackground(File... params) {
            contents = new ArrayList<String>();
            try {
                String buffer;
                BufferedReader br = new BufferedReader(new FileReader(params[0]));
                int lineNumber = 0;
                while ((buffer = br.readLine()) != null) {
                    contents.add(buffer);
                    publishProgress(++lineNumber);
                    Thread.sleep(250);
                }
            } catch (IOException ex) {
                System.out.println("IOException...");
                ex.printStackTrace();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            disableButtons();
            numLinesInFile = getNumberLines(file);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            resetProgressBar();

            if (contents != null) {
                if (!contents.isEmpty()) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            MainActivity.this,
                            android.R.layout.simple_list_item_1,
                            contents
                    );
                    listView.setAdapter(adapter);
                }
            } else {
                System.out.println("null list");
            }

            enableButtons();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            setProgressPercent((int)(((float)values[0] / numLinesInFile) * 100));
        }

        private int getNumberLines(File file) {
            LineNumberReader reader = null;
            try {
                reader = new LineNumberReader(new FileReader(file));
                while ((reader.readLine()) != null); // empty body intended
                return reader.getLineNumber();
            } catch (IOException ex) {
                ex.printStackTrace();
                return -1;
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}
