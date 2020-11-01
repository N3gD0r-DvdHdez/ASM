import java.nio.file.Path;
import java.util.Scanner;

public class PASM {

	private static Path rutaAsm;
	private static Path rutaTabOp;
	private static Files initFiles;

	private static Scanner input = new Scanner(System.in);

	private static MatchesElement matcher;

	private static ListInstr lista;
	private static AddrMode addrmode;
	private static Directivas directivas;
	private static ContLoc contloc;
	private static Bases conv;

	public static void init() throws Exception {
		lista = new ListInstr();
		matcher = new MatchesElement();
		initFiles = new Files(rutaAsm);
		contloc = new ContLoc();
		directivas = new Directivas(initFiles);
		conv = new Bases();
	}

	private static void showHelp() { // Imprimir ayuda
		System.out.println("Compruebe los argumentos como lo siguiente:");
		System.out.println("java <PROGRAMA> <RUTADELASM.TXT> <TABLAOPERACIONES.TXT>");
		System.exit(1);
	}

	private static String compresTab(String comment) {
		if(comment.contains("\t")) {
			comment = comment.replaceAll("\t", " ");
		}
		return comment.toLowerCase();
	}

	private static Instruccion splitElements(String [] elements) {
		String label = "";
		String codop = "";
		String op = "";
		String comment = "";

		for(int i = 0; i < elements.length; i++) {
			if(matcher.isLabel(elements[i]) && i == 0)
				label = elements[i].replace(":", "").toUpperCase();
			else if((matcher.isCodop(elements[i]) || matcher.isDir(elements[i])) && (i == 0 || i == 1)) {
				codop = elements[i].toUpperCase();
				if(elements.length >= 2 && i == 0) {
					if(matcher.isOp(elements[i + 1]) || matcher.isDirOp(elements[i + 1])) {
						if(matcher.isLabel(elements[i + 1] + ":"))
							elements[i + 1] = elements[i + 1].toUpperCase();

						if(elements[i + 1].startsWith(",")) elements[i + 1] = elements[i + 1].replace(",", "0,").toUpperCase();	
						op = elements[i + 1];
						i++;
					}
				} else if(elements.length >= 3 && i == 1) {
					if(matcher.isOp(elements[i + 1]) || matcher.isDirOp(elements[i + 1])){
						if(elements[i + 1].startsWith(",")) elements[i + 1] = elements[i + 1].replace(",", "0,");
						op = elements[i + 1];
						i++;
					}
				}
			} else if(elements[i].startsWith(";") && elements[i].length() <= 80)
				comment = elements[i];
		}
		Instruccion instr = new Instruccion(label, codop, op, "", "", 0, 0);
		instr.setComment(comment);
		return instr;
	}

	private static void fase1(String [] elements) {
		Instruccion instr = splitElements(elements);

		try(Scanner file = new Scanner(rutaTabOp)) {
			addrmode = new AddrMode(file, directivas, contloc, conv);

			addrmode.find(matcher, instr);
			instr = addrmode.processAddrMode(matcher);

			initFiles.flush();

			if(instr != null) {
				lista.add(instr);
				contloc.processLine(instr, initFiles, conv, matcher);

				if(!instr.getLabel().equals("")) {

					if(initFiles.verifyLabel(instr, conv, contloc, matcher)) {

						String type = "";

						if(instr.getCodop().equals("EQU")){
							type = "EQU-ET-ABOSOLUTA";
							String tmpOp = conv.toHex(instr.getOp(), 4, matcher);
							initFiles.writeTabSim(instr, type, tmpOp);
						} else {
							type = "CONTLOC-ET-RELATIVA";
							initFiles.writeTabSim(instr, type, instr.getContloc());
						}

					} else {
						contloc.decrease(lista.pop(), conv, matcher);
						throw new RuntimeErrors(2);
					}
				}
			}
		} catch (RuntimeErrors err) {
			System.out.println(instr.getLabel() + "\t" + instr.getCodop() + "\t" + instr.getOp() + "\t" + err.getMessage());
		} catch(Exception ex) {
			ex.printStackTrace();
			System.out.println("Ha ocurrido un error, compruebe la integridad del archivo TABOP");
			System.out.print("Ingrese la ruta del archivo TABOP: ");

			rutaTabOp = java.nio.file.Paths.get(input.nextLine());
		}
	}

