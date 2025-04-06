import lombok.extern.slf4j.Slf4j;
import model.tmdb.Movie;
import service.tmdbApi.TMDBMovieService;

import java.util.List;

@Slf4j
public class Main {
    public static void main(String[] args) {
        log.info("开始获取TMDB热门电影...");
        List<Movie> movies = TMDBMovieService.getTop5000PopularMovies();

        if (movies != null) {
            log.info("获取成功，共 {} 部电影", movies.size());
        } else {
            log.error("获取电影数据失败！");
        }
    }
}
