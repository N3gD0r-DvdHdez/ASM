public class Instruccion {
	private String etiqueta;
	
	private String codop;
	private String contloc;

	private String operando;

	private String comentario;
	
	private boolean hasOp;

	private String addrMode;
	
	private String codMaq;

	private int bytesCalc;
	private int totalBytes;

	public Instruccion() {
		this.etiqueta = "";
		this.codop = "";
		this.contloc = "";
		this.operando = "";
		this.comentario = "";
		this.addrMode = "";
		this.codMaq = "";
		this.bytesCalc = 0;
		this.totalBytes = 0;
		this.hasOp = false;
	}

	public Instruccion(String label, String direct, String op) {
		this.etiqueta = label;
		this.contloc = direct;
		this.operando = op;
		this.comentario = "";
		this.codop = "";
		this.addrMode = "";
		this.codMaq = "";
		this.bytesCalc = 0;
		this.totalBytes = 0;
		this.hasOp = false;
	}

	public Instruccion(String cod, String op, String addrMode, String codMaq, int bytesC, int ttlBytes) {
		this.etiqueta = "";
		this.codop = cod;
		this.contloc = "";
		this.operando = op;
		this.comentario = "";
		this.addrMode = addrMode;
		this.codMaq = codMaq;
		this.bytesCalc = bytesC;
		this.totalBytes = ttlBytes;
		this.hasOp = false;
	}

	public Instruccion(String label, String cod, String op, String addrMode, String codMaq, int bytesC, int ttlBytes) {
		this.etiqueta = label;
		this.codop = cod;
		this.contloc = "";
		this.operando = op;
		this.comentario = "";
		this.addrMode = addrMode;
		this.codMaq = codMaq;
		this.bytesCalc = bytesC;
		this.totalBytes = ttlBytes;
		this.hasOp = false;
	}

	public Instruccion(String [] instr, String op) {
		if(instr.length == 6) {
			this.etiqueta = "";
			this.codop = instr[0];
			this.contloc = "";
			this.operando = op;
			this.comentario = "";
			this.addrMode = instr[2];
			this.codMaq = instr[3];
			this.bytesCalc = Integer.parseInt(instr[4]);
			this.totalBytes = Integer.parseInt(instr[5]);
			this.hasOp = instr[1].equals("1");
		}
	}

	public Instruccion(String [] instr, String op, String label) {
		if(instr.length == 6) {
			this.etiqueta = label;
			this.codop = instr[0];
			this.contloc = "";
			this.hasOp = instr[1].equals("1");
			this.operando = op;
			this.comentario = "";
			this.addrMode = instr[2];
			this.codMaq = instr[3];
			this.bytesCalc = Integer.parseInt(instr[4]);
			this.totalBytes = Integer.parseInt(instr[5]);
		}
	}

	public Instruccion(String [] instr, String op, String label, String comment) {
		if(instr.length == 6) {
			this.etiqueta = label;
			this.codop = instr[0];
			this.contloc = "";
			this.hasOp = instr[1].equals("1");
			this.operando = op;
			this.comentario = comment;
			this.addrMode = instr[2];
			this.codMaq = instr[3];
			this.bytesCalc = Integer.parseInt(instr[4]);
			this.totalBytes = Integer.parseInt(instr[5]);
		}
	}

	@Override
	public String toString() {
		return this.contloc + "\t" + this.etiqueta + "\t" + this.codop + "\t" + (this.hasOp? "1" : "0") + "\t" + this.operando + (this.comentario.equals("")? "" : "\t" + this.comentario) + "\t" 
			+ this.addrMode + "\t" + this.codMaq + "\t" + this.bytesCalc + "\t" + this.totalBytes;
	}

	public String getLabel() {
		return this.etiqueta;
	}

	public String getCodop() {
		return this.codop;
	}

	public String getContloc() {
		return this.contloc;
	}

	public String getOp() {
		return this.operando;
	}

	public String getComment() {
		return this.comentario;
	}

	public boolean getHasOp() {
		return this.hasOp;
	}

	public String getAddrMode() {
		return this.addrMode;
	}

	public String getCodMaq() {
		return this.codMaq;
	}

	public int getBytesC() {
		return this.bytesCalc;
	}

	public int getTotalBytes() {
		return this.totalBytes;
	}

	public void setLabel(String label) {
		this.etiqueta = label;
	}

	public void setCodop(String cod) {
		this.codop = cod;
	}

	public void setContloc(String contloc) {
		this.contloc = contloc;
	}

	public void setOp(String op) {
		this.operando = op;
	}

	public void setComment(String comment) {
		this.comentario = comment;
	}
	
	public void setCodMaq(String codMaq) {
		this.codMaq = codMaq;
	}
}
