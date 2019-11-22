package enridaga.colatti;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.commons.lang3.ArrayUtils;

import enridaga.colatti.serializer.GraphmlSerializer;
import enridaga.colatti.serializer.JsonSerializer;
import enridaga.colatti.serializer.CSVSerializer;
import enridaga.colatti.serializer.LatticeSerializer;

/**
 * Hello world!
 *
 */
public class Cli {
	public static void main(String[] args) throws Exception{
		System.err.println("File: " + args[0]);
		String format = "csv";
		if (args.length > 1) {
			switch(args[1].toLowerCase()) {
				case "graphml":
					format = "graphml";
					break;
				case "json":
					format = "json";
					break;
				case "csv":
					format = "csv";
					break;
				default:
					throw new Exception("Invalid format: " + format);
			}
		}
		System.err.println("Format: " + format);
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
			LatticeSerializer s;
			switch(format){
				case "json":
					s = new JsonSerializer();
					break;
				case "graphml":
					s = new GraphmlSerializer();
					break;
				case "csv":
				default:
					s = new CSVSerializer(System.out);

			}
			// LatticeSerializer s = (format.equals("json")) ? new JsonSerializer() : new GraphmlSerializer();
			InsertObject io = new InsertObject(lattice);
			String line;
			int filecount = 0;
			while ((line = br.readLine()) != null) {
				// process the line.
				String[] l = line.split(",");
				if (l.length > 0) {
					filecount++;
					System.err.println("Processing object " + filecount + "; lattice size: " + lattice.size());
					io.perform(l[0], (Object[]) ArrayUtils.removeElement(l, l[0]));
					if (incremental) {
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
