package com.tvd12.ezyhttp.server.thymeleaf.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Locale;
import java.util.Properties;

import com.tvd12.ezyfox.util.EzyMapBuilder;
import org.testng.annotations.Test;
import org.thymeleaf.context.ITemplateContext;

import com.tvd12.ezyhttp.server.core.view.AbsentMessageResolver;
import com.tvd12.ezyhttp.server.core.view.MessageProvider;
import com.tvd12.ezyhttp.server.thymeleaf.ThymeleafMessageResolver;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.MethodUtil;

public class ThymeleafMessageResolverTest {

    @Test
    public void resolveMessageEn() {
        // given
        MessageProvider messageProvider = mock(MessageProvider.class);
        Properties exmessages = new Properties();
        exmessages.setProperty("ex.hello", "Ex Hello");
        when(
            messageProvider.provide()
        ).thenReturn(Collections.singletonMap("en", exmessages));

        ThymeleafMessageResolver sut = ThymeleafMessageResolver.builder()
            .messageLocation("messages")
            .messageProviders(Collections.singletonList(messageProvider))
            .build();
        ITemplateContext context = mock(ITemplateContext.class);
        when(context.getLocale()).thenReturn(new Locale("en", "US"));
        Class<?> origin = getClass();

        // when
        String welcomeMessage = sut.resolveMessage(context, origin, "home.welcome", new Object[0]);
        String greetTitle = sut.resolveMessage(context, origin, "greet.title", new Object[]{"Monkey"});
        String greetMessage = sut.resolveMessage(context, origin, "greet.message", new Object[]{"Monkey"});
        String unkown = sut.resolveMessage(context, origin, "unknown", new Object[0]);
        String foo = sut.resolveMessage(context, origin, "foo", new Object[0]);

        // then
        Asserts.assertEquals("DEFAULT", sut.getName());
        Asserts.assertEquals(0, sut.getOrder());
        Asserts.assertEquals("Welcome to EzyHTTP en_US", welcomeMessage);
        Asserts.assertEquals("Greet Monkey", greetTitle);
        Asserts.assertEquals("Great Monkey!", greetMessage, false);
        Asserts.assertNull(unkown);
        Asserts.assertEquals("Bar", foo);
    }

    @Test
    public void resolveMessageDefault() {
        // given
        MessageProvider messageProvider = mock(MessageProvider.class);
        Properties exmessages = new Properties();
        exmessages.setProperty("ex.hello", "Ex Hello");
        when(
            messageProvider.provide()
        ).thenReturn(Collections.singletonMap("en", exmessages));

        ThymeleafMessageResolver sut = ThymeleafMessageResolver.builder()
            .messageLocation("messages")
            .messageProviders(Collections.singletonList(messageProvider))
            .build();
        ITemplateContext context = mock(ITemplateContext.class);
        when(context.getLocale()).thenReturn(new Locale("unkown"));
        Class<?> origin = getClass();

        // when
        String welcomeMessage = sut.resolveMessage(context, origin, "home.welcome", new Object[0]);
        String greetTitle = sut.resolveMessage(context, origin, "greet.title", new Object[]{"Monkey"});
        String greetMessage = sut.resolveMessage(context, origin, "greet.message", null);
        String unkown = sut.resolveMessage(context, origin, "unknown", new Object[0]);
        String foo = sut.resolveMessage(context, origin, "foo", new Object[0]);

        // then
        Asserts.assertEquals("Welcome to EzyHTTP", welcomeMessage);
        Asserts.assertEquals("Greet Monkey", greetTitle);
        Asserts.assertEquals("Great {0}!", greetMessage, false);
        Asserts.assertNull(unkown);
        Asserts.assertEquals("Bar", foo);
    }

