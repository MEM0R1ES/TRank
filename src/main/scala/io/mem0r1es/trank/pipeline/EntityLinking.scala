package io.mem0r1es.trank.pipeline

import java.net.URI
import org.apache.lucene.index.Term
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanClause.Occur
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermQuery
import com.typesafe.config.Config
import io.mem0r1es.trank.util.IndexUtils
import io.mem0r1es.trank.util.TRankIndexType

/**
 * Entity linking (URI) operations.
 */
object EntityLinking {

  /**
   * Links Named Entity labels to DBpedia resource URIs (http://dbpedia.org).
   * Based on Apache Lucene (http://lucene.apache.org) via specific indexes.
   *
   * These indexes can be download here: http://exascale.info/sites/default/files/uploaded/trank/trank-indexes.tgz
   * The .tgz extracted folder should be placed at project root (i.e. "TRank/trank-indexes").
   * Indexes path can be changed in Typesafe config ("reference.conf").
   *
   * @param entityLabels set: named entity labels
   * @param config alternative Typesafe config
   * @return map: URI -> named entity labels
   */
  def linkEntities(entityLabels: Set[String], config: Config): Map[URI, String] = {
    entityLabels.map{ label =>
      val uri = getURI(label, config)
      uri -> label
    }.toMap
  }

  // get URI corresponding to the specified label
  private def getURI(label: String, config: Config): URI = {
    val searcher = IndexUtils.getIndexSearcher(TRankIndexType.URI_INDEX, config) // get indexes (default: /trank-indexes)
    val exact = exactQuery(label, searcher)
    val bool = boolQuery(label, searcher)

    (exact, bool) match { // priority to the exact query
      case (Some(x), _) => x
      case (None, Some(x)) => x
      case _ => new URI("")
    }
  }

  // query matching the exact label
  private def exactQuery(label: String, searcher: IndexSearcher): Option[URI] = {
    val query = new TermQuery(new Term("labelex", label.toLowerCase))

    top1(query, searcher)
  }

  // query matching sub-terms of the label
  private def boolQuery(label: String, searcher: IndexSearcher): Option[URI] = {
    val query = new BooleanQuery
    label.toLowerCase.split(" ").foreach{ term =>
      new BooleanClause(new TermQuery(new Term("label", term)), Occur.SHOULD)
    }
    top1(query, searcher)
  }

  // run the query on the index and returns the first resulting URI (if any)
  private def top1(query: Query, searcher: IndexSearcher): Option[URI] = {
    val docs = searcher.search(query, 1)

    if (docs.scoreDocs.nonEmpty) {
      val d = searcher.doc(docs.scoreDocs(0).doc)
      Option(new URI(d.get("uri")))
    } else {
      None
    }
  }
}
