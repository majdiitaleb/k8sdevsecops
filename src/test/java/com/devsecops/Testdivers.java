package com.devsecops;

import org.junit.Assert;
import org.junit.Test;

public class Testdivers {

  //  private String str="happy";

    @Test
    public void testing(){
        String str="happy";
        str=str+"new";
        System.out.println(new String(str).intern());
        String third = new String("Baeldung");
        String fourth = new String("Baeldung");
        System.out.println(third == fourth);
        String A = "Test" ;String B = "Test";
        B=B.toUpperCase();

        System.out.println(A);
        System.out.println(B);

    }
}
