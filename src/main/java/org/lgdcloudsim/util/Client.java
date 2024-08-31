package org.lgdcloudsim.util;

import com.alibaba.fastjson.JSON;
import okhttp3.*;

import java.util.concurrent.TimeUnit;

/**
 * @author 魏鑫磊
 * @date 2024/5/1 19:14
 */
public class Client {


    public final static int CONNECT_TIMEOUT =60;
    public final static int READ_TIMEOUT=100;
    public final static int WRITE_TIMEOUT=60;

    public static Object request(String suffix, Object params){
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(WRITE_TIMEOUT,TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(CONNECT_TIMEOUT,TimeUnit.SECONDS)//设置连接超时时间
                .build();

        // 定义 URL 和 JSON 数据
        String url = "http://localhost:5000/api/" + suffix;
        String json = JSON.toJSONString(params);
//        System.out.println(json);

        // 构建请求体
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        // 构建请求对象
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        // 发送请求并获取响应
        try (Response response = client.newCall(request).execute()) {
            // 检查响应是否成功
            if (!response.isSuccessful()) throw new RuntimeException("Unexpected code " + response);

            if (response.body() == null){
                return null;
            }
            String data = response.body().string();
//            // 获取响应内容并输出
//            System.out.println("Response Code: " + response.code());
//            System.out.println("Response Body: " + data);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
