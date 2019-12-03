package fr.gaulupeau.apps.Poche.events;

public class ReloadFinishedEvent {
    protected boolean succeed;

    public ReloadFinishedEvent(boolean succeed) {
        this.succeed = succeed;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }
}
