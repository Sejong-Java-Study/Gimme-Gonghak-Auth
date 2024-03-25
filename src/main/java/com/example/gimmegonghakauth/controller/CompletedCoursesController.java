package com.example.gimmegonghakauth.controller;

import com.example.gimmegonghakauth.domain.CompletedCoursesDomain;
import com.example.gimmegonghakauth.exception.FileException;
import com.example.gimmegonghakauth.service.CompletedCoursesService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class CompletedCoursesController {

    private final CompletedCoursesService excelService;

    public CompletedCoursesController(CompletedCoursesService excelService) {
        this.excelService = excelService;
    }

    @GetMapping("/excel")
    public String main() {
        return "excel";
    }

    @PostMapping("/excel/read")
    public String readExcel(@RequestParam("file") MultipartFile file, Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        try {
            List<CompletedCoursesDomain> dataList = excelService.extractExcelFile(file,userDetails);
            model.addAttribute("datas", dataList);
            return "excelList";
        } catch (IOException | FileException e) {
            model.addAttribute("error", e.getMessage());
            return "excel";
        }
    }
}
