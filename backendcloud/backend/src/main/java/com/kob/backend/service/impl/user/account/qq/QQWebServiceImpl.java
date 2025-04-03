package com.kob.backend.service.impl.user.account.qq;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.user.account.acwing.utils.HttpClientUtil;
import com.kob.backend.service.user.account.qq.QQWebService;
import com.kob.backend.utils.JwtUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Service
public class QQWebServiceImpl implements QQWebService {
    private static final String appId = "102745773";
    private static final String appSecret = "JTO4UikMmwufFn7H";
    private static final String redirectUri = "https://app7426.acapp.acwing.com.cn/user/account/qq/receive_code";
    private static final String applyAccessTokenUrl = "https://graph.qq.com/oauth2.0/token";
    private static final String getUserInfoUrl = "https://graph.qq.com/user/get_user_info";
    private static final String getUserOpenIDUrl="https://graph.qq.com/oauth2.0/me";
    private final static Random random = new Random();

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Override
    public JSONObject applyCode() {
        JSONObject resp = new JSONObject();
        String encodeUrl = "";
        try {
            encodeUrl = URLEncoder.encode(redirectUri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            resp.put("result", "failed");
            return resp;
        }

        //随机字符串，防止csrf攻击
        StringBuilder state = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            state.append((char) (random.nextInt(10) + '0'));
        }
        //存到redis里，有效期设置为10分钟
        resp.put("result", "success");
        redisTemplate.opsForValue().set(state.toString(), "true");
        redisTemplate.expire(state.toString(), Duration.ofMinutes(10));

        String applyCodeUrl = "https://graph.qq.com/oauth2.0/authorize"
                + "?response_type="+"code"
                + "&client_id=" + appId
                + "&redirect_uri=" + encodeUrl
                + "&state=" + state
                ;
        resp.put("apply_code_url", applyCodeUrl);

        return resp;
    }

    @Override
    public JSONObject receiveCode(String code, String state) {
        JSONObject resp = new JSONObject();
        resp.put("result", "failed");
        if (code == null || state == null) return resp;
        if (Boolean.FALSE.equals(redisTemplate.hasKey(state))) return resp;
        redisTemplate.delete(state);
//        获取access_token
        List<NameValuePair> nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
        nameValuePairs.add(new BasicNameValuePair("client_id", appId));
        nameValuePairs.add(new BasicNameValuePair("client_secret", appSecret));
        nameValuePairs.add(new BasicNameValuePair("code", code));
        nameValuePairs.add(new BasicNameValuePair("redirect_uri", redirectUri));
        nameValuePairs.add(new BasicNameValuePair("fmt", "json"));

        String getString = HttpClientUtil.get(applyAccessTokenUrl, nameValuePairs);
        if (getString == null) return resp;
        JSONObject getResp = JSONObject.parseObject(getString);
        String accessToken = getResp.getString("access_token");

//        获取open_id
        nameValuePairs=new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("access_token",accessToken));
        nameValuePairs.add(new BasicNameValuePair("fmt", "json"));

        getString=HttpClientUtil.get(getUserOpenIDUrl,nameValuePairs);
        if(getString==null) return resp;
        getResp = JSONObject.parseObject(getString);
        String openId=getResp.getString("openid");

        if (accessToken == null || openId == null) return resp;

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid_qq", openId);
        List<User> users = userMapper.selectList(queryWrapper);

//        用户已经授权，自动登录
        if (!users.isEmpty()) {
            User user = users.get(0);
            //生成JWT
            String jwt = JwtUtil.createJWT(user.getId().toString());

            resp.put("result", "success");
            resp.put("jwt_token", jwt);
            return resp;
        }

//        新用户授权，获取用户信息
        nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
        nameValuePairs.add(new BasicNameValuePair("openid", openId));
        nameValuePairs.add(new BasicNameValuePair("oauth_consumer_key", appId));
        getString = HttpClientUtil.get(getUserInfoUrl, nameValuePairs);
        if (getString == null) return resp;

        getResp = JSONObject.parseObject(getString);
        String username = getResp.getString("nickname");
        String photo = getResp.getString("figureurl_1");//50*50的头像

        if (username == null || photo == null) return resp;

        for (int i = 0; i < 100; i++) {
            QueryWrapper<User> usernameQueryWrapper = new QueryWrapper<>();
            usernameQueryWrapper.eq("username", username);
            if (userMapper.selectList(usernameQueryWrapper).isEmpty()) break;
            username += (char) (random.nextInt(10) + '0');
            if (i == 99) return resp;
        }
        User user = new User(
                null,
                username,
                null,
                photo,
                1500,
                null,
                openId
        );
        userMapper.insert(user);
        //生成JWT
        String jwt = JwtUtil.createJWT(user.getId().toString());
        resp.put("result", "success");
        resp.put("jwt_token", jwt);
        return resp;
    }
}