package br.com.alura.screenmatch_jpa.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

public class ConsultaChatGPT {

	public static String obterTraducao(String texto) {
		//USANDO VARIÁVEIS DE AMBIENTE
		//OpenAiService service = new OpenAiService(System.getenv("OPENAI_APIKEY"));
		OpenAiService service = new OpenAiService("cole aqui sua chave da OpenAI");
		CompletionRequest requisicao = CompletionRequest.builder()
														.model("text-davinci-003")
														.prompt("traduza para o português o texto: " + texto)
														.maxTokens(1000)
														.temperature(0.7)
														.build();
		var resposta = service.createCompletion(requisicao);
		return resposta.getChoices().get(0).getText();
	}
}
