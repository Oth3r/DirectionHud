package one.oth3r.directionhud.common.hud.module;

import com.google.gson.annotations.SerializedName;

public class ModuleTime extends BaseModule {
    public static final String hour24ID = "24hr-clock";
    @SerializedName("24hr-clock")
    protected boolean hour24;

    @Override
    public String[] getSettingIDs() {
        return new String[] { hour24ID };
    }

    public ModuleTime() {
        super(Module.TIME);
        this.order = 1;
        this.state = true;
        this.hour24 = false;
    }

    public ModuleTime(int order, boolean state, boolean hour24) {
        super(Module.TIME, order, state);
        this.hour24 = hour24;
    }

    public boolean isHour24() {
        return hour24;
    }

    public void setHour24(boolean hour24) {
        this.hour24 = hour24;
    }

    @Override
    public ModuleTime clone() {
        return new ModuleTime(this.order, this.state, this.hour24);
    }
}
