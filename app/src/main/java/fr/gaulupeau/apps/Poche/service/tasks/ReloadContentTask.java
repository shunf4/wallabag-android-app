package fr.gaulupeau.apps.Poche.service.tasks;

import android.content.Context;

import fr.gaulupeau.apps.Poche.service.ActionRequest;
import fr.gaulupeau.apps.Poche.service.ActionResult;
import fr.gaulupeau.apps.Poche.service.workers.ContentReloader;

public class ReloadContentTask extends ActionRequestTask {

    public ReloadContentTask(ActionRequest actionRequest) {
        super(actionRequest);
    }

    @Override
    protected ActionResult run(Context context, ActionRequest actionRequest) {
        return new ContentReloader(context).reloadContent(actionRequest);
    }

    // Parcelable implementation

    @SuppressWarnings("unused") // needed for CREATOR
    protected ReloadContentTask() {}

    public static final TaskCreator<ReloadContentTask> CREATOR
            = new TaskCreator<>(ReloadContentTask.class);

}
