package com.example.house.es.repository;

import com.example.house.es.template.HouseIndexTemplate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsHouseRepository extends ElasticsearchRepository<HouseIndexTemplate,String> {

}
