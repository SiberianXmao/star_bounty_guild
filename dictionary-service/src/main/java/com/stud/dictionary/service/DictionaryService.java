package com.stud.dictionary.service;

import com.stud.dictionary.web.dto.DictionaryDtos.CurrencyCreateRequest;
import com.stud.dictionary.web.dto.DictionaryDtos.CurrencyResponse;
import com.stud.dictionary.web.dto.DictionaryDtos.FactionCreateRequest;
import com.stud.dictionary.web.dto.DictionaryDtos.FactionResponse;
import com.stud.dictionary.web.dto.DictionaryDtos.OrderCategoryCreateRequest;
import com.stud.dictionary.web.dto.DictionaryDtos.OrderCategoryResponse;
import com.stud.dictionary.web.dto.DictionaryDtos.PlanetCreateRequest;
import com.stud.dictionary.web.dto.DictionaryDtos.PlanetResponse;
import com.stud.dictionary.web.dto.DictionaryDtos.SectorCreateRequest;
import com.stud.dictionary.web.dto.DictionaryDtos.SectorResponse;
import com.stud.dictionary.web.dto.DictionaryDtos.SkillCreateRequest;
import com.stud.dictionary.web.dto.DictionaryDtos.SkillResponse;

import java.util.List;

public interface DictionaryService {

    List<FactionResponse> getFactions();

    FactionResponse createFaction(FactionCreateRequest request);

    List<SectorResponse> getSectors();

    SectorResponse createSector(SectorCreateRequest request);

    List<PlanetResponse> getPlanets();

    PlanetResponse createPlanet(PlanetCreateRequest request);

    List<OrderCategoryResponse> getOrderCategories();

    OrderCategoryResponse createOrderCategory(OrderCategoryCreateRequest request);

    List<CurrencyResponse> getCurrencies();

    CurrencyResponse createCurrency(CurrencyCreateRequest request);

    List<SkillResponse> getSkills();

    SkillResponse createSkill(SkillCreateRequest request);
}
