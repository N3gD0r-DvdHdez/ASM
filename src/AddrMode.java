import java.util.Scanner;
import java.nio.file.Path;

public class AddrMode {
	private boolean errors;
	private boolean checker;

	private Scanner TABOP; // archivo de la tabla de operaciones
	private ListInstr dataInstr; // estructura de datos específica para las instrucciones encontradas en el archivo de ejemplo

	private Directivas directivas;

	private ContLoc contLoc;
	private Bases conv;

	public AddrMode(Scanner tabop, Directivas direct, ContLoc cont, Bases conv) {
		this.TABOP = tabop;
		this.dataInstr = new ListInstr();
		this.directivas = direct;
		this.contLoc = cont;
		this.conv = conv;
		this.checker = false;
		this.errors = false;
	}

	public void find(MatchesElement matcher, Instruccion instr) throws RuntimeErrors{
		
		if(matcher.isDir(instr.getCodop())) {
			this.dataInstr.add(instr);
			errors = true;
		} else if(matcher.isCodop(instr.getCodop())) {
			while(this.TABOP.hasNextLine()) { // TIENE SIGUIENTE LINEA?
				String line = this.TABOP.nextLine(); // LINE ES IGUAL A LA LINEA DEL TABOP
				String [] element = line.split("\t"); // SEPARAMOS LA LINEA POR TABULADORES O ESPACIOS
				
				if(instr.getCodop().equals(element[0])) { // COMPROBAMOS QUE LOS codopS SEAN IGUALES PARA IDENTIFICAR LOS ELEMENTOS
					Instruccion instrAddrMode = new Instruccion(element, instr.getOp(), instr.getLabel(), instr.getComment());
					this.dataInstr.add(instrAddrMode);
					errors = true;
				}
			}
		}

		if(!errors && (!instr.getCodop().equals(""))) {
			throw new RuntimeErrors(1);
		}
	}

