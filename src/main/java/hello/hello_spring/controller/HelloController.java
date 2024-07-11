package hello.hello_spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    @GetMapping("hello")
   public String hello(Model model) {
       model.addAttribute("data", "hello!!");
       // return "hello" -> resources/templates/hello.html 렌더링 위치를 지정함
        // Controller에서 문자열을 return 하면 viewResolver가 화면을 찾아서 처리함
       return "hello";
   }

   @GetMapping("hello-mvc")
    public String helloMvc(@RequestParam(value = "name", required = false, defaultValue = "spring") String name, Model model) {
         model.addAttribute("name", name);
         return "hello-template";
    }

    @GetMapping("hello-string")
    @ResponseBody // http의 body에 해당 데이터를 직접 넣어준다
    public String helloString(@RequestParam("name") String name) {
        return "hello? " + name; // "hello spring" 그냥 View 자체가 없음
    }

    @GetMapping("hello-api")
    @ResponseBody // 객체를 반환하면 json으로 반환
    public Hello helloApi(@RequestParam("name") String name) {
        Hello hello = new Hello();
        hello.setName(name);
        return hello;
    }


    static class Hello {
        private String name;

        // getter, setter 자동 생성 단축어: alt + insert -> getter and setter
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
