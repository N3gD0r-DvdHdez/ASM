import java.util.regex.Pattern;

public class MatchesElement {

	private Pattern label;
	private Pattern codop, codopR;
	private Pattern codopDot, codopDotR;
	private Pattern operandoIMM, operandoDIR, operandoEXT, operandoREL;
	private Pattern operandoIDX5b, operandoIDXPPDI, operandoIDXACUM, operandoIDX1, operandoIDX2;
	private Pattern operando_IDX2, operandoD_IDX;
	private Pattern HEX, OCT, BIN, DEC;
	private Pattern directOrg, direcENS1B, direcENS2B, direcREM1B, direcREM2B, direcEqu, direcFCC;

	public MatchesElement() {
		this.label = Pattern.compile("^[a-zA-Z]+[a-zA-Z0-9_]*\\:$");
		this.codop = Pattern.compile("^[a-zA-Z]{1,5}");
		
		this.operandoIMM = Pattern.compile("^#[$@%]?[0-9a-fA-F]+");
		this.operandoDIR = Pattern.compile("^([0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$|^[$@%]([a-fA-F0-9]{2}|[0-7]{2}|[1-3][0-7]{2}|[01]{8})$");
		this.operandoEXT = Pattern.compile("^(25[6-9]|2[6-9][0-9]|[3-9][0-9]{2}|[1-9][0-9]{3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$|(^[$@%]([a-fA-F0-9]{3,4}|[01]{9,16}|([4-7][0-7]{2}|[1-7][0-7]{6}))$)|(^[a-zA-Z]+[a-zA-Z0-9_]*$)");

		this.operandoIDX5b = Pattern.compile("(([0-9]|1[0-5]|-[1-9]|-1[0-6]),?([XxYy]|(SP|Sp|sp|sP)?|(PC|Pc|pc|pC)?)|,?([XxYy]|(SP|Sp|sp|sP)?|(PC|Pc|pc|pC)?))");
		this.operandoIDXPPDI = Pattern.compile("([1-8]),?(([\\-+][XxYy]|[\\-+](SP|Sp|sp|sP)?)|([XxYy][\\-+]|(SP|Sp|sp|sP)?[\\-+]))");
		this.operandoIDXACUM = Pattern.compile("^[abdABD],([XxYy]|[sS][pP]|[pP][cC])$");

		this.operandoIDX1 = Pattern.compile("^((1[6-9]|[2-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])|(-(1[7-9]|[2-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-6]))),([XxYy]|[sS][pP]|[pP][cC])$");
		this.operandoIDX2 = Pattern.compile("^(25[6-9]|2[6-9][0-9]|[3-9][0-9]{2}|[1-9][0-9]{3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5]),([XxYy]|[sS][pP]|[pP][cC])$");

		this.operando_IDX2 = Pattern.compile("^\\[([0-9]|[1-9][0-9]{1,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5]),([XxYy]|[sS][pP]|[pP][cC])\\]$");
		this.operandoD_IDX = Pattern.compile("^\\[[dD],([XxYy]|[sS][pP]|[pP][cC])\\]$");

		this.operandoREL = Pattern.compile("[a-zA-Z]+[a-zA-Z0-9_]*");

		this.HEX = Pattern.compile("\\$(0)*[0-9a-fA-F]+");
		this.OCT = Pattern.compile("@(0)*[0-7]+");
		this.BIN = Pattern.compile("%(0)*[0-1]+");
		this.DEC = Pattern.compile("(0)*[0-9]+");

		this.directOrg = Pattern.compile("(ORG|END)");

		this.direcENS1B = Pattern.compile("(DB)|(DC\\.B)|(FCB)");
		this.direcENS2B = Pattern.compile("(DW)|(DC\\.W)|(FDB)");
		this.direcFCC = Pattern.compile("(FCC)");

		this.direcREM1B = Pattern.compile("(DS)|(DS\\.B)|(RMB)");
		this.direcREM2B = Pattern.compile("(DS\\.W)|(RMW)");

		this.direcEqu = Pattern.compile("(EQU)");

	}

	public boolean is8Bits(String op) {
		if(isDec8Bits(op)) return true;
		else if(isOct8Bits(op)) return true;
		else if(isHex8Bits(op)) return true;
		else if(isBin8Bits(op)) return true;
		else return false;
	}

	public boolean is16Bits(String op) {
		if(isDec16Bits(op)) return true;
		else if(isOct16Bits(op)) return true;
		else if(isHex16Bits(op)) return true;
		else if(isBin16Bits(op)) return true;
		else return false;
	}

	public boolean isDirCodMaq(String direct) {
		if(this.direcENS1B.matcher(direct).matches()) return true;
		else if(this.direcENS2B.matcher(direct).matches()) return true;
		else if(this.direcREM1B.matcher(direct).matches()) return false;
		else if(this.direcREM2B.matcher(direct).matches()) return false;
		else if(this.direcEqu.matcher(direct).matches()) return false;
		else if(this.direcFCC.matcher(direct).matches()) return true;
		else if(this.codop.matcher(direct).matches()) return true;
		else return false;
	}

	public boolean isDir(String direct) {
		if(this.directOrg.matcher(direct).matches()) return true;
		else if(this.direcENS1B.matcher(direct).matches()) return true;
		else if(this.direcENS2B.matcher(direct).matches()) return true;
		else if(this.direcREM1B.matcher(direct).matches()) return true;
		else if(this.direcREM2B.matcher(direct).matches()) return true;
		else if(this.direcEqu.matcher(direct).matches()) return true;
		else if(this.direcFCC.matcher(direct).matches()) return true;
		else return false;
	}

