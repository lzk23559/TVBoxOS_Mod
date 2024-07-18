package com.github.tvbox.osc.util;

import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.bean.SourceBean;
import com.github.tvbox.osc.ui.activity.SearchActivity;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SearchHelper {

    public static HashMap<String, String> getSourcesForSearch() {
        HashMap<String, String> mCheckSources;
        try {
            String api = Hawk.get(HawkConfig.API_URL, "");
            if(api.isEmpty())return null;
            HashMap<String, HashMap<String, String>> mCheckSourcesForApi = Hawk.get(HawkConfig.SOURCES_FOR_SEARCH, new HashMap<>());
            mCheckSources = mCheckSourcesForApi.get(api);
        } catch (Exception e) {
            return null;
        }
        if (mCheckSources == null || mCheckSources.isEmpty()) mCheckSources = getSources();
        return mCheckSources;
    }

    public static void putCheckedSources(HashMap<String, String> mCheckSources, boolean isAll) {
        String api = Hawk.get(HawkConfig.API_URL, "");
        if (api.isEmpty()) {
            return;
        }
        HashMap<String, HashMap<String, String>> mCheckSourcesForApi = Hawk.get(HawkConfig.SOURCES_FOR_SEARCH, null);
        if (mCheckSourcesForApi == null) {
            mCheckSourcesForApi = new HashMap<>();
        }
        if (isAll) {
            mCheckSourcesForApi.remove(api);
        } else {
            HashMap<String, String> updatedSources = new HashMap<>();
            for (SourceBean bean : ApiConfig.get().getSourceBeanList()) {
                if (!bean.isSearchable()) {
                    continue;
                }
                if (mCheckSources.containsKey(bean.getKey())) {
                    updatedSources.put(bean.getKey(), "1");
                }
            }
            mCheckSourcesForApi.put(api, updatedSources);
        }
        SearchActivity.setCheckedSourcesForSearch(mCheckSources);
        Hawk.put(HawkConfig.SOURCES_FOR_SEARCH, mCheckSourcesForApi);
    }

    public static HashMap<String, String> getSources() {
        HashMap<String, String> mCheckSources = new HashMap<>();
        HashMap<String, HashMap<String, String>> mCheckSourcesForApi = Hawk.get(HawkConfig.SOURCES_FOR_SEARCH, new HashMap<>());
        for (SourceBean bean : ApiConfig.get().getSourceBeanList()) {
            if (!bean.isSearchable()) {
                continue;
            }
            if (mCheckSourcesForApi != null && mCheckSourcesForApi.containsKey(bean.getKey())) {
                mCheckSources.put(bean.getKey(), "1");
            } else {
                mCheckSources.put(bean.getKey(), "1");
            }
        }
        return mCheckSources;
    }

    public static List<String> splitWords(String text) {
        List<String> result = new ArrayList<>();
        result.add(text);
        String[] parts = text.split("\\W+");
        if (parts.length > 1) {
            result.addAll(Arrays.asList(parts));
        }
        return result;
    }

}
