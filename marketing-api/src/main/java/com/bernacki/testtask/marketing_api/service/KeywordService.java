package com.bernacki.testtask.marketing_api.service;

import com.bernacki.testtask.marketing_api.entity.Campaign;
import com.bernacki.testtask.marketing_api.entity.Keyword;
import com.bernacki.testtask.marketing_api.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeywordService {
    private final KeywordRepository keywordRepository;

    public List<Keyword> getAllKeywords(){
        return keywordRepository.findAll();
    }

    public void saveKeyword(Keyword keyword){
        keywordRepository.save(keyword);
    }

    public List<Keyword> checkKeywords(Campaign campaign){
        List<Keyword> campaignKeywordList = campaign.getKeywordList();
        List<Keyword> databaseKeywordList = keywordRepository.findAll();
        Map<String, Keyword> existingKeywordList = databaseKeywordList.stream()
                .collect(Collectors.toMap(Keyword::getContent, k -> k));
        List<Keyword> updatedKeywords = new ArrayList<>();

        for(Keyword keyword : campaignKeywordList){
            if(existingKeywordList.containsKey(keyword.getContent())){
                updatedKeywords.add(existingKeywordList.get(keyword.getContent()));
            } else{
                Keyword newKeyword = keywordRepository.save(new Keyword(keyword.getContent()));
                updatedKeywords.add(newKeyword);
            }
        }
        return updatedKeywords;
    }
}
