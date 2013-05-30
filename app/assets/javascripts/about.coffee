#
# configure
#

CKEDITOR.disableAutoInline = true
CKEDITOR.inline('aboutEdit')

#
# save
#

window.updateAboutText = ->
  form = document.createElement('form')
  form.setAttribute('method', 'post')
  form.setAttribute('action', 'updateAbout')
  hiddenField = document.createElement('input')
  hiddenField.setAttribute('type', 'hidden')
  hiddenField.setAttribute('name', 'aboutText')
  hiddenField.setAttribute('value', CKEDITOR.instances.aboutEdit.getData())
  form.appendChild(hiddenField)
  document.body.appendChild(form)
  form.submit()