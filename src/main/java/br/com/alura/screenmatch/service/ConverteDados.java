package br.com.alura.screenmatch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConverteDados implements IConverteDados{
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public  <T> T obterDados(String json, Class<T> classe) {
        T saida = null;
        try {
            saida = mapper.readValue(json, classe);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return saida;
    }
    
}
