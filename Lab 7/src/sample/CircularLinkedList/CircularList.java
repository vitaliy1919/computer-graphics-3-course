package sample.CircularLinkedList;

public class CircularList<T> {
    private CircularListNode<T> root;
    private CircularListNode<T> tail;
    private int size;

    public int size() {
        return size;
    }

    public CircularListNode<T> insertToEnd(T data) {
        size++;
        CircularListNode<T> node = new CircularListNode<>(data);
        if (root == null) {
            root = node;
            root.next = root;
            root.prev = root;
            tail = root;
            return node;
        }

        tail.next = node;
        node.prev = tail;
        node.next = root;
        tail = node;
        root.prev = tail;
        return node;
    }

    public CircularListNode<T> getTail() {
        return tail;
    }

    public CircularListNode<T> getRoot() {
        return root;
    }


    public CircularListNode<T> insertAfter(CircularListNode<T> node, T data) {
        CircularListNode<T> newNode = new CircularListNode<>(data);
        newNode.next = node.next;
        newNode.prev = node;
        node.next = newNode;
        newNode.next.prev = newNode;
        if (node == tail) {
            tail = newNode;
            root.prev = tail;
        }
        return newNode;
    }

    public void splitNext(CircularListNode<T> nodeA, CircularListNode<T> nodeB) {
        nodeA.next = nodeB;
        nodeB.prev = nodeA;
        root = nodeA;
        tail = nodeA.prev;
    }

    public void splitPrev(CircularListNode<T> nodeA, CircularListNode<T> nodeB) {
        nodeA.prev = nodeB;
        nodeB.next = nodeA;
        root = nodeA;
        tail = nodeA.prev;
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
