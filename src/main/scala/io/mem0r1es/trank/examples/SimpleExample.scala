package io.mem0r1es.trank.examples

import io.mem0r1es.trank.TRanker
import io.mem0r1es.trank.ranking.ANC_DEPTH
import scala.io.Source

object SimpleExample extends App {

	// loading resource
	val example 			 = Source.fromFile("src/main/resources/MLK-Dream.txt").mkString

	// run TRank pipeline with ranking algorithm ANC_DEPTH
	val pipeline 			 = new TRanker(example, new ANC_DEPTH)

	// aliases
	val entityToLabel 		 = pipeline.entityToLabel
	val entityToTRankedTypes = pipeline.entityToTRankedTypes

	// print recognised entities
	println("\n***** Named entities *****")

	for ((uri, entity) <- entityToLabel)
	 	println(f"$entity%-25s : $uri")

	// print entities map with best types
	println("\n***** Entities to best 3 types *****")

	for {
	 	(entity, types)  <- entityToTRankedTypes if types.nonEmpty
	 	entityLabel 	 = entityToLabel(entity)
	 	bestTypes 		 = types.take(3).map(_._1.getPath.split('/').last).mkString(", ") // getting last id from URI
	} println(f"$entityLabel%-25s -> $bestTypes")

}
