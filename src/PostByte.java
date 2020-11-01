public class PostByte {
	private String operando;
	private String codMaq;

	public PostByte() {

	}

	public PostByte(String op) {
		this.operando = op;
		this.codMaq = "";
	}

	private String removeBrack(String op) {
		String tmp = op;
		tmp = tmp.replace("[", "");
		tmp = tmp.replace("]", "");
		return tmp;
	}

	public String calculate(MatchesElement matcher, Bases conv) {
		String tmpop = removeBrack(this.operando);
		String [] tmp = tmpop.split(",");
		String r = tmp[1];
		int rr = -1;

		if(matcher.isIDX5b(this.operando)) {
			int value = Integer.parseInt(tmp[0], 10);
			rr = xb(r);
			if(rr > -1) {
				String bits = conv.append(Integer.toBinaryString(value), 5);

				bits = conv.c2(bits, 5);
				String binHex = conv.append(Integer.toBinaryString(rr), 2) + "0" + bits;
				String hex = conv.append(Integer.toHexString(Integer.parseInt(binHex, 2)).toUpperCase(), 2);
				this.codMaq += hex;
			}

			return this.codMaq;

		} else if(matcher.isIDXPPDI(this.operando)) {
			String pp = "";
			int value = Integer.parseInt(tmp[0], 10);

			
			if(r.startsWith("+")) {
				pp = "10";
				value -= 1;
			} else if(r.startsWith("-")){
				pp = "10";
				value *= -1;
			} else if(r.endsWith("+")) {
				pp = "11";
				value -= 1;
			} else if(r .endsWith("-")) {
				pp = "11";
				value *= -1;
			}
			
			r = r.replaceAll("\\+|-", "");
			rr = xb(r);

			if(rr > -1) {
				String bits = conv.append(Integer.toBinaryString(value), 4);
				bits = conv.c2(bits, 4);
				String binHex = conv.append(Integer.toBinaryString(rr), 2) + pp + bits;
				this.codMaq += Integer.toHexString(Integer.parseInt(binHex, 2)).toUpperCase();
			} 
			return this.codMaq;
		
		} else if(matcher.isIDXACUM(this.operando)) {
			int value = aa(tmp[0]);
			rr = xb(r);

			if(rr > -1) {
				String bits = conv.append(Integer.toBinaryString(value), 2);
				String binHex = "111" + conv.append(Integer.toBinaryString(rr), 2) + "1" + bits;
				this.codMaq += Integer.toHexString(Integer.parseInt(binHex, 2)).toUpperCase();
			}
			return this.codMaq;

		} else if(matcher.isIDX1(this.operando)) {
			int value = Integer.parseInt(tmp[0], 10);
			rr = xb(r);
			String zs = "00";
			if(value < 0)
				zs = "01";
			else
				zs = "00";

			if(rr > -1) {
				String bits = conv.append(Integer.toBinaryString(value), 8);
				bits = conv.c2(bits, 8);
				String binHex = conv.append(Integer.toBinaryString(rr), 2);
				this.codMaq += Integer.toHexString(Integer.parseInt("111" + binHex + "0" + zs + bits.toUpperCase(), 2)).toUpperCase();
			}

			return this.codMaq;

		} else if(matcher.isIDX2(this.operando)) {
			int value = Integer.parseInt(tmp[0], 10);
			rr = xb(r);
			String zs = "10";
			if(value < 0)
				zs = "11";
			else
				zs = "10";

			if(rr > -1) {
				String bits = conv.append(Integer.toBinaryString(value), 16);
				bits = conv.c2(bits, 16);
				String binHex = conv.append(Integer.toBinaryString(rr), 2);
				this.codMaq += Integer.toHexString(Integer.parseInt("111" + binHex + "0" + zs + bits.toUpperCase(), 2)).toUpperCase();
			}

			return this.codMaq;

		} else if(matcher.is_IDX2(this.operando)) {
			int value = Integer.parseInt(tmp[0], 10);
			rr = xb(r);
			String zs = "11";
			if(rr > -1) {
				String bits = conv.append(Integer.toBinaryString(value), 16);
				bits = conv.c2(bits, 16);
				String binHex = conv.append(Integer.toBinaryString(rr), 2);
				this.codMaq += Integer.toHexString(Integer.parseInt("111" + binHex + "0" + zs + bits.toUpperCase(), 2)).toUpperCase();
			}

			return this.codMaq;

		} else if(matcher.isD_IDX(this.operando)) {
			int value = aa(tmp[0]) + 1;
			rr = xb(r);

			if(rr > -1) {
				String bits = conv.append(Integer.toBinaryString(value), 2);
				String binHex = "111" + conv.append(Integer.toBinaryString(rr), 2) + "1" + bits;
				this.codMaq += Integer.toHexString(Integer.parseInt(binHex, 2)).toUpperCase();
			}
			return this.codMaq;
		
		}
		return this.codMaq;
	}

	private int xb(String r) {
		switch(r) {
			case "x":
			case "X":
				return 0;
			case "y":
			case "Y":
				return 1;
			case "sp":
			case "sP":
			case "Sp":
			case "SP":
				return 2;
			case "pc":
			case "pC":
			case "Pc":
			case "PC":
				return 3;
			default:
				return -1;
		}
	}

	private int aa(String a) {
		switch(a) {
			case "a":
			case "A":
				return 0;
			case "b":
			case "B":
				return 1;
			case "d":
			case "D":
				return 2;
			default:
				return -1;
		}
	}
}