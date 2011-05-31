package jp.gr.java_conf.ussiy.propedit.controller;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

import jp.gr.java_conf.ussiy.propedit.lib.EncodeChanger;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;


public class IndexController extends Controller {

    @Override
    public Navigation run() throws Exception {
        String src1 = asString("src1");
        String src2 = asString("src2");

        String cvt1 = EncodeChanger.unicode2UnicodeEsc(src1);
        String cvt2 = EncodeChanger.unicodeEsc2Unicode(src2);

        requestScope("src1", src1);
        requestScope("cvt1", cvt1);
        requestScope("src2", src2);
        requestScope("cvt2", cvt2);
        
        String cookieValue1 = "";
        String cookieValue2 = "";
        if (src1 != null && !src1.equals("")) {
            cookieValue1 = URLEncoder.encode(src1, "UTF-8");
        }
        if (src2 != null && !src2.equals("")) {
            cookieValue2 = URLEncoder.encode(src2, "UTF-8");
        }
        List<String> options1 = new ArrayList<String>();
        List<String> options2 = new ArrayList<String>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("statement1")) {
                    String cookieValue = cookie.getValue();
                    String[] items = cookieValue.split("<>");
                    for (String item : items) {
                        options1.remove(item);
                        options1.add(item);
                        if (options1.size() > 20) {
                            options1.remove(0);
                        }
                    }
                } else if (cookie.getName().equals("statement2")) {
                    String cookieValue = cookie.getValue();
                    String[] items = cookieValue.split("<>");
                    for (String item : items) {
                        options2.remove(item);
                        options2.add(item);
                        if (options2.size() > 20) {
                            options2.remove(0);
                        }
                    }
                }
            }
        }
        if (cookieValue1 != null && !cookieValue1.equals("")) {
            options1.remove(cookieValue1);
            options1.add(cookieValue1);
            if (options1.size() > 20) {
                options1.remove(0);
            }
        }
        if (cookieValue2 != null && !cookieValue2.equals("")) {
            options2.remove(cookieValue2);
            options2.add(cookieValue2);
            if (options2.size() > 20) {
                options2.remove(0);
            }
        }
        StringBuffer cookieBuffer1 = new StringBuffer();
        for (String option : options1) {
            if (cookieBuffer1.length() == 0) {
                cookieBuffer1.append(option);
            } else {
                cookieBuffer1.append("<>").append(option);
            }
        }
        StringBuffer cookieBuffer2 = new StringBuffer();
        for (String option : options2) {
            if (cookieBuffer2.length() == 0) {
                cookieBuffer2.append(option);
            } else {
                cookieBuffer2.append("<>").append(option);
            }
        }
        Cookie cookie1 = new Cookie("statement1", cookieBuffer1.toString());
        cookie1.setMaxAge(60 * 60 * 24 * 365);
        response.addCookie(cookie1);
        Cookie cookie2 = new Cookie("statement2", cookieBuffer2.toString());
        cookie2.setMaxAge(60 * 60 * 24 * 365);
        response.addCookie(cookie2);
        
        List<String> op1 = new ArrayList<String>();
        for (int i = options1.size() - 1; i >= 0; i--) {
            if (isBlankOrNull(options1.get(i))) continue;
            op1.add(URLDecoder.decode(options1.get(i), "UTF-8"));
        }
        List<String> op2 = new ArrayList<String>();
        for (int i = options2.size() - 1; i >= 0; i--) {
            if (isBlankOrNull(options2.get(i))) continue;
            op2.add(URLDecoder.decode(options2.get(i), "UTF-8"));
        }
        request.setAttribute("options1", op1);
        request.setAttribute("options2", op2);

        return forward("index.jsp");
    }

    public static boolean isBlankOrNull(String str) {
        return (str == null || str.equals(""));
    }
}

