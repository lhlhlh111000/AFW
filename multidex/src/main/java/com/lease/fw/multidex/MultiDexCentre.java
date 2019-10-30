package com.lease.fw.multidex;

public class MultiDexCentre {

    private static MultiDexCentre sInstance;

    private MultiDexPreLoadConfig config;

    private MultiDexCentre() {}

    public static MultiDexCentre getInstance() {
        if(null == sInstance) {
            synchronized (MultiDexCentre.class) {
                if(null == sInstance) {
                    sInstance = new MultiDexCentre();
                }
            }
        }

        return sInstance;
    }

    public void setPreLoadConfig(MultiDexPreLoadConfig config) {
        this.config = config;
    }

    public MultiDexPreLoadConfig getPreLoadConfig() {
        if(null == config) {
            config = new MultiDexPreLoadConfig();
            config.setLoadResId(R.layout.layout_load_default);
        }

        return config;
    }

    public static class MultiDexPreLoadConfig {

        private int loadResId;

        public int getLoadResId() {
            return loadResId;
        }

        public void setLoadResId(int loadResId) {
            this.loadResId = loadResId;
        }
    }
}
