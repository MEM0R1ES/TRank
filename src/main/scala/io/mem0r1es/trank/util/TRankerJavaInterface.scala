package io.mem0r1es.trank.util

import io.mem0r1es.trank.TRanker
import java.net.URI
import scala.collection.JavaConverters._

class TRankerJavaInterface(private val trank: TRanker) {

	def getContentPreProcessed: java.lang.String =
		trank.contentPreProcessed

	def getEntityToLabel: java.util.Map[URI, java.lang.String] =
		trank.entityToLabel.asJava

	def getEntityURIs: java.util.Set[URI] =
		trank.entityURIs.asJava

	def getEntityToTypes: java.util.Map[URI, java.util.Set[URI]] =
		trank.entityToTypes.map(
			p => p._1 -> p._2.asJava
		).asJava

	def getEntityToTRankedTypes: java.util.Map[URI, java.util.Map[URI, java.lang.Double]] =
		trank.entityToTRankedTypes.map(
			p => p._1 -> p._2.toMap.map(
				q => q._1 -> convertDouble(q._2)
			).asJava
		).asJava

	private def convertDouble(x: Double): java.lang.Double = x
}
