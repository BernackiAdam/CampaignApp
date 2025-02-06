package com.bernacki.testtask.marketing_api.service;

import com.bernacki.testtask.marketing_api.entity.Campaign;
import com.bernacki.testtask.marketing_api.entity.Keyword;
import com.bernacki.testtask.marketing_api.entity.Town;
import com.bernacki.testtask.marketing_api.repository.CampaignRepository;
import com.bernacki.testtask.marketing_api.repository.KeywordRepository;
import com.bernacki.testtask.marketing_api.repository.TownRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampaignService {
    private final CampaignRepository campaignRepository;
    private final TownRepository townRepository;
    private final KeywordService keywordService;

    public Campaign assignTown(Campaign campaign) {
        Town town = townRepository.findByTownName(campaign.getTown().getTownName())
                .orElseThrow(() -> new RuntimeException("Town not found: " + campaign.getTown().getTownName()));

        campaign.setTown(town);
        return campaign;
    }

    @Transactional
    public Campaign saveCampaign(Campaign campaign) {
        List<Keyword> updatedKeywords = keywordService.checkKeywords(campaign);
        campaign.setKeywordList(updatedKeywords);
        campaign.setCampaignFunds(campaign.getBidAmount() * 1500.0);
        return campaignRepository.save(campaign);
    }

    public boolean isNameUnique(String campaignName){
        List<Campaign> allCampaigns = campaignRepository.findAll();
        Set<String> campaignNames = allCampaigns.stream()
                .map(Campaign::getCampaignName)
                .collect(Collectors.toSet());
        for(String name : campaignNames){
            if (name.equals(campaignName)){
                return false;
            }
        }
        return true;
    }
}
