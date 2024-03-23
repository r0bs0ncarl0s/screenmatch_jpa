package br.com.alura.screenmatch_jpa.type;

public enum Categoria {
	ACAO("Action","ação"),
	ROMANCE("Romance","romance"),
	COMEDIA("Comedy","comédia"),
	CRIME("Crime","crime"),
	DRAMA("Drama","drama"),
	TERROR("Terror","terror");
	
	private String categoriaOmdb;
	
	Categoria(String categoriaOmdb, String categoriaPortugues){
        this.categoriaOmdb = categoriaOmdb;
        this.categoriaPortugues = categoriaPortugues;
    }
	
	private String categoriaPortugues;
	
	public static Categoria fromString(String text) {
	    for (Categoria categoria : Categoria.values()) {
	        if (categoria.categoriaOmdb.equalsIgnoreCase(text)) {
	            return categoria;
	        }
	    }
	    throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
	}
	
	public static Categoria fromPortuguesString(String text) {
	    for (Categoria categoria : Categoria.values()) {
	        if (categoria.categoriaPortugues.equalsIgnoreCase(text)) {
	            return categoria;
	        }
	    }
	    throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
	}
}
