package fr.gaulupeau.apps.Poche.service.workers;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import org.greenrobot.greendao.DaoException;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import fr.gaulupeau.apps.Poche.data.Settings;
import fr.gaulupeau.apps.Poche.data.StorageHelper;
import fr.gaulupeau.apps.Poche.data.dao.ArticleDao;
import fr.gaulupeau.apps.Poche.data.dao.entities.Article;
import fr.gaulupeau.apps.Poche.events.ArticlesChangedEvent;
import fr.gaulupeau.apps.Poche.events.DownloadFileFinishedEvent;
import fr.gaulupeau.apps.Poche.events.DownloadFileStartedEvent;
import fr.gaulupeau.apps.Poche.events.ReloadFinishedEvent;
import fr.gaulupeau.apps.Poche.network.WallabagConnection;
import fr.gaulupeau.apps.Poche.network.WallabagWebService;
import fr.gaulupeau.apps.Poche.network.exceptions.IncorrectConfigurationException;
import fr.gaulupeau.apps.Poche.service.ActionRequest;
import fr.gaulupeau.apps.Poche.service.ActionResult;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import wallabag.apiwrapper.CompatibilityHelper;
import wallabag.apiwrapper.NotFoundPolicy;
import wallabag.apiwrapper.exceptions.UnsuccessfulResponseException;

import static fr.gaulupeau.apps.Poche.events.EventHelper.postEvent;
import static fr.gaulupeau.apps.Poche.events.EventHelper.postStickyEvent;
import static fr.gaulupeau.apps.Poche.events.EventHelper.removeStickyEvent;

public class ContentReloader extends BaseNetworkWorker {

    private static final String TAG = ContentReloader.class.getSimpleName();

    public ContentReloader(Context context) {
        super(context);
    }

    public ActionResult reloadContent(ActionRequest actionRequest) {
        return doReloadContent(actionRequest);
    }

    private ActionResult doReloadContent(ActionRequest actionRequest) {
        int articleID = actionRequest.getArticleID();
        Log.d(TAG, String.format("reloadContent(%d) started", articleID));

        ActionResult result = new ActionResult();
        ArticlesChangedEvent event = null;

        if(WallabagConnection.isNetworkAvailable()) {
            final Settings settings = getSettings();

            try {
                if (getWallabagService().reloadArticle(articleID) == null) {
                    Log.e(TAG, "reloadContent() server unable");
                    result.setErrorType(ActionResult.ErrorType.SERVER_ERROR);
                } else {
                    event = new ArticlesChangedEvent();
                }
            } catch(UnsuccessfulResponseException | IOException e) {
                ActionResult r = processException(e, "reloadContent()");
                result.updateWith(r);
            } catch(Exception e) {
                Log.e(TAG, "reloadContent() exception", e);

                result.setErrorType(ActionResult.ErrorType.UNKNOWN);
                result.setMessage(e.toString());
            }
        } else {
            result.setErrorType(ActionResult.ErrorType.NO_NETWORK);
        }

        if(event != null) {
            postEvent(event);
            postEvent(new ReloadFinishedEvent(true));
        } else {
            postEvent(new ReloadFinishedEvent(false));
        }

        Log.d(TAG, "reloadContent() finished");
        return result;
    }

    protected WallabagWebService getWallabagWebService() {
        Settings settings = getSettings();
        return WallabagWebService.fromSettings(settings);
    }

}
