package scommons.client.ui.popup

import scommons.react.test.TestSpec
import scommons.react.test.dom.util.TestDOMUtils
import scommons.react.test.util.ShallowRendererUtils

class ModalHeaderSpec extends TestSpec
  with ShallowRendererUtils
  with TestDOMUtils {

  it should "call onClose function when onCloseClick" in {
    //given
    val onClose = mockFunction[Unit]
    val props = ModalHeaderProps("Test Header", onClose = onClose)
    domRender(<(ModalHeader())(^.wrapped := props)())
    val button = domContainer.querySelector(".close")

    //then
    onClose.expects()

    //when
    fireDomEvent(Simulate.click(button))
  }

  it should "render closable header component" in {
    //given
    val props = ModalHeaderProps("Test Header", () => ())
    val component = <(ModalHeader())(^.wrapped := props)()

    //when
    val result = shallowRender(component)

    //then
    assertNativeComponent(result, <.div(^.className := "modal-header")(), { case List(closeButton, h3) =>
      assertNativeComponent(closeButton, <.button(
        ^.`type` := "button",
        ^.className := "close"
      )("×"))
      assertNativeComponent(h3, <.h3()(props.header))
    })
  }

  it should "render non-closable header component" in {
    //given
    val props = ModalHeaderProps("Test Header", () => (), closable = false)
    val component = <(ModalHeader())(^.wrapped := props)()

    //when
    val result = shallowRender(component)

    //then
    assertNativeComponent(result, <.div(^.className := "modal-header")(), { case List(h3) =>
      assertNativeComponent(h3, <.h3()(props.header))
    })
  }
}
