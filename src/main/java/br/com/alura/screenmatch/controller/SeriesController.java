package br.com.alura.screenmatch.controller;

import br.com.alura.screenmatch.dto.SeriesDTO;
import br.com.alura.screenmatch.repository.SeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SeriesController {
    @Autowired
    private SeriesRepository seriesRepository;

    @GetMapping("/series")
    public List<SeriesDTO> getSeries() {
        return seriesRepository.findAll().stream()
                .map(s -> new SeriesDTO(s.getId(),
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
