public class Bases {

	public int convert(String n, int r) {
		return Integer.parseInt(n, r);
	}

	public String append(String bits, int n) {
		if(bits.length() >= n)
			return bits;
		else {
			while(bits.length() < n)
				bits = "0" + bits;

			return bits;
		}
	}

	public String toHex(String n, int b, int r) {
		return append(Integer.toHexString(Integer.parseInt(n, r)), b).toUpperCase();
	}

	public String toHex(String op, int r, MatchesElement matcher) {
		String tmpOp = op;
		if(matcher.isDec8Bits(tmpOp) || matcher.isDec16Bits(tmpOp)) {
			return toHex(tmpOp, r, 10);
		} else if(matcher.isOct8Bits(tmpOp) || matcher.isOct16Bits(tmpOp)) {
			tmpOp = tmpOp.replace("@", "");
			return toHex(tmpOp, r, 8);
		} else if(matcher.isHex8Bits(tmpOp) || matcher.isHex16Bits(tmpOp)) {
			tmpOp = tmpOp.replace("$", "");
			return toHex(tmpOp, r, 16);
		} else if(matcher.isBin8Bits(tmpOp) || matcher.isBin16Bits(tmpOp)) {
			tmpOp = tmpOp.replace("%", "");
			return toHex(tmpOp, r, 2);
		} else {
			return "";
		}
	}

	public String c2(String bits, int n) {
		if(bits.length() > n) {
			StringBuilder toNbits = new StringBuilder(bits);
			toNbits.delete(0, bits.length() - n);
			bits = toNbits.toString();
		}
		return bits;
	}

}
