package io.mem0r1es.trank.util

import io.mem0r1es.trank.TRanker
import java.net.URI
import scala.collection.JavaConverters._

/**
 * Java interface for TRanker class
 *
 * Defines getter aliases in Java's style
 * Convert Scala's collection to Java's
 *
 * @param trank TRanker class
 */
class TRankerJavaInterface(private val trank: TRanker) {

	/**
	 * Get raw content access
	 */
	val getContentRaw = trank.contentRaw

	/**
	 * Get content without formatting tags
	 */
	val getContentPreProcessed: java.lang.String =
		trank.contentPreProcessed

	/**
	 * Get entities and their names
	 * @return URI mapped to name
	 */
	val getEntityToLabel: java.util.Map[URI, java.lang.String] =
		trank.entityToLabel.asJava

	/**
	 * Get all entities
	 * @return Set of all entities
	 */
	val getEntityURIs: java.util.Set[URI] =
		trank.entityURIs.asJava

	/**
	 * Get entities and their possible types
	 * @return Entity URI mapped to set of types URI
	 */
	val getEntityToTypes: java.util.Map[URI, java.util.Set[URI]] =
		trank.entityToTypes.map(
			p => p._1 -> p._2.asJava
		).asJava

	/**
	 * Get entities and their ranked types
	 * @return Entity URI mapped to type URI mapped to ranking scores
	 */
	val getEntityToTRankedTypes: java.util.Map[URI, java.util.LinkedHashMap[URI, java.lang.Double]] =
		trank.entityToTRankedTypes.map {
			case (entity, types) => entity -> {
				val converted = new java.util.LinkedHashMap[URI, java.lang.Double]()
				for {
					(uri, score) <- types
				} converted.put(uri, score)
				converted
			}
		}.asJava
}
