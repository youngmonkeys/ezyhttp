package com.tvd12.ezyhttp.server.graphql.test.fetcher;

import com.tvd12.ezyfox.annotation.EzyManagement;
import com.tvd12.ezyfox.annotation.EzyPayment;
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
import java.util.concurrent.atomic.AtomicInteger;

import static com.tvd12.ezyhttp.server.graphql.constants.GraphQLConstants.DEFAULT_QL_GROUP_NAME;

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

        // then
        assertFetcherManager(instance);
    }

    @Test
    public void addDataFetcherTest() {
        // given
        Fetcher1 fetcher1 = new Fetcher1();
        Fetcher11 fetcher11 = new Fetcher11();
        Fetcher2 fetcher2 = new Fetcher2();
        Fetcher22 fetcher22 = new Fetcher22();
        NoGroupFetcher noGroupFetcher = new NoGroupFetcher();
        GraphQLDataFetcherManager instance = GraphQLDataFetcherManager
            .builder()
            .build();

        // when
        instance.addDataFetcher(fetcher1.getQueryName(), fetcher1);
        instance.addDataFetcher(fetcher11.getQueryName(), fetcher11);
        instance.addDataFetcher(fetcher2.getQueryName(), fetcher2);
        instance.addDataFetcher(fetcher22.getQueryName(), fetcher22);
        instance.addDataFetcher(noGroupFetcher.getQueryName(), noGroupFetcher);

        // then
        assertFetcherManager(instance);
        Asserts.assertEquals(
            instance.getDataFetcher(fetcher1.getQueryName()),
            fetcher1
        );
        Asserts.assertEquals(
            instance.getDataFetcher(noGroupFetcher.getQueryName()),
            noGroupFetcher
        );
        Asserts.assertEquals(
            instance.getGroupNameByQueryName(noGroupFetcher.getQueryName()),
            DEFAULT_QL_GROUP_NAME
        );
    }

    @Test
    public void getDataFetcherTest() {
        // given
        Fetcher1 fetcher1 = new Fetcher1();
        Fetcher2 fetcher2 = new Fetcher2();
        GraphQLDataFetcherManager instance = GraphQLDataFetcherManager
            .builder()
            .addDataFetcher(fetcher1)
            .addDataFetcher(fetcher2)
            .build();

        // when
        GraphQLDataFetcher actualFetcher1 = instance.getDataFetcher(
            fetcher1.getQueryName()
        );
        GraphQLDataFetcher actualFetcher2 = instance.getDataFetcher(
            fetcher2.getQueryName()
        );
        GraphQLDataFetcher unknownFetcher = instance.getDataFetcher(
            "unknown"
        );

        // then
        Asserts.assertEquals(actualFetcher1, fetcher1);
        Asserts.assertEquals(actualFetcher2, fetcher2);
        Asserts.assertNull(unknownFetcher);
    }

    @Test
    public void getDataFetcherWithProviderTest() {
        // given
        Fetcher1 fetcher1 = new Fetcher1();
        Fetcher2 fetcher2 = new Fetcher2();
        AtomicInteger provideCount = new AtomicInteger();
        GraphQLDataFetcherManager instance = GraphQLDataFetcherManager
            .builder()
            .addDataFetcher(fetcher1)
            .dataFetcherProvider(queryName -> {
                provideCount.incrementAndGet();
                return fetcher2.getQueryName().equals(queryName)
                    ? fetcher2
                    : null;
            })
            .build();

        // when
        GraphQLDataFetcher existingFetcher = instance.getDataFetcher(
            fetcher1.getQueryName()
        );
        int provideCountAfterExistingFetcher = provideCount.get();
        GraphQLDataFetcher providedFetcher = instance.getDataFetcher(
            fetcher2.getQueryName()
        );
        GraphQLDataFetcher unknownFetcher = instance.getDataFetcher(
            "unknown"
        );

        // then
        Asserts.assertEquals(existingFetcher, fetcher1);
        Asserts.assertEquals(provideCountAfterExistingFetcher, 0);
        Asserts.assertEquals(providedFetcher, fetcher2);
        Asserts.assertNull(unknownFetcher);
        Asserts.assertEquals(provideCount.get(), 2);
    }

    private void assertFetcherManager(GraphQLDataFetcherManager instance) {
        Map<String, List<String>> queryNameByGroupName = instance
            .getQueryNameByGroupName();

        Map<String, List<String>> sortedQueryNameByGroupName = instance
            .getSortedQueryNameByGroupName();

        Asserts.assertEquals(
            queryNameByGroupName.get("core1"),
            new ArrayList<>(Sets.newHashSet("core1_fetcher1", "core1_fetcher11")),
            false
        );
        Asserts.assertEquals(
            queryNameByGroupName.get("core2"),
            new ArrayList<>(Sets.newHashSet("core2_fetcher2", "core2_fetcher22")),
            false
        );

        Asserts.assertEquals(
            sortedQueryNameByGroupName.get("core1"),
            Arrays.asList("core1_fetcher1", "core1_fetcher11"),
            false
        );
        Asserts.assertEquals(
            sortedQueryNameByGroupName.get("core2"),
            Arrays.asList("core2_fetcher2", "core2_fetcher22"),
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

    private static class NoGroupFetcher implements GraphQLDataFetcher {

        @Override
        public String getData(
            RequestArguments arguments,
            GraphQLQueryDefinition query
        ) {
            return null;
        }

        @Override
        public String getQueryName() {
            return "no_group_fetcher";
        }

        @Override
        public String getQueryGroupName() {
            return "";
        }
    }
}
