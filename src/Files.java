import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Scanner;

public class Files {
	private PrintWriter tmpdir;
	private PrintWriter tabsim;
	private PrintWriter tmpFile;
	private PrintWriter objFile;

	private Path rutasim;
	private Path rutatmp;
	private Path rutaObj;
	private Path tmp;

	private String objLines;
	private String data;
	private String dir;
	private String filename;

	public Files(Path ruta) throws Exception{
		String route = ruta.toAbsolutePath().getParent().toString();

		this.rutatmp = java.nio.file.Paths.get(route + "\\tmp.txt");
		this.rutasim = java.nio.file.Paths.get(route + "\\tabsim.txt");

		this.tmpdir = new PrintWriter(route + "\\tmp.txt");
		this.tabsim = new PrintWriter(route + "\\tabsim.txt");

		this.filename = ruta.getFileName().toString();

		String nameFile = "\\" + ruta.getFileName().toString();

		nameFile = nameFile.replaceAll("\\....+$", "") + ".S19";

		this.rutaObj = java.nio.file.Paths.get(route + nameFile);
		this.objFile = new PrintWriter(route + nameFile);

		this.objLines = "";
		this.data = "";
		this.dir = "";
	}

	public boolean verifyLabel(Instruccion instr, Bases conv, ContLoc contloc, MatchesElement matcher) {
		try(Scanner file = new Scanner(this.rutasim)) {
			while(file.hasNextLine()) {
				String data = file.nextLine().trim();
				String [] line = data.split("\t");

				String tmp = line[1];
				String value = line[2];

				if(tmp.equals(instr.getLabel()) && !value.equals(instr.getContloc())) {
					return false;
				}
			}
		} catch (Exception ex) {
			System.out.println("Error al leer la tabla de simbolos: " + ex.getMessage());
		}
		return true;
	}

	public boolean repeated(Instruccion instr) {
		try(Scanner file = new Scanner(this.rutatmp)) {
			while(file.hasNextLine()) {
				String data = file.nextLine().trim();
				String [] line = data.split("\t");

				String tmp = line[3];
				String value = line[2];
				if(tmp.equals(instr.getCodop()) && value.equals(instr.getLabel())) {
					return false;
				}
			}
		} catch (Exception ex) {
			System.out.println("Error al leer la tabla de simbolos: " + ex.getMessage());
		}
		return true;
	}

	public Path getRSim() {
		return this.rutasim;
	}

	public Path getRTmp() {
		return this.rutatmp;
	}

	public void writeTmp(Instruccion instr, String type) {
		String label = instr.getLabel();
		String op = instr.getOp();
		if(instr.getLabel().equals("")) {
			label = "NULL";
		}
		if(instr.getOp().equals("")) {
			op = "NULL";
		}
		this.tmpdir.println(type + "\t" + instr.getContloc() + "\t" + label + "\t" + instr.getCodop() + "\t" + op);
	}

	public void writeTabSim(Instruccion instr, String type, String value) {
		this.tabsim.println(type + "\t" + instr.getLabel() + "\t" + value);
	}

	public void writeTmpFile(String line, String codMaq) {
		this.tmpFile.println(line + (codMaq.equals("")? "" : "\t" + codMaq));
	}

	public void tmpFile() throws java.io.IOException, java.io.FileNotFoundException {
        this.tmp = java.nio.file.Files.createTempFile("tmpfile", ".txt");
        this.tmpFile = new PrintWriter(tmp.toString());
	}

