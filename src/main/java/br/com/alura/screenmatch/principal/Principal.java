package br.com.alura.screenmatch.principal;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

public class Principal {
    private List<DadosTemporada> temporadas = new ArrayList<>(); 
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados cd = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?apikey=7215ebcb";

    public void exibeMenu() {
        System.out.println("Digite o nome da série para busca: ");
        var nomeSerie = leitura.nextLine();

		var json = consumoApi.obterDados(ENDERECO + "&t=" + nomeSerie.replace(" ", "+"));

		DadosSerie dados = cd.obterDados(json, DadosSerie.class);
        System.out.println(dados);
        exibeTemporadas(json, dados);
    }

    private void exibeTemporadas(String json,DadosSerie dados) {
		for(int i = 1; i < dados.totalTemporadas(); i++) {
			json = consumoApi.obterDados("https://www.omdbapi.com/?apikey=7215ebcb&t=gilmore+girls&season=" + i);
			DadosTemporada dadosTemporada = cd.obterDados(json, DadosTemporada.class);
            //dadosTemporada.episodios().forEach(System.out::println);
			temporadas.add(dadosTemporada);
		}
		
        temporadas.forEach(System.out::println);
        
    }
}
