import java.util.Stack;

public class Node {
    Node left;
    Node right;
    int value;
    public Node(int value) {
        this.value = value;
    }

    static void preOrderPrint(Node n) {
        System.out.print(n.value);
        if(n.left != null) {
            preOrderPrint(n.left);
        }
        if(n.right != null) {
            preOrderPrint(n.right);
        }
    }

    static void preOrderPrintNonRec(Node n) {
        Stack<Node> stack = new Stack<Node>();
        stack.push(n);
        while (!stack.isEmpty()) {
            n = stack.pop();
            System.out.print(n.value);
            if(n.right != null) {
                stack.push(n.right);
            }
            if(n.left != null) {
                stack.push(n.left);
            }
        }
    }


    static void inOrderPrint(Node n) {
        if(n.left != null) {
            inOrderPrint(n.left);
        }
        System.out.print(n.value);
        if(n.right != null) {
            inOrderPrint(n.right);
        }
    }
    static void postOrderPrint(Node n) {
        if(n.left != null) {
            postOrderPrint(n.left);
        }
        if(n.right != null) {
            postOrderPrint(n.right);
        }
        System.out.print(n.value);
    }

    public static void main(String[] args) {
        Node n = new Node(5);
        n.left = new Node(3);
        n.right = new Node(7);

        n.left.left = new Node(2);
        n.left.right = new Node(4);

        n.right.left = new Node(6);
        n.right.right = new Node(8);

        n.left.left.left = new Node(1);
        n.right.right.right = new Node(9);

//		532147689  preorder
//		123456789  inorder
//		124369875  postorder

        preOrderPrint(n);
        System.out.println();

        inOrderPrint(n);
        System.out.println();

        postOrderPrint(n);
        System.out.println();

        preOrderPrintNonRec(n);
        System.out.println();
    }
}
