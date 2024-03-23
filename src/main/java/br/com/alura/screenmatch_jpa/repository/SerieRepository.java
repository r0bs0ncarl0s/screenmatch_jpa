package br.com.alura.screenmatch_jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.alura.screenmatch_jpa.model.Serie;
import br.com.alura.screenmatch_jpa.type.Categoria;

public interface SerieRepository extends JpaRepository<Serie, Long>{

	/* A estrutura básica de uma derived query na JPA consiste em:
	  verbo introdutório + palavra-chave “By” + critérios de busca*/
	Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);
	List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, Double avaliacao);
	List<Serie> findTop5ByOrderByAvaliacaoDesc();
	List<Serie> findByGenero(Categoria categoria);
}
