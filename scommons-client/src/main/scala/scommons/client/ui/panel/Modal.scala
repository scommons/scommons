package scommons.client.ui.panel

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import io.github.shogowada.scalajs.reactjs.events.MouseSyntheticEvent
import scommons.client.ui.{ButtonData, ButtonsPanel, ButtonsPanelProps}
import scommons.client.util.ActionsData
import scommons.react.modal.ReactModal._

case class ModalProps(show: Boolean,
                      header: Option[String],
                      buttons: List[ButtonData],
                      actions: ActionsData,
                      onClose: () => Unit,
                      closable: Boolean = true,
                      onOpen: () => Unit = () => ())

object Modal {

  def apply(): ReactClass = reactClass

  private lazy val reactClass = React.createClass[ModalProps, Unit] { self =>
    val props = self.props.wrapped

    val closeButton =
      if (props.closable) {
        Some(<.button(
          ^.`type` := "button",
          ^.className := "close",
          ^.onClick := { _: MouseSyntheticEvent =>
            props.onClose()
          }
        )("×"))
      }
      else None

    <.ReactModal(
      ^.isOpen := props.show,
      ^.shouldCloseOnOverlayClick := props.closable,
      ^.onAfterOpen := props.onOpen,
      ^.onRequestClose := props.onClose,
      ^.overlayClassName := "scommons-modal-overlay",
      ^.modalClassName := "scommons-modal"
    )(
      props.header.map { header =>
        <.div(^.className := "modal-header")(
          closeButton,
          <.h3()(header)
        )
      },
      <.div(^.className := "modal-body")(
        self.props.children
      ),
      <(ButtonsPanel())(^.wrapped := ButtonsPanelProps(
        props.buttons,
        props.actions,
        group = false,
        className = Some("modal-footer")
      ))()
    )
  }
}
