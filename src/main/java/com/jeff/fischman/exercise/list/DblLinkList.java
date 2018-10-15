package com.jeff.fischman.exercise.list;

import java.util.function.Consumer;


// This is a very simple doubly linked list class. It has one advantage over the
// Java linked list. This list exposes the "Node" which is hidden in the normal
// Java list. By exposing the node, it is possible to delete a node if you have
// a reference to it without having to iterate to search for an item. For us, in this project
// when we add an order to a book level, we can create a hash to that particular node.
// Later, when we want to delete that order, we can do so without having to search for it.
//

public class DblLinkList<T> {

    private Node<T> _first;
    private Node<T> _last;

    public void forEach(Consumer<? super T> consumer) {
        for (Node<T> current = _first; current != null; current = current._next) {
            consumer.accept(current._data);
        }
    }

    public void forEachReverse(Consumer<? super T> consumer) {
        for (Node<T> current = _last; current != null; current = current._prev) {
            consumer.accept(current._data);
        }
    }

    public Node<T> getFirst() {
        return _first;
    }

    public Node<T> getLast() {
        return _last;
    }

    public Node<T> add(T nodeData) {
        Node<T> node = new Node<>(nodeData);
        add(node);
        return node;
    }

    public boolean isEmpty() {
        return  _first == null;
    }

    private void add(Node<T> node) {
        if (_first == null) {
            // Since our list is empty, this node is both our first and last.
            _first = node;
            _last = node;
        } else {
            // otherwise, not the first. Make the old last one point to our new node
            _last.setNext(node);
            // and the new node point to the old last node.
            node.setPrev(_last);
            // finally, set the _last ptr to point to the new node.
            _last = node;
        }
    }

    public void remove(Node<T> node) {

        // Zap this node out of the list, making the removed node's next and prev
        // neighbors point at each other. Of course, we need to handle the case
        // where the zapped node is at the beginning or end of the list in which
        // case we set the _first and/or _last values accordingly.
        //
        if (node._next != null) {
            node._next.setPrev(node._prev);
        } else {
            _last = node._prev;
        }

        if (node._prev != null) {
            node._prev._next = node._next;
        } else {
            _first = node._next;
        }
    }

}