	private static void fase2() throws java.io.IOException, Exception, RuntimeErrors {
		if(!lista.isEmpty()) {
			initFiles.tmpFile();

			System.out.print("\n\t\t\t\tINSTRUCCIONES PROCESADAS CORRECTAMENTE\n\n" + lista.toString());

			Path filetmp = initFiles.getRTmp();

			for(int i = 0; i < lista.size(); i++) {
				Instruccion instr = lista.get(i);
				String label = instr.getLabel().equals("")? "NULL" : instr.getLabel();
				String codop = instr.getCodop();
				String op = instr.getOp().equals("")? "NULL" : instr.getOp();
				String codMaq = instr.getCodMaq();
				String line = "";

				PostByte xb;

				if(matcher.isDir(instr.getCodop())) {

					if(instr.getCodop().equals("ORG")) {
						line = "DIR-INIT" + "\t" + instr.getContloc() + "\t" + label + "\t" + codop + "\t" + op;
					} else if(instr.getCodop().equals("EQU")) {
						line = "VALOR-EQU" + "\t" + instr.getContloc() + "\t" + label + "\t" + codop + "\t" + op;
					} else {
						line = "CONTLOC" + "\t" + instr.getContloc() + "\t"+ label + "\t" + codop + "\t" + op;
					}

					initFiles.writeObj(instr, conv, matcher);					
					initFiles.writeTmpFile(line, codMaq);
				} else if(matcher.isCodop(instr.getCodop())) {
					switch(instr.getAddrMode()) {
						case "INH":
							line = "CONTLOC" + "\t" + instr.getContloc() + "\t"+ label + "\t" + codop + "\t" + op;
							initFiles.writeTmpFile(line, codMaq);
							break;

						case "IMM":
							line = "CONTLOC" + "\t" + instr.getContloc() + "\t"+ label + "\t" + codop + "\t" + op;
							codMaq += conv.toHex(op.replace("#", ""), instr.getTotalBytes(), matcher);
							instr.setCodMaq(codMaq);
							initFiles.writeTmpFile(line, codMaq);
							break;

						case "DIR":
							line = "CONTLOC" + "\t" + instr.getContloc() + "\t"+ label + "\t" + codop + "\t" + op;
							codMaq += conv.toHex(op, instr.getTotalBytes(), matcher);
							instr.setCodMaq(codMaq);
							initFiles.writeTmpFile(line, codMaq);
							break;

						case "EXT":
							try {
								line = "CONTLOC" + "\t" + instr.getContloc() + "\t"+ label + "\t" + codop + "\t" + op;

								if(matcher.isLabel(op + ":")) {
									codMaq += initFiles.getSymb(op);
								} else {
									codMaq += conv.toHex(op, 4, matcher);
								}
								instr.setCodMaq(codMaq);
								initFiles.writeTmpFile(line, codMaq);
							} catch (RuntimeErrors ex) {
								System.out.println(instr.getContloc() + "\t" + label + "\t" + codop + "\t" + "\t" + op + "\t" + ex.getMessage());
								System.out.println("Corrija la instruccion y vuelva a correr el programa");
								System.exit(4);
							}
							break;

						case "IDX":
							xb = new PostByte(op);
							line = "CONTLOC" + "\t" + instr.getContloc() + "\t"+ label + "\t" + codop + "\t" + op;
							codMaq += xb.calculate(matcher, conv);
							instr.setCodMaq(codMaq);
							initFiles.writeTmpFile(line, codMaq);
							break;

						case "IDX1":
							xb = new PostByte(op);
							line = "CONTLOC" + "\t" + instr.getContloc() + "\t"+ label + "\t" + codop + "\t" + op;
							codMaq += xb.calculate(matcher, conv);
							instr.setCodMaq(codMaq);
							initFiles.writeTmpFile(line, codMaq);
							break;

						case "IDX2":
							xb = new PostByte(op);
							line = "CONTLOC" + "\t" + instr.getContloc() + "\t"+ label + "\t" + codop + "\t" + op;
							codMaq += xb.calculate(matcher, conv);
							instr.setCodMaq(codMaq);
							initFiles.writeTmpFile(line, codMaq);
							break;

						case "[IDX2]":
							xb = new PostByte(op);
							line = "CONTLOC" + "\t" + instr.getContloc() + "\t"+ label + "\t" + codop + "\t" + op;
							codMaq += xb.calculate(matcher, conv);
							instr.setCodMaq(codMaq);
							initFiles.writeTmpFile(line, codMaq);
							break;

						case "[D,IDX]":
							xb = new PostByte(op);
							line = "CONTLOC" + "\t" + instr.getContloc() + "\t"+ label + "\t" + codop + "\t" + op;
							codMaq += xb.calculate(matcher, conv);
							instr.setCodMaq(codMaq);
							initFiles.writeTmpFile(line, codMaq);
							break;

						case "REL":
							try {
								line = "CONTLOC" + "\t" + instr.getContloc() + "\t"+ label + "\t" + codop + "\t" + op;
								String dest = "-1";

								for(int pos = i; pos < lista.size() && pos + 1 < lista.size(); pos++) {
									if(lista.get(pos + 1).getCodop().equals("END")) {
										dest = lista.get(pos + 1).getContloc();
										break;
									} else if(!lista.get(pos + 1).getCodop().equals("EQU")) {
										dest = lista.get(pos + 1).getContloc();
										break;
									} else if(!lista.get(pos + 2).getCodop().equals("EQU")) {
										dest = lista.get(pos + 2).getContloc();
										break;
									}
								}

								if(dest.equals(-1))
									throw new RuntimeErrors("ContLocNotFound");

								if(matcher.isLabel(op + ":")) {
									int origen = Integer.parseInt(dest, 16);
									int destino = Integer.parseInt(initFiles.getSymb(op), 16);
									int despl = destino - origen;
									String tohexvalue = Integer.toHexString(despl).toUpperCase();

									if(instr.getBytesC() == 1 && (despl < 128 && despl > -129)) {
										if(tohexvalue.length() > 2) {
											StringBuilder to8bits = new StringBuilder(tohexvalue);
											to8bits.delete(0, tohexvalue.length() - 2);
											tohexvalue = to8bits.toString();
										} else {
											tohexvalue = conv.append(tohexvalue, 2);
										}
										codMaq += tohexvalue;
									} else if(instr.getBytesC() == 2 && (despl < 32768 && despl > -32769)) {
										if(tohexvalue.length() > 4) {
											StringBuilder to8bits = new StringBuilder(tohexvalue);
											to8bits.delete(0, tohexvalue.length() - 4);
											tohexvalue = to8bits.toString();
										} else {
											tohexvalue = conv.append(tohexvalue, 4);
										}
										codMaq += tohexvalue;
									}
									instr.setCodMaq(codMaq);
									initFiles.writeTmpFile(line, codMaq);
								} else
									throw new RuntimeErrors(4);
							} catch (RuntimeErrors ex) {
								System.out.println(instr.getContloc() + "\t" + label + "\t" + codop + "\t" + "\t" + op + "\t" + ex.getMessage());
								System.out.println("Corrija la instruccion y vuelva a correr el programa");
								System.exit(4);
							}
							break;
					}

					initFiles.writeObj(instr, conv, matcher);
				}
			}
			initFiles.endFase();
		}
	}

