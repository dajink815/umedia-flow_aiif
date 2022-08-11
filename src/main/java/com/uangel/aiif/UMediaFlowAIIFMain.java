package com.uangel.aiif;

import com.uangel.aiif.service.AppInstance;
import com.uangel.aiif.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class UMediaFlowAIIFMain {
    static final Logger log = LoggerFactory.getLogger(UMediaFlowAIIFMain.class);

    public static void main(String[] args) {
        log.info("Process Start [{}]", args[0]);
        AppInstance instance = AppInstance.getInstance();
        instance.setConfigPath(args[0]);

        ServiceManager serviceManager = ServiceManager.getInstance();
        serviceManager.loop();
    }
}
