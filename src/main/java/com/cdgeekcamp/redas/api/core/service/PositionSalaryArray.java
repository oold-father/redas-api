package com.cdgeekcamp.redas.api.core.service;

import org.springframework.stereotype.Service;

/**
 * 职位与薪资水平
 */
@Service
public class PositionSalaryArray {
    public int[][] getInterval(){
        return new int[][]{
                {3, 5},
                {6, 8},
                {9, 12},
                {13, 15},
                {16, 20}
        };
    }

    public int[][] getAbove(){
        return new int[][]{
                {21, 0}
        };
    }

    public String intervalString(int[] interval){
        return interval[0] + "k-" + interval[1] + "k";
    }

    public String aboveString(int[] above){
        return above[0] + "k以上";
    }
}
