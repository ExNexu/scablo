package backend.data.service

import scala.reflect.ClassTag
import backend.data.dao.PostDaoComponent
import model.blog.Post
import model.blog.PostEnriched

/**
  * The data service trait for posts
  *
  * @author Stefan Bleibinhaus
  *
  */
trait PostDataService extends BaseDataService[Post] {
  this: PostDaoComponent =>

  protected val enrichedPostObject = PostEnriched
  private var postChangeListener: List[PostChangeListener] = Nil

  override def save(post: Post): Post = {
    val savedPost = super.save(post)
    updateListener(savedPost)
    savedPost
  }
  override def delete(post: Post): Unit = {
    super.delete(post)
    updateListener(post)
  }

  override def delete(id: String): Unit =
    delete(get(id).get)

  override def update(post: Post): Post = {
    val updatedPost = super.update(post)
    updateListener(updatedPost)
    updatedPost
  }
  override def saveOrUpdate(post: Post): Post = {
    val updatedPost = super.saveOrUpdate(post)
    updateListener(updatedPost)
    updatedPost
  }

  /**
    * Adds this post change listener as listener, which will be informed on post updates
    *
    * @param pCL
    */
  def addPostChangeListener(pCL: PostChangeListener) = postChangeListener = pCL :: postChangeListener

  private def updateListener(post: Post) =
    postChangeListener.foreach(_.update(post))
}