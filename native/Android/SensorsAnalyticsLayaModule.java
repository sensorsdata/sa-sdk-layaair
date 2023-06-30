package demo;

import android.text.TextUtils;

import com.sensorsdata.analytics.android.sdk.SAConfigOptions;
import com.sensorsdata.analytics.android.sdk.SALog;
import com.sensorsdata.analytics.android.sdk.SensorsAnalyticsAutoTrackEventType;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPIEmptyImplementation;
import com.sensorsdata.analytics.android.sdk.data.persistent.PersistentLoader;
import com.sensorsdata.analytics.android.sdk.plugin.property.SAPropertyPlugin;
import com.sensorsdata.analytics.android.sdk.plugin.property.SAPropertyPluginPriority;
import com.sensorsdata.analytics.android.sdk.plugin.property.beans.SAPropertiesFetcher;
import com.sensorsdata.analytics.android.sdk.plugin.property.beans.SAPropertyFilter;
import com.sensorsdata.analytics.android.sdk.util.TimeUtils;

import java.util.Date;
import java.util.Iterator;
import java.util.Locale;


import org.json.JSONObject;

import layaair.game.browser.ExportJavaFunction;


public class SensorsAnalyticsLayaModule {

    public static void initSDK(JSONObject config) {
        if (!(SensorsDataAPI.sharedInstance() instanceof SensorsDataAPIEmptyImplementation)) {
            return;
        }

        SAConfigOptions configOptions;
        if (config != null && config.length() > 0) {
            configOptions = new SAConfigOptions(config.optString("server_url"));
            // 打开自动采集, 并指定追踪哪些 AutoTrack 事件
            JSONObject appConfig = config.optJSONObject("app");
            if (appConfig != null && appConfig.length() > 0) {
                boolean start = appConfig.optBoolean("app_start");
                boolean end = appConfig.optBoolean("app_end");
                int autotrack = 0;
                if(start)
                    autotrack = SensorsAnalyticsAutoTrackEventType.APP_START;
                if(end ){
                    autotrack = autotrack | SensorsAnalyticsAutoTrackEventType.APP_END;
                }
                configOptions.setAutoTrackEventType(autotrack);
            }
            JSONObject superProperties = config.optJSONObject("super_properties");
            if (superProperties != null && superProperties.length() > 0) {
                configOptions.registerPropertyPlugin(new SAPropertyPlugin() {
                    private JSONObject mProperties = superProperties;

                    @Override
                    public void properties(SAPropertiesFetcher fetcher) {
                        if (mProperties == null || mProperties.length() == 0) {
                            return;
                        }
                        JSONObject properties = PersistentLoader.getInstance().getSuperPropertiesPst().get();
                        try {
                            mergeSuperJSONObject(mProperties, properties);
                            PersistentLoader.getInstance().getSuperPropertiesPst().commit(properties);
                            mProperties = null;
                        } catch (Exception ignored) {
                        }
                    }

                    @Override
                    public boolean isMatchedWithFilter(SAPropertyFilter filter) {
                        return filter.getType().isTrack();
                    }

                    @Override
                    public SAPropertyPluginPriority priority() {
                        return SAPropertyPluginPriority.LOW;
                    }
                });
            }
            configOptions.enableLog(config.optBoolean("show_log"));
        } else {
            configOptions = new SAConfigOptions("");
        }
        SensorsDataAPI.startWithConfigOptions(JSBridge.mMainActivity, configOptions);
    }

    public static void track(String eventName, JSONObject properties) {
        try {
            SensorsDataAPI.sharedInstance().track(eventName, properties);
        } catch (Exception ignored) {

        }
    }

    public static void identify(String id) {
        try {
            SensorsDataAPI.sharedInstance().identify(id);
        } catch (Exception ignored) {

        }
    }

    public static void login(String id) {
        try {
            SensorsDataAPI.sharedInstance().login(id);
        } catch (Exception ignored) {

        }
    }

    public static void logout() {
        SensorsDataAPI.sharedInstance().logout();
    }

    public static void registerApp(JSONObject properties) {
        try {
            if (properties != null && properties.length() > 0) {
                SensorsDataAPI.sharedInstance().registerSuperProperties(properties);
            }
        } catch (Exception ignored) {

        }
    }

    public static void getPresetProperties() {
        try {
            JSONObject presetProperties = SensorsDataAPI.sharedInstance().getPresetProperties();
            JSBridge.m_Handler.post(new Runnable() {
                @Override
                public void run() {
                    if (presetProperties != null) {
                        ExportJavaFunction.CallBackToJS(SensorsAnalyticsLayaModule.class, "getPresetProperties", presetProperties.toString());
                    } else {
                        ExportJavaFunction.CallBackToJS(SensorsAnalyticsLayaModule.class, "getPresetProperties", new JSONObject().toString());
                    }
                }
            });
        } catch (Exception ignored) {

        }
    }

    public static void clearAppRegister(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        try {
            SensorsDataAPI.sharedInstance().unregisterSuperProperty(key);
        } catch (Exception ignored) {

        }
    }

    public static void setProfile(JSONObject properties) {
        try {
            SensorsDataAPI.sharedInstance().profileSet(properties);
        } catch (Exception ignored) {

        }
    }

    public static void setOnceProfile(JSONObject properties) {
        try {
            SensorsDataAPI.sharedInstance().profileSetOnce(properties);
        } catch (Exception ignored) {

        }
    }

    public static void trackAppInstall(JSONObject properties) {
        try {
            SensorsDataAPI.sharedInstance().trackAppInstall(properties);
        } catch (Exception ignored) {

        }
    }

    public static void flush() {
        try {
            SensorsDataAPI.sharedInstance().flush();
        } catch (Exception ignored) {

        }
    }

    public static void deleteAll() {
        try {
            SensorsDataAPI.sharedInstance().deleteAll();
        } catch (Exception ignored) {

        }
    }

    /**
     * 合并、去重公共属性
     *
     * @param source 新加入或者优先级高的属性
     * @param dest 本地缓存或者优先级低的属性，如果有重复会删除该属性
     * @return 合并后的属性
     */
    public static JSONObject mergeSuperJSONObject(JSONObject source, JSONObject dest) {
        if (source == null) {
            source = new JSONObject();
        }
        if (dest == null) {
            return source;
        }

        try {
            Iterator<String> sourceIterator = source.keys();
            while (sourceIterator.hasNext()) {
                String key = sourceIterator.next();
                Iterator<String> destIterator = dest.keys();
                while (destIterator.hasNext()) {
                    String destKey = destIterator.next();
                    if (!TextUtils.isEmpty(key) && key.equalsIgnoreCase(destKey)) {
                        destIterator.remove();
                    }
                }
            }
            //重新遍历赋值，如果在同一次遍历中赋值会导致同一个 json 中大小写不一样的 key 被删除
            mergeJSONObject(source, dest);
        } catch (Exception ex) {
            SALog.printStackTrace(ex);
        }
        return dest;
    }

    /**
     * merge source JSONObject to dest JSONObject
     *
     * @param source
     * @param dest
     */
    public static void mergeJSONObject(final JSONObject source, JSONObject dest) {
        try {
            if (source == null) {
                return;
            }
            Iterator<String> sourceIterator = source.keys();
            while (sourceIterator.hasNext()) {
                String key = sourceIterator.next();
                Object value = source.get(key);
                if (value instanceof Date && !"$time".equals(key)) {
                    dest.put(key, TimeUtils.formatDate((Date) value, Locale.CHINA));
                } else {
                    dest.put(key, value);
                }
            }
        } catch (Exception ex) {
            SALog.printStackTrace(ex);
        }
    }
}
