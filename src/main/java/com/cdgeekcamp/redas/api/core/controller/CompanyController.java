package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.api.core.service.Pagination;
import com.cdgeekcamp.redas.db.model.Company;
import com.cdgeekcamp.redas.db.model.CompanyRepository;
import com.cdgeekcamp.redas.lib.core.api.ApiResponseX;
import com.cdgeekcamp.redas.lib.core.api.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@RestController
@RequestMapping(value = "/company")
public class CompanyController {
    @Autowired
    private CompanyRepository companyRepository;

    @GetMapping(value = "/getCompanyList")
    public ApiResponseX<LinkedHashMap<String, Object>> getCompanyList(@PathParam("page") Integer page){
        Integer pageNum = new Pagination().Page(page);

        Pageable pageable = PageRequest.of(pageNum, 20, Sort.Direction.ASC, "Id");
        Page<Company> companies = companyRepository.findAll(pageable);

        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("totalPage", companies.getTotalPages());

        ArrayList<Company> companyList = new ArrayList<>();
        for (Company company : companies) {
            companyList.add(company);
        }
        map.put("companyList", companyList);

        return new ApiResponseX<>(ResponseCode.SUCCESS, "成功", map);
    }
}
