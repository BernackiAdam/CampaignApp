package com.bernacki.testtask.marketing_api.service;

import com.bernacki.testtask.marketing_api.entity.Campaign;
import com.bernacki.testtask.marketing_api.entity.Keyword;
import com.bernacki.testtask.marketing_api.entity.Town;
import com.bernacki.testtask.marketing_api.repository.CampaignRepository;
import com.bernacki.testtask.marketing_api.repository.KeywordRepository;
import com.bernacki.testtask.marketing_api.repository.TownRepository;
import com.bernacki.testtask.marketing_api.response.ApiError;
import com.bernacki.testtask.marketing_api.response.ApiResponse;
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

    public ResponseEntity<ApiResponse<?>> checkCampaignCorrectness(Campaign campaign){
        if (isNameUnique(campaign.getCampaignName())) {
            try {
                campaign = assignTown(campaign);
                Campaign savedCampaign = saveCampaign(campaign);
                ApiResponse<Campaign> response = new ApiResponse<>(true);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
            } catch (RuntimeException e) {
                ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), e.getMessage(), System.currentTimeMillis());
                ApiResponse<?> response = new ApiResponse<>(false, error);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        }
        ApiError error = new ApiError(HttpStatus.CONFLICT.value(), "Campaign name already exists", System.currentTimeMillis());
        ApiResponse<?> response = new ApiResponse<>(false, error);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    public ResponseEntity<ApiResponse<?>> editCampaign(Campaign modifiedCampaign, BigInteger id){
        if (!checkIfExistById(id)) {
            ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), "Campaign not found", System.currentTimeMillis());
            ApiResponse<?> response = new ApiResponse<>(false, error);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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
        Campaign savedCampaign = saveCampaign(modifiedCampaign);
        ApiResponse<Campaign> response = new ApiResponse<>(true);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
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