	public static void main(String [] args) {
		if(args.length == 2) {
			rutaAsm = java.nio.file.Paths.get(args[0]);
			rutaTabOp = java.nio.file.Paths.get(args[1]);

			try(Scanner asm = new Scanner(rutaAsm)) {
				init();
				boolean exitSuccess = false;

				while(!exitSuccess) {
					while(asm.hasNextLine()) {
						String line = asm.nextLine();
						line = line.trim();
						exitSuccess = line.matches("(END|ENd|End|end|enD|eND)"); // SI SE ENCUENTRA UN END SE TERMINA EL PROGRAMA
						if(!line.startsWith(";")) {
							if(line.contains(";")) {
								String [] tmp = line.split(";");
								tmp[1] = compresTab(tmp[1]);
								line = tmp[0] + ";" + tmp[1];
							}
							String [] elements = line.split("\t");
							fase1(elements);
						}
					}
				}
				initFiles.closeAll();
				fase2();
				System.exit(0);

			} catch(java.io.IOException ex) {
				ex.printStackTrace();
				System.out.println("Ha ocurrido en error en los archivos durante la fase 2");
				System.out.print("vuelva a ejecutar el programa");
				System.exit(2);
			} catch(Exception ex) {
				ex.printStackTrace();
				System.out.println("Ha ocurrido un error, compruebe la integridad del archivo ASM");
				System.out.print("Ingrese la ruta del archivo ASM: ");

				rutaAsm = java.nio.file.Paths.get(input.nextLine());
			}

		} else
			showHelp();
	}
}