	public boolean isDirOp(String direct) {
		if(this.HEX.matcher(direct).matches()) return true;
		else if(this.DEC.matcher(direct).matches()) return true;
		else if(this.OCT.matcher(direct).matches()) return true;
		else if(this.BIN.matcher(direct).matches()) return true;
		else if(direct.matches("\"[\\u0020-\\u007e]*\"")) return true;
		else return false;
	}

	public boolean isOp(String op) {
		if(this.operandoIMM.matcher(op).matches()) return true;
		else if(this.operandoDIR.matcher(op).matches()) return true;
		else if(this.operandoEXT.matcher(op).matches()) return true;
		else if(this.operandoIDX5b.matcher(op).matches()) return true;
		else if(this.operandoIDXPPDI.matcher(op).matches()) return true;
		else if(this.operandoIDXACUM.matcher(op).matches()) return true;
		else if(this.operandoIDX1.matcher(op).matches()) return true;
		else if(this.operandoIDX2.matcher(op).matches()) return true;
		else if(this.operando_IDX2.matcher(op).matches()) return true;
		else if(this.operandoD_IDX.matcher(op).matches()) return true;
		else if(this.operandoREL.matcher(op).matches()) return true;
		else return false;
	}

	public boolean isDec8Bits(String value) {
		if(this.DEC.matcher(value).matches() && (Integer.parseInt(value) >= 0 && Integer.parseInt(value) < 256)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isDec16Bits(String value) {
		if(this.DEC.matcher(value).matches() && (Integer.parseInt(value) > 255 && Integer.parseInt(value) < 65536)){
			return true;
		} else {
			return false;
		}
	}

	public boolean isBin8Bits(String value) {
		if(this.BIN.matcher(value).matches()) {
			String tmpvalue = value.replaceAll("^%(0)+|^%", "");
			if(tmpvalue.length() < 9 && tmpvalue.length() > 0)
				return true;
			else
				return false;
		} else
			return false;
	}

	public boolean isBin16Bits(String value) {
		if(this.BIN.matcher(value).matches()) {
			String tmpvalue = value.replaceAll("^%(0)+|^%", "");
			if(tmpvalue.length() < 17 && tmpvalue.length() > 8)
				return true;
			else
				return false;
		} else
			return false;
	}

	public boolean isOct8Bits(String value) {
		if(this.OCT.matcher(value).matches()) {
			String tmpvalue = value.replace("@", "");
			return isBin8Bits(octalToBin(tmpvalue));
		} else
			return false;
	}

	public boolean isOct16Bits(String value) {
		if(this.OCT.matcher(value).matches()) {
			String tmpvalue = value.replace("@", "");
			return isBin16Bits(octalToBin(tmpvalue));
		} else
			return false;
	}

	public boolean isHex8Bits(String value) {
		if(this.HEX.matcher(value).matches()) {
			String tmpvalue = value.replace("$", "");
			return isBin8Bits(hexToBin(tmpvalue));
		} else
			return false;
	}

	public boolean isHex16Bits(String value) {
		if(this.HEX.matcher(value).matches()) {
			String tmpvalue = value.replace("$", "");
			return isBin16Bits(hexToBin(tmpvalue));
		} else
			return false;
	}

	private String octalToBin(String value) {
		int octalValue = Integer.parseInt(value, 8);
		return "%" + Integer.toBinaryString(octalValue);
	}

	private String hexToBin(String value) {
		int hexValue = Integer.parseInt(value, 16);
		return "%" + Integer.toBinaryString(hexValue);
	}

	public boolean isCodop(String cop) {
		return this.codop.matcher(cop).matches();
	}

	public boolean isLabel(String lab) {
		return this.label.matcher(lab).matches();
	}

	public boolean isIMM(String mode) {
		return this.operandoIMM.matcher(mode).matches();
	}

	public boolean isDIR(String mode) {
		return this.operandoDIR.matcher(mode).matches();
	}

	public boolean isEXT(String mode) {
		return this.operandoEXT.matcher(mode).matches();
	}

	public boolean isREL(String mode) {
		return this.operandoREL.matcher(mode).matches();
	}

	public boolean isIDX5b(String mode) {
		return this.operandoIDX5b.matcher(mode).matches();
	}

	public boolean isIDXPPDI(String mode) {
		return this.operandoIDXPPDI.matcher(mode).matches();
	}

	public boolean isIDXACUM(String mode) {
		return this.operandoIDXACUM.matcher(mode).matches();
	}

	public boolean isIDX1(String mode) {
		return this.operandoIDX1.matcher(mode).matches();
	}

	public boolean isIDX2(String mode) {
		return this.operandoIDX2.matcher(mode).matches();
	}

	public boolean is_IDX2(String mode) {
		return this.operando_IDX2.matcher(mode).matches();
	}

	public boolean isD_IDX(String mode) {
		return this.operandoD_IDX.matcher(mode).matches();
	}

	public boolean isDirOrg(String direct) {
		return this.directOrg.matcher(direct).matches();
	}

	public boolean isDirEqu(String direct) {
		return this.direcEqu.matcher(direct).matches();
	}

	public boolean isDirFcc(String direct) {
		return this.direcFCC.matcher(direct).matches();
	}

	public boolean isDirEns1B(String direct) {
		return this.direcENS1B.matcher(direct).matches();
		
	}

	public boolean isDirEns2B(String direct) {
		return this.direcENS2B.matcher(direct).matches();
		
	}

	public boolean isDirRem1B(String direct) {
		return this.direcREM1B.matcher(direct).matches();
	}

	public boolean isDirRem2B(String direct) {
		return this.direcENS2B.matcher(direct).matches();
	}

}
