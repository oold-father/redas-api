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

    /**
     * 修改职位列表url的爬取状态
     * 若爬虫意外停止，或者消息队列意外停止，则将还没有爬取的url状态还原到初始状态
     */
    public void modifyPositionsUrlStatus(){
        Iterable<PositionsUrl> result = PositionsUrls.findByState(1);

        for (PositionsUrl positionsUrl: result){
            positionsUrl.setState(0);
            PositionsUrls.save(positionsUrl);
        }
    }

    /**
     * 修改职位url的爬取状态
     * 若爬虫意外停止，或者消息队列意外停止，则将还没有爬取的url状态还原到初始状态
     */
    public void modifyPositionUrlStatus(){
        Iterable<PositionUrl> result = positionUrls.findAllByState(1);

        for (PositionUrl positionUrl: result){
            positionUrl.setState(0);
            positionUrls.save(positionUrl);
        }
    }
}
