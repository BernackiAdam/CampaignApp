package com.bernacki.testtask.marketing_api.controller;

import com.bernacki.testtask.marketing_api.entity.Campaign;
import com.bernacki.testtask.marketing_api.entity.Keyword;
import com.bernacki.testtask.marketing_api.repository.CampaignRepository;
import com.bernacki.testtask.marketing_api.repository.KeywordRepository;
import com.bernacki.testtask.marketing_api.service.CampaignService;
import com.bernacki.testtask.marketing_api.service.KeywordService;
import com.bernacki.testtask.marketing_api.service.TownService;
import jakarta.transaction.Status;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {
    private final CampaignRepository campaignRepository;
    private final KeywordRepository keywordRepository;
    private final KeywordService keywordService;
    private final CampaignService campaignService;
    private final TownService townService;

    @GetMapping
    public List<Campaign> getAllCampaigns(){
        return campaignRepository.findAll();
    }

    @GetMapping("/{id}")
    public Campaign getCampaignById(@PathVariable BigInteger id){
        return campaignRepository.findById(id).orElseThrow(() -> new RuntimeException("There is no campaign with such ID"));
    }

    @Transactional
    @PostMapping("/add")
    public ResponseEntity<HttpStatus> addNewCampaign(@Valid @RequestBody Campaign campaign){
        if (campaignService.isNameUnique(campaign.getCampaignName())) {
            try {
                campaign = campaignService.assignTown(campaign);
                campaignService.saveCampaign(campaign);
                return ResponseEntity.status(HttpStatus.ACCEPTED).build();
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @Transactional
    @PutMapping("/edit/{id}")
    public ResponseEntity<HttpStatus> editCampaign(@Valid @RequestBody Campaign modifiedCampaign, @PathVariable BigInteger id) {
        if (!campaignRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Campaign originalCampaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("There is no campaign with such ID"));

        if (!originalCampaign.getCampaignName().equals(modifiedCampaign.getCampaignName()) &&
                !campaignService.isNameUnique(modifiedCampaign.getCampaignName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        try {
            modifiedCampaign.setId(id);
            modifiedCampaign = campaignService.assignTown(modifiedCampaign);
            campaignService.saveCampaign(modifiedCampaign);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @Transactional
    @DeleteMapping("/delete/{id}")
    public void deleteCampaign(@PathVariable BigInteger id){
        campaignRepository.deleteById(id);
    }

}
