package com.ma3ka.vilka.repository;

import com.ma3ka.vilka.domain.CarDetailed;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarDetailedRepository extends CrudRepository<CarDetailed, Long> {

    @Query(value ="SELECT e.Id, e.Full_Detail FROM cardetailed e", nativeQuery = true)
    List<CarDetailed> findAllCars(Pageable pageable);

}