	public void endFase() throws java.io.IOException {
		this.objFile.print(objLines);
		this.objFile.flush();
		this.tmpFile.close();
		java.nio.file.Files.copy(this.tmp, this.rutatmp, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
		this.objFile.close();
	}

	public String getSymb(String label) throws RuntimeErrors {
		try(Scanner file = new Scanner(this.rutasim)) {
			while(file.hasNextLine()) {
				String data = file.nextLine().trim();
				String [] line = data.split("\t");

				if(label.equals(line[1])) {
					return line[2];
				}
			}
		} catch (Exception ex) {
			System.out.println("Error al leer la tabla de simbolos: " + ex.getMessage());
		}
		throw new RuntimeErrors(4);
	}

	public void writeObj(Instruccion instr, Bases conv, MatchesElement matcher) {
		String reg = "";
		String len = "";
		String checksum = "";

		if(instr.getCodop().equals("ORG")){
			reg = "S0";

			this.dir = "0000";

			String dataTmp = this.rutaObj.getRoot().toString().replace("\\", "") + " " + this.filename;
			
			for(int i = 0; i < dataTmp.length(); i++) {
				this.data += Integer.toHexString(dataTmp.codePointAt(i)).toUpperCase();
			}
			this.data += "0A";

			int size = getLen(this.dir + this.data) + 1;
			len = conv.append(Integer.toHexString(size), 2).toUpperCase();
			
			checksum = Integer.toHexString(~getCheckSum(len + this.dir + this.data)).toUpperCase();
			checksum = conv.c2(checksum, 2);

			this.objLines += reg + len + this.dir + this.data + checksum + "\n";
			
			this.dir = "";
			this.data = "";
		} else if(instr.getCodop().equals("END")) {
			reg = "S9";
			len = "03";
			this.dir = "0000";
			this.data = "";
			checksum = "FC";
			this.objLines += "\n" + reg + len + this.dir + this.data + checksum + "\n";
			this.dir = "";
		} else if(matcher.isDirCodMaq(instr.getCodop())) {
			reg = "S1";
			
			if(this.dir.equals("")){
				this.dir = instr.getContloc();
			}

			if((this.data + instr.getCodMaq()).length() / 2 < 17) {
				String tmp = this.data;
				this.data += instr.getCodMaq();

				int size = getLen(this.dir + this.data) + 1;
				len = conv.append(Integer.toHexString(size), 2).toUpperCase();

				checksum = Integer.toHexString(~getCheckSum(len + this.dir + this.data)).toUpperCase();
				checksum = conv.c2(checksum, 2);
				
				if(tmp.equals("")) {
					this.objLines += reg + len + this.dir + instr.getCodMaq() + checksum;
				} else {
					StringBuilder tmpl = new StringBuilder(this.objLines);
					
					int pS = this.objLines.lastIndexOf('S') + 2;
					
					tmpl.setCharAt(pS, len.charAt(0));
					tmpl.setCharAt(pS + 1, len.charAt(1));
					
					this.objLines = tmpl.toString().replaceAll("..$", "") + instr.getCodMaq() + checksum;
				}

			} else {

				this.objLines += "\n";

				this.dir = instr.getContloc();

				this.data = instr.getCodMaq();

				int size = getLen(this.dir + this.data) + 1;
				len = conv.append(Integer.toHexString(size), 2).toUpperCase();

				checksum = Integer.toHexString(~getCheckSum(len + this.dir + this.data)).toUpperCase();
				checksum = conv.c2(checksum, 2);

				String newline = reg + len + dir + this.data + checksum;
				this.objLines += newline;
			}
		}
	}

	private int getCheckSum(String hexValues) {
		String [] tmp = hexValues.split("(?<=\\G..)");
		int value = 0;
		for(String str: tmp) {
			value += Integer.parseInt(str, 16);
		}
		return value;
	}

	private int getLen(String data) {
		String [] tmp = data.split("(?<=\\G..)");
		return tmp.length;
	}


	public void flushTmp() {
		this.tmpFile.flush();
	}

	public void flush() {
		this.tmpdir.flush();
		this.tabsim.flush();
	}

	public void closeTmp() {
		this.tmpdir.close();
	}

	public void closeTabSim() {
		this.tabsim.close();
	}

	public void closeAll() {
		this.tmpdir.close();
		this.tabsim.close();
	}
}
