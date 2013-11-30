package backend.data.service

import model.blog.{ Post, Tag }

/**
 * The data service trait for tags
 *
 * @author Stefan Bleibinhaus
 *
 */
trait TagDataService extends PostChangeListener {
  private var tagsList: List[Tag] = Nil
  private var bigMinCount: Int = 0
  private var middleMinCount: Int = 0
  private var keywordsString: String = ""

  postDataService.addPostChangeListener(this)
  updateTagsInfo()

  /**
   * @return All tags and their total count in a list sorted by their name
   */
  def tags(): List[Tag] = tagsList

  /**
   * @return The minimum count of a tag to be in category 'big'
   */
  def bigTagMinCount(): Int = bigMinCount

  /**
   * @return The minimum count of a tag to be in category 'middle'
   */
  def middleTagMinCount(): Int = middleMinCount

  /**
   * @return All tags sorted by their count as a comma separated list in a String
   */
  def keywords(): String = keywordsString

  /**
   * Gets tags which have the given term in their name
   *
   * @param term
   * @return
   */
  def getByNameParticle(term: String): List[Tag] = {
    val lowercaseTerm = term.toLowerCase
    tagsList.filter(_.name.toLowerCase.contains(lowercaseTerm))
  }

  override def update(post: Post) = {
    updateTagsInfo()
  }

  protected def postDataService: PostDataService

  protected def updateTagsInfo() = {
    /*
     * tagsList and keywordsString
     */
    val allListedPosts = postDataService.allAsList.filter(_.listed)
    val allTags = allListedPosts.flatMap(_.tags)
    tagsList = allTags.distinct.map(key ⇒ Tag(key, allTags.count(_ == key))).sortBy(tag ⇒ tag.name.toLowerCase)
    keywordsString = tagsList.sortBy(-_.count).map(_.name).mkString(",")
    /*
     * counts
     */
    val groupedTags = tagsList.groupBy(_.count)
    groupedTags.keys.size match {
      // 0, 1 different counts? => all middle
      case n if n < 2 ⇒ {
        bigMinCount = groupedTags.values.headOption.getOrElse(List()).headOption.getOrElse(Tag("", 0)).count + 1
        middleMinCount = 0
      }
      // 2-3 different counts? => big + middle (+ small)
      case n if n < 4 ⇒ {
        bigMinCount = groupedTags.keys.max
        middleMinCount = groupedTags.keys.toList.sortBy(-_).drop(1).head
      }
      // 4+ counts
      case _ ⇒ {
        val countsList = tagsList.map(_.count)
        var countToTagsCount: Map[Int, Int] = Map()
        for (count ← countsList)
          if (!countToTagsCount.isDefinedAt(count))
            countToTagsCount += (count -> countsList.count(_ == count))
        val tagClusterRes =
          tagClustering(countToTagsCount, tagsList.size, countToTagsCount.keys.min, countToTagsCount.keys.max)
        bigMinCount = tagClusterRes._2
        middleMinCount = tagClusterRes._1
      }
    }
  }

  /**
   * This function calculates a middleMinCount and a bigMinCount for the tags
   * so that big and small tags both are slightly more than 25% each.
   *
   * @param countToTagsCount
   * 	A map mapping the count of a tag used against the number of how many tags have been
   *  	used exactly that often. E.g Tag A and B have been used 4 times and tag C 3 times,
   *   then the map would look like Map(4 -> 2, 3 -> 1).
   * @param totalTags Tags count total
   * @param sugMid The suggested middleMinCount
   * @param sugBig The suggested bigMinCount
   * @return (middleMinCount, bigMinCount)
   */
  protected def tagClustering(countToTagsCount: Map[Int, Int], totalTags: Int, sugMid: Int, sugBig: Int): (Int, Int) = {
    def negScore(sMid: Int, sBig: Int) = {
      def perc(filterFun: PartialFunction[(Int, Int), Boolean]) = {
        val filteredMap = countToTagsCount filter {
          filterFun
        }
        if (filteredMap.isEmpty)
          0
        else
          filteredMap.values.reduce(_ + _).toDouble / totalTags
      }

      val smallPerc = perc {
        case (count, _) ⇒ count < sMid
      }
      val midPerc = perc {
        case (count, _) ⇒ count >= sMid && count < sBig
      }
      val bigPerc = perc {
        case (count, _) ⇒ count >= sBig
      }
      val perfectPerc = 1d / 3
      Math.abs(perfectPerc - smallPerc) +
        Math.abs(perfectPerc - midPerc) +
        Math.abs(perfectPerc - bigPerc)
    }

    val sugsToNegScore: Seq[((Int, Int), Double)] =
      for {
        sMid ← sugMid to sugBig;
        sBig ← sugMid to sugBig
        if sBig >= sMid
      } yield {
        ((sMid, sBig), negScore(sMid, sBig))
      }
    val sugsToNegScoreMap: Map[(Int, Int), Double] = sugsToNegScore.toMap
    val bestScore = sugsToNegScoreMap.minBy {
      case (_, negScore) ⇒ negScore
    }._2
    val sug = sugsToNegScoreMap.find {
      case (_, negScore) ⇒ negScore == bestScore
    }.get._1
    sug
  }

}
