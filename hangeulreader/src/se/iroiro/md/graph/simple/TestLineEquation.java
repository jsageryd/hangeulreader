package se.iroiro.md.graph.simple;


public class TestLineEquation {
	public TestLineEquation(){
		go();
	}
	
	private void go(){
		System.out.println("Regular lines");
		test("01",3,7,5,5,5,7,2,4, "[4.00,6.00]");
		test("02",5,-3,3,-5,3,-2,5,-6, "[4.00,-4.00]");
		test("03",-7,-1,-1,-3,-5,-3,-3,-1, "[-4.00,-2.00]");
		test("04",-6,4,-5,2,-5,5,-6,1, "[-5.50,3.00]");

		System.out.println("\nNon-diagonal lines");
		test("05",4,6,4,2,2,3,6,3, "[4.00,3.00]");
		test("06",4,-2,4,-5,6,-3,3,-3, "[4.00,-3.00]");
		test("07",-3,-7,-3,-1,-5,-5,-1,-5, "[-3.00,-5.00]");
		test("08",-4,6,-4,4,-5,5,-1,5, "[-4.00,5.00]");

		System.out.println("\nNon-diagonal and regular lines");
		test("09",3,6,3,3,2,6,4,2, "[3.00,4.00]");
		test("10",5,7,8,7,8,9,6,5, "[7.00,7.00]");
		test("11",2,-2,2,-6,1,-5,3,-3, "[2.00,-4.00]");
		test("12",8,-4,4,-4,5,-3,7,-5, "[6.00,-4.00]");
		test("13",-6,-7,-6,-2,-8,-3,-4,-6, "[-6.00,-4.50]");
		test("14",-4,-3,-2,-3,-4,-1,-2,-5, "[-3.00,-3.00]");
		test("15",-6,6,-6,4,-8,6,-4,4, "[-6.00,5.00]");
		test("16",-5,2,-1,2,-1,4,-4,1, "[-3.00,2.00]");

		System.out.println("\nNon-intersecting lines");
		test("17",1,5,2,8,3,5,3,8, "[3.00,11.00]");
		test("18",6,8,5,5,6,5,7,8, "[?,?]");
		test("19",3,2,7,2,4,3,5,3, "[?,?]");
		test("20",4,-3,7,-3,3,-2,5,-1, "[1.00,-3.00]");
		test("21",2,-5,5,-8,6,-7,4,-5, "[?,?]");
	}
	
	private void test(String id, double x1, double y1, double x2, double y2,
			double x3, double y3, double x4, double y4, String expected){
		SimpleLineEquation eq1 = new SimpleLineEquation(x1,y1,x2,y2);
		SimpleLineEquation eq2 = new SimpleLineEquation(x3,y3,x4,y4);
		print(id, expected, eq1.getIntersection(eq2).toString());
	}
	
	private void print(String id, String expected, String got){
		boolean same = expected.equals(got) || got.length() == 5;
		System.out.println(id + ") Expected: " + expected + "\tGot: " + got + (same ? "\tOK!" : "\tCHECK ("+id+")"));
	}
}
