package com.atguigu.gmalllogger.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.constants.GmallConstants;
import lombok.extern.slf4j.Slf4j;
import netscape.javascript.JSObject;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController //等于下面全部添加了 @ResponseBody
//等价于@Controller +  @ResponseBody

@Slf4j //方便构建log对象  log.*
public class LogController {
    @Autowired  //自动注入
    KafkaTemplate<String, String> kafkaTemplate;  //红色误报



    //@RequestMapping(value = "test", method = "get")//跟下面等价
    @GetMapping("test")
    //@ResponseBody的作用其实是将java对象转为json格式的数据
    //作用与下面的方法
    public String getTest(@RequestParam("aa") String str) {

        System.out.println("!!!!!!!!!!!");
        return str;
    }

    @PostMapping("log")
    public String doLog(@RequestParam("logString") String str) {
        System.out.println(str);
        //1.添加时间

        JSONObject jsonObject = JSONObject.parseObject(str);
        jsonObject.put("ts", System.currentTimeMillis());
        String jsonStr = jsonObject.toString();
        //2.写入本地文件
        log.info(str);


        //3.写往Kafka
        if ("startup".equals(jsonObject.getString("type"))) {
            kafkaTemplate.send(GmallConstants.GMALL_STARTUP, jsonStr);
        } else {
            kafkaTemplate.send(GmallConstants.GMALL_EVENT, jsonStr);
        }


        return "success";
    }

}
