package com.cdgeekcamp.redas.api.core.service;

import com.cdgeekcamp.redas.db.model.PositionUrl;
import com.cdgeekcamp.redas.db.model.PositionUrlRepository;
import com.cdgeekcamp.redas.db.model.PositionsUrl;
import com.cdgeekcamp.redas.db.model.PositionsUrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModifyUrlStatusService {
    @Autowired
    private PositionsUrlRepository PositionsUrls;

    @Autowired
    private PositionUrlRepository positionUrls;

    public void modifyPositionsUrlStatus(){
        Iterable<PositionsUrl> result = PositionsUrls.findByState(1);

        for (PositionsUrl positionsUrl: result){
            positionsUrl.setState(0);
            PositionsUrls.save(positionsUrl);
        }
    }

    public void modifyPositionUrlStatus(){
        Iterable<PositionUrl> result = positionUrls.findAllByState(1);

        for (PositionUrl positionUrl: result){
            positionUrl.setState(0);
            positionUrls.save(positionUrl);
        }
    }
}
