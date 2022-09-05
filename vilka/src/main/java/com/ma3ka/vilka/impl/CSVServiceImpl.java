package com.ma3ka.vilka.impl;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.ma3ka.vilka.domain.CarDetailed;
import com.ma3ka.vilka.domain.CarDetailedCSV;
import com.ma3ka.vilka.repository.CarDetailedRepository;
import com.ma3ka.vilka.service.CSVService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CSVServiceImpl implements CSVService {

    @Autowired
    private CarDetailedRepository carDetailedRepository;

    @Override
    public void parse() throws IOException {
        CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper mapper = new CsvMapper();
        //MappingIterator<CarDetailedCSV> readValues = mapper.with(bootstrapSchema).readValues("C\\autostat\\example\\pledges_vin.csv");
        MappingIterator<CarDetailedCSV> readValues = mapper.readerFor(CarDetailedCSV.class).with(bootstrapSchema).readValues("C\\autostat\\example\\pledges_vin.csv");

        List<CarDetailedCSV> allValues = readValues.readAll();


        CarDetailed carDetailed;
        List<CarDetailed> carDetailedList = new ArrayList<>();
        for (CarDetailedCSV allValue : allValues) {
            carDetailed = new CarDetailed();
            carDetailed.setFullDetail(allValue.getVehicleproperty_vin() + allValue.getVehicleproperty_description_short());
            carDetailedList.add(carDetailed);
            if (carDetailedList.size() == 100) {
                carDetailedRepository.saveAll(carDetailedList);
                carDetailedList.clear();
            }
        }
        System.out.println("Finished");
    }

}
