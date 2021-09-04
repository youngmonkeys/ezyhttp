package com.tvd12.ezyhttp.core.boot.test.graphql;

import java.util.Arrays;
import java.util.List;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.core.boot.test.graphql.GraphQLMeDataFetcher.MeRequest;
import com.tvd12.ezyhttp.core.boot.test.graphql.GraphQLMeDataFetcher.MeResponse;
import com.tvd12.ezyhttp.server.graphql.GraphQLAbstractDataFetcher;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@EzySingleton
public class GraphQLMeDataFetcher 
		extends GraphQLAbstractDataFetcher<MeRequest, MeResponse> {

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
				.build();
	}
	
	@Override
	public String getOperationName() {
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
	}
	
	@Getter
	@Builder
	public static class Friend {
		private long id;
		private String name;
	}
}
