package com.example.cbrcurrencyparse.service;

import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.example.cbrcurrencyparse.data.CurrencyRepository;
import com.example.cbrcurrencyparse.model.Currency;
import com.example.cbrcurrencyparse.model.ExchangeRateMessage;
import com.example.cbrcurrencyparse.model.ValCurs;
import com.example.cbrcurrencyparse.model.Valute;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class ExchangeRateService {
    private final CurrencyRepository currencyRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final Logger LOG = LoggerFactory.getLogger(ExchangeRateService.class);

    public ExchangeRateService(CurrencyRepository currencyRepository, KafkaTemplate<String, String> kafkaTemplate) 
    {
        this.currencyRepository = currencyRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void fetchExchangeRates() 
    {
        LOG.info("Starting scheduled fetchExchangeRates job.");
        LocalDate today = LocalDate.now();
        String url = "http://www.cbr.ru/scripts/XML_daily.asp?date_req=" + today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        RestTemplate restTemplate = new RestTemplate();
        String xml = restTemplate.getForObject(url, String.class);

        try 
        {
            List<Currency> exchangeRates = parseAndSaveExchangeRates(xml);
            sendMessageToKafka(exchangeRates);
        } 
        catch (JAXBException e) 
        {
            LOG.error(String.format("Exception catched in parsing XML process %s", e.getMessage()));
        }
    }

    /**
     * Return currency rates with processed date.
     * @return list of currency rates.
     */
    public ExchangeRateMessage getRates()
    {
        List<Currency> currencyList = currencyRepository.findAll();
        ExchangeRateMessage message = new ExchangeRateMessage();
        if (CollectionUtils.isEmpty(currencyList))
        {
            return message;
        }
        message.setExchangeRates(currencyList);
        message.setDate(currencyList.get(0).getUpdatedAt());
        return message;
    }

    private List<Currency> parseAndSaveExchangeRates(final String xml) throws JAXBException 
    {
        JAXBContext jaxbContext = JAXBContext.newInstance(ValCurs.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        StringReader reader = new StringReader(xml);
        ValCurs valCurs = (ValCurs) unmarshaller.unmarshal(reader);

        LocalDate date = LocalDate.parse(valCurs.getDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        List<Currency> exchangeRates = new ArrayList<>();

        for (Valute valute : valCurs.getValutes()) 
        {
            Currency exchangeRate = currencyRepository.findByNumCode(valute.getNumCode())
                .orElse(new Currency());

            exchangeRate.setNumCode(valute.getNumCode());
            exchangeRate.setCharCode(valute.getCharCode());
            exchangeRate.setNominal(valute.getNominal());
            exchangeRate.setName(valute.getName());
            exchangeRate.setValue(parseCurrencyRateValue(valute.getValue()));
            exchangeRate.setUpdatedAt(date);

            currencyRepository.save(exchangeRate);
            exchangeRates.add(exchangeRate);
        }
        return exchangeRates;
    }

    private Double parseCurrencyRateValue(final String value)
    {
        BigDecimal bd = new BigDecimal(value.replace(",", "."));
        BigDecimal formattedValue = bd.setScale(4, RoundingMode.HALF_DOWN);
        return formattedValue.doubleValue();
    }

    private void sendMessageToKafka(final List<Currency> exchangeRates) 
    {
        ExchangeRateMessage message = new ExchangeRateMessage();
        message.setDate(exchangeRates.get(0).getUpdatedAt());
        message.setExchangeRates(exchangeRates);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonMessage;
        try 
        {
            jsonMessage = objectMapper.writeValueAsString(message);
        } 
        catch (JsonProcessingException e) 
        {
            LOG.error(String.format("Exception catched in creating JSON to Kafka process %s", e.getMessage()));
            return;
        }

        kafkaTemplate.send("exchange-rates", jsonMessage);
    }

}
