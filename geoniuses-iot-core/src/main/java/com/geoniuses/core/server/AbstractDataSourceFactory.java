package com.geoniuses.core.server;



/**
 * @Author liuxin
 * @Date: 2021/5/12 11:54
 * @Description:
 */
public  interface AbstractDataSourceFactory {
    public Object createDataSource(String dataType);
}
