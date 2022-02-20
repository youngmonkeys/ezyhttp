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

    private final Map<String, String> featureByURI =
        new ConcurrentHashMap<>();
    private final Map<String, Set<String>> urisByFeature =
        new ConcurrentHashMap<>();
    
    public void addFeatureURI(String feature, String uri) {
        this.featureByURI.put(uri, feature);
        this.urisByFeature.computeIfAbsent(
            feature,
            k -> ConcurrentHashMap.newKeySet()
        ).add(uri);
    }
    
    public List<String> getFeatures() {
        return new ArrayList<>(urisByFeature.keySet());
    }
    
    public String getFeatureByURI(String uri) {
        return featureByURI.get(uri);
    }
    
    public List<String> getURIsByFeature(String feature) {
        return new ArrayList<>(urisByFeature.get(feature));
    }
    
    public Map<String, String> getFeatureByURIMap() {
        return new HashMap<>(featureByURI);
    }
    
    public Map<String, List<String>> getURIsByFeatureMap() {
        return EzyMaps.newHashMapNewValues(urisByFeature, ArrayList::new);
    }
    
    @Override
    public void destroy() {
        this.featureByURI.clear();
        this.urisByFeature.clear();
    }
}
