package controllers

import backend.engine.TextCompileException
import model.blog.{ PostEnriched, StaticPage, User }
import model.ui.{ BreadcrumbItem, MetaTags, PostDraft, SidebarContainer }
import play.api.data.{ Form, FormError }
import play.api.data.Forms.{ mapping, nonEmptyText, text }
import play.api.mvc.{ Action, SimpleResult }

/**
  * The admin controller contains all admin actions.
  * The order of the methods is the same as in the routes file.
  *
  * @author Stefan Bleibinhaus
  *
  */
object AdminController extends BaseController {
  private val draftForm = Form(
    mapping(
      "id" -> text,
      "Title" -> nonEmptyText,
      "Text" -> text,
      "Tags" -> text)(PostDraft.apply)(PostDraft.unapplyToStrings))
  private val updateAboutForm = Form("aboutText" -> text)
  private var savedDraft: Option[PostDraft] = None
  private val adminBcItem = BreadcrumbItem("Admin", "/blog/admin", "icon-globe")

  /**
    * Shows the admin page
    *
    * @return
    */
  def admin = Action {
    implicit request =>
      withUser {
        user =>
          {
            Ok(views.html.admin.adminpage(
              title("Admin"),
              Some(user),
              SidebarContainer(),
              fileDataService.files))
          }
      }
  }

  /**
    * Shows the create a new post page
    *
    * @return
    */
  def createPost = Action {
    implicit request =>
      withUser {
        user =>
          {
            val breadcrumb = List(
              adminBcItem,
              BreadcrumbItem("Create Post", "/blog/createPost", "icon-edit"))
            Ok(views.html.admin.editPost(
              title("Create Post"),
              Some(user),
              breadcrumb,
              SidebarContainer(),
              draftForm.fill(PostDraft.templateDraft)))
          }
      }
  }

  /**
    * Shows the edit post page. If there is a postId given, it tries to edit that post,
    * if not, it tries to edit the most recent draft.
    *
    * @param postId
    * @return
    */
  def editPost(postId: Option[String]) = Action {
    implicit request =>
      withUser {
        user =>
          postId match {
            // if there is a postId, we are editing an existing post
            case Some(postId) =>
              postDataService.get(postId) match {
                case Some(post) => {
                  // Are we editing the draft of an existing post?
                  if (savedDraft.isDefined && savedDraft.get.id.isDefined && savedDraft.get.id.get == postId) {
                    val breadcrumb = List(
                      adminBcItem,
                      BreadcrumbItem("Edit Post", "/blog/editPost?postId=" + postId, "icon-edit"),
                      BreadcrumbItem(savedDraft.get.title, "/blog/" + post.relUrl, "icon-caret-right"))
                    Ok(views.html.admin.editPost(
                      title("Edit post " + savedDraft.get.title),
                      Some(user),
                      breadcrumb,
                      SidebarContainer(),
                      draftForm.fill(savedDraft.get)))
                    // Initial edit of an existing post
                  } else {
                    val breadcrumb = List(
                      adminBcItem,
                      BreadcrumbItem("Edit Post", "/blog/editPost?postId=" + postId, "icon-edit"),
                      BreadcrumbItem(post.title, "/blog/" + post.relUrl, "icon-caret-right"))
                    val postDraftForm = draftForm.fill(PostDraft.createDraft(post))
                    Ok(views.html.admin.editPost(
                      title("Edit post " + post.title),
                      Some(user),
                      breadcrumb,
                      SidebarContainer(),
                      postDraftForm))
                  }
                }
                case None => redirectToRoot
              }
            // no postId => new post
            case None =>
              savedDraft match {
                // new post: existing draft
                case Some(draft) => {
                  val breadcrumb = List(
                    adminBcItem,
                    BreadcrumbItem("Edit Post", "/blog/editPost", "icon-edit"),
                    BreadcrumbItem(draft.title, "/blog/showDraft", "icon-caret-right"))
                  Ok(views.html.admin.editPost(
                    title("Edit post " + draft.title),
                    Some(user),
                    breadcrumb,
                    SidebarContainer(),
                    draftForm.fill(draft)))
                }
                // new post: new draft
                case None =>
                  Redirect(routes.AdminController.createPost)
              }
          }
      }
  }

  /**
    * Deletes the corresponding post of the given id
    *
    * @param postId
    * @return
    */
  def deletePost(postId: String) = Action {
    implicit request =>
      withUser {
        user =>
          postDataService.delete(postId)
          redirectToRoot
      }
  }

  /**
    * Discards the most recent draft
    *
    * @return
    */
  def discardDraft() = Action {
    implicit request =>
      withUser {
        user =>
          val draftId = savedDraft match {
            case Some(draft) => draft.id match {
              case Some(id) => Some(id)
              case None => None
            }
            case None => None
          }
          savedDraft = None
          draftId match {
            case Some(id) => Redirect("/blog/" + postDataService.get(id).get.relUrl)
            case None => redirectToRoot
          }
      }
  }

