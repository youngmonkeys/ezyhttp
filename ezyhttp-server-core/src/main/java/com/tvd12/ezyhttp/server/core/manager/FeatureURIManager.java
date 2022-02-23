package com.tvd12.ezyhttp.server.core.manager;

import static com.tvd12.ezyfox.io.EzyMaps.newHashMapNewValues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.tvd12.ezyfox.util.EzyDestroyable;
import com.tvd12.ezyhttp.core.constant.HttpMethod;

public class FeatureURIManager implements EzyDestroyable {

    private final Map<String, Map<HttpMethod, String>> featureByURI =
        new ConcurrentHashMap<>();
    private final Map<String, Map<String, Set<HttpMethod>>> urisByFeature =
        new ConcurrentHashMap<>();
    
    public void addFeatureURI(String feature, HttpMethod method, String uri) {
        this.featureByURI
            .computeIfAbsent(uri, k -> new ConcurrentHashMap<>())
            .put(method, feature);
        this.urisByFeature
            .computeIfAbsent(feature, k -> new ConcurrentHashMap<>())
            .computeIfAbsent(uri, k -> ConcurrentHashMap.newKeySet())
            .add(method);
    }
    
    public List<String> getFeatures() {
        return new ArrayList<>(urisByFeature.keySet());
    }
    
    public String getFeatureByURI(HttpMethod method, String uri) {
        Map<HttpMethod, String> featureByMethod = featureByURI.get(uri);
        return featureByMethod != null ? featureByMethod.get(method) : null;
    }
    
    public Map<String, List<HttpMethod>> getURIsByFeature(String feature) {
        return newHashMapNewValues(
            urisByFeature.getOrDefault(feature, Collections.emptyMap()),
            ArrayList::new
        );
    }
    
    public Map<String, Map<HttpMethod, String>> getFeatureByURIMap() {
        return newHashMapNewValues(featureByURI, HashMap::new);
    }
    
    public Map<String, Map<String, List<HttpMethod>>> getURIsByFeatureMap() {
        return newHashMapNewValues(urisByFeature, v ->
            newHashMapNewValues(v, ArrayList::new)
        );
    }
    
    @Override
    public void destroy() {
        this.featureByURI.clear();
        this.urisByFeature.clear();
    }
}
