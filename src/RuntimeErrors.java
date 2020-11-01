public class RuntimeErrors extends Exception {
	private String error;

	public RuntimeErrors(int i) {
		this.error = "";

		switch(i) {
			case 1:
				this.error = "CodopNotFound";
				break;
			case 2:
				this.error = "LabelAlreadyExists";
				break;
			case 3:
				this.error = "ErrorInOperand";
				break;
			case 4:
				this.error = "SymbolNotFound";
				break;
		}
	}

	public RuntimeErrors(String type) {
		this.error = type;
	}

	public String getMessage() {
		return this.error;
	}
}