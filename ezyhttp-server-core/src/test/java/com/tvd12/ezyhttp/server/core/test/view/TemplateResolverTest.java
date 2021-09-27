package com.tvd12.ezyhttp.server.core.test.view;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.core.view.TemplateResolver;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;

public class TemplateResolverTest {

	@Test
	public void test() {
		// given
		String prefix = RandomUtil.randomShortAlphabetString();
		String suffix = RandomUtil.randomShortAlphabetString();
		int cacheTTLMs = RandomUtil.randomInt();
		boolean cacheable = RandomUtil.randomBoolean();
		String templateMode = RandomUtil.randomShortAlphabetString();
		String messagesLocation = RandomUtil.randomShortAlphabetString();
		
		// when
		TemplateResolver sut = TemplateResolver.builder()
				.prefix(prefix)
				.suffix(suffix)
				.cacheTTLMs(cacheTTLMs)
				.cacheable(cacheable)
				.templateMode(templateMode)
				.messagesLocation(messagesLocation)
				.build();
		
		// then
		Asserts.assertEquals(prefix, sut.getPrefix());
		Asserts.assertEquals(suffix, sut.getSuffix());
		Asserts.assertEquals(cacheTTLMs, sut.getCacheTTLMs());
		Asserts.assertEquals(cacheable, sut.isCacheable());
		Asserts.assertEquals(templateMode, sut.getTemplateMode());
		Asserts.assertEquals(messagesLocation, sut.getMessagesLocation());
	}
}
