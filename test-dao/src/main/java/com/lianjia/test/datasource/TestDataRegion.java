package com.lianjia.test.datasource;

import com.lianjia.common.datasource.DataRegion;

/**
 * Created by helloworld on 2017/3/17.
 */
public class TestDataRegion implements DataRegion {

    private static TestDataRegion ML = new TestDataRegion();
    private TestDataRegion() {
    }

    public static TestDataRegion getInstance(){
        if(ML==null){
            synchronized (ML) {
                ML=new TestDataRegion();
            }
        }
        return ML;
    }

    @Override
    public String getRegion() {
        return "test";
    }
}