	public Instruccion processAddrMode(MatchesElement matcher) throws RuntimeErrors, Exception{
		if(!this.dataInstr.isEmpty()) {
			this.checker = false;

			for(int i = 0; i < this.dataInstr.size(); i++) {
				
				if(matcher.isDir(this.dataInstr.get(i).getCodop())) {
					return this.directivas.processDirectiva(this.dataInstr.get(i), matcher, this.contLoc, this.conv);
				} else {
					switch(this.dataInstr.get(i).getAddrMode()) {
						case "INH":
							System.out.println(this.dataInstr.get(i).getLabel() + "\t" + this.dataInstr.get(i).getCodop() +"\t"
										+ (this.dataInstr.get(i).getOp().equals("")? "\tInherente " + this.dataInstr.get(i).getTotalBytes() + " byte"
											: this.dataInstr.get(i).getOp() + "\tError"));
							if(this.dataInstr.get(i).getHasOp() && this.dataInstr.get(i).getOp().equals(""))
								throw new RuntimeErrors(3);
							return this.dataInstr.get(i);

						case "IMM":
							if(this.dataInstr.get(i).getHasOp() && !this.dataInstr.get(i).getOp().equals("") && matcher.isIMM(this.dataInstr.get(i).getOp())) {
								String tmpOp = this.dataInstr.get(i).getOp().replace("#", "");
								
								if(this.dataInstr.get(i).getBytesC() == 2 && matcher.isDec16Bits(tmpOp)) {
									System.out.println((this.dataInstr.get(i).getLabel().equals("")? "\t" : this.dataInstr.get(i).getLabel() + "\t") + this.dataInstr.get(i).getCodop() +"\t"
											+ this.dataInstr.get(i).getOp() + "\tInmediato " + this.dataInstr.get(i).getTotalBytes() + " bytes");
									this.checker = true;
									return this.dataInstr.get(i);
								} else if(this.dataInstr.get(i).getBytesC() == 1 && matcher.isDec8Bits(tmpOp)) {
									System.out.println((this.dataInstr.get(i).getLabel().equals("")? "\t" : this.dataInstr.get(i).getLabel() + "\t") + this.dataInstr.get(i).getCodop() +"\t"
											+ this.dataInstr.get(i).getOp() + "\tInmediato " + this.dataInstr.get(i).getTotalBytes() + " bytes");
									this.checker = true;
									return this.dataInstr.get(i);
								} else if(this.dataInstr.get(i).getBytesC() == 2 && matcher.isOct16Bits(tmpOp)) {
									System.out.println((this.dataInstr.get(i).getLabel().equals("")? "\t" : this.dataInstr.get(i).getLabel() + "\t") + this.dataInstr.get(i).getCodop() +"\t"
											+ this.dataInstr.get(i).getOp() + "\tInmediato " + this.dataInstr.get(i).getTotalBytes() + " bytes");
									this.checker = true;
									return this.dataInstr.get(i);
								} else if(this.dataInstr.get(i).getBytesC() == 1 && matcher.isOct8Bits(tmpOp)) {
									System.out.println((this.dataInstr.get(i).getLabel().equals("")? "\t" : this.dataInstr.get(i).getLabel() + "\t") + this.dataInstr.get(i).getCodop() +"\t"
											+ this.dataInstr.get(i).getOp() + "\tInmediato " + this.dataInstr.get(i).getTotalBytes() + " bytes");
									this.checker = true;
									return this.dataInstr.get(i);
								} else if(this.dataInstr.get(i).getBytesC() == 2 && matcher.isHex16Bits(tmpOp)) {
									System.out.println((this.dataInstr.get(i).getLabel().equals("")? "\t" : this.dataInstr.get(i).getLabel() + "\t") + this.dataInstr.get(i).getCodop() +"\t"
											+ this.dataInstr.get(i).getOp() + "\tInmediato " + this.dataInstr.get(i).getTotalBytes() + " bytes");
									this.checker = true;
									return this.dataInstr.get(i);
								} else if(this.dataInstr.get(i).getBytesC() == 1 && matcher.isHex8Bits(tmpOp)) {
									System.out.println((this.dataInstr.get(i).getLabel().equals("")? "\t" : this.dataInstr.get(i).getLabel() + "\t") + this.dataInstr.get(i).getCodop() +"\t"
											+ this.dataInstr.get(i).getOp() + "\tInmediato " + this.dataInstr.get(i).getTotalBytes() + " bytes");
									this.checker = true;
									return this.dataInstr.get(i);
								} else if(this.dataInstr.get(i).getBytesC() == 2 && matcher.isBin16Bits(tmpOp)) {
									System.out.println((this.dataInstr.get(i).getLabel().equals("")? "\t" : this.dataInstr.get(i).getLabel() + "\t") + this.dataInstr.get(i).getCodop() +"\t"
											+ this.dataInstr.get(i).getOp() + "\tInmediato " + this.dataInstr.get(i).getTotalBytes() + " bytes");
									this.checker = true;
									return this.dataInstr.get(i);
								} else if(this.dataInstr.get(i).getBytesC() == 1 && matcher.isBin8Bits(tmpOp)) {
									System.out.println((this.dataInstr.get(i).getLabel().equals("")? "\t" : this.dataInstr.get(i).getLabel() + "\t") + this.dataInstr.get(i).getCodop() +"\t"
											+ this.dataInstr.get(i).getOp() + "\tInmediato " + this.dataInstr.get(i).getTotalBytes() + " bytes");
									this.checker = true;
									return this.dataInstr.get(i);
								} else {
									
									if(this.dataInstr.get(i).getHasOp() && this.dataInstr.get(i).getOp().equals(""))
										throw new RuntimeErrors(3);
								}
							}
							break;
						
						case "DIR":
							if(this.dataInstr.get(i).getHasOp() && !this.dataInstr.get(i).getOp().equals("")) {
								
								if(matcher.isDIR(this.dataInstr.get(i).getOp()) && this.dataInstr.get(i).getBytesC() == 1){
									String tmpOp = this.dataInstr.get(i).getOp();
									if(matcher.isDec8Bits(tmpOp)) {
										System.out.println((this.dataInstr.get(i).getLabel().equals("")? "\t" : this.dataInstr.get(i).getLabel() + "\t") + this.dataInstr.get(i).getCodop() +"\t"
												+ this.dataInstr.get(i).getOp() + "\tDirecto " + this.dataInstr.get(i).getTotalBytes() + " bytes");
										this.checker = true;
										return this.dataInstr.get(i);
									} else if(matcher.isBin8Bits(tmpOp)) {
										System.out.println((this.dataInstr.get(i).getLabel().equals("")? "\t" : this.dataInstr.get(i).getLabel() + "\t") + this.dataInstr.get(i).getCodop() +"\t"
											+ this.dataInstr.get(i).getOp() + "\tDirecto " + this.dataInstr.get(i).getTotalBytes() + " bytes");
										this.checker = true;
										return this.dataInstr.get(i);
									} else if(matcher.isOct8Bits(tmpOp)) {
										System.out.println((this.dataInstr.get(i).getLabel().equals("")? "\t" : this.dataInstr.get(i).getLabel() + "\t") + this.dataInstr.get(i).getCodop() +"\t"
												+ this.dataInstr.get(i).getOp() + "\tDirecto " + this.dataInstr.get(i).getTotalBytes() + " bytes");
										this.checker = true;
										return this.dataInstr.get(i);
									} else if(matcher.isHex8Bits(tmpOp)) {
										System.out.println((this.dataInstr.get(i).getLabel().equals("")? "\t" : this.dataInstr.get(i).getLabel() + "\t") + this.dataInstr.get(i).getCodop() +"\t"
												+ this.dataInstr.get(i).getOp() + "\tDirecto " + this.dataInstr.get(i).getTotalBytes() + " bytes");
										this.checker = true;
										return this.dataInstr.get(i);
									} else {
										
										if(this.dataInstr.get(i).getHasOp() && this.dataInstr.get(i).getOp().equals(""))
											throw new RuntimeErrors(3);
									}
								}
							}
							break;

						case "EXT":
							if(this.dataInstr.get(i).getHasOp() && !this.dataInstr.get(i).getOp().equals("")) {
								
								if(matcher.isEXT(this.dataInstr.get(i).getOp()) && this.dataInstr.get(i).getBytesC() == 2){
									String tmpOp = this.dataInstr.get(i).getOp();
									if(matcher.isDec16Bits(tmpOp)) {
										System.out.println((this.dataInstr.get(i).getLabel().equals("")? "\t" : this.dataInstr.get(i).getLabel() + "\t") + this.dataInstr.get(i).getCodop() +"\t"
												+ this.dataInstr.get(i).getOp() + "\tExtendido " + this.dataInstr.get(i).getTotalBytes() + " bytes");
										this.checker = true;
										return this.dataInstr.get(i);
									} else if(matcher.isBin16Bits(tmpOp)) {
										System.out.println((this.dataInstr.get(i).getLabel().equals("")? "\t" : this.dataInstr.get(i).getLabel() + "\t") + this.dataInstr.get(i).getCodop() +"\t"
											+ this.dataInstr.get(i).getOp() + "\tExtendido " + this.dataInstr.get(i).getTotalBytes() + " bytes");
										this.checker = true;
										return this.dataInstr.get(i);
									} else if(matcher.isOct16Bits(tmpOp)) {
										System.out.println((this.dataInstr.get(i).getLabel().equals("")? "\t" : this.dataInstr.get(i).getLabel() + "\t") + this.dataInstr.get(i).getCodop() +"\t"
												+ this.dataInstr.get(i).getOp() + "\tExtendido " + this.dataInstr.get(i).getTotalBytes() + " bytes");
										this.checker = true;
										return this.dataInstr.get(i);
									} else if(matcher.isHex16Bits(tmpOp)) {
										System.out.println((this.dataInstr.get(i).getLabel().equals("")? "\t" : this.dataInstr.get(i).getLabel() + "\t") + this.dataInstr.get(i).getCodop() +"\t"
												+ this.dataInstr.get(i).getOp() + "\tExtendido " + this.dataInstr.get(i).getTotalBytes() + " bytes");
										this.checker = true;
										return this.dataInstr.get(i);
									} else if(matcher.isEXT(this.dataInstr.get(i).getOp()) && !matcher.is8Bits(this.dataInstr.get(i).getOp())) {
										System.out.println((this.dataInstr.get(i).getLabel().equals("")? "\t" : this.dataInstr.get(i).getLabel() + "\t") + this.dataInstr.get(i).getCodop() +"\t"
												+ this.dataInstr.get(i).getOp() + "\tExtendido " + this.dataInstr.get(i).getTotalBytes() + " bytes");
										this.checker = true;
										return this.dataInstr.get(i);
									} else {
										
										if(this.dataInstr.get(i).getHasOp() && this.dataInstr.get(i).getOp().equals(""))
											throw new RuntimeErrors(3);
									}
								}
							}
							break;
						
						case "REL":
							if(this.dataInstr.get(i).getHasOp() && !this.dataInstr.get(i).getOp().equals("")) {
								
								if(matcher.isREL(this.dataInstr.get(i).getOp()) && (this.dataInstr.get(i).getBytesC() == 1 || this.dataInstr.get(i).getBytesC() == 2)) {
									System.out.println((this.dataInstr.get(i).getLabel().equals("")? "\t" : this.dataInstr.get(i).getLabel() + "\t") + this.dataInstr.get(i).getCodop() +"\t"
												+ this.dataInstr.get(i).getOp() + "\tRelativo de " + (this.dataInstr.get(i).getBytesC() < 2? "8": "16") + " bits, "+ this.dataInstr.get(i).getTotalBytes() + " bytes");
									this.checker = true;
									return this.dataInstr.get(i);
								} else {
										
										if(this.dataInstr.get(i).getHasOp() && this.dataInstr.get(i).getOp().equals(""))
											throw new RuntimeErrors(3);
								}
							}
							break;
						
						case "IDX":
							if(this.dataInstr.get(i).getHasOp() && !this.dataInstr.get(i).getOp().equals("")) {
								
								if(matcher.isIDX5b(this.dataInstr.get(i).getOp())) {
									System.out.println((this.dataInstr.get(i).getLabel().equals("")? "\t" : this.dataInstr.get(i).getLabel() + "\t") + this.dataInstr.get(i).getCodop() +"\t"
											+ this.dataInstr.get(i).getOp() + "\tIndizado de 5 bits, (IDX), " + this.dataInstr.get(i).getTotalBytes() + " bytes");
									this.checker = true;
									return this.dataInstr.get(i);
								} else if(matcher.isIDXPPDI(this.dataInstr.get(i).getOp())) {
									System.out.println((this.dataInstr.get(i).getLabel().equals("")? "\t" : this.dataInstr.get(i).getLabel() + "\t") + this.dataInstr.get(i).getCodop() +"\t"
											+ this.dataInstr.get(i).getOp() + "\tIndizado de " + (this.dataInstr.get(i).getOp().matches("[1-8],\\+.+")? "pre incremento"
											: this.dataInstr.get(i).getOp().matches("[1-8],-.+")? "pre decremento" : this.dataInstr.get(i).getOp().matches("[1-8],.+\\+")? "post incremento"
											: "post decremento")
											+ ", (IDX), " + this.dataInstr.get(i).getTotalBytes() + " bytes");
									this.checker = true;
									return this.dataInstr.get(i);
								} else if(matcher.isIDXACUM(this.dataInstr.get(i).getOp())) {
									System.out.println((this.dataInstr.get(i).getLabel().equals("")? "\t" : this.dataInstr.get(i).getLabel() + "\t") + this.dataInstr.get(i).getCodop() +"\t"
											+ this.dataInstr.get(i).getOp() + "\tIndizado de acumulador, (IDX), " + this.dataInstr.get(i).getTotalBytes() + " bytes");
									this.checker = true;
									return this.dataInstr.get(i);
								} else {
										
										if(this.dataInstr.get(i).getHasOp() && this.dataInstr.get(i).getOp().equals(""))
											throw new RuntimeErrors(3);
								}
							}
							break;
						
						case "IDX1":
							if(this.dataInstr.get(i).getHasOp() && !this.dataInstr.get(i).getOp().equals("")) {
								
								if(matcher.isIDX1(this.dataInstr.get(i).getOp())) {
									System.out.println((this.dataInstr.get(i).getLabel().equals("")? "\t" : this.dataInstr.get(i).getLabel() + "\t") + this.dataInstr.get(i).getCodop() +"\t"
											+ this.dataInstr.get(i).getOp() + "\tIndizado de 9 bits, (IDX1), " + this.dataInstr.get(i).getTotalBytes() + " bytes");
									this.checker = true;
									return this.dataInstr.get(i);
								}
							}
							break;
						
						case "IDX2":
							if(this.dataInstr.get(i).getHasOp() && !this.dataInstr.get(i).getOp().equals("")) {
								
								if(matcher.isIDX2(this.dataInstr.get(i).getOp())) {
									System.out.println((this.dataInstr.get(i).getLabel().equals("")? "\t" : this.dataInstr.get(i).getLabel() + "\t") + this.dataInstr.get(i).getCodop() +"\t"
											+ this.dataInstr.get(i).getOp() + "\tIndizado de 16 bits, (IDX2), " + this.dataInstr.get(i).getTotalBytes() + " bytes");
									this.checker = true;
									return this.dataInstr.get(i);
								}
							} else {
								
								if(this.dataInstr.get(i).getHasOp() && this.dataInstr.get(i).getOp().equals(""))
									throw new RuntimeErrors(3);
							}
							break;
						
						case "[IDX2]":
							if(this.dataInstr.get(i).getHasOp() && !this.dataInstr.get(i).getOp().equals("")) {
								
								if(matcher.is_IDX2(this.dataInstr.get(i).getOp())) {
									System.out.println((this.dataInstr.get(i).getLabel().equals("")? "\t" : this.dataInstr.get(i).getLabel() + "\t") + this.dataInstr.get(i).getCodop() +"\t"
											+ this.dataInstr.get(i).getOp() + "\tIndizado indirecto de 16 bits, ([IDX2]), " + this.dataInstr.get(i).getTotalBytes() + " bytes");
									this.checker = true;
									return this.dataInstr.get(i);
								} else {
									
									if(this.dataInstr.get(i).getHasOp() && this.dataInstr.get(i).getOp().equals(""))
										throw new RuntimeErrors(3);
								}
							}
							break;
						
						case "[D,IDX]":
							if(this.dataInstr.get(i).getHasOp() && !this.dataInstr.get(i).getOp().equals("")) {
								
								if(matcher.isD_IDX(this.dataInstr.get(i).getOp())) {
									System.out.println((this.dataInstr.get(i).getLabel().equals("")? "\t" : this.dataInstr.get(i).getLabel() + "\t") + this.dataInstr.get(i).getCodop() +"\t"
											+ this.dataInstr.get(i).getOp() + "\tIndizado indirecto de acumulador \"D\", ([D,IDX]), " + this.dataInstr.get(i).getTotalBytes() + " bytes");
									this.checker = true;
									return this.dataInstr.get(i);
								} else {
									
									if(this.dataInstr.get(i).getHasOp() && this.dataInstr.get(i).getOp().equals(""))
										throw new RuntimeErrors(3);
								}
							}
							break;
					}
				}
			}

			if(!this.checker) {
				throw new RuntimeErrors("ErrorInInstruction");
			}

			return null;

		} else
			return null;
	}
	
}
