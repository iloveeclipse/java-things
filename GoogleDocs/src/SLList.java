class SLList<V> {

	class Node<T> {
		Node<T> next;
		T content;

		Node(T o) {
			this.content = o;
		}

	}

	Node<V> first;

	public SLList() {

	}

	int size() {
		int size = 0;
		Node<V> curr = first;
		while (curr != null) {
			size++;
			Node<V> next = curr.next;
			if (next == null) {
				return size;
			}
			curr = next;
		}
		return 0;
	}

	/**
	 *
	 * @return may return null, if list is empty.
	 */
	V last() {
		Node<V> curr = first;
		while (curr != null) {
			Node<V> next = curr.next;
			if (next == null) {
				return curr.content;
			}
			curr = next;
		}
		return null;
	}

	/**
	 * may return null, if list is empty.
	 */
	V first() {
		return first != null ? first.content : null;
	}

	boolean isEmpty() {
		return first == null; // size() == 0
	}

	void add(V o) {
		if (isEmpty()) {
			first = new Node<V>(o);
			return;
		}
		Node<V> curr = first;
		while (curr != null) {
			Node<V> next = curr.next;
			if (next == null) {
				curr.next = new Node<V>(o);
				return;
			}
			curr = next;
		}
	}

	void remove(V o) {
		if (isEmpty()) {
			return;
		}
		Node<V> curr = first;
		Node<V> prev = first;
		while (curr != null) {
			if (equals(curr.content, o)) {
				if (curr == first) {
					first = first.next;
				} else {
					prev.next = curr.next;
				}
				return;
			}
			Node<V> next = curr.next;
			if (next == null) {
				return;
			}
			prev = curr;
			curr = next;
		}
	}

	/**
	 * o1 and o2 can be both null. Returns true if and only the objects are both
	 * null or o1 is equals to o2 in terms of Object#equals(Object)
	 */

	static boolean equals(Object o1, Object o2) {
		if (o1 == null) {
			return o2 == null;
		}
		return o1.equals(o2);
	}

	/**
	 * argument may be null. return true if the list contains the given
	 * argument, where the comparison is done by the mean of
	 * Object#equals(Object)
	 */
	boolean contains(V o) {
		return indexOf(o) >= 0;
	}

	int indexOf(V o) {
		int count = -1;
		if (isEmpty()) {
			return count;
		}
		Node<V> curr = first;
		while (curr != null) {
			count++;
			if (equals(curr.content, o)) {
				return count;
			}
			curr = curr.next;
		}
		return -1;
	}

	V get(int i) {
		if (isEmpty()) {
			return null;
		}
		int count = 0;
		Node<V> curr = first;
		while (curr != null && count <= i) {
			count++;
			if (i == count) {
				return curr.content;
			}
			curr = curr.next;
		}
		return null;
	}

	void set(int i, V o){

	}

	public static void main(String[] args) {
		SLList<String> list = new SLList<String>();
		assert (list.isEmpty());
		assert (list.size() == 0);
		assert (list.first() == null);
		assert (list.last() == null);
		assert (!list.contains(null));
		assert (!list.contains(""));
		assert (!list.contains("hallo"));
		assert (list.indexOf(null) == -1);
		list.add(null);
		assert (!list.isEmpty());
		assert (list.size() == 1);
		assert (list.first() == null);
		assert (list.last() == null);
		assert (list.contains(null));
		assert (!list.contains(""));
		assert (!list.contains("hallo"));
		assert (list.indexOf(null) == 0);
		list.add("");
		list.add("hallo");
		assert (!list.isEmpty());
		assert (list.size() == 3);
		assert (list.first() == null);
		assert (list.last().equals("hallo"));
		assert (list.contains(null));
		assert (list.contains(""));
		assert (list.contains("hallo"));
		assert (list.indexOf(null) == 0);
		assert (list.indexOf("") == 1);
		assert (list.indexOf("hallo") == 2);
		list.remove(null);
		assert (!list.isEmpty());
		assert (list.size() == 2);
		assert (list.first().equals(""));
		assert (list.last().equals("hallo"));
		assert (!list.contains(null));
		assert (list.contains(""));
		assert (list.contains("hallo"));
		assert (list.indexOf(null) == -1);
		assert (list.indexOf("") == 0);
		assert (list.indexOf("hallo") == 1);
		list.remove("hallo");
		assert (!list.isEmpty());
		assert (list.size() == 1);
		assert (list.first().equals(""));
		assert (list.last().equals(""));
		assert (!list.contains(null));
		assert (list.contains(""));
		assert (!list.contains("hallo"));
		assert (list.indexOf(null) == -1);
		assert (list.indexOf("") == 0);
		assert (list.indexOf("hallo") == -1);
		list.remove("");
		assert (list.isEmpty());
		assert (list.size() == 0);
		assert (list.first() == null);
		assert (list.last() == null);
		assert (!list.contains(null));
		assert (!list.contains(""));
		assert (!list.contains("hallo"));
		assert (list.indexOf(null) == -1);
		assert (list.indexOf("") == -1);
		assert (list.indexOf("hallo") == -1);
		list.remove("");
		list.remove(null);
		list.remove("hallo");
		assert (list.isEmpty());
		assert (list.size() == 0);
		assert (list.first() == null);
		assert (list.last() == null);
		assert (!list.contains(null));
		assert (!list.contains(""));
		assert (!list.contains("hallo"));
	}
}
