package br.com.alura.screenmatch_jpa.principal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import br.com.alura.screenmatch_jpa.model.DadosSerie;
import br.com.alura.screenmatch_jpa.model.DadosTemporada;
import br.com.alura.screenmatch_jpa.model.Episodio;
import br.com.alura.screenmatch_jpa.model.Serie;
import br.com.alura.screenmatch_jpa.repository.SerieRepository;
import br.com.alura.screenmatch_jpa.service.ConsumoApi;
import br.com.alura.screenmatch_jpa.service.ConverteDados;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    private List<DadosSerie> listaSerieBuscadas = new ArrayList<>();
    
    private SerieRepository serieRepo;
    
    private List<Serie> listSeries = new ArrayList<>();
    
    public Principal(SerieRepository repositorio) {
		this.serieRepo = repositorio;
	}

	public void exibeMenu() {
    	var opcao = -1;    	
    	while (opcao != 0) {
	        var menu = """
	                Digite a opção desejada:
	                
	                1 - Buscar séries
	                2 - Buscar episódios
	                3 - Listar séries buscadas
	                0 - Sair                                                 
	                """;
	
	        System.out.println(menu);
	        opcao = leitura.nextInt();
	        leitura.nextLine();
	
	        switch (opcao) {
	            case 1:
	                buscarSerieWeb();
	                break;
	            case 2:
	                buscarEpisodioPorSerie();
	                break;
	            case 3:
	                listarSeriesBuscadas();
	                break;    
	            case 0:
	                System.out.println("Saindo...");
	                break;
	            default:
	                System.out.println("Opção inválida");
	        }
    	}    
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie itemSerie = new Serie(dados);
        serieRepo.save(itemSerie);
        /*Versão antes de salvar no banco
        listaSerieBuscadas.add(dados);*/
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        /* VERSÃO 1.0 
    	*/
    	listarSeriesBuscadas();
    	System.out.println("Escolha a série pelo nome: ");
    	var nomeSerie = leitura.nextLine();
    	Optional<Serie> serie =  listSeries.stream()
						    			   .filter(e -> e.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase()))
						    			   .findFirst();
    	if(serie.isPresent()) {
    		//DadosSerie dadosSerie = getDadosSerie();
    		var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();
            //for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                //var json = consumo.obterDados(ENDERECO + dadosSerie.titulo().replace(" ", "+") + "&season=" + i + API_KEY);
            	var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);
            
            List<Episodio> episodios = temporadas.stream()
							            		 .flatMap(d -> d.episodios().stream()
							            			  					    .map(e -> new Episodio(d.numero(), e)))
							            		 .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            serieRepo.save(serieEncontrada);
    	}else {
    		System.out.println("Série não encontrada.");
    	}
    }
    
    private void listarSeriesBuscadas() {
    	/*Versão antes de salvar no banco
    	List<Serie> listSeries = new ArrayList<>();    	
    	listSeries = listaSerieBuscadas.stream()
			    					   .map(d -> new Serie(d))
			    					   .collect(Collectors.toList());*/
		listSeries = serieRepo.findAll();	    					   
    	listSeries.stream()
    			  .sorted(Comparator.comparing(Serie::getGenero))
    			  .forEach(System.out::println);
    	
    }
}