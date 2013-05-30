#
# This file will only be loaded for admin users
# Note: It is accessible for all clients!
#

$('#deletePostLink').click(
  (e) ->
    e.preventDefault()
    if confirm('Do you really want to delete the post?')
        document.location = e.target
)

$('#discardDraftLink').click(
  (e) ->
    e.preventDefault()
    if confirm('Do you really want to discard the draft?')
        document.location = e.target
)

$('.deleteFileLink').click(
  (e) ->
    e.preventDefault()
    if confirm('Do you really want to delete the file?')
        document.location = e.target
)