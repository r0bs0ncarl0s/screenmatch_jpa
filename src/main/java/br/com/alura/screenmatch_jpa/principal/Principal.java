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
import br.com.alura.screenmatch_jpa.type.Categoria;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    private List<DadosSerie> listaSerieBuscadas = new ArrayList<>();
    
    private SerieRepository repositorySerie;
    
    private List<Serie> listSeries = new ArrayList<>();
    
    public Principal(SerieRepository repositorio) {
		this.repositorySerie = repositorio;
	}

	public void exibeMenu() {
    	var opcao = -1;    	
    	while (opcao != 0) {
	        var menu = """
	                Digite a opção desejada:
	                
	                1 - Buscar séries
	                2 - Buscar episódios
	                3 - Listar séries buscadas
	                4 - Buscar séries por título
	                5 - Buscar séries por ator
	                6 - Buscar Top 5 Séries
	                7 - Buscar séries por categoria
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
	            case 4:
	                buscarSeriePorTitulo();
	                break; 
	            case 5:
	                buscarSeriePorAtor();
	                break;
	            case 6:
	                buscarTop5Series();
	                break;
	            case 7:
	            	buscarSeriePorCategoria();
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
        repositorySerie.save(itemSerie);
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
    	
    	/* PRIMEIRA FORMA DE FAZER
    	Optional<Serie> serie =  listSeries.stream()
						    			   .filter(e -> e.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase()))
						    			   .findFirst();*/
    	//SEGUNDA FORMA DE FAZER
    	Optional<Serie> serie =  repositorySerie.findByTituloContainingIgnoreCase(nomeSerie);
    	
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
            repositorySerie.save(serieEncontrada);
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
		listSeries = repositorySerie.findAll();	    					   
    	listSeries.stream()
    			  .sorted(Comparator.comparing(Serie::getGenero))
    			  .forEach(System.out::println);
    	
    }
    
    private void buscarSeriePorTitulo() {
    	System.out.println("Escolha a série pelo nome: ");
    	var nomeSerie = leitura.nextLine();
    	Optional<Serie> seriesBuscadas = repositorySerie.findByTituloContainingIgnoreCase(nomeSerie);
    	if(seriesBuscadas.isPresent()) {
    		System.out.println("Dados da série: " + seriesBuscadas.get());    		
    	}else {
    		System.out.println("Série não encontrada.");
    	}
    }
    
    private void buscarSeriePorAtor() {
    	System.out.println("Qual o nome do ator? ");
    	var nomeAtor = leitura.nextLine();
    	System.out.println("Avaliações a partir de qual valor?");
    	var avaliacao = leitura.nextDouble();
    	List<Serie> seriesEncontradas = repositorySerie.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
    	if(!seriesEncontradas.isEmpty()) {
    		System.out.println("Série em que " + nomeAtor + " trabalhou.");
    		seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    	}else {
    		System.out.println("Série não encontrada.");
    	}
    }
    
    private void buscarTop5Series() {
    	List<Serie> seriesTop5 = repositorySerie.findTop5ByOrderByAvaliacaoDesc();
    	seriesTop5.forEach(e -> System.out.println(e.getTitulo() + " avaliação: " + e.getAvaliacao()));    	
    }
    
    private void buscarSeriePorCategoria() {
    	System.out.println("Deseja buscar por qual categoria/gênero? ");
    	var auxCategoria = leitura.nextLine();
    	Categoria categPort = Categoria.fromPortuguesString(auxCategoria.toLowerCase());
    	List<Serie> seriesPorCategoria = repositorySerie.findByGenero(categPort);
    	System.out.println("Séries da categoria " + categPort.name());
    	seriesPorCategoria.forEach(System.out::println);
    }
}