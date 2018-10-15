package com.jeff.fischman.exercise.messages;

import org.junit.Assert;
import org.junit.Test;

public class ActionTests {
    @Test
    public void testGetActionWithNull() {
        Assert.assertNull(Action.getAction(null));
    }
    @Test
    public void testGetActionWithEmptyStr() {
        Assert.assertNull(Action.getAction(""));
    }
    @Test
    public void testGetActionWithBadStr() {
        Assert.assertNull(Action.getAction("bad"));
    }
    @Test
    public void testGetActionWithGoodAbbrevs() {
        Assert.assertEquals(Action.Add, Action.getAction("A"));
        Assert.assertEquals(Action.Remove, Action.getAction("X"));
        Assert.assertEquals(Action.Modify, Action.getAction("M"));
        Assert.assertEquals(Action.Trade, Action.getAction("T"));
    }

    @Test
    public void testActionGetMsgType() {
        Assert.assertEquals(MsgType.Order, Action.Add.getMsgType());
        Assert.assertEquals(MsgType.Order, Action.Modify.getMsgType());
        Assert.assertEquals(MsgType.Order, Action.Remove.getMsgType());
        Assert.assertEquals(MsgType.Trade, Action.Trade.getMsgType());
    }

    @Test
    public void testGetAbbrevs() {
        Assert.assertEquals("A", Action.Add.getAbbrev());
        Assert.assertEquals("X", Action.Remove.getAbbrev());
        Assert.assertEquals("M", Action.Modify.getAbbrev());
        Assert.assertEquals("T", Action.Trade.getAbbrev());
    }

}
