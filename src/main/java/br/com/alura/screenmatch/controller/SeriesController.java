package br.com.alura.screenmatch.controller;

import br.com.alura.screenmatch.dto.EpisodeDTO;
import br.com.alura.screenmatch.dto.SeriesDTO;
import br.com.alura.screenmatch.services.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/series")
public class SeriesController {
    @Autowired
    private SeriesService service;

    @GetMapping
    public List<SeriesDTO> getSeries() {
        return service.getAllSeries();
    }

    @GetMapping("/categoria/{genre}")
    public List<SeriesDTO> getSeriesByGenre(@PathVariable String genre) {
        return service.getSeriesByGenre(genre);
    }

    @GetMapping("/top5")
    public List<SeriesDTO> getTop5Series() {
        return service.getTop5Series();
    }

    @GetMapping("/lancamentos")
    public List<SeriesDTO> getReleases() {
        return service.getReleases();
    }

    @GetMapping("/{id}")
    public SeriesDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/{id}/temporadas/todas")
    public List<EpisodeDTO> getAllSeasons(@PathVariable Long id) {
        return service.getAllSeasons(id);
    }

    @GetMapping("/{id}/temporadas/{number}")
    public List<EpisodeDTO> getSeasonByNumber(@PathVariable Long id, @PathVariable Integer number) {
        return service.getSeasonByNumber(id, number);
    }
}
