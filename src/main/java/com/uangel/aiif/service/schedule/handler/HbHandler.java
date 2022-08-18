package com.uangel.aiif.service.schedule.handler;

import com.uangel.aiif.rmq.handler.RmqMsgSender;
import com.uangel.aiif.service.schedule.base.IntervalTaskUnit;

/**
 * @author dajin kim
 */
public class HbHandler extends IntervalTaskUnit {

    public HbHandler(int interval) {
        super(interval);
    }

    @Override
    public void run() {
        // todo Status
        RmqMsgSender.getInstance().sendIHbReq(1);
    }
}
