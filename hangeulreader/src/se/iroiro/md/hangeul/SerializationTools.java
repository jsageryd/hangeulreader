/**
 *
 */
package se.iroiro.md.hangeul;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * The <code>SerializationTools</code> class contains static methods for serialization.
 * @author j
 *
 */
public class SerializationTools {
	public static void saveObject(Serializable object, String filename) throws IOException{
		ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(filename)));
		oos.writeObject(object);
		oos.close();
	}

	public static Object loadObject(String filename) throws ClassNotFoundException, IOException{
		ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(filename)));
		Object object = ois.readObject();
		ois.close();
		return object;
	}
}
