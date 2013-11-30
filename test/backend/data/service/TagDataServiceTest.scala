package backend.data.service

import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

class TagDataServiceTest extends Specification with Mockito with TagDataService {
  "The tag clustering method" should {

    "sort a countToTagsCount with 2,2,1,1 into 2>2>2" in {
      val countToTagsCount = Map(10 → 2, 7 → 2, 5 → 1, 4 → 1)
      tagClustering(countToTagsCount, 4, 4, 10) must equalTo((7, 9))
    }

    "sort a countToTagsCount with 5,2,1,1 into 5>2>2" in {
      val countToTagsCount = Map(10 → 5, 7 → 2, 5 → 1, 4 → 1)
      tagClustering(countToTagsCount, 9, 4, 10) must equalTo((7, 9))
    }

    "sort a countToTagsCount with 1,5,2,1,1 into 1>5+2>2" in {
      val countToTagsCount = Map(12 → 1, 10 → 5, 7 → 2, 5 → 1, 4 → 1)
      tagClustering(countToTagsCount, 10, 4, 12) must equalTo((10, 12))
    }

  }

  override protected def postDataService = mock[PostDataService]
  override protected def updateTagsInfo = {}
}
