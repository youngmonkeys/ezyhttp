package com.tvd12.ezyhttp.server.core.test.request;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.IntSupplier;

public class HeaderMapBenchmark {

    private static final int WARMUP_ROUNDS = 5;
    private static final int MEASURE_ROUNDS = 5;
    private static final int ITERATIONS = 2_000_000;

    private static final String[][] HEADERS = {
        {"Host", "localhost:8080"},
        {"Connection", "keep-alive"},
        {"Cache-Control", "max-age=0"},
        {"Sec-Ch-Ua", "\"Chromium\";v=\"128\", \"Not;A=Brand\";v=\"24\""},
        {"Sec-Ch-Ua-Mobile", "?0"},
        {"Sec-Ch-Ua-Platform", "\"macOS\""},
        {"Upgrade-Insecure-Requests", "1"},
        {"User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36"},
        {"Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"},
        {"Sec-Fetch-Site", "none"},
        {"Sec-Fetch-Mode", "navigate"},
        {"Sec-Fetch-User", "?1"},
        {"Sec-Fetch-Dest", "document"},
        {"Accept-Encoding", "gzip, deflate, br, zstd"},
        {"Accept-Language", "vi-VN,vi;q=0.9,en-US;q=0.8,en;q=0.7"},
        {"Cookie", "session=abc123; theme=dark"},
        {"Content-Type", "application/json"},
        {"Content-Length", "128"},
        {"Authorization", "Bearer xyz"},
        {"X-Request-Id", "4f9c2b1e-8a7d-4c3b-9e2f-1a2b3c4d5e6f"}
    };

    private static final String[] NAMES = new String[HEADERS.length];
    private static final String[] VALUES = new String[HEADERS.length];

    private static final String[] LOOKUP_EXACT_NAMES = {
        "Content-Type", "Authorization", "X-Request-Id", "Accept-Language"
    };

    private static final String[] LOOKUP_OTHER_CASE_NAMES = {
        "content-type", "AUTHORIZATION", "x-request-id", "accept-language"
    };

    private static volatile int sink;

    static {
        for (int i = 0; i < HEADERS.length; ++i) {
            NAMES[i] = HEADERS[i][0];
            VALUES[i] = HEADERS[i][1];
        }
    }

    public static void main(String[] args) {
        Map<String, String> hashMap = buildHashMap();
        Map<String, String> lowerCaseHashMap = buildLowerCaseHashMap();
        Map<String, String> treeMap = buildTreeMap();

        run("build HashMap", () -> buildHashMap().size());
        run("build HashMap (lowercase key)", () -> buildLowerCaseHashMap().size());
        run("build TreeMap (case insensitive)", () -> buildTreeMap().size());

        run("get HashMap exact x4", () -> getAll(hashMap, LOOKUP_EXACT_NAMES));
        run("get HashMap fallback exact x4", () ->
            getAllWithFallback(lowerCaseHashMap, LOOKUP_EXACT_NAMES)
        );
        run("get HashMap fallback other case x4", () ->
            getAllWithFallback(lowerCaseHashMap, LOOKUP_OTHER_CASE_NAMES)
        );
        run("get TreeMap exact x4", () -> getAll(treeMap, LOOKUP_EXACT_NAMES));
        run("get TreeMap other case x4", () -> getAll(treeMap, LOOKUP_OTHER_CASE_NAMES));

        run("build + get HashMap exact x4", () ->
            getAll(buildHashMap(), LOOKUP_EXACT_NAMES)
        );
        run("build + get HashMap fallback x4", () ->
            getAllWithFallback(buildLowerCaseHashMap(), LOOKUP_OTHER_CASE_NAMES)
        );
        run("build + get TreeMap x4", () ->
            getAll(buildTreeMap(), LOOKUP_OTHER_CASE_NAMES)
        );
    }

    private static Map<String, String> buildHashMap() {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < NAMES.length; ++i) {
            map.put(NAMES[i], VALUES[i]);
        }
        return map;
    }

    private static Map<String, String> buildLowerCaseHashMap() {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < NAMES.length; ++i) {
            map.put(NAMES[i].toLowerCase(Locale.ROOT), VALUES[i]);
        }
        return map;
    }

    private static Map<String, String> buildTreeMap() {
        Map<String, String> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < NAMES.length; ++i) {
            map.put(NAMES[i], VALUES[i]);
        }
        return map;
    }

    private static int getAll(Map<String, String> map, String[] names) {
        int result = 0;
        for (String name : names) {
            String value = map.get(name);
            result += value != null ? value.length() : 0;
        }
        return result;
    }

    private static int getAllWithFallback(Map<String, String> map, String[] names) {
        int result = 0;
        for (String name : names) {
            String value = map.get(name);
            if (value == null) {
                value = map.get(name.toLowerCase(Locale.ROOT));
            }
            result += value != null ? value.length() : 0;
        }
        return result;
    }

    private static void run(String name, IntSupplier task) {
        for (int round = 0; round < WARMUP_ROUNDS; ++round) {
            measure(task);
        }
        double bestNsPerOp = Double.MAX_VALUE;
        double totalNsPerOp = 0;
        double bytesPerOp = 0;
        for (int round = 0; round < MEASURE_ROUNDS; ++round) {
            long allocatedBefore = allocatedBytes();
            double nsPerOp = measure(task);
            bytesPerOp = (double) (allocatedBytes() - allocatedBefore) / ITERATIONS;
            bestNsPerOp = Math.min(bestNsPerOp, nsPerOp);
            totalNsPerOp += nsPerOp;
        }
        System.out.printf(
            "%-40s avg: %8.2f ns/op  best: %8.2f ns/op  alloc: %8.1f B/op%n",
            name,
            totalNsPerOp / MEASURE_ROUNDS,
            bestNsPerOp,
            bytesPerOp
        );
    }

    private static double measure(IntSupplier task) {
        int result = 0;
        long start = System.nanoTime();
        for (int i = 0; i < ITERATIONS; ++i) {
            result += task.getAsInt();
        }
        long elapsed = System.nanoTime() - start;
        sink += result;
        return (double) elapsed / ITERATIONS;
    }

    private static long allocatedBytes() {
        java.lang.management.ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        if (bean instanceof com.sun.management.ThreadMXBean) {
            return ((com.sun.management.ThreadMXBean) bean)
                .getThreadAllocatedBytes(Thread.currentThread().getId());
        }
        return 0;
    }
}
