#
# content
#

window.loadMore =
  (event, divid, url) ->
    event.preventDefault()
    $('#morePager').remove()
    $('#'+divid).load(
      url,
      -> window.location.hash = divid)

#
# footer
#

adminPopoverLink = $('#admin-popover-link')

adminPopoverLink.popover(
  html: true
  placement: 'top'
  trigger: 'manual'
  title: -> $("#admin-popover-title").html()
  content: -> $("#admin-popover-content").html()
)

adminPopoverLink.click(
  (e) -> e.preventDefault()
)

adminPopoverLink.click(
  ->
    adminPopoverLink.popover('toggle')
    positionAdminPopover()
)

positionAdminPopover = ->
  popover = adminPopoverLink.next('.popover')
  if popover.hasClass('in')
    wrapWidth = $('#wrap').width()
    popoverWidth = popover.width()
    popoverLeftValStr = popover.css('left')
    popoverLeftVal = popoverLeftValStr.substring(0, popoverLeftValStr.search('px'))
    if wrapWidth - popoverWidth < popoverLeftVal
      footerWidth = $('#footer').width()
      leftpx = wrapWidth - (wrapWidth - footerWidth)/2 - popoverWidth
      popover.css({'left': leftpx+'px'})

window.showAdminPopover = ->
  adminPopoverLink.popover('show')
  positionAdminPopover()
  window.location.hash = 'footer'

sizeFooter = ->
  footerContentHeight = $('#footer-content').height()
  currentfooterHeight = $('#footer').height()
  if footerContentHeight > 32 || currentfooterHeight > footerContentHeight + 10
    footerHeight = footerContentHeight + 10
    $('#wrap').css('margin', -> '0px auto -' + (footerHeight+2) + 'px')
    $('#push').css('height', footerHeight)
    $('#footer').css('height', footerHeight)

sizeFooter()
$(window).resize(sizeFooter)