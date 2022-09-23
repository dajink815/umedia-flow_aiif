/*
 * Copyright (C) 2018. Uangel Corp. All rights reserved.
 *
 */

/**
 * Config 설정
 *
 * @file DefaultConfig.java
 * @author Tony Lim
 */

package com.uangel.aiif.config;

import org.apache.commons.configuration2.*;
import org.apache.commons.configuration2.builder.ConfigurationBuilderEvent;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class DefaultConfig {
    static final Logger log = LoggerFactory.getLogger( DefaultConfig.class);
    private static final String DEFAULT_CONFIG = "aiif_user.config";
    private boolean loadBool = false;
    private ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration> builder;

    public DefaultConfig(String configPath) {
        log.debug("Config Path [{}] ", configPath);
    }

    protected boolean load(String configPath) {
        try {
            Parameters params = new Parameters();
            File configFile = new File(configPath + DEFAULT_CONFIG);
            builder = new ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration>(INIConfiguration.class).configure(params.fileBased().setFile(configFile));

            builder.addEventListener(ConfigurationBuilderEvent.RESET, new EventListener<Event>() {
                Configuration config1 = builder.getConfiguration();

                public void onEvent(Event event) {
                    try {
                        Configuration config2 = builder.getConfiguration();
                        ConfigurationComparator comparator = new StrictConfigurationComparator();
                        if (!comparator.compare(config1, config2))
                        {
                            DiffConfig.diff(config1, config2,
                                    (String k, Object v1, Object v2)-> log.debug("Default.config changed"));
                            config1 = config2;
                            builder.save();
                        } else
                        {
                            log.debug("Default.config not changed" );
                        }
                    } catch (ConfigurationException e)
                    {
                        log.error("DefaultConfig.load",e);
                    }
                }
            });

            PeriodicReloadingTrigger trigger = new PeriodicReloadingTrigger(builder.getReloadingController(),null, 1, TimeUnit.SECONDS);
            trigger.start();
            loadBool = true;
        }
        catch (Exception e) {
            log.error("DefaultConfig.load",e);
        }

        return loadBool;
    }

    public Configuration getConfig() {
        CompositeConfiguration config = new CompositeConfiguration();
        try {
            config.addConfiguration(builder.getConfiguration());
        } catch (ConfigurationException e) {
            log.error("DefaultConfig.getConfig",e);
        }
        return config;
    }

    public void save() throws ConfigurationException {
        builder.save();
    }

    public Configuration getWritableConfig() {
        try {
            return builder.getConfiguration();
        } catch (ConfigurationException e) {
            log.error("DefaultConfig.getWritableConfig",e);
        }
        return null;
    }


    public String getStrValue(String section, String key, String defaultValue) {
        String mkey = section + "." + key;
        String value = null;

        if (section == null) {
            return defaultValue;
        }
        try {
            value = getConfig().getString(mkey, defaultValue);
        } catch (Exception e) {
            log.error("DefaultConfig.getStrValue",e);
        }


        return value;
    }

    public int getIntValue(String section, String key, int defaultValue) {

        String mkey = section + "." + key;
        int rvalue = 0;

        if (section == null) {
            return defaultValue;
        }

        try {
            rvalue = getConfig().getInt(mkey, defaultValue);
        } catch (Exception e) {
            log.error("DefaultConfig.getIntValue",e);
        }

        return rvalue;
    }

    public int getIntValueException(String section, String key, int defaultValue, int max, int min) {
        String mkey = section + "." + key;

        int rvalue = 0;

        if (section == null) {
            return defaultValue;
        }

        try {
            rvalue = getConfig().getInt(mkey);
            if (max != 0) {
                if (rvalue > max || rvalue < min) {
                    log.error(" Config [{}:{}] is not invalid. Max Value is [{}], Min Value is [{}]",  section, key, max, min);
                    Runtime.getRuntime().exit(0);
                }
            } else {
                if (rvalue < min) {
                    log.error(" Config [{}:{}] is not invalid. Min Value is [{}]",  section, key, min);
                    Runtime.getRuntime().exit(0);
                }
            }
            return rvalue;
        } catch (Exception e) {
            log.error("DefaultConfig.getIntValueException",e);
        }

        return rvalue;

    }

    public float getFloatValue(String section, String key, float defaultValue) {

        float result;
        String mkey = section + "." + key;
        String value = null;

        if (section == null) {
            return defaultValue;
        }
        try {
            value = getConfig().getString(mkey);
            result = Float.valueOf(value);
        } catch (Exception e) {
            log.error("DefaultConfig.getFloatValue",e);
            result = defaultValue;
        }

        return result;
    }
}

