package br.com.alura.screenmatch.principal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import br.com.alura.screenmatch.model.DadosEpsodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

public class Principal {
    private List<Episodio> episodios;
    private List<DadosTemporada> temporadas = new ArrayList<>(); 
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados cd = new ConverteDados(); 
    private String enderecoPesquisa;
    private final String ENDERECO = "https://www.omdbapi.com/?apikey=7215ebcb";

    public void iniciar() {
        lerNomeSerie();
        try {
            consultarEExtrairDados();
        } catch (NullPointerException e) {
            System.out.println("\nSérie não encontrada no banco de dados!\n");
            iniciar();
        }

        extrairTodosEpisodios();
        avaliacaoPorTemporada();
        estatisticasDaSerie();
        exibirTodosEpisodios();
        top5Episodios();
        //episodiosPorData();
        //buscarEpisodio();
    }

    private void lerNomeSerie() {
        System.out.println("Digite o nome da série para busca: ");
        var nomeSerie = leitura.nextLine();
        enderecoPesquisa = ENDERECO + "&t=" + nomeSerie.replace(" ", "+");
    }

    private void consultarEExtrairDados() {
		var json = consumoApi.obterDados(enderecoPesquisa);
        DadosSerie dados = cd.obterDados(json, DadosSerie.class);

        extrairDadosTemporadas(json, dados);
    }

    private void extrairDadosTemporadas(String json,DadosSerie dados) {
		for(int i = 1; i < dados.totalTemporadas(); i++) {
			json = consumoApi.obterDados(enderecoPesquisa + "&season=" + i);
			DadosTemporada dadosTemporada = cd.obterDados(json, DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}
        //temporadas.forEach(System.out::println);
        //System.out.println();
    }

    private void top5Episodios() {
        List<DadosEpsodio> dadosEpisodios = 
            temporadas.stream()
            .flatMap(t -> t.episodios().stream())
            .collect(Collectors.toList());

        System.out.println("\nTop 5 episodios:");

        dadosEpisodios.stream()
            .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
            .sorted(Comparator.comparing(DadosEpsodio::avaliacao).reversed())
            .limit(5)
            .forEach(System.out::println);
    }

    private void extrairTodosEpisodios() {
        episodios = 
            temporadas.stream()
            .flatMap(t -> t.episodios().stream()
                .map(d -> new Episodio(t.numero(), d))
            )
            .collect(Collectors.toList());
        ;
    }

    private void exibirTodosEpisodios() {
        episodios.forEach(System.out::println);
        System.out.println();
    }


    private void buscarEpisodio() {
        System.out.println("Digite o nome do episodio que deseja buscar: ");
        var trechoTitulo = leitura.nextLine();

        //vai aprarecer muito no ava web
        Optional<Episodio> ep = episodios.stream()
            .filter(e -> e.getTitulo().toLowerCase().contains(trechoTitulo.toLowerCase()))
            .findFirst();

        if (ep.isPresent()) {
            System.out.printf("Episódio buscado foi encontrado na temporada %s", ep.get().getTemporada());
            return;
        }
        System.out.println("Referencia não encontrada!");
    }

    private void episodiosPorData() {
        System.out.println("\nA partir de que ano você deseja ver os episódios?");
        var ano = leitura.nextInt();
        //leitura.nextLine();

        LocalDate dataBusca = LocalDate.of(ano, 1, 1);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        episodios.stream()
            .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
            .forEach(e -> System.out.println(
                "Temporada: " +e.getTemporada() +
                " Episodio: " + e.getTitulo() +
                " Data: " + e.getDataLancamento().format(dtf)
            ));
    }

    private void avaliacaoPorTemporada() {
        Map<Integer, Double> avaliacaoMap = episodios
            .stream()
            .filter(e -> e.getAvaliacao() > 0.0)
            .collect(Collectors.groupingBy(Episodio::getTemporada, Collectors.averagingDouble(Episodio::getAvaliacao)));

        System.out.println(avaliacaoMap);
    }

    private void estatisticasDaSerie() {
        DoubleSummaryStatistics est = episodios
            .stream()
            .filter(e -> e.getAvaliacao() > 0.0)
            .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
    
        System.out.printf(
            "\nMédia de avaliação: %.2f" +  // Ajuste para duas casas decimais
            "\nMaior avaliação de episódio: %.2f" +  // Ajuste para duas casas decimais
            "\nMenor avaliação de episódio: %.2f\n",  // Ajuste para duas casas decimais
            est.getAverage(), est.getMax(), est.getMin()
        ); 
    }    

}
