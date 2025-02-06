package com.bernacki.testtask.marketing_api.service;

import com.bernacki.testtask.marketing_api.entity.Campaign;
import com.bernacki.testtask.marketing_api.entity.Keyword;
import com.bernacki.testtask.marketing_api.entity.Town;
import com.bernacki.testtask.marketing_api.repository.CampaignRepository;
import com.bernacki.testtask.marketing_api.repository.KeywordRepository;
import com.bernacki.testtask.marketing_api.repository.TownRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    public ResponseEntity<HttpStatus> checkCampaignCorrectness(Campaign campaign){
        if (isNameUnique(campaign.getCampaignName())) {
            try {
                campaign = assignTown(campaign);
                saveCampaign(campaign);
                return ResponseEntity.status(HttpStatus.ACCEPTED).build();
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    public ResponseEntity<HttpStatus> editCampaign(Campaign modifiedCampaign, BigInteger id){
        if (!checkIfExistById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Campaign originalCampaign = findById(id);
        modifiedCampaign.setId(id);
        if(!originalCampaign.getCampaignName().equals(modifiedCampaign.getCampaignName())){
            return checkCampaignCorrectness(modifiedCampaign);
        }
        else{
            modifiedCampaign = assignTown(modifiedCampaign);
            saveCampaign(modifiedCampaign);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    public List<Campaign> findAll(){
        return campaignRepository.findAll();
    }

    public Campaign findById(BigInteger id){
        return campaignRepository.findById(id).orElseThrow(() -> new RuntimeException("There is no campaign with such ID"));
    }

    public boolean checkIfExistById(BigInteger id){
        return campaignRepository.existsById(id);
    }

    public void deleteById(BigInteger id){
        campaignRepository.deleteById(id);
    }
}
