package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.models.Category;
import br.com.alura.screenmatch.models.Episode;
import br.com.alura.screenmatch.models.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SeriesRepository extends JpaRepository<Series, Long> {
    Optional<Series> findByTitleContainingIgnoreCase(String seriesName);

    List<Series> findByActorsContainingIgnoreCaseAndRatingGreaterThanEqual(String actorName, Double rating);

    List<Series> findTop5ByOrderByRatingDesc();

    List<Series> findByGenre(Category genre);

    @Query("SELECT s FROM Series s WHERE s.totalSeasons <= :maximumSeasons AND s.rating >= :minimumRating")
    List<Series> seriesBySeasonAndRating(Integer maximumSeasons, Double minimumRating);

    @Query("SELECT e FROM Series s JOIN s.episodes e WHERE e.title ILIKE %:episodeExcerpt%")
    List<Episode> episodesByExcerpt(String episodeExcerpt);

    @Query("SELECT e FROM Series s JOIN s.episodes e WHERE s = :series ORDER BY e.rating DESC NULLS LAST LIMIT 5")
    List<Episode> topEpisodesBySeries(Series series);

    @Query("SELECT e FROM Series s JOIN s.episodes e WHERE  s = :series AND YEAR (e.releaseDate) >= :releaseYear")
    List<Episode> episodeBySeriesAndYear(Series series, String releaseYear);

    @Query("SELECT s FROM Series s JOIN s.episodes e GROUP BY s ORDER BY MAX(e.releaseDate) DESC LIMIT 5")
    List<Series> findTop5MostRecentSeries();

    @Query("SELECT e FROM Series s JOIN s.episodes e WHERE s.id = :id AND e.season = :number")
    List<Episode> findEpisodesBySeason(Long id, Integer number);

    @Query("SELECT e FROM Series s JOIN s.episodes e WHERE s.id = :id ORDER BY e.rating DESC NULLS LAST LIMIT 5")
    List<Episode> findTop5EpisodesByRating(Long id);
}
