package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.FormDTO;
import com.datingapp.datingapp.entity.FormParams;
import com.datingapp.datingapp.services.FormsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forms")
@RequiredArgsConstructor
public class FormsController {
    private final FormsService formsService;

    @GetMapping
    public ResponseEntity<List<FormDTO>> getForms( @RequestParam("params") FormParams formParams) {
        return ResponseEntity.ok(formsService.findQuestUsers(formParams.getUserId(), formParams.getAge_min(), formParams.getAge_max()
                                            , formParams.getHeight_min(), formParams.getHeight_max(), formParams.getLimit(),
                                            formParams.getOffset(), formParams.getGender()));
    }
}
