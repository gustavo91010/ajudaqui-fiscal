package com.ajudaqui.service;

import com.ajudaqui.exception.FiscalParseException;
import com.ajudaqui.model.*;
import com.ajudaqui.internal.parsers.nfce.pe.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FiscalParseService {
    private static final Logger logger = LoggerFactory.getLogger(FiscalParseService.class);
    private final ObjectMapper mapper;

    public FiscalParseService() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Faz o parse do input e retorna o objeto FiscalDocument.
     */
    public FiscalDocument parse(Input input) {
        if (input instanceof UrlInput) {
            String url = ((UrlInput) input).getUrl();
            if (url.contains("sefaz.pe.gov.br")) {
                logger.info("Detectado NFCe de Pernambuco");
                NFCePEParser parser = new NFCePEParser();
                NFCePENormalizer normalizer = new NFCePENormalizer();
                
                NFCePERawDocument raw = parser.parse((UrlInput) input);
                return normalizer.normalize(raw);
            }
        }
        
        throw new FiscalParseException("Tipo de documento ou estado não suportado para o input fornecido.");
    }

    /**
     * Faz o parse do input e retorna uma String JSON.
     */
    public String parseToJson(Input input) {
        try {
            FiscalDocument doc = parse(input);
            return mapper.writeValueAsString(doc);
        } catch (FiscalParseException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao converter para JSON", e);
            throw new FiscalParseException("Erro ao processar documento", e);
        }
    }
}
