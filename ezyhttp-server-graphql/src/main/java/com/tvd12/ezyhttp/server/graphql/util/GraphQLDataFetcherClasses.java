package com.tvd12.ezyhttp.server.graphql.util;

import com.tvd12.ezyfox.annotation.EzyManagement;
import com.tvd12.ezyfox.annotation.EzyPayment;
import com.tvd12.ezyhttp.server.core.annotation.Authenticated;
import com.tvd12.ezyhttp.server.core.handler.AuthenticatedController;
import com.tvd12.ezyhttp.server.core.handler.ManageableController;
import com.tvd12.ezyhttp.server.core.handler.ManagementController;
import com.tvd12.ezyhttp.server.core.handler.PaymentController;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLDataFetcher;

import static com.tvd12.ezyfox.reflect.EzyClasses.isAnnotationPresentIncludeSuper;

public class GraphQLDataFetcherClasses {

    private GraphQLDataFetcherClasses() {}

    public static boolean isAuthenticatedFetcher(
        GraphQLDataFetcher fetcher
    ) {
        if (isAnnotationPresentIncludeSuper(
            fetcher.getClass(),
            Authenticated.class)
        ) {
            return true;
        }
        if (fetcher instanceof AuthenticatedController) {
            return ((AuthenticatedController) fetcher).isAuthenticated();
        }
        return false;
    }

    public static boolean isManagementFetcher(
        GraphQLDataFetcher fetcher
    ) {
        if (isAnnotationPresentIncludeSuper(
            fetcher.getClass(),
            EzyManagement.class)
        ) {
            return true;
        }
        if (fetcher instanceof ManagementController) {
            return true;
        }
        if (fetcher instanceof ManageableController) {
            return ((ManageableController) fetcher).isManagement();
        }
        return false;
    }

    public static boolean isPaymentFetcher(
        GraphQLDataFetcher fetcher
    ) {
        if (isAnnotationPresentIncludeSuper(
            fetcher.getClass(),
            EzyPayment.class)
        ) {
            return true;
        }
        if (fetcher instanceof PaymentController) {
            return ((PaymentController) fetcher).isPayment();
        }
        return false;
    }
}
