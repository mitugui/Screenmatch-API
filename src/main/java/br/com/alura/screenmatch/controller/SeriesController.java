package br.com.alura.screenmatch.controller;

import br.com.alura.screenmatch.dto.SeriesDTO;
import br.com.alura.screenmatch.services.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SeriesController {
    @Autowired
    private SeriesService service;

    @GetMapping("/series")
    public List<SeriesDTO> getSeries() {
        return service.getAllSeries();
    }

    @GetMapping("/series/top5")
    public List<SeriesDTO> getTop5Series() {
        return service.getTop5Series();
    }
}
