package br.com.alura.screenmatch_jpa.model;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

import br.com.alura.screenmatch_jpa.type.Categoria;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name= "series")
public class Serie {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;	
	
	@Column(name = "nomeDaSerie", unique = true)
	private String titulo;
	
	private Integer totalTemporadas;
	
	private Double avaliacao;
	
	@Enumerated(EnumType.STRING)
	private Categoria genero;
	
	private String atores;
	
	private String poster;
	
	private String sinope;
	
	@OneToMany(mappedBy = "serie", cascade = CascadeType.ALL)
	private List<Episodio> episodios = new ArrayList<>();
	
	public List<Episodio> getEpisodios() {
		return episodios;
	}

	public void setEpisodios(List<Episodio> episodios) {
		episodios.forEach(e -> e.setSerie(this));
		this.episodios = episodios;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Integer getTotalTemporadas() {
		return totalTemporadas;
	}

	public void setTotalTemporadas(Integer totalTemporadas) {
		this.totalTemporadas = totalTemporadas;
	}

	public Double getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(Double avaliacao) {
		this.avaliacao = avaliacao;
	}

	public Categoria getGenero() {
		return genero;
	}

	public void setGenero(Categoria genero) {
		this.genero = genero;
	}

	public String getAtores() {
		return atores;
	}

	public void setAtores(String atores) {
		this.atores = atores;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getSinope() {
		return sinope;
	}

	public void setSinope(String sinope) {
		this.sinope = sinope;
	}

	//Construtor padrão para utilizar o JPA
	public Serie() {
		
	}
	
	public Serie(DadosSerie dadosSerie) {
		this.titulo = dadosSerie.titulo();
		this.totalTemporadas = dadosSerie.totalTemporadas();
		this.avaliacao = OptionalDouble.of(Double.valueOf(dadosSerie.avaliacao())).orElse(0D);
		this.atores = dadosSerie.atores();
		this.poster = dadosSerie.poster();
		//Usando ChatGPT para traduzir
		//this.sinope = ConsultaChatGPT.obterTraducao(dadosSerie.sinope()).trim();
		this.sinope = dadosSerie.sinope(); //Sem tradução
		this.genero = Categoria.fromString(dadosSerie.genero().split(",")[0].trim());
	}

	@Override
	public String toString() {
		return  " genero=" + genero +
				", titulo=" + titulo + 
				", totalTemporadas=" + totalTemporadas + 
				", avaliacao=" + avaliacao + 
				", atores=" + atores + 
				", poster=" + poster + 
				", sinope=" + sinope;
	}
	
	
}
