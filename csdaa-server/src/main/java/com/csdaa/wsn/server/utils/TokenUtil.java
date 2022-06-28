package com.csdaa.wsn.server.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.csdaa.wsn.commons.entity.LoginUser;

import java.util.Date;

/**
 * @ Program       :  com.ljnt.jwt_login.utils.TokenUtil
 * @ Description   :  token工具类（生成、验证）
 * @ Author        :  lj
 * @ CreateDate    :  2021-7-7 14:28
 */
public class TokenUtil {
    private static final long EXPIRE_TIME= 10*60*60*1000;//token到期时间10小时
    private static final String TOKEN_SECRET="ljdyaishijin**3nkjnj??";  //密钥盐

   //生成token
    public static String sign(LoginUser user){

        String token=null;
        try {
            Date expireAt=new Date(System.currentTimeMillis()+EXPIRE_TIME);
            token = JWT.create()
                    .withIssuer("auth0")//发行人
                    .withClaim("userFlag",user.getTag())//存放数据
                    .withExpiresAt(expireAt)//过期时间
                    .sign(Algorithm.HMAC256(TOKEN_SECRET));
        } catch (IllegalArgumentException| JWTCreationException je) {

        }
        return token;
    }


    //验证token
    public static Boolean verify(String token){
        try {
            JWTVerifier jwtVerifier=JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("auth0").build();//创建token验证器
            DecodedJWT decodedJWT=jwtVerifier.verify(token);
            System.out.println("认证通过：");
            System.out.println("userFlag: " + decodedJWT.getClaim("userFlag").asString());
            System.out.println("过期时间：      " + decodedJWT.getExpiresAt());
        } catch (IllegalArgumentException | JWTVerificationException e) {
            //抛出错误即为验证不通过
            return false;
        }
        return true;
    }

    public static String getUflag(String token){
        try {
            JWTVerifier jwtVerifier=JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("auth0").build();//创建token验证器
            DecodedJWT decodedJWT=jwtVerifier.verify(token);
            System.out.println("认证通过：");
            String userFlag = decodedJWT.getClaim("userFlag").asString();
            System.out.println("userFlag: " + decodedJWT.getClaim("userFlag").asString());
            System.out.println("过期时间：      " + decodedJWT.getExpiresAt());
            return userFlag;
        } catch (IllegalArgumentException | JWTVerificationException e) {
            //抛出错误即为验证不通过
            return "";
        }
    }
}