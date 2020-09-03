package fr.gaulupeau.apps.Poche.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;

import fr.gaulupeau.apps.InThePoche.R;
import fr.gaulupeau.apps.Poche.App;
import fr.gaulupeau.apps.Poche.data.Settings;
import fr.gaulupeau.apps.Poche.service.OperationsHelper;

public class AddUrlProxyActivity extends AppCompatActivity {

    public static final String PARAM_ORIGIN_URL = "origin_url";

    private static final String TAG = AddUrlProxyActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

<<<<<<< HEAD
        final String extraText = extras.getString(Intent.EXTRA_TEXT);
        String pageUrl = "";
=======
        final String extraText = intent.getStringExtra(Intent.EXTRA_TEXT);

        String foundUrl;
>>>>>>> upstream/master

        // Parsing string for urls.
        Matcher matcher;
        matcher = Patterns.WEB_URL.matcher(extraText);
        boolean hasMatch = false;
        while (!matcher.hitEnd()) {
            if (matcher.find()) {
                hasMatch = true;
                pageUrl = matcher.group();
            }
        }
        if(extraText != null && !extraText.isEmpty()
<<<<<<< HEAD
                && hasMatch) {
            ;
=======
                && (matcher = Patterns.WEB_URL.matcher(extraText)).find()) {
            foundUrl = matcher.group();
>>>>>>> upstream/master
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.d_add_fail_title)
                    .setMessage(getString(R.string.d_add_fail_text) + extraText)
                    .setPositiveButton(R.string.ok, null)
                    .setOnDismissListener(dialog -> finish())
                    .show();
            return;
        }

        boolean showDialog = !Settings.checkFirstRunInit(this);

        if (showDialog) {
            showDialog = App.getSettings().isShowArticleAddedDialog();
        }

        String originUrl = intent.getStringExtra(PARAM_ORIGIN_URL);

        Log.d(TAG, "Bagging: " + foundUrl + ", origin: " + originUrl);

        OperationsHelper.addArticle(this, foundUrl, originUrl);

        if (showDialog) {
            Intent editActivityIntent = new Intent(this, EditAddedArticleActivity.class);
            editActivityIntent.putExtra(EditAddedArticleActivity.PARAM_ARTICLE_URL, foundUrl);

            startActivity(editActivityIntent);
        } else {
            Toast.makeText(getApplicationContext(),
                    R.string.addLink_success_text, Toast.LENGTH_SHORT).show();
        }

        finish();
    }

}