  /**
    * Handles the submit of the post edit form.
    * Results in either a bad request if the form is not valid or
    * shows the edit as draft.
    *
    * @return
    */
  def editPostFormSubmit = Action {
    implicit request =>
      withUser {
        user =>
          val mappedForm = draftForm.bindFromRequest()
          mappedForm.fold(
            // form has errors
            formWithErrors => {
              val breadcrumb = List(
                adminBcItem,
                BreadcrumbItem("Edit Post", None, Some("icon-edit")))
              BadRequest(views.html.admin.editPost(
                title("Edit Post"),
                Some(user),
                breadcrumb,
                SidebarContainer(),
                formWithErrors))
              // form is fine, show draft
            }, draft => {
              savedDraft = Some(draft)
              Redirect(routes.AdminController.showDraft)
            })
      }
  }

  /**
    * Displays the current draft
    *
    * @return
    */
  def showDraft = Action {
    implicit request =>
      withUser {
        user =>
          savedDraft match {
            // we have some draft
            case Some(draft) =>
              draft.id match {
                // we have a draft of an existing post
                case Some(id) => postDataService.get(id) match {
                  case Some(origPost) =>
                    showDraftTry(user, draft, origPost, PostDraft.createPostEnrichedPostFun)
                  case None => redirectToRoot
                }
                // we have a draft of a new post
                case None =>
                  showDraftTry(user, draft, user, PostDraft.createPostEnrichedUserFun)
              }
            // we have no draft at all
            case None => redirectToRoot
          }
      }
  }

  /**
    * Publishes the recent draft.
    * Either updates an existing post or publishes a new post depending on the postId of the current draft.
    *
    * @return
    */
  def publishDraft = Action {
    implicit request =>
      withUser {
        user =>
          savedDraft match {
            case Some(draft) =>
              draft.id match {
                case Some(id) => postDataService.get(id) match {
                  case Some(origPost) => {
                    val post = postDataService.update(PostDraft.createPost(draft, origPost))
                    Redirect("/blog/" + post.relUrl)
                  }
                  case None => redirectToRoot
                }
                case None => {
                  val post = postDataService.save(PostDraft.createPost(draft, user))
                  Redirect("/blog/" + post.relUrl)
                }
              }
            case None =>
              redirectToRoot
          }
      }
  }

  /**
    * Handles an update of the about page
    *
    * @return
    */
  def updateAboutSubmit = Action {
    implicit request =>
      withUser {
        user =>
          {
            updateAboutForm.bindFromRequest().fold(
              formWithErrors => {
                redirectToAbout
              }, text => {
                val aboutPage = staticPageDataService.getByName("about").getOrElse(StaticPage("about"))
                staticPageDataService.saveOrUpdate(aboutPage.copy(text = text))
                redirectToAbout
              })
          }
      }
  }

  /**
    * Handles the file upload
    *
    * @return
    */
  def uploadFile = Action(parse.multipartFormData) {
    implicit request =>
      withUser {
        user =>
          {
            request.body.file("file").map {
              file =>
                val name = file.filename
                fileDataService.save(name, file.ref)
                redirectToAdmin
            }.getOrElse {
              redirectToAdmin
            }
          }
      }
  }

  /**
    * Deletes the file of the given name
    *
    * @param name
    * @return
    */
  def deleteFile(name: String) = Action {
    implicit request =>
      withUser {
        user =>
          {
            fileDataService.remove(name)
            redirectToAdmin
          }
      }
  }

  private def showDraftTry[A](
    user: User,
    draft: PostDraft,
    createEle: A,
    createFun: (PostDraft, A) => PostEnriched): SimpleResult[_] = {
    try {
      val enrichedPost = createFun(draft, createEle)
      val monthString = monthToString(enrichedPost.created.getMonthOfYear())
      val breadcrumb = List(
        homeBcItem,
        BreadcrumbItem(enrichedPost.created.getYear().toString, "/blog/" + enrichedPost.created.getYear()),
        BreadcrumbItem(monthString, "/blog/" + enrichedPost.created.getYear() + "/" + monthString),
        BreadcrumbItem(enrichedPost.title, "/blog/" + enrichedPost.relUrl, "icon-caret-right"))
      Ok(views.html.content.post(
        title(draft.title),
        MetaTags.empty,
        Some(user),
        breadcrumb,
        SidebarContainer(),
        Some(enrichedPost),
        true))
    } catch {
      case ex: TextCompileException => {
        val breadcrumb = List(
          adminBcItem,
          BreadcrumbItem("Edit Post"))
        val formWithErrors =
          draftForm.fill(draft).copy(errors = List(FormError("", ex.getMessage())))
        BadRequest(views.html.admin.editPost(
          title("Edit Post"),
          Some(user),
          breadcrumb,
          SidebarContainer(),
          formWithErrors))
      }
    }
  }
}