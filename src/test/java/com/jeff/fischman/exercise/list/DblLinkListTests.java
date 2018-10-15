package com.jeff.fischman.exercise.list;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.IntStream;

public class DblLinkListTests {

    @Test
    public void testAddRemoveScenario() {
        // To simplify tests, just make it be a list of Integers
        DblLinkList<Integer> sut = new DblLinkList<>();
        Assert.assertTrue(sut.isEmpty());
        verifyList(new ArrayList<>(), sut);
        verifyListReverse(new ArrayList<>(), sut);

        // Lets remember some nodes so we can remove them later.
        Map<Integer, Node<Integer>> map = new HashMap<>();
        // put numbers 0 to 9 on the list and create an entry in the map to the nodes for each list item
        IntStream.range(0, 10).forEach(i -> map.put(i, sut.add(i)));
        verifyList(Arrays.asList(0,1,2,3,4,5,6,7,8,9), sut);
        verifyListReverse(Arrays.asList(9,8,7,6,5,4,3,2,1,0), sut);

        // Now lets remove all the even elements
        for (int i=0; i<10; i+= 2) {
            Node<Integer> node = map.get(i);
            sut.remove(node);
        }
        verifyList(Arrays.asList(1,3,5,7,9), sut);
        verifyListReverse(Arrays.asList(9,7,5,3,1), sut);

        // Now lets remove all the odd elements
        for (int i=1; i<10; i+= 2) {
            Node<Integer> node = map.get(i);
            sut.remove(node);
        }
        Assert.assertTrue(sut.isEmpty());
        verifyList(new ArrayList<>(), sut);
        verifyListReverse(new ArrayList<>(), sut);


    }

    private  void verifyList(List<Integer> expected, DblLinkList<Integer> sut) {
        List<Integer> actual = copyToJavaList(sut);
        Assert.assertEquals(expected, actual);
    }
    private  void verifyListReverse(List<Integer> expected, DblLinkList<Integer> sut) {
        List<Integer> actual = copyToJavaListReverse(sut);
        Assert.assertEquals(expected, actual);
    }

    private  List<Integer> copyToJavaList(DblLinkList<Integer> sut) {
        List<Integer> res = new ArrayList<>();
        sut.forEach(res::add);
        return res;
    }
    private  List<Integer> copyToJavaListReverse(DblLinkList<Integer> sut) {
        List<Integer> res = new ArrayList<>();
        sut.forEachReverse(res::add);
        return res;
    }

}
