package com.tvd12.ezyhttp.server.boot.test.graphql;

import java.util.Arrays;
import java.util.List;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.server.boot.test.graphql.GraphQLMeDataFetcher.MeRequest;
import com.tvd12.ezyhttp.server.boot.test.graphql.GraphQLMeDataFetcher.MeResponse;
import com.tvd12.ezyhttp.server.graphql.GraphQLAbstractDataFetcher;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@EzySingleton
public class GraphQLMeDataFetcher
    extends GraphQLAbstractDataFetcher<MeRequest, MeResponse> {

    @Override
    public MeResponse getData(MeRequest argument) {
        return MeResponse.builder()
            .id(1)
            .name("Dzung")
            .nickName("Hello")
            .friends(
                Arrays.asList(
                    Friend.builder().id(1).name("Foo").build(),
                    Friend.builder().id(1).name("Bar").build()
                )
            )
            .bank(Bank.builder().id(100).build())
            .build();
    }

    @Override
    public String getQueryName() {
        return "me";
    }

    @Data
    public static class MeRequest {
        private long id;
    }

    @Getter
    @Builder
    public static class MeResponse {
        private long id;
        private String name;
        private String nickName;
        private List<Friend> friends;
        private Bank bank;
    }

    @Getter
    @Builder
    public static class Friend {
        private long id;
        private String name;
    }

    @Data
    @Builder
    public static class Bank {
        private long id;
    }
}
