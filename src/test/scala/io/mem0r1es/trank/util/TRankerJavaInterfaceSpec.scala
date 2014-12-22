package io.mem0r1es.trank.util

import io.mem0r1es.trank.TRanker
import org.scalatest.FlatSpec

import scala.io.Source

class TRankerJavaInterfaceSpec extends FlatSpec {

	val text = Source.fromFile("src/main/resources/MLK-Dream.txt").mkString
	val scalaTRanker = new TRanker(text)
	val javaTRanker = new TRankerJavaInterface(scalaTRanker)

	"An interface" should "keep the same order" in {

		val scalaEntityToTRankedTypes = scalaTRanker.entityToTRankedTypes
		val javaEntityToTRankedTypes = javaTRanker.getEntityToTRankedTypes

		for {
			(entity, types) <- scalaEntityToTRankedTypes
			ordered = types.toList
			i <- ordered.indices
		} {
			assert(javaEntityToTRankedTypes.get(entity).get(ordered(i)._1) === ordered(i)._2)
		}

	}

}
