package com.bernacki.testtask.marketing_api.controller;

import com.bernacki.testtask.marketing_api.entity.Town;
import com.bernacki.testtask.marketing_api.repository.TownRepository;
import com.bernacki.testtask.marketing_api.service.TownService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/towns")
@RequiredArgsConstructor
public class TownController {
    private final TownService townService;

    @GetMapping()
    public List<Town> getAvailableTowns(){
        return townService.getAllTowns();
    }
}
