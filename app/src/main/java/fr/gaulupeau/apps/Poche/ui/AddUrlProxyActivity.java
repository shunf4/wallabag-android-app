package fr.gaulupeau.apps.Poche.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
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

        final String extraText = intent.getStringExtra(Intent.EXTRA_TEXT);

        ArrayList<CharSequence> foundUrls = new ArrayList<>();
        String finalUrl = "";

        // Parsing string for urls.
        Matcher matcher;
        matcher = Patterns.WEB_URL.matcher(extraText);
        boolean hasMatch = false;
        while (!matcher.hitEnd()) {
            if (matcher.find()) {
                hasMatch = true;
                foundUrls.add(matcher.group());
            }
        }
        if (extraText != null && !extraText.isEmpty()
                && hasMatch) {
            CharSequence[] choices = new CharSequence[foundUrls.size()];
            foundUrls.toArray(choices);
            final int[] index = {0};
            new AlertDialog.Builder(this)
                    .setTitle("Select one")
                    .setSingleChoiceItems(
                            choices,
                            0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    index[0] = which;
                                }
                            }
                    )
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String finalUrl = choices[index[0]].toString();
                            boolean showDialog = !Settings.checkFirstRunInit(AddUrlProxyActivity.this);

                            if (showDialog) {
                                showDialog = App.getSettings().isShowArticleAddedDialog();
                            }

                            String originUrl = intent.getStringExtra(PARAM_ORIGIN_URL);

                            Log.d(TAG, "Bagging: " + finalUrl + ", origin: " + originUrl);

                            OperationsHelper.addArticle(AddUrlProxyActivity.this, finalUrl, originUrl);

                            if (showDialog) {
                                Intent editActivityIntent = new Intent(AddUrlProxyActivity.this, EditAddedArticleActivity.class);
                                editActivityIntent.putExtra(EditAddedArticleActivity.PARAM_ARTICLE_URL, finalUrl);

                                startActivity(editActivityIntent);
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        R.string.addLink_success_text, Toast.LENGTH_SHORT).show();
                            }

                            finish();
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    })
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.d_add_fail_title)
                    .setMessage(getString(R.string.d_add_fail_text) + extraText)
                    .setPositiveButton(R.string.ok, null)
                    .setOnDismissListener(dialog -> finish())
                    .show();
            return;
        }
    }

}
