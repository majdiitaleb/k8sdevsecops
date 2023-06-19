package com.devsecops;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class NumericApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void smallerThanOrEqualToFiftyMessage() throws Exception {
        this.mockMvc.perform(get("/compare/50")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string("Smaller than or equal to 50"));
    }

    @Test
    void greaterThanFiftyMessage() throws Exception {
        this.mockMvc.perform(get("/compare/51")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string("Greater than 50"));
    }
    
    @Test
    void welcomeMessage() throws Exception {
         this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk())
         .andExpect(content().string(("Kubernetes DevSecOps")));
    }

    @Test
    void test(){
        List<Integer> list= new ArrayList<Integer>();
        for(int i=1; i<10; i++){
            list.add(i);
        }
        Stream<Integer> streamList=list.stream();
        streamList.filter(i -> i%2 ==0).forEach(System.out::println);
        //System.out.println(liststream);

        List<String> memberNames = new ArrayList<>();
        memberNames.add("Amitabh");
        memberNames.add("Shekhar");
        memberNames.add("Aman");
        memberNames.add("Rahul");
        memberNames.add("Shahrukh");
        memberNames.add("Salman");
        memberNames.add("Yana");
        memberNames.add("Lokesh");
        memberNames.stream().filter(name->name.startsWith("A")).map(String::toUpperCase).forEach(System.out::println);
    }


}