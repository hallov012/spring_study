package hello.hello_spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("hello")
   public String hello(Model model) {
       model.addAttribute("data", "hello!!");
       // return "hello" -> resources/templates/hello.html 렌더링 위치를 지정함
        // Controller에서 문자열을 return 하면 viewResolver가 화면을 찾아서 처리함
       return "hello";
   }
}
