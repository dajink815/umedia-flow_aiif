package com.uangel.aiif.service.schedule.handler;

import com.uangel.aiif.service.schedule.base.IntervalTaskUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class SessionMonitor extends IntervalTaskUnit {
    private static final Logger log = LoggerFactory.getLogger(SessionMonitor.class);

    public SessionMonitor(int interval) {
        super(interval);
    }

    @Override
    public void run() {
        printSessionCount();
    }

    private void printSessionCount() {
        log.debug("CallSession count : {}", callManager.getCallInfoSize());
    }
}
