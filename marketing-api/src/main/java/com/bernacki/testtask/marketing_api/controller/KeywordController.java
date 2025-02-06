package com.bernacki.testtask.marketing_api.controller;

import com.bernacki.testtask.marketing_api.entity.Keyword;
import com.bernacki.testtask.marketing_api.repository.KeywordRepository;
import com.bernacki.testtask.marketing_api.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/keywords")
@RequiredArgsConstructor
public class KeywordController {
    private final KeywordService keywordService;

    @GetMapping
    public List<Keyword> getKeywordList(){
        return keywordService.getAllKeywords();
    }

    @PostMapping("/add")
    public void addNewKeyword(@RequestBody Keyword keyword){
        keywordService.saveKeyword(keyword);
    }


}
