package io.mem0r1es.trank.pipeline

import java.net.URI

import scala.Array.canBuildFrom

import org.apache.lucene.index.Term
import org.apache.lucene.search.TermQuery

import com.typesafe.config.Config

import io.mem0r1es.trank.util.IndexUtils
import io.mem0r1es.trank.util.TRankIndexType

/**
 * Type retrieval (URI) operations.
 */
object TypeRetrieval {

  /**
   * Retrieve all URI of RDF types corresponding to all given DBpedia resource URI.
   * Based on Apache Lucene (http://lucene.apache.org) via specific indexes.
   *
   * These indexes can be download here: http://exascale.info/sites/default/files/uploaded/trank/trank-indexes.tgz
   * The .tgz extracted folder should be placed at project root (i.e. "TRank/trank-indexes").
   * Indexes path can be changed in Typesafe config ("reference.conf").
   *
   * @param entities set: entity URI
   * @param config alternative Typesafe config
   * @return map: entity URI -> set: URI types
   */
  def retrieveTypes(entities: Set[URI], config: Config): Map[URI, Set[URI]] = {

    entities.map { entity =>
      val types = getTypes(entity, config)
      entity -> types
    }.toMap
  }

  // queries all URI of RDF types corresponding to an entity
  private def getTypes(entity: URI, config: Config): Set[URI] = {
    val searcher = IndexUtils.getIndexSearcher(TRankIndexType.TYPE_INDEX, config) // get indexes (default: /trank-indexes)
    val query = new TermQuery(new Term("uri", entity.toString))
    val docs = searcher.search(query, 1)

    if (docs.scoreDocs.nonEmpty) {
      val d = searcher.doc(docs.scoreDocs(0).doc)
      d.getValues("type").map(new URI(_)).toSet
    } else {
      Set.empty
    }
  }
}
