package sample.CircularLinkedList;

public class CircularList<T> {
    private CircularListNode<T> root;
    private CircularListNode<T> tail;
    private int size;

    public int size() {
        return size;
    }

    public void insertToEnd(T data) {
        size++;
        CircularListNode<T> node = new CircularListNode<>(data);
        if (root == null) {
            root = node;
            root.next = root;
            root.prev = root;
            tail = root;
            return;
        }

        tail.next = node;
        node.prev = tail;
        node.next = root;
        tail = node;
        root.prev = tail;
    }

    public CircularListNode<T> getTail() {
        return tail;
    }

    public CircularListNode<T> getRoot() {
        return root;
    }

    public void remove(CircularListNode<T> iter) {
        if (iter == null)
            return;
        if (root == null)
            return;
        size--;
        if (root == tail) {
            if (iter != root)
                return;
            root = null;
            tail = null;
            return;
        }

        if (iter == root) {
            root = root.next;
            root.prev = tail;
        } else if (iter == tail) {
            tail = tail.prev;
            root.prev = tail;
        } else {
            iter.next.prev = iter.prev;
            iter.prev.next = iter.next;
        }
    }

}
