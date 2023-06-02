package com.devsecops;



import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//import org.junit.jupiter.api.Test;
//import org.junit.Test;


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

/*    @Test
    void givenAndBmustReturnSum(){
        when((anyInt(),anyInt())).thenReturn(2);
        assertEqual(sum(a,b),2);

    }*/



    /*   @Test
       void testSeq(){
           String s= "abaaakkky";
           StringBuilder sb = new StringBuilder();
           for (int i = 0; i < s.length(); i++) {

               // Counting occurrences of s[i]
               int count = 1;
               while (i + 1 < s.length()
                       && s.charAt(i)
                       == s.charAt(i + 1)) {
                   i++;
                   count++;
               }
               sb.append(count);
               sb.append(s.charAt(i));

           }
           System.out.println(sb.toString());
       }*/
   @Test
   void testSeq(){
     Map<Character, Integer> charCountMap = new HashMap<>();

     char currentChar = '\0'; // Initialize with a non-valid character
     int currentCount = 0;
     String input = "aabbbccdddeeeee";

     for (char c : input.toCharArray()) {
         if (c == currentChar) {
             currentCount++;
         } else {
             if (currentChar != '\0') {
                 charCountMap.put(currentChar, currentCount);
             }
             currentChar = c;
             currentCount = 1;
         }
     }

     if (currentChar != '\0') {
         charCountMap.put(currentChar, currentCount);
     }

     // Print the character count
     StringBuilder sb = new StringBuilder();
     for (Map.Entry<Character, Integer> entry : charCountMap.entrySet()) {
         sb.append(entry.getValue());
         sb.append(entry.getKey());

         System.out.println(entry.getKey() + " -> " + entry.getValue());
     }
     System.out.println(sb.toString());
 }
}