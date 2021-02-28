package fr.gaulupeau.apps.Poche.service;

import android.app.Notification;

import androidx.core.app.NotificationCompat;

import fr.gaulupeau.apps.InThePoche.R;
import fr.gaulupeau.apps.Poche.ui.NotificationsHelper;

//private ActionResult reloadContent(final ActionRequest actionRequest) {
//        int articleID = actionRequest.getArticleID();
//        Log.d(TAG, String.format("reloadContent(%d) started", articleID));
//
//        ActionResult result = new ActionResult();
//        ArticlesChangedEvent event = null;
//
//        if(WallabagConnection.isNetworkAvailable()) {
//final Settings settings = getSettings();
//
//        try {
//        if (getWallabagServiceWrapper().getWallabagService().reloadArticle(articleID) == null) {
//        Log.e(TAG, "reloadContent() server unable");
//        result.setErrorType(ActionResult.ErrorType.SERVER_ERROR);
//        } else {
//        event = new ArticlesChangedEvent();
//        }
//        } catch(UnsuccessfulResponseException | IOException e) {
//        ActionResult r = processException(e, "reloadContent()");
//        result.updateWith(r);
//        } catch(Exception e) {
//        Log.e(TAG, "reloadContent() exception", e);
//
//        result.setErrorType(ActionResult.ErrorType.UNKNOWN);
//        result.setMessage(e.toString());
//        }
//        } else {
//        result.setErrorType(ActionResult.ErrorType.NO_NETWORK);
//        }
//
//        if(event != null) {
//        postEvent(event);
//        postEvent(new ReloadFinishedEvent(true));
//        } else {
//        postEvent(new ReloadFinishedEvent(false));
//        }
//
//        Log.d(TAG, "reloadContent() finished");
//        return result;
//        }

//case RELOAD_CONTENT: {
//        try {
//        result = reloadContent(actionRequest);
//        } finally {
//        if(result == null) result = new ActionResult(ActionResult.ErrorType.UNKNOWN);
//        }
//        break;
//        }

public class MainService extends TaskService {

    public MainService() {
        super(MainService.class.getSimpleName());
    }

    @Override
    protected int getForegroundNotificationId() {
        return 100;
    }

    @Override
    protected Notification getForegroundNotification() {
        NotificationsHelper.initNotificationChannels();

        return new NotificationCompat.Builder(
                this, NotificationsHelper.CHANNEL_ID_BACKGROUND_OPERATIONS)
                .setSmallIcon(R.drawable.ic_action_refresh)
                .setContentTitle(getString(R.string.notification_backgroundOperations))
                .build();
    }

}