    @Test
    public void putI18nMessagesTest() {
        // given
        MessageProvider messageProvider = mock(MessageProvider.class);
        Properties exmessages = new Properties();
        exmessages.setProperty("ex.hello", "Ex Hello");
        when(
            messageProvider.provide()
        ).thenReturn(Collections.singletonMap("en", exmessages));

        ThymeleafMessageResolver sut = ThymeleafMessageResolver.builder()
            .messageLocation("messages")
            .messageProviders(Collections.singletonList(messageProvider))
            .build();
        sut.putI18nMessages(
            EzyMapBuilder.mapBuilder()
                .put(
                    "",
                    EzyMapBuilder.mapBuilder()
                        .put("new_key_1", "a")
                        .toMap()
                )
                .put(
                    "vi",
                    EzyMapBuilder.mapBuilder()
                        .put("new_key_2", "b")
                        .toMap()
                )
                .put(
                    "en",
                    EzyMapBuilder.mapBuilder()
                        .put("new_key_3", "c")
                        .toMap()
                )
                .put(
                    "en_US",
                    EzyMapBuilder.mapBuilder()
                        .put("new_key_3", "c")
                        .put("home.welcome", "new welcome")
                        .toMap()
                )
                .put(
                    "kr_KR",
                    EzyMapBuilder.mapBuilder()
                        .put("new_key_4", "d")
                        .toMap()
                )
                .toMap()
        );
        ITemplateContext context = mock(ITemplateContext.class);
        when(context.getLocale()).thenReturn(new Locale("en", "US"));
        Class<?> origin = getClass();

        // when
        String welcomeMessage = sut.resolveMessage(context, origin, "home.welcome", new Object[0]);
        String greetTitle = sut.resolveMessage(context, origin, "greet.title", new Object[]{"Monkey"});
        String greetMessage = sut.resolveMessage(context, origin, "greet.message", new Object[]{"Monkey"});
        String unknown = sut.resolveMessage(context, origin, "unknown", new Object[0]);
        String foo = sut.resolveMessage(context, origin, "foo", new Object[0]);
        String newKey1 = sut.resolveMessage(context, origin, "new_key_1", new Object[0]);
        String newKey2 = sut.resolveMessage(context, origin, "new_key_2", new Object[0]);
        String newKey3 = sut.resolveMessage(context, origin, "new_key_3", new Object[0]);

        // then
        Asserts.assertEquals("DEFAULT", sut.getName());
        Asserts.assertEquals(0, sut.getOrder());
        Asserts.assertEquals("new welcome", welcomeMessage);
        Asserts.assertEquals("Greet Monkey", greetTitle);
        Asserts.assertEquals("Great Monkey!", greetMessage, false);
        Asserts.assertNull(unknown);
        Asserts.assertEquals("Bar", foo);
        Asserts.assertEquals(newKey1, "a");
        Asserts.assertEquals(newKey2, null);
        Asserts.assertEquals(newKey3, "c");
    }

    @Test
    public void createAbsentMessageRepresentation() {
        // given
        MessageProvider messageProvider = mock(MessageProvider.class);
        Properties exmessages = new Properties();
        exmessages.setProperty("ex.hello", "Ex Hello");
        when(
            messageProvider.provide()
        ).thenReturn(Collections.singletonMap("en", exmessages));

        ThymeleafMessageResolver sut = ThymeleafMessageResolver.builder()
            .messageLocation("messages")
            .messageProviders(Collections.singletonList(messageProvider))
            .build();
        ITemplateContext context = mock(ITemplateContext.class);
        when(context.getLocale()).thenReturn(new Locale("unkown"));
        Class<?> origin = getClass();

        // when
        String welcomeMessage = sut.createAbsentMessageRepresentation(context, origin, "home.welcome", new Object[0]);

        // then
        Asserts.assertEquals("home.welcome", welcomeMessage);
    }

    @Test
    public void createAbsentMessageRepresentationOK() {
        // given
        MessageProvider messageProvider = mock(MessageProvider.class);
        Properties exmessages = new Properties();
        exmessages.setProperty("ex.hello", "Ex Hello");
        when(
            messageProvider.provide()
        ).thenReturn(Collections.singletonMap("en", exmessages));

        ThymeleafMessageResolver sut = ThymeleafMessageResolver.builder()
            .messageLocation("unknow")
            .messageProviders(Collections.singletonList(messageProvider))
            .build();
        ITemplateContext context = mock(ITemplateContext.class);
        when(context.getLocale()).thenReturn(new Locale("unkown"));
        Class<?> origin = getClass();

        // when
        String welcomeMessage = sut.createAbsentMessageRepresentation(context, origin, "home.welcome", new Object[0]);

        // then
        Asserts.assertEquals("home.welcome", welcomeMessage);
    }

    @Test
    public void createAbsentMessageRepresentationWithResolver() {
        // given
        MessageProvider messageProvider = mock(MessageProvider.class);
        Properties exmessages = new Properties();
        exmessages.setProperty("ex.hello", "Ex Hello");
        when(
            messageProvider.provide()
        ).thenReturn(Collections.singletonMap("EN", exmessages));

        ThymeleafMessageResolver sut = ThymeleafMessageResolver.builder()
            .messageLocation("unknow")
            .messageProviders(Collections.singletonList(messageProvider))
            .absentMessageResolver(new TestAbsentMessageResolver())
            .build();
        ITemplateContext context = mock(ITemplateContext.class);
        when(context.getLocale()).thenReturn(new Locale("unkown"));
        Class<?> origin = getClass();

        // when
        String helloMessage = sut.createAbsentMessageRepresentation(context, origin, "hello", new Object[0]);
        String welcomeMessage = sut.createAbsentMessageRepresentation(context, origin, "home.welcome", new Object[0]);

        // then
        Asserts.assertEquals("Hello", helloMessage);
        Asserts.assertEquals("home.welcome", welcomeMessage);
    }

    @Test
    public void isFormatCandidateTest() {
        // given
        String message = "hello '0'";

        // when
        boolean actual = MethodUtil.invokeStaticMethod("isFormatCandidate", ThymeleafMessageResolver.class, message);

        // then
        Asserts.assertTrue(actual);
    }

    private static class TestAbsentMessageResolver implements AbsentMessageResolver {

        @Override
        public String resolve(Locale locale, Class<?> origin, String key, Object[] parameters) {
            if (key.equals("hello")) {
                return "Hello";
            }
            return null;
        }
    }
}
