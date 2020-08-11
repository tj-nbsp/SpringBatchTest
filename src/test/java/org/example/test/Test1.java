package org.example.test;

import org.example.support.SpringBatchSupport;
import org.example.support.SpringTestCase;
import org.junit.Test;

public class Test1 extends SpringTestCase {

    @Test
    public void test1() throws Exception {
        SpringBatchSupport.launchJob("billJob");
    }

}
