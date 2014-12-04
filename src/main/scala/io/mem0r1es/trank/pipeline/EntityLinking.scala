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

object EntityLinking {

  /**
   * Links Named Entity labels to DBpedia URIs.
   */
  def linkEntities(entityLabels: Set[String], config: Config): Map[URI, String] = {
    var entities = Map[URI, String]()

    entityLabels.foreach { label =>
      val uri = getURI(label, config)
      entities += uri -> label
    }
    entities.toMap
  }

  private def getURI(label: String, config: Config): URI = {
    val searcher = IndexUtils.getIndexSearcher(TRankIndexType.URI_INDEX, config)
    val exact = exactQuery(label, searcher)
    val bool = boolQuery(label, searcher)

    exact match {
      case Some(x) => return x
      case None =>
    }
    bool match {
      case Some(x) => return x
      case _ => return new URI("")
    }
  }

  private def exactQuery(label: String, searcher: IndexSearcher): Option[URI] = {
    val query = new TermQuery(new Term("labelex", label.toLowerCase()))
    top1(query, searcher)
  }

  private def boolQuery(label: String, searcher: IndexSearcher): Option[URI] = {
    val query = new BooleanQuery
    label.toLowerCase().split(" ").foreach { term =>
      query.add(new BooleanClause(new TermQuery(new Term("label", term)), Occur.SHOULD))
    }
    top1(query, searcher)
  }

  private def top1(query: Query, searcher: IndexSearcher): Option[URI] = {
    val docs = searcher.search(query, 1)
    if (docs.scoreDocs.length > 0) {
      val d = searcher.doc(docs.scoreDocs(0).doc)
      Option(new URI(d.get("uri")))
    } else {
      None
    }
  }
}
