package br.com.alura.screenmatch.principal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import br.com.alura.screenmatch.model.DadosEpsodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

public class Principal {
    private List<DadosTemporada> temporadas = new ArrayList<>(); 
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados cd = new ConverteDados();
    private String enderecoPesquisa;
    private final String ENDERECO = "https://www.omdbapi.com/?apikey=7215ebcb";

    public void exibeMenu() {
        System.out.println("Digite o nome da série para busca: ");
        var nomeSerie = leitura.nextLine();

        enderecoPesquisa = ENDERECO + "&t=" + nomeSerie.replace(" ", "+");
		var json = consumoApi.obterDados(enderecoPesquisa);
        try {
            DadosSerie dados = cd.obterDados(json, DadosSerie.class);
            exibeTemporadas(json, dados);
        } catch (NullPointerException e) {
            System.out.println("\nSérie não encontrada no banco de dados!\n");
            exibeMenu();
        }

        top5Epsodios();
        todosEpisodios();
    }

    private void exibeTemporadas(String json,DadosSerie dados) {
		for(int i = 1; i < dados.totalTemporadas(); i++) {
			json = consumoApi.obterDados(enderecoPesquisa + "&season=" + i);
			DadosTemporada dadosTemporada = cd.obterDados(json, DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}
        temporadas.forEach(System.out::println);
        System.out.println();
    }

    private void top5Epsodios() {
        List<DadosEpsodio> dadosEpisodios = 
            temporadas.stream()
            .flatMap(t -> t.episodios().stream())
            .collect(Collectors.toList());

        System.out.println();
        System.out.println("Top 5 episodios:");

        dadosEpisodios.stream()
            .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
            .sorted(Comparator.comparing(DadosEpsodio::avaliacao).reversed())
            .limit(5)
            .forEach(System.out::println);
    }

    private void todosEpisodios() {
        List<Episodio> episodios = 
            temporadas.stream()
            .flatMap(t -> t.episodios().stream()
                .map(d -> new Episodio(t.numero(), d))
            )
            .collect(Collectors.toList());
        ;
        
        episodios.forEach(System.out::println);
    }

}
