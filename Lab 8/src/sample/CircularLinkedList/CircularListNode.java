package sample.CircularLinkedList;

public class CircularListNode<T> {
    public CircularListNode<T> prev;
    public CircularListNode<T> next;
    public T data;

    public CircularListNode(T data) {
        this.data = data;
    }

    public CircularListNode(T data, CircularListNode<T> prev, CircularListNode<T> next) {
        this.prev = prev;
        this.next = next;
        this.data = data;
    }
}
