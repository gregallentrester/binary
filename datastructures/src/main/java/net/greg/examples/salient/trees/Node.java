package net.greg.examples.salient.trees;

public final class Node {

  Node left, right;
  int data;

  public Node(int incoming) {
    data = incoming;
  }

  public void insert(int incoming) {

    if (incoming <= data) {

System.err.println("16 " + (incoming + " <= " + data) + " " + (incoming <= data));
System.err.println("17 left: " + left);

      if (left == null) {

        left = new Node(incoming);

System.err.println("23 left.data: " + left.data);
      }
      else {
System.err.println("26 left != null: " + (left != null));
        left.insert(incoming);

System.err.println("29 left.data: " + left.data);
      }
    }
    else {

System.err.println("34 " + (incoming + " <= " + data) + " " + (incoming <= data));

      if (right == null) {
System.err.println("37 right == null: " + (right == null));
        right = new Node(incoming);

System.err.println("40 right.data: " + right.data);
      }
      else {
System.err.println("43 right != null: " + (right != null));// + ", right " + right.data);
        right.insert(incoming);

System.err.println("46 right.data: " + right.data);
      }
    }
  }

  public boolean contains(int incoming) {

    if (incoming == data) {
      return true;
    }

    if (incoming < data) {
      if (left == null) {
        return false;
      }
      else {
        return left.contains(incoming);
      }
    }
    else {

      if (right == null) {
        return false;
      }
      else {
        return right.contains(incoming);
      }
    }
  }

  public void printInOrder() {

    if (left != null) {
      left.printInOrder();
    }

    System.err.println(data);

    if (right != null) {
      right.printInOrder();
    }
  }
}
