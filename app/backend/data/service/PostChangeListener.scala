package backend.data.service

import model.blog.Post

/**
  * A trait for listeners of post changes
  *
  * @author Stefan Bleibinhaus
  *
  */
trait PostChangeListener {
  /**
    * Called by the observed object, when a post has been added or changed
    *
    * @param post
    */
  def update(post: Post)
}