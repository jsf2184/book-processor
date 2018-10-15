package com.jeff.fischman.exercise.messages;

import org.junit.Assert;
import org.junit.Test;

public class SideTests {
    @Test
    public void testGetSideWithNull() {
        Assert.assertNull(Side.getSide(null));
    }
    @Test
    public void testGetSideWithEmptyStr() {
        Assert.assertNull(Side.getSide(""));
    }
    @Test
    public void testGetSideWithBadStr() {
        Assert.assertNull(Side.getSide("bad"));
    }
    @Test
    public void testGetSideWithGoodAbbrevs() {
        Assert.assertEquals(Side.Buy, Side.getSide("B"));
        Assert.assertEquals(Side.Sell, Side.getSide("S"));
    }
    @Test
    public void testGetAbbrevs() {
        Assert.assertEquals("B", Side.Buy.getAbbrev());
        Assert.assertEquals("S", Side.Sell.getAbbrev());
    }

}
