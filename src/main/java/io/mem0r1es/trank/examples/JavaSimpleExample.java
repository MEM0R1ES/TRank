package io.mem0r1es.trank.examples;

import io.mem0r1es.trank.TRanker;
import io.mem0r1es.trank.ranking.ANC_DEPTH;
import io.mem0r1es.trank.util.TRankerJavaInterface;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class JavaSimpleExample {

	public static void main(String[] args) {
		try {

			// loading resource
			InputStream example = new FileInputStream("src/main/resources/MLK-Dream.txt");

			// run TRank pipeline with ranking algorithm ANC_DEPTH
			TRanker scalaPipeline = new TRanker(example, new ANC_DEPTH());

			// convert collections to Java
			TRankerJavaInterface pipeline = new TRankerJavaInterface(scalaPipeline);

			// print recognised entities
			printNamedEntities(pipeline);

			// print entities map with best types
			printEntitiesToBestTypes(pipeline);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Print recognised entities
	 * @param pipeline Java-converted TRank pipeline
	 */
	public static void printNamedEntities(TRankerJavaInterface pipeline) {

		System.out.println("\n***** Named entities *****");
		Map<URI, String> entityToLabel = pipeline.getEntityToLabel();

		for (Map.Entry<URI, String> element : entityToLabel.entrySet())
			System.out.format("%-25s -> %s\n", element.getValue(), element.getKey());
	}

	/**
	 * Print entities map with best types
	 * @param pipeline Java-converted TRank pipeline
	 */
	public static void printEntitiesToBestTypes(TRankerJavaInterface pipeline) {

		System.out.println("\n***** Entities to best 3 types *****");

		// aliases
		Map<URI, LinkedHashMap<URI, Double>> entityToTRankedTypes = pipeline.getEntityToTRankedTypes();
		Map<URI, String> entityToLabel = pipeline.getEntityToLabel();

		for (Map.Entry<URI, LinkedHashMap<URI, Double>> element : entityToTRankedTypes.entrySet()) {

			String entity = entityToLabel.get(element.getKey());
			LinkedHashMap<URI, Double> types = element.getValue();

			if (types.size() > 0) { // print only entity with result

				StringBuilder bestTypes = new StringBuilder();
				Iterator<Map.Entry<URI, Double>> typesRanked = types.entrySet().iterator();

				int count = 3;
				while (typesRanked.hasNext() && 0 < count--) {

					URI type = typesRanked.next().getKey();
					String[] component = type.getPath().split("/"); // getting last id from URI

					bestTypes.append(component[component.length - 1]);

					if (typesRanked.hasNext() && 1 < count)
						bestTypes.append(", ");
				}

				System.out.format("%-25s -> %s\n", entity, bestTypes);
			}
		}
	}
}
