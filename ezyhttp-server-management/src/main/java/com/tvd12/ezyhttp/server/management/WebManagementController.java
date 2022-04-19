package com.tvd12.ezyhttp.server.management;

import static com.tvd12.ezyhttp.server.management.constant.ManagementConstants.DEFAULT_FEATURE_NAME;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.tvd12.ezyfox.annotation.EzyFeature;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.annotation.Api;
import com.tvd12.ezyhttp.server.core.annotation.Authenticated;
import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.handler.ManagementController;
import com.tvd12.ezyhttp.server.core.manager.FeatureURIManager;
import com.tvd12.ezyhttp.server.core.manager.RequestHandlerManager;
import com.tvd12.ezyhttp.server.management.data.ApiInformation;

import lombok.AllArgsConstructor;

@Api
@Authenticated
@Controller
@AllArgsConstructor
public class WebManagementController implements ManagementController {

    private final FeatureURIManager futureURIManager;
    private final RequestHandlerManager requestHandlerManager;

    @EzyFeature(DEFAULT_FEATURE_NAME)
    @DoGet("/management/apis")
    public List<ApiInformation> apiListGet() {
        return requestHandlerManager.getHandlerListByURI()
            .entrySet()
            .stream()
            .map(it ->
                new ApiInformation(it.getKey(), it.getValue())
            )
            .collect(Collectors.toList());
    }

    @EzyFeature(DEFAULT_FEATURE_NAME)
    @DoGet("/management/features")
    public Map<String, Map<String, List<HttpMethod>>> featuresGet() {
        return futureURIManager.getURIsByFeatureMap();
    }

    @EzyFeature(DEFAULT_FEATURE_NAME)
    @DoGet("/management/feature-names")
    public List<String> featureNamesGet() {
        return futureURIManager.getFeatures();
    }
}
