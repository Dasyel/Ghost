package ghost.mprog.nl.ghost;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


public class DispatcherActivity extends Activity {
    private Class nextClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatcher);
        SharedPreferencesWrapper spw = new SharedPreferencesWrapper(this);

        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBarInitDB);
        pb.setVisibility(ProgressBar.INVISIBLE);

        String currentActivity = spw.getCurrentActivity();
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

        if (spw.getDbVersion() == DatabaseHandler.getDatabaseVersion()) {
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
            SharedPreferencesWrapper spw = new SharedPreferencesWrapper(this.context);
            spw.setDbVersion(DatabaseHandler.getDatabaseVersion());
            Intent intent = new Intent(this.context, this.nextClass);
            context.startActivity(intent);
        }

        @Override
        protected void onPreExecute() {
            ProgressBar pb = (ProgressBar) findViewById(R.id.progressBarInitDB);
            TextView tv = (TextView) findViewById(R.id.initTextView);
            Button btn = (Button) findViewById(R.id.initButton);

            pb.setVisibility(ProgressBar.VISIBLE);
            tv.setText(R.string.init_started_text);
            btn.setEnabled(false);
        }

        @Override
        protected void onProgressUpdate(Void... params) {}
    }
}
