package com.lx.wxpush.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lx.wxpush.service.ProverbService;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;


/**
 * @Author: wenqiaogang
 * @DateTime: 2022/8/23 14:50
 * @Description: TODO
 */
@Service
public class ProverbServiceImpl implements ProverbService {
    @Override
    public String getOneProverbRandom() {
        String proverb;
        do {
            proverb = null;
            try {
                OkHttpClient client = new OkHttpClient().newBuilder().build();
                Request request = new Request.Builder()
                        .url("https://api.xygeng.cn/one")
                        .get()
                        .addHeader("Content-Type","")
                        .build();
                Response response = client.newCall(request).execute();
                //解析
                JSONObject jsonObject = JSONObject.parseObject(response.body().string());
                JSONObject content = jsonObject.getJSONObject("data");
                proverb = content.getString("content");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (proverb.length()>25);
        return proverb;
    }

    @Override
    public String translateToEnglish(String sentence) {
        String result = null;
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            Request request = new Request.Builder()
                    .url("https://fanyi.youdao.com/translate?&doctype=json&type=AUTO&i="+sentence)
                    .get()
                    .addHeader("Content-Type","")
                    .build();
            Response response = client.newCall(request).execute();
            result = response.body().string();
            //解析
            JSONObject jsonObject = JSONObject.parseObject(result);
            result = jsonObject.getJSONArray("translateResult").getJSONArray(0).getJSONObject(0).getString("tgt");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String getOneNormalProverb() {
        String proverb = null;
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, "titleID="+new Random().nextInt(9));
            Request request = new Request.Builder()
                    .url("https://eolink.o.apispace.com/myjj/common/aphorism/getAphorismList")
                    .method("POST",body)
                    .addHeader("X-APISpace-Token","5cc26sj7p4rosgf7ckb2wnusb1t21tl0")
                    .addHeader("Authorization-Type","apikey")
                    .addHeader("Content-Type","")
                    .build();

            Response response = client.newCall(request).execute();
            JSONObject jsonObject = JSONObject.parseObject(response.body().string());
            //随机取出一条句子
            String s = (String) JSONObject.parseArray((String) jsonObject.getJSONArray("result").getJSONObject(0).get("words")).get(new Random().nextInt(100));
            //去除无关元素
            proverb = s.replaceAll("^.*、", "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return proverb;
    }


}