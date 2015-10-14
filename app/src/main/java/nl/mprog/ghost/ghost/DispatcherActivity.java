package nl.mprog.ghost.ghost;

// Dasyel Willems (10172548)

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


public class DispatcherActivity extends Activity {
    private Class nextClass;

    private SharedPreferencesWrapper spWrapper;

    ProgressBar initProgressBar;
    TextView initTextView;
    Button initButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatcher);
        this.spWrapper = new SharedPreferencesWrapper(this);

        this.initProgressBar = (ProgressBar) findViewById(R.id.initProgressBar);
        this.initTextView = (TextView) findViewById(R.id.initTextView);
        this.initButton = (Button) findViewById(R.id.initButton);

        this.initProgressBar.setVisibility(ProgressBar.INVISIBLE);

        String currentActivity = this.spWrapper.getCurrentActivity();
        Class nextClass;
        switch (currentActivity){
            case MainMenuActivity.ACTIVITY_NAME:
                nextClass = MainMenuActivity.class;
                break;
            case GameActivity.ACTIVITY_NAME:
                nextClass = GameActivity.class;
                break;
            case WinActivity.ACTIVITY_NAME:
                nextClass = WinActivity.class;
                break;
            case ResetActivity.ACTIVITY_NAME:
                nextClass = ResetActivity.class;
                break;
            default:
                nextClass = MainMenuActivity.class;
        }
        this.nextClass = nextClass;

        if (this.spWrapper.getDbVersion() == DatabaseHandler.getDatabaseVersion()) {
            startActivity(new Intent(this, nextClass));
        }
    }

    public void startInit(View v){
        new AsyncDbInit(this, this.nextClass).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class AsyncDbInit extends AsyncTask<Void, Void, Void> {
        private Context context;
        private Class nextClass;

        public AsyncDbInit(Context context, Class nextClass){
            this.context = context;
            this.nextClass = nextClass;
        }

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHandler db = new DatabaseHandler(this.context);
            db.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            super.onPostExecute(param);
            spWrapper.setDbVersion(DatabaseHandler.getDatabaseVersion());
            Intent intent = new Intent(this.context, this.nextClass);
            this.context.startActivity(intent);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            initProgressBar.setVisibility(ProgressBar.VISIBLE);
            initTextView.setText(R.string.init_started_text);
            initButton.setEnabled(false);
        }

        @Override
        protected void onProgressUpdate(Void... params) {}
    }
}
