package com.tvd12.ezyhttp.server.graphql.test.datafetcher;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.server.graphql.GraphQLAbstractDataFetcher;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@EzySingleton
public class GraphQLMeDataFetcher
		extends GraphQLAbstractDataFetcher<GraphQLMeDataFetcher.MeRequest, GraphQLMeDataFetcher.MeResponse> {
	
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
				.address(null)
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
		private String address;
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
