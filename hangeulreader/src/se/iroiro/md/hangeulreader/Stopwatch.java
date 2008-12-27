/**
 * 
 */
package se.iroiro.md.hangeulreader;

/**
 * <code>Stopwatch</code> is a simple class to time code execution.
 * 
 * @author j
 */
public final class Stopwatch {

	private long start, stop, acc;
	
	/**
	 * Class constructor.
	 */
	public Stopwatch() {
		reset();
	}
	
	/**
	 * Resets the timer.
	 */
	public void reset(){
		acc = 0;
	}
	
	/**
	 * Starts the timer.
	 */
	public void start(){
		start = System.currentTimeMillis();
		stop = start;
	}
	
	/**
	 * Stops the timer.
	 */
	public void stop(){
		stop = System.currentTimeMillis();
		acc += stop-start;
	}

	/**
	 * Last timing.
	 * 
	 * @return	the elapsed time in milliseconds.
	 */
	public Long time(){
		return stop-start;
	}
	
	/**
	 * Last timing.
	 * 
	 * @return	the elapsed time in milliseconds, as a <code>String</code>.
	 */
	public String time_str(){
//		return formatNumber(time())+" ms";
		return Long.toString(time())+" ms";
	}
	
	/**
	 * Total time.
	 * 
	 * @return	the total elapsed time since last reset.
	 */
	public Long totaltime(){
		return acc;
	}
	
	/**
	 * Total time.
	 * 
	 * @return	the total elapsed time since last reset, as a <code>String</code>.
	 */
	public String totaltime_str(){
//		return formatNumber(totaltime())+" ms";
		return Long.toString(totaltime())+" ms";
	}
	
//	/**
//	 * Formats a number according to Swedish locale.
//	 * 
//	 * @param number	the number to format
//	 * @return	the formatted number
//	 */
//	static String formatNumber(Long number){
//		return NumberFormat.getNumberInstance(new Locale("sv", "SE")).format(number);
//	}
}
