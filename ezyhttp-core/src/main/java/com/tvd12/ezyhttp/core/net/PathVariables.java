package com.tvd12.ezyhttp.core.net;

import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyfox.util.EzyEntry;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public final class PathVariables {

    private PathVariables() {}

    public static List<Entry<String, String>> getVariables(
        String template,
        String uri
    ) {
        String[] templatePaths = template.split("/");
        String[] uriPaths = uri.split("/");
        List<Entry<String, String>> answer = new ArrayList<>();
        for (int i = 0; i < templatePaths.length; ++i) {
            String templatePath = templatePaths[i];
            if (isPathVariable(templatePath)) {
                String varName = getVariableName(templatePath);
                String varValue = decodeUriPathValue(uriPaths[i]);
                answer.add(EzyEntry.of(varName, varValue));
            } else if (templatePath.equals("*")) {
                StringBuilder varValue = new StringBuilder();
                int lastIndex = uriPaths.length - 1;
                for (; i < uriPaths.length; ++i) {
                    varValue.append(decodeUriPathValue(uriPaths[i]));
                    if (i < lastIndex) {
                        varValue.append("/");
                    }
                }
                answer.add(EzyEntry.of("*", varValue.toString()));
                break;
            }
        }
        return answer;
    }

    private static String decodeUriPathValue(String value) {
        try {
            return URLDecoder.decode(
                value,
                EzyStrings.UTF_8
            );
        } catch (Exception e) {
            return value;
        }
    }

    public static String getVariableName(String path) {
        return path.substring(1, path.length() - 1);
    }

    public static boolean isPathVariable(String path) {
        return path.startsWith("{") && path.endsWith("}");
    }
}
