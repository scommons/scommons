package scommons.showcase.client

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import org.scalajs.dom
import scommons.client.ui._
import scommons.client.ui.panel._
import scommons.client.util.ActionsData

case class ModalState(showModal: Boolean = false,
                      showInputBox: Boolean = false,
                      showLoading: Boolean = false,
                      showStatus: Boolean = false)

object PopupsDemo {

  def apply(): ReactClass = reactClass

  private lazy val reactClass = React.createClass[Unit, ModalState](
    getInitialState = { _ => ModalState() },
    render = { self =>
      <.div()(
        <.h2()("Modal"),
        <.hr()(),
        <.p()(
          <(ImageButton())(^.wrapped := ImageButtonProps(Buttons.OK.copy(text = "Modal", primary = true), { () =>
            self.setState(_.copy(showModal = true))
          }))(),
          <(Modal())(^.wrapped := ModalProps(
            self.state.showModal,
            Some("Modal header"),
            List(Buttons.OK, Buttons.CANCEL),
            ActionsData(Set(Buttons.OK.command, Buttons.CANCEL.command), {
              case Buttons.CANCEL.command => self.setState(_.copy(showModal = false))
              case _ =>
            }),
            onClose = { () =>
              self.setState(_.copy(showModal = false))
            }
          ))(
            <.p()("One fine body...")
          )
        ),

        <.h2()("InputBox"),
        <.hr()(),
        <.p()(
          <(ImageButton())(^.wrapped := ImageButtonProps(Buttons.OK.copy(text = "InputBox", primary = true), { () =>
            self.setState(_.copy(showInputBox = true))
          }))(),
          <(InputBox())(^.wrapped := InputBoxProps(
            self.state.showInputBox,
            "Please, enter a value",
            onOk = { _ =>
              self.setState(_.copy(showInputBox = false))
            },
            onCancel = { () =>
              self.setState(_.copy(showInputBox = false))
            },
            initialValue = Some("initial value")
          ))()
        ),

        <.h2()("Popups"),
        <.hr()(),
        <.p()(
          <(ImageButton())(^.wrapped := ImageButtonProps(
            Buttons.OK.copy(text = "Show Loading and Status", primary = true), { () =>
              self.setState(_.copy(showLoading = true, showStatus = true))

              var timerId = 0
              timerId = dom.window.setInterval({ () =>
                self.setState(_.copy(showLoading = false))
                dom.window.clearInterval(timerId)
              }, 3000)
            }
          ))(),
          <(LoadingPopup())(^.wrapped := LoadingPopupProps(show = self.state.showLoading))(),
          <(StatusPopup())(^.wrapped := StatusPopupProps("Fetching data...", show = self.state.showStatus, { () =>
            if (!self.state.showLoading) {
              self.setState(_.copy(showStatus = false))
            }
          }))()
        )
      )
    }
  )
}
