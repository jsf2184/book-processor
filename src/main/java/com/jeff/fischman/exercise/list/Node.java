package com.jeff.fischman.exercise.list;

public class Node<T> {

    T _data;
    Node<T> _prev;
    Node<T> _next;

    public Node(T data) {
        _data = data;
        _prev = null;
        _next = null;
    }

    public T getData() {
        return _data;
    }

    public void setData(T data) {
        _data = data;
    }

    public Node getPrev() {
        return _prev;
    }

    public void setPrev(Node<T> prev) {
        _prev = prev;
    }

    public Node<T> getNext() {
        return _next;
    }

    public void setNext(Node<T> next) {
        _next = next;
    }
}
