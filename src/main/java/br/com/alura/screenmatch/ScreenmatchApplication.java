package br.com.alura.screenmatch;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.alura.screenmatch.model.DadosEpsodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		ConsumoApi consumoApi = new ConsumoApi();

		var json = consumoApi.obterDados("https://www.omdbapi.com/?apikey=7215ebcb&t=gilmore+girls");
		ConverteDados cd = new ConverteDados();
		DadosSerie dados = cd.obterDados(json, DadosSerie.class);

		json = consumoApi.obterDados("https://www.omdbapi.com/?apikey=7215ebcb&t=gilmore+girls&season=1&episode=1");
		DadosEpsodio dadosEpsodio = cd.obterDados(json, DadosEpsodio.class);

		System.out.println(dadosEpsodio);
	}

}