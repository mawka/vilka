package com.ma3ka.vilka.repository;

import com.ma3ka.vilka.domain.AggregatedCarData;
import com.ma3ka.vilka.domain.CarDetailed;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AggregatedDataRepository extends CrudRepository<AggregatedCarData, Long> {

    @Query(value = "SELECT e.id, e.mark, e.model, e.number, e.details FROM CARFINISHED e", nativeQuery = true)
    List<AggregatedCarData> findAll(Pageable pageable);
}
