package utils.tmdbApi;

import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import service.tmdbApi.TmdbTokenService;

import java.io.IOException;

import static org.junit.Assert.*;

@Slf4j
public class TmdbTokenServiceTest {
    private MockWebServer mockWebServer;
    private String originalBaseUrl;

    @Before
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        originalBaseUrl = TmdbTokenService.getBaseUrl();
        TmdbTokenService.setBaseUrl("http://" + mockWebServer.getHostName() + ":" + mockWebServer.getPort() + "/");
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
        TmdbTokenService.setBaseUrl(originalBaseUrl);
    }

    @Test
    public void testGetRequestTokenSuccess() throws Exception {
        // 准备模拟响应
        String expectedToken = "test_token";
        String responseJson = String.format("{\"success\":true,\"expires_at\":\"2024-12-31\"," +
                                                    "\"request_token\":\"%s\"}", expectedToken);
        mockWebServer.enqueue(new MockResponse()
                                      .setBody(responseJson)
                                      .setResponseCode(200));

        // 执行请求
        String token = TmdbTokenService.getRequestToken();

        // 验证响应
        assertNotNull(token);
        assertEquals(expectedToken, token);
    }

    @Test
    public void testGetRequestTokenFailure() throws Exception {
        // 准备模拟错误响应
        String responseJson = "{\"success\":false,\"status_message\":\"Invalid API key\"}";
        mockWebServer.enqueue(new MockResponse()
                                      .setBody(responseJson)
                                      .setResponseCode(401));

        // 执行请求
        String token = TmdbTokenService.getRequestToken();

        // 验证响应
        assertNull(token);
    }

    @Test
    public void testGetRequestTokenInvalidResponse() throws Exception {
        // 准备模拟无效响应
        mockWebServer.enqueue(new MockResponse()
                                      .setBody("invalid json")
                                      .setResponseCode(200));

        // 执行请求
        String token = TmdbTokenService.getRequestToken();

        // 验证响应
        assertNull(token);
    }
}
