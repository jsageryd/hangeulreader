/**
 * 
 */
package se.iroiro.md.hangeulreader;

import java.io.File;

import se.iroiro.md.hangeul.CharacterMeasurement;
import se.iroiro.md.hangeul.CharacterRenderer;
import se.iroiro.md.hangeul.Hangeul;
import se.iroiro.md.hangeul.HangeulClassifier;
import se.iroiro.md.hangeul.Line;
import se.iroiro.md.hangeul.LineGroup;

/**
 * Starter class.
 * @author j
 */
public class Go {

	public static boolean assertClassification(String hangeul){
		char c = UnicodeHangeul.composeHangul(hangeul).charAt(0);
		ImageRenderer ir = new ImageRenderer(new CharacterMeasurement(CharacterRenderer.makeCharacterImage(c, 200, 200)));
//		ImageRenderer ir = new ImageRenderer(hc.getCharacterMeasurement());
		GUI gui = new GUI();
		gui.setImageRenderer(ir);
		gui.show();
		HangeulClassifier hc = new HangeulClassifier(CharacterRenderer.makeCharacterImage(c, 200, 200));
		Helper.p("Image read: "+c+"\n");
		Hangeul h = hc.getHangeul();
		if(h != null){
			Helper.p("Classified: "+h.toString().charAt(0)+"\n");
		}
		for(LineGroup lg : hc.getCharacterMeasurement().getLineGroups()){
			for(Line l : lg.getMap().keySet()){
				Helper.p(l.getType()+"\t"+l.getLength()+"\n");
			}
		}
		return (h != null) && (h.toString().charAt(0) == c);
	}
	
	public static void printResult(HangeulClassifier hc, String fileName){
		hc.newClassification(fileName);
		String name = new File(fileName).getName();
		Hangeul h = hc.getHangeul();
		if(h != null){
			Helper.p(name+" looks like: "+h+" ("+h.getName()+")\n");
		}else{
			Helper.p(name+" not recognized.\n");
		}
	}
	
	public static void showLines(String fileName){
//		ImageRenderer i = new ImageRenderer(new CharacterMeasurement(System.getProperty("user.dir")+"/specials/"+"gg.png"));
//		ImageRenderer i = new ImageRenderer(new CharacterMeasurement(CharacterRenderer.makeCharacterImage('ㄲ', 600, 600)));
		ImageRenderer i = new ImageRenderer(new CharacterMeasurement(fileName));
		GUI g = new GUI();
		g.setImageRenderer(i);
		g.show();
	}
	
