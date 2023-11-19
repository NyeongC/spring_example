package com.example.spring_example.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index(){
        
        // 머스테치 스타터 덕분에 컨트롤러에서 문자열을 반환할때  /template/xxx.mustache 자동으로 지정
        return "index";
    }
}
