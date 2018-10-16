package scommons.client.ui.popup

import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import org.scalatest.{Assertion, Succeeded}
import scommons.client.test.TestSpec
import scommons.client.test.raw.ShallowRenderer.ComponentInstance
import scommons.client.ui.Buttons
import scommons.client.util.ActionsData

class ModalSpec extends TestSpec {

  it should "render closable modal with header" in {
    //given
    val props = getModalProps()
    val component = <(Modal())(^.wrapped := props)(
      <.p()("some children")
    )

    //when
    val result = shallowRender(component)

    //then
    assertModal(result, props)
  }

  it should "render non-closable modal with header" in {
    //given
    val props = getModalProps(closable = false)
    val component = <(Modal())(^.wrapped := props)(
      <.p()("some children")
    )

    //when
    val result = shallowRender(component)

    //then
    assertModal(result, props)
  }

  it should "render modal without header" in {
    //given
    val props = getModalProps(header = None)
    val component = <(Modal())(^.wrapped := props)(
      <.p()("some children")
    )

    //when
    val result = shallowRender(component)

    //then
    assertComponent(result, Popup)({ popupProps =>
      popupProps shouldBe PopupProps(
        show = props.show,
        onClose = props.onClose,
        closable = props.closable,
        onOpen = props.onOpen
      )
    }, { case List(body, footer) =>
      assertComponent(body, ModalBody)({ _ => Succeeded }, { case List(child) =>
        assertDOMComponent(child, <.p()("some children"))
      })
      assertComponent(footer, ModalFooter) { footerProps =>
        footerProps shouldBe ModalFooterProps(props.buttons, props.actions, props.dispatch)
      }
    })
  }

  private def assertModal(result: ComponentInstance, props: ModalProps): Assertion = {
    assertComponent(result, Popup)({ popupProps =>
      popupProps shouldBe PopupProps(
        show = props.show,
        onClose = props.onClose,
        closable = props.closable,
        onOpen = props.onOpen
      )
    }, { case List(header, body, footer) =>
      assertComponent(header, ModalHeader) { headerProps: ModalHeaderProps =>
        headerProps shouldBe ModalHeaderProps(props.header.get, props.onClose, closable = props.closable)
      }
      assertComponent(body, ModalBody)({ _ => Succeeded }, { case List(child) =>
        assertDOMComponent(child, <.p()("some children"))
      })
      assertComponent(footer, ModalFooter) { footerProps =>
        footerProps shouldBe ModalFooterProps(props.buttons, props.actions, props.dispatch)
      }
    })
  }

  private def getModalProps(header: Option[String] = Some("test header"),
                            onClose: () => Unit = () => (),
                            closable: Boolean = true,
                            onOpen: () => Unit = () => ()): ModalProps = ModalProps(
    show = true,
    header,
    List(Buttons.OK, Buttons.CANCEL),
    ActionsData.empty.copy(enabledCommands = Set(Buttons.OK.command, Buttons.CANCEL.command)),
    _ => (),
    onClose,
    closable,
    onOpen
  )
}