	/**
	 * Main method.
	 * @param args
	 */
	public static void main(String[] args) {
		
		GUI2 g = new GUI2();
		g.show();
		
//		showLines("/Users/j/Desktop/Picture 1.png");

		System.out.println("Java version "+System.getProperty("java.version"));
		if(true) return;

//		double diff = GraphTools.getAngleDifference(Math.toRadians(10),Math.toRadians(20));
//		System.out.println(Math.toDegrees(diff));
		
//		if(true) return;
		
//		HangeulClassifier hc = new HangeulClassifier();
		
//		showLines("/Users/j/Documents/md/misc/testimages/test_141.png");
//		printResult(hc,"/Users/j/Documents/md/misc/testimages/test_141.png");
//		Helper.p("Done.\n");
		
//		if(true) return;
		
		if(true){

//			String h = "임정한굃";
//			String hh = "툓툫퓂흼흦힁";	// 푤푠
			String hh = "카";	// 푤푠
//			String h = "ᄈ";

			if(false){
				Stopwatch s = new Stopwatch();
				s.start();
				if(assertClassification(hh)){
					System.out.println("\t\tCORRECT.\n");
				}else{
					System.out.println("\t\tNOT correct.\n");
				}
				s.stop();
				System.out.println("Total time: "+s.totaltime_str());
			}

//			Helper.dump(hc.getCharacterMeasurement().getLineGroups().get(0).getGraph().toString(), "/Users/j/Desktop/h.dot");


//			HangeulClassification hc = new HangeulClassification(System.getProperty("user.dir")+"/specials/"+"lb.png");
			if(true){
				HangeulClassifier hclf = new HangeulClassifier("/Users/j/Documents/md/misc/img/test_images/test_53.png");
//				HangeulClassification hclf = new HangeulClassification("/Users/j/Documents/md/misc/testimages/test_138.png");
//				HangeulClassifier hclf = new HangeulClassifier(System.getProperty("user.dir")+"/specials/"+"m.png");
//				HangeulClassifier hclf = new HangeulClassifier("/Users/j/Desktop/Picture 1.png");
				ImageRenderer ir = new ImageRenderer(hclf.getCharacterMeasurement());
				GUI gui = new GUI();
				gui.setImageRenderer(ir);
				gui.show();

				Hangeul hangeul = hclf.getHangeul();
				if(hangeul != null){
					Helper.p("Image looks like: "+hangeul+" (HANGEUL SYLLABLE "+hangeul.getName()+")\n");
				}else{
					System.out.println("Hangeul not recognized.");
				}
			}
		}else{

//			HangeulReaderTest t1 = new HangeulReaderTest('\uAC00','\uC1D2');
//			HangeulReaderTest t2 = new HangeulReaderTest('\uC1D3','\uD7A3');

//			HangeulReaderTest t1 = new HangeulReaderTest('\uAC00','\uD7A3');
//			HangeulReaderTest t1 = new HangeulReaderTest('\uAC00','\uAF00');
//			HangeulReaderTest t1 = new HangeulReaderTest("갍감값갖갬갮갰걈걊걥걦걺검겂겜겞겸겺곔곖곗곰곲괅괆괌괎괨괪괶괷괸괹괼굀굁굃굄굆굈굋굌굏굠굢구국굮굯군굱굲굳굴굵굶굷굸굹굺굻굼굽굾굿궀궁궂궃궄궅궆궇궈궉궊궋권궍궎궏궐궑궒궓궔궕궖궗궘궙궚궛궜궝궞궟궠궡궢궣궤궥궦궧궨궩궪궫궬궭궮궯궰궱궲궳궴궵궶궷궸궹궺궻궼궽궾궿귀귁귂귃귄귅귆귇귈귉귊귋귌귍귎귏귐귑귒귓귔귕귖귗귘귙귚귛규귝귞귟균귡귢귣귤귥귦귧귨귩귪귫귬귭귮귯귰귱귲귳귴귵귶귷그극귺귻근귽귾귿글긁긂긃긄긅긆긇금급긊긋긌긍긎긏긐긑긒긓긔긕긖긗긘긙긚긛긜긝긞긟긠긡긢긣긤긥긦긧긨긩긪긫긬긭긮긯긺긻긼김깂깇깍깎깏깕깖깗깛깜깞깟깠깡깢깣깥깧깴깸깺꺅꺊꺎꺓꺔꺖꺗꺘꺚꺛꺮꺰꺲꺷껇껎껖께껛껝껣껨껪껷껿");
//			HangeulReaderTest t1 = new HangeulReaderTest("갍감값갖갬갮갰");
//			HangeulReaderTest t1 = new HangeulReaderTest("한글은한국어의고유문자로서년조선제대임금세종이훈민정음이라는이름으로창제하여년에반포하였다이후한문을고수하는사대부들에게는경시되었으나서민층을중심으로이어지다가");
			HangeulReaderTest t1 = new HangeulReaderTest("는를응");
//			HangeulReaderTest t1 = new HangeulReaderTest("임");
//			HangeulReaderTest t1 = new HangeulReaderTest("현재텐에서지원가능한상용비트맵한글서체는");
//			HangeulReaderTest t1 = new HangeulReaderTest("결국은우분투에도윈도우의상용한글폰트를복사하고맑은고딕을윈비스타에서복사해서");
//			HangeulReaderTest t2 = new HangeulReaderTest('\uAC00','\uAC25');

//			new HangeulReaderTest("협상직후공개된주요내용과년월일에공개된합의문은축산농가의피해광우병의위험성에대한우려와함께국민의의견이반영되지않은것이지적되어각계각층으로부터논란을불러일으켰고정당각종언론전문가등이문제를제기하면서논란은더욱증폭되었다");
			
//			t1.start();
//			t2.start();

//			try {
//				t1.join();
//				t2.join();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
		
		Helper.p("Done.");
	}

}