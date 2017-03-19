package com.lianjia.test.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by helloworld on 2017/3/19.
 */
public class HttpClientUtil {

    private final static String lianjiaUrl = "http://10.33.106.146:8081/houseshowing/recordFeedbackStatus?record_ids=%s";

    public static Map<Long, Integer> getCustomerFeedBackStatus(List<Long> showingIds) {
        if (showingIds == null || showingIds.isEmpty()) {
            return Collections.emptyMap();
        } else {
            String param = StringUtils.join(showingIds, ",");
            String result = com.lianjia.common.util.HttpClientUtil.executeGet(String.format(lianjiaUrl, param), "UTF-8");
            if (StringUtils.isNotBlank(result)) {
                JSONObject object = JSON.parseObject(result);
                if (object == null || StringUtils.isBlank(object.getString("errno")) || !"0".equals(object.getString("errno"))) {
                    return Collections.emptyMap();
                } else {
                    String data = object.getString("data");
                    Map<Long, Integer> map = JSON.parseObject(data, Map.class);
                    if (map == null) {
                        return Collections.emptyMap();
                    }
                    return map;
                }
            } else {
                return Collections.emptyMap();
            }
        }
    }

    public static void main(String[] args) {
        List<Long> list = Lists.newArrayList();
        list.add(232779L);
        list.add(232778L);
        Map<Long,Integer> map = HttpClientUtil.getCustomerFeedBackStatus(list);
        for(Map.Entry entry:map.entrySet()){
            System.out.println(entry.getKey()+" "+entry.getValue());
        }
        Integer status = map.get(1);
        if(status==null){
            status=0;
        }
        System.out.println(status);
    }
}
