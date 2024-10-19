package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.models.*;
import br.com.alura.screenmatch.repository.SeriesRepository;
import br.com.alura.screenmatch.services.ApiConsumption;
import br.com.alura.screenmatch.services.DataConverter;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private final Scanner reading = new Scanner(System.in);
    private final ApiConsumption apiConsumption = new ApiConsumption();
    private final DataConverter converter = new DataConverter();

    private final String BASE_URL = "http://www.omdbapi.com/?t=";
    private final String API_KEY_PARAM;

    private final SeriesRepository seriesRepository;

    private List<Series> seriesList = new ArrayList<>();

    private Optional<Series> searchedSeries;

    public Main(Dotenv dotenv, SeriesRepository seriesRepository) {
        String API_KEY = dotenv.get("API_KEY");
        this.API_KEY_PARAM = "&apikey=" + API_KEY;

        this.seriesRepository = seriesRepository;
    }

    public void displayMenu() {
        var option = -1;

        while (option != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar série por título
                    5 - Buscar séries por ator
                    6 - Top 5 séries
                    7 - Buscar séries por categoria
                    8 - Buscar séries por quantidade de temporadas e avaliação
                    9 - Buscar episódio pelo trecho
                    10 - Top 5 episódios de uma série
                    11 - Buscar episódios a partir de um ano
                    
                    0 - Sair
                    """;

            System.out.println(menu);
            option = reading.nextInt();
            reading.nextLine();
                switch (option) {
                    case 1:
                        searchSeries();
                        break;
                    case 2:
                        searchEpisodesBySeries();
                        break;
                    case 3:
                        listSearchedSeries();
                        break;
                    case 4:
                        searchSeriesByTitle();
                        break;
                    case 5:
                        searchSeriesByActor();
                        break;
                    case 6:
                        searchTop5Series();
                        break;
                    case 7:
                        searchSeriesByGenre();
                        break;
                    case 8:
                        searchSeriesByTotalSeasonsAndRating();
                        break;
                    case 9:
                        searchEpisodeByExcerpt();
                        break;
                    case 10:
                        topEpisodesBySeries();
                        break;
                    case 11:
                        searchEpisodesStartingFromYear();
                        break;
                    case 0:
                        System.out.println("Saindo...");
                        break;
                    default:
                        System.out.println("Opção inválda");
                }
        }
    }

    private SeriesData getSeriesData() {
        System.out.println("Digite o nome da série para busca:");
        var seriesName = reading.nextLine();
        var json = apiConsumption.getData(
                BASE_URL
                        + seriesName.replace(" ", "+")
                        + API_KEY_PARAM);

        return converter.getData(json, SeriesData.class);
    }

    private void searchSeries() {
        SeriesData seriesData = getSeriesData();
        var series = new Series(seriesData);
        System.out.println(series);

        seriesRepository.save(series);
    }

    private void searchEpisodesBySeries() {
        listSearchedSeries();
        System.out.println("Digite o nome da série para busca:");
        var seriesName = reading.nextLine();

        Optional<Series> series = seriesList.stream()
                .filter(s -> s.getTitle().toLowerCase().contains(seriesName.toLowerCase()))
                .findFirst();

        if (series.isPresent()) {
            var foundSeries = series.get();
            List<SeasonData> seasons = new ArrayList<>();

            for (int i = 1; i <= foundSeries.getTotalSeasons(); i++) {
                var json = apiConsumption.getData(
                        BASE_URL
                                + foundSeries.getTitle().replace(" ", "+")
                                + "&season=" + i
                                + API_KEY_PARAM);

                SeasonData seasonData = converter.getData(json, SeasonData.class);
                seasons.add(seasonData);
            }

            seasons.forEach(System.out::println);

            List<Episode> episodes = seasons.stream()
                    .flatMap(s -> s.episodes().stream()
                            .map(e -> new Episode(s.number(), e)))
                    .collect(Collectors.toList());

            foundSeries.setEpisodes(episodes);
            seriesRepository.save(foundSeries);
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void listSearchedSeries() {
        seriesList = seriesRepository.findAll();
        seriesList.stream()
                .sorted(Comparator.comparing(Series::getTitle))
                .forEach(System.out::println);
    }

    private void searchSeriesByTitle() {
        System.out.println("Digite o nome da série para busca:");
        var seriesName = reading.nextLine();

        searchedSeries = seriesRepository.findByTitleContainingIgnoreCase(seriesName);

        if (searchedSeries.isPresent()) {
            System.out.println("Dados da série: " + searchedSeries.get());
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void searchSeriesByActor() {
        System.out.println("Qual o nome para a busca?");
        var actorName = reading.nextLine();
        System.out.println("Avaliações a partir de que valor?");
        var rating = reading.nextDouble();

        List<Series> foundedSeries = seriesRepository.findByActorsContainingIgnoreCaseAndRatingGreaterThanEqual(actorName, rating);

        System.out.println("Séries em que " + actorName +" trabalhou");
        foundedSeries.forEach(f ->
                System.out.println(f.getTitle() + ", Avaliação: " + f.getRating()));
    }

    private void searchTop5Series() {
        List<Series> topSeries = seriesRepository.findTop5ByOrderByRatingDesc();
        topSeries.forEach(s ->
                System.out.println(s.getTitle() + ", Avaliação: " + s.getRating()));
    }

    private void searchSeriesByGenre() {
        System.out.println("Deseja buscar séries de que categoria/gênero?");
        var genreName = reading.nextLine();
        Category genre = Category.fromPortuguese(genreName);
        List<Series> seriesByGenre = seriesRepository.findByGenre(genre);
        System.out.println("Séries da categoria " + genreName);
        seriesByGenre.forEach(System.out::println);
    }

    private void searchSeriesByTotalSeasonsAndRating() {
        System.out.println("Digite a quantidade máxima de temporadas:");
        var maximumSeasons = reading.nextInt();
        reading.nextLine();
        System.out.println("Avaliações a partir de que valor?");
        var minimumRating = reading.nextDouble();
        reading.nextLine();
        List<Series> searchedSeries = seriesRepository.
                seriesBySeasonAndRating(maximumSeasons, minimumRating);

        if (searchedSeries.isEmpty()) {
            System.out.println("Nenhuma série encontrada!");
        } else {
            System.out.println("*** Séries filtradas ***");
            searchedSeries.forEach(s ->
                    System.out.println(s.getTitle() + "  - avaliação: " + s.getRating()));
        }
    }

    private void searchEpisodeByExcerpt() {
        System.out.println("Qual o nome do episódio para a busca?");
        var episodeExcerpt = reading.nextLine();
        List<Episode> foundedEpisodes = seriesRepository.episodesByExcerpt(episodeExcerpt);

        foundedEpisodes.forEach(f ->
                System.out.printf("Série: %s, Temporada %s - Episódio %s - %s\n",
                        f.getSeries().getTitle(), f.getSeason(),
                        f.getEpisodeNumber(), f.getTitle()));
    }

    private void topEpisodesBySeries() {
        searchSeriesByTitle();
        if (searchedSeries.isPresent()) {
            Series series = searchedSeries.get();
            List<Episode> topEpisodes = seriesRepository.topEpisodesBySeries(series);
            if (!topEpisodes.isEmpty()) {
                System.out.printf("*** Top 5 episódios de %s ***\n", series.getTitle());
                topEpisodes.forEach(e ->
                        System.out.printf("Série: %s, Temporada %s - Episódio %s - Avaliação %s - %s\n",
                                e.getSeries().getTitle(), e.getSeason(),
                                e.getEpisodeNumber(), e.getRating(), e.getTitle()));
            } else {
                System.out.println("Episódios não encontrados! Tente pesquisá-los antes de buscar o top 5");
            }
        }
    }

    private void searchEpisodesStartingFromYear() {
        searchSeriesByTitle();
        if (searchedSeries.isPresent()) {
            Series series = searchedSeries.get();

            System.out.println("Digite o ano limite e lançamento");
            var releaseYear = reading.nextLine();

            List<Episode> episodes = seriesRepository.episodeBySeriesAndYear(series, releaseYear);
            if (!episodes.isEmpty()) {
                System.out.printf("*** Episódios de %s lançados a partir de %s ***\n", series.getTitle(), releaseYear);
                episodes.forEach(System.out::println);
            } else {
                System.out.println("Episódio não encontrados!");
                System.out.println("Pode ser que não hajam episódios após a data selecionada.");
                System.out.println("Ou considere pesquisar os episódios antes de buscar por data!");
            }
        }
    }
}
