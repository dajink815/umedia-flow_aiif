package com.uangel.aiif.service.schedule.handler;

import com.uangel.aiif.rmq.handler.RmqMsgSender;
import com.uangel.aiif.service.schedule.base.IntervalTaskUnit;

import static com.uangel.aiif.service.ServiceDefine.NORMAL;

/**
 * @author dajin kim
 */
public class HbHandler extends IntervalTaskUnit {

    public HbHandler(int interval) {
        super(interval);
    }

    @Override
    public void run() {
        RmqMsgSender.getInstance().sendIHbReq(NORMAL.getNum());
    }
}
