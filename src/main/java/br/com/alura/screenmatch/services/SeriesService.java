package br.com.alura.screenmatch.services;

import br.com.alura.screenmatch.dto.EpisodeDTO;
import br.com.alura.screenmatch.dto.SeriesDTO;
import br.com.alura.screenmatch.models.Series;
import br.com.alura.screenmatch.repository.SeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SeriesService {
    @Autowired
    private SeriesRepository seriesRepository;

    public List<SeriesDTO> getAllSeries() {
        return convertsData(seriesRepository.findAll());
    }

    public List<SeriesDTO> getTop5Series() {
        return convertsData(seriesRepository.findTop5ByOrderByRatingDesc());
    }

    public List<SeriesDTO> getReleases() {
        return convertsData(seriesRepository.findTop5MostRecentSeries());
    }

    public SeriesDTO getById(Long id) {
        Optional<Series> series = seriesRepository.findById(id);

        if (series.isPresent()) {
            Series s = series.get();
            return new SeriesDTO(
                    s.getId(),
                    s.getTitle(),
                    s.getTotalSeasons(),
                    s.getRating(),
                    s.getGenre(),
                    s.getActors(),
                    s.getPoster(),
                    s.getSynopsis());
        }
        return null;
    }

    public List<EpisodeDTO> getAllSeasons(Long id) {
        Optional<Series> series = seriesRepository.findById(id);

        if (series.isPresent()) {
            Series s = series.get();
            return s.getEpisodes().stream()
                    .map(e -> new EpisodeDTO(e.getSeason(), e.getEpisodeNumber(), e.getTitle()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    private List<SeriesDTO> convertsData(List<Series> series) {
        return series.stream()
                .map(s -> new SeriesDTO(
                        s.getId(),
                        s.getTitle(),
                        s.getTotalSeasons(),
                        s.getRating(),
                        s.getGenre(),
                        s.getActors(),
                        s.getPoster(),
                        s.getSynopsis()))
                .collect(Collectors.toList());
    }
}
