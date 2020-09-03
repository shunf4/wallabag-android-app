package fr.gaulupeau.apps.Poche.service.workers;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import fr.gaulupeau.apps.Poche.network.WallabagConnection;
import fr.gaulupeau.apps.Poche.network.exceptions.IncorrectConfigurationException;
import fr.gaulupeau.apps.Poche.service.ActionResult;
import wallabag.apiwrapper.WallabagService;
import wallabag.apiwrapper.exceptions.AuthorizationException;
import wallabag.apiwrapper.exceptions.UnsuccessfulResponseException;

public class BaseNetworkWorker extends BaseWorker {

    private static final String TAG = BaseNetworkWorker.class.getSimpleName();

    public BaseNetworkWorker(Context context) {
        super(context);
    }

    protected ActionResult processException(Exception e, String scope) {
        ActionResult result = new ActionResult();

        Log.w(TAG, String.format("%s %s", scope, e.getClass().getName()), e);

        if (e instanceof UnsuccessfulResponseException) {
            UnsuccessfulResponseException ure = (UnsuccessfulResponseException) e;
            if (ure instanceof AuthorizationException) {
                result.setErrorType(ActionResult.ErrorType.INCORRECT_CREDENTIALS);
                result.setMessage(ure.getResponseBody()); // TODO: fix message
            } else {
                result.setErrorType(ure.getResponseCode() == 500
                        ? ActionResult.ErrorType.SERVER_ERROR
                        : ActionResult.ErrorType.UNKNOWN);
                result.setMessage(e.toString());
                result.setException(e);
            }
        } else if (e instanceof IncorrectConfigurationException) {
            result.setErrorType(ActionResult.ErrorType.INCORRECT_CONFIGURATION);
            result.setMessage(e.getMessage());
        } else if (e instanceof IOException) {
            boolean handled = false;

            if (getSettings().isConfigurationOk()) {
                if (e instanceof java.net.UnknownHostException
                        || e instanceof java.net.ConnectException // TODO: maybe filter by message
                        || e instanceof java.net.SocketTimeoutException) {
                    result.setErrorType(ActionResult.ErrorType.TEMPORARY);
                    handled = true;
                } else if (e instanceof javax.net.ssl.SSLException
                        && e.getMessage() != null
                        && e.getMessage().contains("Connection timed out")) {
                    result.setErrorType(ActionResult.ErrorType.TEMPORARY);
                    handled = true;
                } else if (e instanceof java.net.SocketException
                        && e.getMessage() != null
                        && e.getMessage().contains("Software caused connection abort")) {
                    result.setErrorType(ActionResult.ErrorType.TEMPORARY);
                    handled = true;
                }
            }

            if (!handled) {
                result.setErrorType(ActionResult.ErrorType.UNKNOWN);
                result.setMessage(e.toString());
                result.setException(e);
            }
            // IOExceptions in most cases mean temporary error,
            // in some cases may mean that the action was completed anyway.
        } else if (e instanceof IllegalArgumentException && !getSettings().isConfigurationOk()) {
            result.setErrorType(ActionResult.ErrorType.INCORRECT_CONFIGURATION);
            result.setMessage(e.toString());
        } else { // other exceptions meant to be handled outside
            result.setErrorType(ActionResult.ErrorType.UNKNOWN);
            result.setMessage(e.toString());
            result.setException(e);
        }

        return result;
    }

    protected WallabagService getWallabagService()
            throws IncorrectConfigurationException {
        return WallabagConnection.getWallabagService();
    }

}
