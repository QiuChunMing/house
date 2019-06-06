package com.example.house.es;

import com.example.house.form.MapQuery;
import com.example.house.form.SearchForm;

import java.util.List;

public interface ISearchService {
    void index(String houseId);

    void remove(String houseId);

    List<String> suggest(String prefix);

    List<String> query(SearchForm searchForm);

    List<String> mapQuery(String cityName, String orderBy, String orderDirection, int page, int size);

    List<String> mapQuery(MapQuery mapQuery);
}
