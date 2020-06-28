package net.greg.examples.salient.trees;

/**
 * https://www.youtube.com/watch?v=oSWTXtMglKE
 */
public final class App {

  private static final Node root =
    new Node(10);


  public static void main(String[] args) {
    new App().populateFromRoot(root, args[0]);
  }


  private void populateFromRoot(Node root, String key) {

    System.err.println(
      "\n\npopulateFromRoot(" +
      root.data + ")");

    root.left = new Node(5);
    root.left.right = new Node(9);
    root.left.left = new Node(3);
    root.left.left.left = new Node(1);
    root.left.left.right = new Node(4);

    root.right = new Node(20);
    root.right.left = new Node(15);
    root.right.left.left = new Node(13);
    root.right.left.right = new Node(17);
    root.right.right = new Node(25);
    root.right.right.right = new Node(30);

    int incoming = Integer.parseInt(key);

    System.err.println("\nInsert " + incoming + "\n");
    root.right.right.insert(incoming);

    root.printInOrder();

      System.err.println(
        "\nroot.right.left.right.contains(18) " +
        root.right.left.right.contains(18));
  }
}
