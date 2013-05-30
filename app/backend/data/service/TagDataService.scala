package backend.data.service

import model.blog.{ Post, Tag }
import play.api.Play.current

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

  private def updateTagsInfo() = {
    /*
     * tagsList
     */
    val allPosts = postDataService.allAsList
    val allTags = allPosts.flatMap(_.tags)
    tagsList = allTags.distinct.map(key => Tag(key, allTags.count(_ == key))).sortBy(tag => tag.name)
    /*
     * counts
     */
    val groupedTags = tagsList.groupBy(_.count)
    groupedTags.keys.size match {
      // 0, 1 different counts? => all middle
      case n if n < 2 => {
        bigMinCount = groupedTags.values.headOption.getOrElse(List()).headOption.getOrElse(Tag("", 0)).count + 1
        middleMinCount = 0
      }
      // 2-3 different counts? => big + middle (+ small)
      case n if n < 4 => {
        bigMinCount = groupedTags.keys.max
        middleMinCount = groupedTags.keys.toList.sortBy(-_).drop(1).head
      }
      // 4+ counts
      case _ => {
        val countsList = tagsList.map(_.count)
        var countToTagsCount: Map[Int, Int] = Map()
        for (count <- countsList)
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
  private def tagClustering(countToTagsCount: Map[Int, Int], totalTags: Int, sugMid: Int, sugBig: Int): (Int, Int) = {
    val minTagsCounted =
      for (n <- 1 to sugMid - 1 if countToTagsCount.isDefinedAt(n))
        yield countToTagsCount(n)
    val minTagsCount = minTagsCounted match {
      case _ if minTagsCounted.size > 0 => minTagsCounted.reduce(_ + _)
      case _ => 0
    }
    if (minTagsCount.toDouble / totalTags < 0.2)
      tagClustering(countToTagsCount, totalTags, sugMid + 1, sugBig)
    else {
      val bigTagsCounted =
        for (n <- sugBig to countToTagsCount.keys.max if countToTagsCount.isDefinedAt(n))
          yield countToTagsCount(n)
      val bigTagsCount = bigTagsCounted match {
        case _ if bigTagsCounted.size > 0 => bigTagsCounted.reduce(_ + _)
        case _ => 0
      }
      if (bigTagsCount.toDouble / totalTags < 0.2)
        tagClustering(countToTagsCount, totalTags, sugMid, sugBig - 1)
      else
        (sugMid, sugBig)
    }
  }
}