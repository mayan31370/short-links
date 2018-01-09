package com.onecodepay;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Created by mayan on 2017/12/18.
 */
@Controller
public class LinkController {

  private final int LENGTH = 36;
  private final Map<Character, Integer> CHAR_MAPPING = new HashMap(LENGTH) {
    {
      put('a', 0);
      put('b', 1);
      put('c', 2);
      put('d', 3);
      put('e', 4);
      put('f', 5);
      put('g', 6);
      put('h', 7);
      put('i', 8);
      put('j', 9);
      put('k', 10);
      put('l', 11);
      put('m', 12);
      put('n', 13);
      put('o', 14);
      put('p', 15);
      put('q', 16);
      put('r', 17);
      put('s', 18);
      put('t', 19);
      put('u', 20);
      put('v', 21);
      put('w', 22);
      put('x', 23);
      put('y', 24);
      put('z', 25);
      put('1', 26);
      put('2', 27);
      put('3', 28);
      put('4', 29);
      put('5', 30);
      put('6', 31);
      put('7', 32);
      put('8', 33);
      put('9', 34);
      put('0', 35);
    }
  };
  private final Map<Integer, Character> INT_MAPPING = new HashMap(LENGTH) {
    {
      put(0, 'a');
      put(1, 'b');
      put(2, 'c');
      put(3, 'd');
      put(4, 'e');
      put(5, 'f');
      put(6, 'g');
      put(7, 'h');
      put(8, 'i');
      put(9, 'j');
      put(10, 'k');
      put(11, 'l');
      put(12, 'm');
      put(13, 'n');
      put(14, 'o');
      put(15, 'p');
      put(16, 'q');
      put(17, 'r');
      put(18, 's');
      put(19, 't');
      put(20, 'u');
      put(21, 'v');
      put(22, 'w');
      put(23, 'x');
      put(24, 'y');
      put(25, 'z');
      put(26, '1');
      put(27, '2');
      put(28, '3');
      put(29, '4');
      put(30, '5');
      put(31, '6');
      put(32, '7');
      put(33, '8');
      put(34, '9');
      put(35, '0');
    }
  };
  @Autowired
  StringRedisTemplate template;
  @Value("${short-link.base-url}")
  private String baseUrl;

  private String getCode(long id) {
    StringBuffer result = new StringBuffer();
    while (id > 0) {
      result.append(INT_MAPPING.get((int) (id % LENGTH)));
      id = id / LENGTH;
    }
    return result.toString();
  }

  private int getCode(String code) {
    int result = 0;
    int counter = 0;
    for (char ch : code.toCharArray()) {
      result += CHAR_MAPPING.get(ch) * Math.pow(LENGTH, counter);
      counter++;
    }
    return result;
  }

  @GetMapping("/{code}")
  public RedirectView redirect(@PathVariable("code") String code) {
    String url = template.boundValueOps("i_" + code).get();
    if (!StringUtils.startsWithIgnoreCase(url, "http://") && !StringUtils
        .startsWithIgnoreCase(url, "https://")) {
      url = "http://" + url;
    }
    System.out.println("GET\t" + code + "\t:\t" + url);
    return new RedirectView(url);
  }

  @PostMapping("/")
  @ResponseBody
  public String getCodeByUrl(String url) {
    BoundValueOperations<String, String> index = template.boundValueOps("c_index");
    index.setIfAbsent(0 + "");
    Long c_index = index.increment(1l);
    String shortCode = getCode(c_index);
    template.boundValueOps("i_" + shortCode).set(url);
    System.out.println("POST\t" + shortCode + "\t:\t" + url);
    return baseUrl + shortCode;
  }
}