package enridaga.colatti;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.commons.lang3.ArrayUtils;

import enridaga.colatti.serializer.GraphmlSerializer;
import enridaga.colatti.serializer.JsonSerializer;
import enridaga.colatti.serializer.LatticeSerializer;

/**
 * Hello world!
 *
 */
public class Cli {
	public static void main(String[] args) {
		System.err.println("File: " + args[0]);
		String format = "json";
		if (args.length > 1) {
			if (args[1].equals("graphml")) {
				format = "graphml";
			}
			System.err.println("Format: " + args[1]);
		}
		Boolean incremental = false;
		if (args.length > 2) {
			if (Boolean.valueOf(args[2])) {
				incremental = true;
			}
			System.err.println("Incremental: " + incremental);
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
			Lattice lattice = new LatticeInMemory();
			LatticeSerializer s = (format.equals("json")) ? new JsonSerializer() : new GraphmlSerializer();
			InsertObject io = new InsertObject(lattice);
			String line;
			int filecount = 0;
			while ((line = br.readLine()) != null) {
				// process the line.
				String[] l = line.split(",");
				if (l.length > 0) {
					io.perform(l[0], (Object[]) ArrayUtils.removeElement(l, l[0]));
					if (incremental) {
						filecount++;
						try (PrintWriter out = new PrintWriter(args[0] + ".lattice." + filecount + "." + format)) {
							String tostring = s.serialize(lattice);
							out.println(tostring);
						}
					}
				}
			}
			br.close();
			if (!incremental) {
				try (PrintWriter out = new PrintWriter(args[0] + ".lattice." + format)) {
					String tostring = s.serialize(lattice);
					out.println(tostring);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ColattiException e) {
			e.printStackTrace();
		}
	}
}
