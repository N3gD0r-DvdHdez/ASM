public class Directivas {

	private boolean oneORG;
	private Files files;

	public Directivas(Files initFiles) throws Exception {
		this.oneORG = false;
		this.files = initFiles;
	}

	public Instruccion processDirectiva(Instruccion instr, MatchesElement matcher, ContLoc contloc, Bases conv) throws RuntimeErrors{
		switch(instr.getCodop()) {
			case "ORG":
				if(!oneORG && (instr.getLabel().equals("") && matcher.isDirOp(instr.getOp()))) {
					oneORG = true;
					String tmpOp = instr.getOp();
					if(matcher.isDec8Bits(tmpOp) || matcher.isDec16Bits(tmpOp)) {
						System.out.println("\t" + instr.getCodop() +"\t" + instr.getOp() + "\tDirectiva ORG ");
						contloc.setDirInit(tmpOp, 10);
						contloc.setContLoc(tmpOp, 10);
						String value = conv.append(contloc.getContLoc(), 4);
						instr.setContloc(value);
						files.writeTmp(instr, "DIR-INIT");
						return instr;
					} else if(matcher.isOct8Bits(tmpOp) || matcher.isOct16Bits(tmpOp)) {
						System.out.println("\t" + instr.getCodop() +"\t" + instr.getOp() + "\tDirectiva ORG ");
						tmpOp = tmpOp.replace("@", "");
						contloc.setDirInit(tmpOp, 8);
						contloc.setContLoc(tmpOp, 8);
						String value = conv.append(contloc.getContLoc(), 4);
						instr.setContloc(value);
						files.writeTmp(instr, "DIR-INIT");
						return instr;
					} else if(matcher.isHex8Bits(tmpOp) || matcher.isHex16Bits(tmpOp)) {
						System.out.println("\t" + instr.getCodop() +"\t" + instr.getOp() + "\tDirectiva ORG ");
						tmpOp = tmpOp.replace("$", "");
						contloc.setDirInit(tmpOp, 16);
						contloc.setContLoc(tmpOp, 16);
						String value = conv.append(contloc.getContLoc(), 4);
						instr.setContloc(value);
						files.writeTmp(instr, "DIR-INIT");
						return instr;
					} else if(matcher.isBin8Bits(tmpOp) || matcher.isBin16Bits(tmpOp)) {
						System.out.println("\t" + instr.getCodop() +"\t" + instr.getOp() + "\tDirectiva ORG ");
						tmpOp = tmpOp.replace("%", "");
						contloc.setDirInit(tmpOp, 2);
						contloc.setContLoc(tmpOp, 2);
						String value = conv.append(contloc.getContLoc(), 4);
						instr.setContloc(value);
						files.writeTmp(instr, "DIR-INIT");
						return instr;
					}
				}
				break;

			case "EQU": // etiqueta
				if((!instr.getLabel().equals("") && !instr.getOp().equals("")) && files.repeated(instr)) {
					System.out.println(instr.getLabel() + "\t" + instr.getCodop() +"\t" + instr.getOp() + "\tDirectiva EQU ");
					String tmpOp = conv.toHex(instr.getOp(), 4, matcher);
					instr.setContloc(tmpOp);
					files.writeTmp(instr, "VALOR-EQU");
					return instr;
					
				} else if(!files.repeated(instr))
					throw new RuntimeErrors(2);
				break;
			
			case "FCC": // chars
				if(oneORG){
					System.out.println(instr.getLabel() + "\t" + instr.getCodop() +"\t" + instr.getOp() + "\tDirectiva constante de caracteres ");
					String tmpOp = instr.getOp();
					String value = conv.append(contloc.getContLoc(), 4);
					instr.setContloc(value);
					files.writeTmp(instr, "CONTLOC");

					contloc.incFCC(tmpOp.replace("\"", ""));
					String codmaq = "";
					for(int i = 1; i < tmpOp.length() - 1; i++){
						codmaq += Integer.toHexString(tmpOp.codePointAt(i));
					}
					instr.setCodMaq(codmaq.toUpperCase());
					return instr;
				}
				break;

			case "DC.B": // 1 byte			
			case "FCB": // 1 byte		
			case "DB": // 1 byte
				if(oneORG){
					System.out.println(instr.getLabel() + "\t" + instr.getCodop() +"\t" + instr.getOp() + "\tDirectiva constante 1 byte");
					String tmpOp = conv.toHex(instr.getOp(), 2, matcher);
					String value = conv.append(contloc.getContLoc(), 4);
					instr.setContloc(value);
					files.writeTmp(instr, "CONTLOC");
					contloc.incB1();
					
					if(matcher.is8Bits(instr.getOp()))
						instr.setCodMaq(tmpOp);
					else
						throw new RuntimeErrors(3);

					return instr;
				}
				break;

			case "FDB": // 2 bytes
			case "DC.W": // 2 bytes
			case "DW": // 2 bytes
				if(oneORG){
					System.out.println(instr.getLabel() + "\t" + instr.getCodop() +"\t" + instr.getOp() + "\tDirectiva constante 2 byte");
					String tmpOp = conv.toHex(instr.getOp(), 4, matcher);
					String value = conv.append(contloc.getContLoc(), 4);
					instr.setContloc(value);
					files.writeTmp(instr, "CONTLOC");
					contloc.incB2();
					
					if(matcher.is16Bits(instr.getOp()) || matcher.is8Bits(instr.getOp()))
						instr.setCodMaq(tmpOp);
					else {
						throw new RuntimeErrors(3);
					}

					return instr;
				}
				break;

			case "DS": // REM 1 byte
			case "DS.B": // REM 1 byte
			case "RMB": // REM 1 byte
				if(oneORG){
					String tmpOp = instr.getOp();
					if(matcher.isDec8Bits(tmpOp)) {
						System.out.println(instr.getLabel() + "\t" + instr.getCodop() +"\t" + instr.getOp() + "\tDirectiva reserva de memoria 1 byte");
						String value = conv.append(contloc.getContLoc(), 4);
						instr.setContloc(value);
						files.writeTmp(instr, "CONTLOC");
						contloc.incB1REM(Integer.parseInt(tmpOp));
						return instr;
					} else if(matcher.isOct8Bits(tmpOp)) {
						System.out.println(instr.getLabel() + "\t" + instr.getCodop() +"\t" + instr.getOp() + "\tDirectiva reserva de memoria 1 byte");
						tmpOp = tmpOp.replace("@", "");
						String value = conv.append(contloc.getContLoc(), 4);
						instr.setContloc(value);
						files.writeTmp(instr, "CONTLOC");
						contloc.incB1REM(Integer.parseInt(tmpOp, 8));
						return instr;
					} else if(matcher.isHex8Bits(tmpOp)) {
						System.out.println(instr.getLabel() + "\t" + instr.getCodop() +"\t" + instr.getOp() + "\tDirectiva reserva de memoria 1 byte");
						tmpOp = tmpOp.replace("$", "");
						String value = conv.append(contloc.getContLoc(), 4);
						instr.setContloc(value);
						files.writeTmp(instr, "CONTLOC");
						contloc.incB1REM(Integer.parseInt(tmpOp, 16));
						return instr;
					} else if(matcher.isBin8Bits(tmpOp)) {
						System.out.println(instr.getLabel() + "\t" + instr.getCodop() +"\t" + instr.getOp() + "\tDirectiva reserva de memoria 1 byte");
						tmpOp = tmpOp.replace("%", "");
						String value = conv.append(contloc.getContLoc(), 4);
						instr.setContloc(value);
						files.writeTmp(instr, "CONTLOC");
						contloc.incB1REM(Integer.parseInt(tmpOp, 2));
						return instr;
					} else {
						throw new RuntimeErrors(3);
					}
				}
				break;

			case "DS.W": // REM 2 bytes
			case "RMW": // REM 2 bytes
				if(oneORG){
					String tmpOp = instr.getOp();
					if(matcher.isDec8Bits(tmpOp) || matcher.isDec16Bits(tmpOp)) {
						System.out.println(instr.getLabel() + "\t" + instr.getCodop() +"\t" + instr.getOp() + "\tDirectiva reserva de memoria 2 bytes");
						String value = conv.append(contloc.getContLoc(), 4);
						instr.setContloc(value);
						files.writeTmp(instr, "CONTLOC");
						contloc.incB2REM(Integer.parseInt(tmpOp));
						return instr;
					} else if(matcher.isOct8Bits(tmpOp) || matcher.isOct16Bits(tmpOp)) {
						System.out.println(instr.getLabel() + "\t" + instr.getCodop() +"\t" + instr.getOp() + "\tDirectiva reserva de memoria 2 bytes");
						tmpOp = tmpOp.replace("@", "");
						String value = conv.append(contloc.getContLoc(), 4);
						instr.setContloc(value);
						files.writeTmp(instr, "CONTLOC");
						contloc.incB2REM(Integer.parseInt(tmpOp, 8));
						return instr;
					} else if(matcher.isHex8Bits(tmpOp) || matcher.isHex16Bits(tmpOp)) {
						System.out.println(instr.getLabel() + "\t" + instr.getCodop() +"\t" + instr.getOp() + "\tDirectiva reserva de memoria 2 bytes");
						tmpOp = tmpOp.replace("$", "");
						String value = conv.append(contloc.getContLoc(), 4);
						instr.setContloc(value);
						files.writeTmp(instr, "CONTLOC");
						contloc.incB2REM(Integer.parseInt(tmpOp, 16));
						return instr;
					} else if(matcher.isBin8Bits(tmpOp) || matcher.isBin16Bits(tmpOp)) {
						System.out.println(instr.getLabel() + "\t" + instr.getCodop() +"\t" + instr.getOp() + "\tDirectiva reserva de memoria 1 bytes");
						tmpOp = tmpOp.replace("%", "");
						String value = conv.append(contloc.getContLoc(), 4);
						instr.setContloc(value);
						files.writeTmp(instr, "CONTLOC");
						contloc.incB2REM(Integer.parseInt(tmpOp, 2));
						return instr;
					} else {
						throw new RuntimeErrors(3);
					}
				}
				break;

			case "END":
				if(oneORG && (instr.getLabel().equals("") && instr.getOp().equals(""))) {
					System.out.println(instr.getLabel() + "\t" + instr.getCodop() +"\t" + instr.getOp() + "\tDirectiva END");
					String value = conv.append(contloc.getContLoc(), 4);
					instr.setContloc(value);
					files.writeTmp(instr, "CONTLOC");
					return instr;
				}	
				break;
		}
		return null;
	}
}