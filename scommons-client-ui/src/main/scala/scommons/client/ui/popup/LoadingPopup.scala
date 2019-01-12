package scommons.client.ui.popup

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import scommons.react.UiComponent

case class LoadingPopupProps(show: Boolean)

object LoadingPopup extends UiComponent[LoadingPopupProps] {

  protected def create(): ReactClass = React.createClass[PropsType, Unit] { self =>
    val props = self.props.wrapped

    <(Popup())(^.wrapped := PopupProps(
      show = props.show,
      onClose = () => (),
      closable = false,
      focusable = false,
      overlayClass = PopupCss.loadingOverlay,
      popupClass = PopupCss.loadingContent
    ))(
      <.img(^.className := PopupCss.loadingImg, ^.src := "")()
    )
  }
}
