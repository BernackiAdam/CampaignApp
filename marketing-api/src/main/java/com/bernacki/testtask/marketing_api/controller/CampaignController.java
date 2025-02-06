package com.bernacki.testtask.marketing_api.controller;
import com.bernacki.testtask.marketing_api.entity.Campaign;
import com.bernacki.testtask.marketing_api.service.CampaignService;

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
    private final CampaignService campaignService;


    @GetMapping
    public List<Campaign> getAllCampaigns(){
        return campaignService.findAll();
    }

    @GetMapping("/{id}")
    public Campaign getCampaignById(@PathVariable BigInteger id){
        return campaignService.findById(id);
    }

    @Transactional
    @PostMapping("/add")
    public ResponseEntity<HttpStatus> addNewCampaign(@Valid @RequestBody Campaign campaign){
        return campaignService.checkCampaignCorrectness(campaign);
    }

    @Transactional
    @PutMapping("/edit/{id}")
    public ResponseEntity<HttpStatus> editCampaign(@Valid @RequestBody Campaign modifiedCampaign, @PathVariable BigInteger id) {
        return campaignService.editCampaign(modifiedCampaign, id);
    }


    @Transactional
    @DeleteMapping("/delete/{id}")
    public void deleteCampaign(@PathVariable BigInteger id){
        campaignService.deleteById(id);
    }

}
