package com.example.wideking.myapplication.settings;

/**
 * Created by widek on 29.7.2015..
 */
public class SettingsEditFeed {
    private String settingsName;
    private String value;

    public SettingsEditFeed(String settingsName, String value) {
        this.settingsName = settingsName;
        this.value = value;

    }

    public String getSettingsName() {
        return settingsName;
    }

    public void setSettingsName(String settingsName) {
        this.settingsName = settingsName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
