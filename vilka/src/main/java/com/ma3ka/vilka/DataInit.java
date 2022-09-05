package com.ma3ka.vilka;

import com.ma3ka.vilka.repository.AggregatedDataRepository;
import com.ma3ka.vilka.repository.CarDetailedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInit implements ApplicationRunner {

    private CarDetailedRepository carDetailedRepository;

    private AggregatedDataRepository aggregatedDataRepository;

    @Autowired
    public DataInit(CarDetailedRepository carDetailedRepository, AggregatedDataRepository aggregatedDataRepository) {
        this.carDetailedRepository = carDetailedRepository;
        this.aggregatedDataRepository = aggregatedDataRepository;
    }

    @Override
    public void run(ApplicationArguments args) {

    }
}
