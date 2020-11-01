public class ListInstr {
	class Node {
		Node prev;
		Node next;
		Instruccion instr;

		public Node(Instruccion i) {
			this.instr = i;
			this.prev = null;
			this.next = null;
		}
	}

	private Node head;
	private int i;

	public ListInstr() {
		this.head = null;
		this.i = 0;

	}

	public int size() {
		return this.i;
	}

	public Instruccion first() {
		return this.head == null?
			null :
			this.head.instr;
	}

	public boolean isEmpty() {
		return this.head == null;
	}

	public void add(Instruccion instr) {
		Node node = new Node(instr);

		if(this.head == null)
			this.head = node;
		else {
			int pos = 0;
			Node aux = this.head;

			while(aux.next != null && pos < this.i) {
				aux = aux.next;
				pos++;
			}

			aux.next = node;
			node.prev = aux;
		}
		this.i++;
	}

	public void remove(int index) {
		if(this.head != null && index < this.i) {
			Node aux = this.head;
			int pos = 0;

			while(aux.next != null && pos < index) {
				aux = aux.next;
				pos++;
			}
			
			if (aux.next == null && aux.prev != null) {
				aux.prev.next = null;
				aux.prev = null;
			} else if(aux.next == null)
				this.head = null;
			else if(aux.prev != null) {
				aux.prev.next = aux.next;
				aux.next.prev = aux.prev;
				aux.prev = null;
				aux.next = null;
			} else {
				this.head = aux.next;
				aux.next.prev = null;
				aux.next = null; 
			}

			aux = null;
			this.i--;
		}
	}

	public Instruccion get(int index) {
		if(this.head != null && index < this.i) {
			Node aux = this.head;
			int pos = 0;

			while(aux.next != null && pos < index) {
				aux = aux.next;
				pos++;
			}

			return aux.instr;
		} else
			return null;
	}

	public Instruccion pop() {
		if(this.head != null) {
			Node aux = this.head;
			int pos = 0;

			while(aux.next != null && pos < this.i) {
				aux = aux.next;
				pos++;
			}
			
			if (aux.next == null && aux.prev != null) {
				aux.prev.next = null;
				aux.prev = null;
			} else if(aux.next == null){
				this.head = null;
			} else if(aux.prev != null) {
				aux.prev.next = aux.next;
				aux.next.prev = aux.prev;
				aux.prev = null;
				aux.next = null;
			} else {
				this.head = aux.next;
				aux.next.prev = null;
				aux.next = null; 
			}
			
			this.i--;
			return aux.instr;
		} else
			return null;
	}

	@Override
	public String toString() {
		String instr = "";

		if(this.head != null) {
			Node aux = this.head;
			int pos = 0;

			while(aux != null && pos < this.i) {
				instr += aux.instr.toString() + "\n";
				aux = aux.next;
				pos++;
			}
		}

		return instr;
	}
}
