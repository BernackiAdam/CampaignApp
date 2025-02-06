package com.bernacki.testtask.marketing_api.service;

import com.bernacki.testtask.marketing_api.entity.Campaign;
import com.bernacki.testtask.marketing_api.entity.Town;
import com.bernacki.testtask.marketing_api.repository.TownRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TownService {
    private final TownRepository townRepository;

    public boolean isTownOnTheList(Campaign campaign){
        List<Town> allTowns = townRepository.findAll();
        Set<String> availableTownNames = allTowns.stream()
                .map(Town::getTownName)
                .collect(Collectors.toSet());
        for(String town : availableTownNames){
            if(town.equals(campaign.getTown().getTownName())){
                return true;
            }
        }
        return false;
    }

    public List<Town> getAllTowns(){
        return townRepository.findAll();
    }
}
