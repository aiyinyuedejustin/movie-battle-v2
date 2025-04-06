package service.tmdbApi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import utils.HttpUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * TMDB Token服务类
 */
@Slf4j
public class TmdbTokenService {
    private static final String TMDB_API_KEY = "b66be751fa2a2b0abc87f18e1767150d";
    private static final String TMDB_ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9" +
            ".eyJhdWQiOiJiNjZiZTc1MWZhMmEyYjBhYmM4N2YxOGUxNzY3MTUwZCIsIm5iZiI6MTc0Mzk0MzQxMi4wMzAwMDAyLCJzdWIiOiI2N2YyNzZmNDJmN2Q0MzcwMjc5OWQ2ZjIiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.kVps53qR3qYXbmakBAH4XVd0QIJdmMvBHLMBUgoH_3o";
    private static String baseUrl = "https://api.themoviedb.org/3";
    private static String authUrl = baseUrl + "/authentication/token/new";

    /**
     * 获取基础URL
     */
    public static String getBaseUrl() {
        return baseUrl;
    }

    /**
     * 设置基础URL（仅用于测试）
     */
    public static void setBaseUrl(String url) {
        baseUrl = url;
        authUrl = baseUrl + "/authentication/token/new";
    }

    /**
     * 获取TMDB请求token
     *
     * @return 请求token
     */
    public static String getRequestToken() {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + TMDB_ACCESS_TOKEN);
            headers.put("accept", "application/json");

            String response = HttpUtil.get(authUrl, headers);
            TokenResponse tokenResponse = HttpUtil.fromJson(response, TokenResponse.class);

            if (tokenResponse != null && tokenResponse.isSuccess()) {
                return tokenResponse.getRequestToken();
            }
            log.error("获取TMDB请求token失败: {}", response);
            return null;
        } catch (Exception e) {
            log.error("获取TMDB请求token异常", e);
            return null;
        }
    }

    @Data
    private static class TokenResponse {
        @JsonProperty("success")
        private boolean success;

        @JsonProperty("expires_at")
        private String expiresAt;

        @JsonProperty("request_token")
        private String requestToken;
    }
}
