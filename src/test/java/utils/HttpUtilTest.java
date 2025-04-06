package utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@Slf4j
public class HttpUtilTest {
    private MockWebServer mockWebServer;
    private String baseUrl;

    @Before
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        baseUrl = "http://" + mockWebServer.getHostName() + ":" + mockWebServer.getPort() + "/";
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void testGetSuccess() throws Exception {
        // 准备模拟响应
        String expectedResponse = "{\"message\":\"success\"}";
        mockWebServer.enqueue(new MockResponse()
                                      .setBody(expectedResponse)
                                      .setResponseCode(200));

        // 准备请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        // 执行请求
        String response = HttpUtil.get(baseUrl, headers);

        // 验证响应
        assertEquals(expectedResponse, response);

        // 验证请求
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("GET", request.getMethod());
        assertTrue(request
                           .getHeader("Content-Type")
                           .startsWith("application/json"));
    }

    @Test(expected = IOException.class)
    public void testGetFailure() throws Exception {
        // 准备模拟错误响应
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        // 执行请求
        HttpUtil.get(baseUrl, null);
    }

    @Test
    public void testPostSuccess() throws Exception {
        // 准备模拟响应
        String expectedResponse = "{\"message\":\"success\"}";
        mockWebServer.enqueue(new MockResponse()
                                      .setBody(expectedResponse)
                                      .setResponseCode(200));

        // 准备请求头和请求体
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        String requestBody = "{\"test\":\"data\"}";

        // 执行请求
        String response = HttpUtil.post(baseUrl, headers, requestBody);

        // 验证响应
        assertEquals(expectedResponse, response);

        // 验证请求
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertTrue(request
                           .getHeader("Content-Type")
                           .startsWith("application/json"));
        assertEquals(requestBody, request
                .getBody()
                .readUtf8());
    }

    @Test
    public void testJsonConversion() throws Exception {
        // 测试对象
        TestData testData = new TestData("test", 123);

        // 转换为JSON
        String json = HttpUtil.toJson(testData);
        assertNotNull(json);
        assertTrue(json.contains("test"));
        assertTrue(json.contains("123"));

        // 从JSON转换回对象
        TestData converted = HttpUtil.fromJson(json, TestData.class);
        assertEquals(testData.getName(), converted.getName());
        assertEquals(testData.getValue(), converted.getValue());
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    private static class TestData {
        private String name;
        private int value;

        public TestData(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }
}
