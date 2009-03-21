/**
 *
 */
package se.iroiro.md.hangeulreader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * Helper class for quicker debugging
 * @author j
 *
 */
public class Helper {

	public static void dump(String s, String fileName){
		PrintStream ps = null;
		try{
			ps = new PrintStream(new FileOutputStream(fileName),false,"UTF-8");
		}catch(IOException e){
			e.printStackTrace();
		}
		if(ps != null){
			ps.print(s);
			ps.close();
		}
	}

	public static void p(Object o){
		if(o != null){
			p(o.toString());
		}else{
			p("null");
		}
	}

	public static void p(boolean b){
		p(Boolean.toString(b));
	}

	public static void p(int i){
		p(Integer.toString(i));
	}

	public static void p(char c){
		p(Character.toString(c));
	}

	public static void p(String s){
		try {
			PrintStream ps = new PrintStream(System.out, true, "UTF-8");
			ps.print(s);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

//	public static void printJamo(List<Jamo> jamoDB) {
//		for(Jamo j : jamoDB){
//			Helper.p(j);
//			if(j.getLineGroups() != null){
//				int s = j.getLineGroups().size();
//				Helper.p(": "+j.getType()+"\t"+j.getName()+"\t"+s+" group"+(s != 1 ? "s" : "")+".\tLine types:");
//				for(LineGroup lg : j.getLineGroups()){
//					Helper.p(" [");
//					for(Line l : lg.getMap().keySet()){
//						Helper.p(l.getType());
//					}
//					Helper.p("]");
//				}
//			}else{
//				Helper.p(": "+j.getType()+"\t"+j.getName()+"\tNo lines found.");
//			}
//			System.out.println();
//		}
//	}

}