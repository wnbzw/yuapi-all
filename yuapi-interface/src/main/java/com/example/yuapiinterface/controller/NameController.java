package com.example.yuapiinterface.controller;



import com.example.yuapiclientsdk.model.User;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

import static com.example.yuapiclientsdk.utils.SignUtils.genSign;

/**
 * 名称 API
 *
 * @author yupi
 */
@RestController
@RequestMapping("/name")
public class NameController {
    @GetMapping("/get")
    public String getNameByGet(String name) {
        return "GET 你的名字是" + name;
    }

    @PostMapping("/post")
    public String getNameByPost(@RequestParam String name) {
        return "POST 你的名字是" + name;
    }

    @PostMapping("/user")
    public String getUserNameByPost(@RequestBody User user, HttpServletRequest request) {
        // 从请求头中获取参数
//        String accessKey = request.getHeader("accessKey");
//        String nonce = request.getHeader("nonce");
//        String timestamp = request.getHeader("timestamp");
//        String sign = request.getHeader("sign");
//        String body = request.getHeader("body");
//
//        // todo 实际情况应该是去数据库中查是否已分配给用户
//        if (!accessKey.equals("yupi")){
//            throw new RuntimeException("无权限");
//        }
//        // 校验随机数，模拟一下，直接判断nonce是否大于10000
//        if (Long.parseLong(nonce) > 10000) {
//            throw new RuntimeException("无权限");
//        }
//
//        // todo 时间和当前时间不能超过5分钟
////        if (timestamp) {}
//        String serverSign=genSign(body, "abcdefgh");
//        if (!sign.equals(serverSign)) {
//            throw new RuntimeException("无权限");
//        }
        return "POST 用户名字是" + user.getUsername();
    }

}
