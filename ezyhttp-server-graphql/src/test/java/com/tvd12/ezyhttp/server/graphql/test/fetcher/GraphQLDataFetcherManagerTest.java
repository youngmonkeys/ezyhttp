package com.tvd12.ezyhttp.server.graphql.test.fetcher;

import com.tvd12.ezyfox.annotation.EzyManagement;
import com.tvd12.ezyfox.annotation.EzyPayment;
import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.server.core.annotation.Authenticated;
import com.tvd12.ezyhttp.server.core.handler.AuthenticatedController;
import com.tvd12.ezyhttp.server.core.handler.ManageableController;
import com.tvd12.ezyhttp.server.core.handler.ManagementController;
import com.tvd12.ezyhttp.server.core.handler.PaymentController;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLDataFetcher;
import com.tvd12.ezyhttp.server.graphql.fetcher.GraphQLDataFetcherManager;
import com.tvd12.ezyhttp.server.graphql.query.GraphQLQueryDefinition;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.testng.collections.Sets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GraphQLDataFetcherManagerTest {

    @Test
    public void test() {
        // given
        GraphQLDataFetcherManager instance = GraphQLDataFetcherManager.builder()
            .addDataFetcher(new Fetcher1())
            .addDataFetcher(new Fetcher11())
            .addDataFetcher(new Fetcher2())
            .addDataFetcher(new Fetcher22())
            .build();

        // when
        Map<String, List<String>> queryNameByGroupName = instance
            .getQueryNameByGroupName();

        Map<String, List<String>> sortedQueryNameByGroupName = instance
            .getSortedQueryNameByGroupName();

        // then
        Asserts.assertEquals(
            queryNameByGroupName,
            EzyMapBuilder.mapBuilder()
                .put("core1", new ArrayList<>(Sets.newHashSet("core1_fetcher1", "core1_fetcher11")))
                .put("core2", new ArrayList<>(Sets.newHashSet("core2_fetcher2", "core2_fetcher22")))
                .toMap(),
            false
        );

        Asserts.assertEquals(
            sortedQueryNameByGroupName,
            EzyMapBuilder.mapBuilder()
                .put("core1", Arrays.asList("core1_fetcher1", "core1_fetcher11"))
                .put("core2", Arrays.asList("core2_fetcher2", "core2_fetcher22"))
                .toMap(),
            false
        );

        Asserts.assertTrue(
            instance.isAuthenticatedQuery("core1_fetcher1")
        );
        Asserts.assertTrue(
            instance.isManagementQuery("core1_fetcher1")
        );
        Asserts.assertTrue(
            instance.isPaymentQuery("core1_fetcher1")
        );

        Asserts.assertTrue(
            instance.isAuthenticatedQuery("core1_fetcher11")
        );
        Asserts.assertTrue(
            instance.isManagementQuery("core1_fetcher11")
        );
        Asserts.assertTrue(
            instance.isPaymentQuery("core1_fetcher11")
        );

        Asserts.assertFalse(
            instance.isAuthenticatedQuery("core2_fetcher2")
        );
        Asserts.assertTrue(
            instance.isManagementQuery("core2_fetcher2")
        );
        Asserts.assertFalse(
            instance.isPaymentQuery("core2_fetcher2")
        );

        Asserts.assertFalse(
            instance.isAuthenticatedQuery("core2_fetcher22")
        );
        Asserts.assertFalse(
            instance.isManagementQuery("core2_fetcher22")
        );
        Asserts.assertFalse(
            instance.isPaymentQuery("core2_fetcher22")
        );
    }

    @Authenticated
    @EzyManagement
    @EzyPayment
    private static class Fetcher1 implements GraphQLDataFetcher {

        @Override
        public String getData(
            RequestArguments arguments,
            GraphQLQueryDefinition query
        ) {
            return null;
        }

        @Override
        public String getQueryName() {
            return "core1_fetcher1";
        }

        @Override
        public String getQueryGroupName() {
            return "core1";
        }
    }

    private static class Fetcher11 implements
        GraphQLDataFetcher,
        AuthenticatedController,
        ManagementController,
        PaymentController {

        @Override
        public String getData(
            RequestArguments arguments,
            GraphQLQueryDefinition query
        ) {
            return null;
        }

        @Override
        public String getQueryName() {
            return "core1_fetcher11";
        }

        @Override
        public String getQueryGroupName() {
            return "core1";
        }

        @Override
        public boolean isAuthenticated() {
            return true;
        }

        @Override
        public boolean isPayment() {
            return true;
        }
    }

    private static class Fetcher2 implements
        GraphQLDataFetcher,
        AuthenticatedController,
        ManageableController,
        PaymentController {

        @Override
        public String getData(
            RequestArguments arguments,
            GraphQLQueryDefinition query
        ) {
            return null;
        }

        @Override
        public String getQueryName() {
            return "core2_fetcher2";
        }

        @Override
        public String getQueryGroupName() {
            return "core2";
        }

        @Override
        public boolean isAuthenticated() {
            return false;
        }

        @Override
        public boolean isManagement() {
            return true;
        }

        @Override
        public boolean isPayment() {
            return false;
        }
    }

    private static class Fetcher22 implements
        GraphQLDataFetcher,
        ManageableController {

        @Override
        public String getData(
            RequestArguments arguments,
            GraphQLQueryDefinition query
        ) {
            return null;
        }

        @Override
        public String getQueryName() {
            return "core2_fetcher22";
        }

        @Override
        public String getQueryGroupName() {
            return "core2";
        }

        @Override
        public boolean isManagement() {
            return false;
        }
    }
}
