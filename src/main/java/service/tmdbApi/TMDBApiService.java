package service.tmdbApi;

import config.AppConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import model.tmdb.MovieList;
import utils.HttpUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * TMDB API服务
 * 只封装TMDB的原始API请求
 */
@Slf4j
public class TMDBApiService {
    private static final AppConfig CONFIG = AppConfig.getInstance();

    // 测试模式标志
    private static boolean testMode = false;
    // 测试数据
    private static MovieList testMovieList;

    @Getter
    private static String baseUrl = CONFIG.getProperty("tmdb.api.base-url", "https://api" +
            ".themoviedb.org/3");
    private static String discoverMovieUrl = baseUrl + "/discover/movie";

    /**
     * 设置基础URL（仅用于测试）
     */
    public static void setBaseUrl(String url) {
        baseUrl = url;
        discoverMovieUrl = baseUrl + "/discover/movie";
    }

    /**
     * 设置测试模式
     *
     * @param isTestMode 是否为测试模式
     * @param movieList  测试用电影列表数据
     */
    public static void setTestMode(boolean isTestMode, MovieList movieList) {
        testMode = isTestMode;
        testMovieList = movieList;
    }

    /**
     * 发现电影API
     *
     * @param page   页码
     * @param sortBy 排序方式
     * @return 电影列表
     */
    public static MovieList discoverMovies(int page, String sortBy) {
        // 测试模式下直接返回测试数据
        if (testMode && testMovieList != null) {
            log.info("测试模式：返回测试数据");
            return testMovieList;
        }

        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("accept", "application/json");

            // 构建带参数的URL
            String url =
                    String.format("%s?include_adult=false&include_video=false&language=%s" +
                                          "&page=%d&sort_by=%s&api_key=%s", discoverMovieUrl,
                                  CONFIG.getProperty("tmdb.api.language", "en-US"), page, sortBy,
                                  CONFIG.getProperty("tmdb.api.key"));

            String response = HttpUtil.get(url, headers);
            return HttpUtil.fromJson(response, MovieList.class);
        } catch (Exception e) {
            log.error("获取电影列表异常", e);
            return null;
        }
    }
}
