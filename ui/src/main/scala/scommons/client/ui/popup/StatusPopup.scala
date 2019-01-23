package scommons.client.ui.popup

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import scommons.react.UiComponent

case class StatusPopupProps(text: String,
                            show: Boolean,
                            onHide: () => Unit)

object StatusPopup extends UiComponent[StatusPopupProps] {

  protected def create(): ReactClass = React.createClass[PropsType, Unit] { self =>
    val props = self.props.wrapped

    <(Popup())(^.wrapped := PopupProps(
      show = props.show,
      onClose = () => (),
      focusable = false,
      overlayClass = "scommons-modal-no-overlay",
      popupClass = PopupCss.statusContent
    ))(
      <(WithAutoHide())(^.wrapped := WithAutoHideProps(props.onHide))(props.text)
    )
  }
}
