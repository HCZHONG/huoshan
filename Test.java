package com.eefung.kuaishou.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.TreeMap;

public class Test {

    private static char[] KEY_1 = "57218436".toCharArray();

    private static char[] KEY_2 = "15387264".toCharArray();

    private static String RSTR = "3ea57347";

    private static String shuffle(int time, char[] key){
        char[] timeHex = Integer.toHexString(time).toCharArray();
        StringBuilder result = new StringBuilder();
        result.append(timeHex[(key[0] - 49)]);
        result.append(timeHex[(key[1] - 49)]);
        result.append(timeHex[(key[2] - 49)]);
        result.append(timeHex[(key[3] - 49)]);
        result.append(timeHex[(key[4] - 49)]);
        result.append(timeHex[(key[5] - 49)]);
        result.append(timeHex[(key[6] - 49)]);
        result.append(timeHex[(key[7] - 49)]);
        return result.toString();
    }


    public static String calcSig(int ts, String str){
        char[] shuffle1 = shuffle(ts, KEY_1).toCharArray();
        char[] shuffle2 = shuffle(ts, KEY_2).toCharArray();
        String md5 = DigestUtils.md5Hex(str);
        if((ts & 1) == 1){
            md5 = DigestUtils.md5Hex(md5);
        }
        char[] as = new char[18];
        char[] cp = new char[18];
        as[0] = 'a';
        as[1] = '2';
        for (int i = 0; i < 8; i++){
            as[2 * (i + 1)] = md5.charAt(i);
            as[2 * i + 3] = shuffle2[i];
            cp[2 * i] = shuffle1[i];
            cp[2 * i + 1] = md5.charAt(i + 24);
        }
        cp[16] = 'e';
        cp[17] = '2';
        return new String(as) + new String(cp);
    }

    public static String getUserInfo(int ts, String url){
        Map<String, String> treeMap = new TreeMap<>();
        if(StringUtils.isEmpty(url)){
            return "";
        }
        String param = url.substring(url.indexOf("?") + 1);
        String[] params = param.split("&");
        for(int i = 0; i < params.length; i++){
            String[] temp = params[i].split("=");
            if(temp.length == 1){
                treeMap.put(temp[0], "");
            }else {
                treeMap.put(temp[0], temp[1]);
            }
        }
        treeMap.put("rstr", RSTR);
        StringBuffer result = new StringBuffer();
        for(Map.Entry<String, String> entry : treeMap.entrySet()){
            result.append(entry.getValue());
        }
        String ascp = calcSig(ts, result.toString());
        return url + "?as=" + ascp.substring(0, 18) + "&" + "cp=" + ascp.substring(18);
    }

    public static void main(String[] args) {
        System.out.println(getUserInfo(1573119511,"https://hotsoon.snssdk.com/hotsoon/item/6756036139469131015/comments/?offset=20&count=20&live_sdk_version=230&iid=0&device_id=0&ac=4g&channel=wandoujia&aid=1112&app_name=live_stream&version_code=230&version_name=2.3.0&device_platform=android&ssmix=a&device_type=SM-G9250&device_brand=samsung&os_api=23&os_version=6.0.1&uuid=357556061126835&openudid=6f98fe530dfa108f&manifest_version_code=230&resolution=1080*1794&dpi=420&update_version_code=2301&ts=1573119511"));
    }
}
