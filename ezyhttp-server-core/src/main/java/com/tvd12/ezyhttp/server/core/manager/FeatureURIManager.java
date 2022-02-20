package com.tvd12.ezyhttp.server.core.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.tvd12.ezyfox.io.EzyMaps;
import com.tvd12.ezyfox.util.EzyDestroyable;

public class FeatureURIManager implements EzyDestroyable {

    private final Map<String, String> featureByUri =
        new ConcurrentHashMap<>();
    private final Map<String, Set<String>> urisByFeature =
        new ConcurrentHashMap<>();
    
    public void addFeatureURI(String feature, String uri) {
        this.featureByUri.put(uri, feature);
        this.urisByFeature.computeIfAbsent(
            feature,
            k -> ConcurrentHashMap.newKeySet()
        ).add(uri);
    }
    
    public List<String> getFeatures() {
        return new ArrayList<>(urisByFeature.keySet());
    }
    
    public String getFeatureByUri(String uri) {
        return featureByUri.get(uri);
    }
    
    public List<String> getUrisByFeature(String feature) {
        return new ArrayList<>(urisByFeature.get(feature));
    }
    
    public Map<String, String> getFeatureByUriMap() {
        return new HashMap<>(featureByUri);
    }
    
    public Map<String, List<String>> getUrisByFeatureMap() {
        return EzyMaps.newHashMapNewValues(urisByFeature, ArrayList::new);
    }
    
    @Override
    public void destroy() {
        this.featureByUri.clear();
        this.urisByFeature.clear();
    }
}
