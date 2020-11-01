public class ContLoc {
	
	private int CONTLOC;
	private int DIR_INIT;

	public ContLoc() {
		this.CONTLOC = 0;
		this.DIR_INIT = 0;
	}

	public void incB2() {
		this.CONTLOC += 2;
	}

	public void incB1() {
		this.CONTLOC += 1;
	}

	public void incB1REM(int op) {
		this.CONTLOC += op * 1;
	}

	public void incB2REM(int op) {
		this.CONTLOC += op * 2;
	}

	public void setDirInit(String dirOp, int rad) {
		this.DIR_INIT = Integer.parseInt(dirOp, rad);
	}

	public void setContLoc(String dirOp, int rad) {
		this.CONTLOC = Integer.parseInt(dirOp, rad);
	}

	public String getDirInit() {
		return Integer.toHexString(this.DIR_INIT).toUpperCase();
	}

	public String getContLoc() {
		return Integer.toHexString(this.CONTLOC).toUpperCase();
	}

	public int contLoc() {
		return this.CONTLOC;
	}

	public void incFCC(String value) {
		this.CONTLOC += value.length();
	}

	public void processLine(Instruccion instr, Files files, Bases conv, MatchesElement matcher) {
		if(!matcher.isDir(instr.getCodop())) {
			String value = conv.append(getContLoc(), 4);
			instr.setContloc(value);
			files.writeTmp(instr, "CONTLOC");
			incB1REM(instr.getTotalBytes());
		}
	}

	public void decrease(Instruccion instr, Bases conv, MatchesElement matcher) {
		int dec = 0;
		switch(instr.getCodop()) {
			case "FCC":
				this.CONTLOC -= instr.getOp().replace("\"", "").length();
				break;
			case "DC.B": // 1 byte			
			case "FCB": // 1 byte		
			case "DB": // 1 byte
				this.CONTLOC --;
				break;
			case "FDB": // 2 bytes
			case "DC.W": // 2 bytes
			case "DW": // 2 bytes
				this.CONTLOC -= 2;
				break;
			case "DS": // REM 1 byte
			case "DS.B": // REM 1 byte
			case "RMB": // REM 1 byte
				if(matcher.isDec8Bits(instr.getOp())) {
					dec = conv.convert(instr.getOp(), 10);
				} else if(matcher.isOct8Bits(instr.getOp())) {
					dec = conv.convert(instr.getOp().replace("@", ""), 8);
				} else if(matcher.isHex8Bits(instr.getOp())) {
					dec = conv.convert(instr.getOp().replace("$", ""), 16);
				} else if(matcher.isBin8Bits(instr.getOp())) {
					dec = conv.convert(instr.getOp().replace("%", ""), 2);
				}
				this.CONTLOC -= dec;
				break;
			case "DS.W": // REM 2 bytes
			case "RMW": // REM 2 bytes
				if(matcher.isDec8Bits(instr.getOp()) || matcher.isDec16Bits(instr.getOp())) {
					dec = conv.convert(instr.getOp(), 10);
				} else if(matcher.isOct8Bits(instr.getOp()) || matcher.isOct16Bits(instr.getOp())) {
					dec = conv.convert(instr.getOp().replace("@", ""), 8);
				} else if(matcher.isHex8Bits(instr.getOp()) || matcher.isHex16Bits(instr.getOp())) {
					dec = conv.convert(instr.getOp().replace("$", ""), 16);
				} else if(matcher.isBin8Bits(instr.getOp()) || matcher.isBin16Bits(instr.getOp())) {
					dec = conv.convert(instr.getOp().replace("%", ""), 2);
				}
				this.CONTLOC -= dec * 2;
				break;
			default:
				this.CONTLOC -= instr.getTotalBytes();
				break;
		}
	}
}
