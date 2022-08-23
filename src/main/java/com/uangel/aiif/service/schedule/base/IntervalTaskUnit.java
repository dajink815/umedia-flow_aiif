package com.uangel.aiif.service.schedule.base;


import com.uangel.aiif.config.AiifConfig;
import com.uangel.aiif.service.AppInstance;
import com.uangel.aiif.session.CallManager;

/**
 * @author kangmoo Heo
 */
public abstract class IntervalTaskUnit implements Runnable {
    protected int interval;
    protected IntervalTaskUnit(int interval) {
        this.interval = interval;
    }

    protected final AppInstance appInstance = AppInstance.getInstance();
    protected final CallManager callManager = CallManager.getInstance();
    protected final AiifConfig config = appInstance.getConfig();

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
