package com.lease.fw.ui.config;

public class StatusBarConfig {

    private boolean isLightStatus;

    private StatusBarConfig(Builder builder) {
        setLightStatus(builder.isLightStatus);
    }

    public boolean isLightStatus() {
        return isLightStatus;
    }

    public void setLightStatus(boolean lightStatus) {
        isLightStatus = lightStatus;
    }


    public static final class Builder {
        private boolean isLightStatus;

        public Builder() {
        }

        public Builder isLightStatus(boolean val) {
            isLightStatus = val;
            return this;
        }

        public StatusBarConfig build() {
            return new StatusBarConfig(this);
        }
    }
}
