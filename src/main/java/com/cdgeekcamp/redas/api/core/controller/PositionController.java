package com.cdgeekcamp.redas.api.core.controller;

import com.cdgeekcamp.redas.api.core.service.Pagination;
import com.cdgeekcamp.redas.db.model.Position;
import com.cdgeekcamp.redas.db.model.PositionRepository;
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
@RequestMapping(value = "/position")
public class PositionController {
    @Autowired
    private PositionRepository positionRepository;

    @GetMapping(value = "/getPositionList")
    public ApiResponseX<LinkedHashMap<String, Object>> getPositionList(@PathParam("page") Integer page){
        Integer pageNum = new Pagination().Page(page);

        Pageable pageable = PageRequest.of(pageNum, 20, Sort.Direction.ASC, "Id");
        Page<Position> positions = positionRepository.findAll(pageable);

        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("totalPage", positions.getTotalPages());

        ArrayList<Position> positionList = new ArrayList<>();
        for (Position position : positions) {
            positionList.add(position);
        }
        map.put("positionList", positionList);

        return new ApiResponseX<>(ResponseCode.SUCCESS, "成功", map);
    }
}
