package com.lianjia.test.log;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by helloworld on 2017/3/16.
 */
public class LoggerUtil {

    private static final String hsErrMsg = "hsErrMsg ";
    private static final Logger loggerUtil = LoggerFactory.getLogger(LoggerUtil.class);

    /**
     * 是HouseResponseException且RstStatus不是serviceError的原因，不打印，其他情况打印
     * @param logger
     * @param throwable
     * @param format 同logger.info(format, arguments)形式 ，如 name:{},age:{},sex:{}
     * @param arguments
     */
    public static void logException(Logger logger,Throwable throwable,String format, Object... arguments){
        if(logger == null || throwable == null){
            return;
        }

        try {
            logger.info(getErrorMsg(throwable, format,arguments));

        } catch (Exception e) {
            loggerUtil.error(e.getMessage(),e);
        }

    }

    /**
     *
     * @param logger
     * @param throwable
     */
    public static void logException(Logger logger,Throwable throwable){
        logException(logger, throwable, null);
    }

    private static String getErrorMsg(Throwable throwable,String format, Object... arguments){
        //待优化
        try {
            if(format!=null){
                if(format.indexOf("{}")!=-1 && arguments.length>0){
                    return StringUtils.join(hsErrMsg,String.format(StringUtils.replace(format, "{}", "%s"), arguments)," ",throwable.getMessage());
                }else{
                    return StringUtils.join(hsErrMsg,format,StringUtils.join(arguments)," ",throwable.getMessage());
                }
            }else{
                return StringUtils.join(hsErrMsg,throwable.getMessage());
            }
        } catch (Exception e) {
            return hsErrMsg;
        }
    }

    public static void main(String[] args) {
        for(int i=0;i<10000;i++){
            long t1 = System.currentTimeMillis();
            System.out.println(System.currentTimeMillis()-t1);
        }
    }
}
