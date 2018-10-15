package com.jeff.fischman.exercise;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.stream.IntStream;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        IntStream.range(0,10).forEach(System.out::println);
        assertTrue( true );
    }
}
