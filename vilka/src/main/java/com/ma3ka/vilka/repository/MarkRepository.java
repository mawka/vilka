package com.ma3ka.vilka.repository;

import com.ma3ka.vilka.domain.MachineCoreInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface MarkRepository extends ElasticsearchRepository<MachineCoreInfo, String> {

}
