package br.com.alura.screenmatch.services;

import br.com.alura.screenmatch.dto.SeriesDTO;
import br.com.alura.screenmatch.models.Series;
import br.com.alura.screenmatch.repository.SeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
