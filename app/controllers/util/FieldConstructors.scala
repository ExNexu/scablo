package controllers.util

import views.html.helper.FieldConstructor

/**
  * FieldConstructors used for our own field constructor template.
  *
  * @author Stefan Bleibinhaus
  *
  */
object FieldConstructors {
  implicit val fields = FieldConstructor(views.html.fieldConstructorTemplate.f)
}