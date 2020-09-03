package fr.gaulupeau.apps.Poche.service.tasks;

import android.content.Context;

import fr.gaulupeau.apps.Poche.service.workers.OperationsWorker;

public class UpdateArticleProgressTask extends GenericFieldsTask {

    public UpdateArticleProgressTask(int articleId, double progress) {
        this.genericIntField1 = articleId;
        this.genericDoubleField1 = progress;
    }

    @Override
    public void run(Context context) {
        new OperationsWorker(context).setArticleProgress(genericIntField1, genericDoubleField1);
    }

    // Parcelable implementation

    @SuppressWarnings("unused") // needed for CREATOR
    protected UpdateArticleProgressTask() {}

    public static final TaskCreator<UpdateArticleProgressTask> CREATOR
            = new TaskCreator<>(UpdateArticleProgressTask.class);

}
