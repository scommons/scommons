package scommons.client.ui.popup

import scommons.client.ui.Buttons
import scommons.client.ui.icon.IconCss
import scommons.react.test.TestSpec
import scommons.react.test.raw.ShallowInstance
import scommons.react.test.util.ShallowRendererUtils

class OkPopupSpec extends TestSpec with ShallowRendererUtils {

  it should "call onClose function when onOkCommand" in {
    //given
    val onClose = mockFunction[Unit]
    val props = getOkPopupProps("Test message", onClose = onClose)
    val component = shallowRender(<(OkPopup())(^.wrapped := props)())
    val modalProps = findComponentProps(component, Modal)

    //then
    onClose.expects()

    //when
    modalProps.actions.onCommand(_ => ())(Buttons.OK.command)
  }

  it should "render component with image" in {
    //given
    val props = getOkPopupProps("Test message", image = Some(IconCss.dialogInformation))
    val component = <(OkPopup())(^.wrapped := props)()

    //when
    val result = shallowRender(component)

    //then
    assertOkPopup(result, props)
  }

  it should "render component without image" in {
    //given
    val props = getOkPopupProps("Test message")
    val component = <(OkPopup())(^.wrapped := props)()

    //when
    val result = shallowRender(component)

    //then
    assertOkPopup(result, props)
  }

  it should "set focusedCommand when onOpen" in {
    //given
    val props = getOkPopupProps("Test message")
    val renderer = createRenderer()
    renderer.render(<(OkPopup())(^.wrapped := props)())
    val comp = renderer.getRenderOutput()
    val modalProps = findComponentProps(comp, Modal)
    modalProps.actions.focusedCommand shouldBe None

    //when
    modalProps.onOpen()

    //then
    val updatedComp = renderer.getRenderOutput()
    val updatedModalProps = findComponentProps(updatedComp, Modal)
    updatedModalProps.actions.focusedCommand shouldBe Some(Buttons.OK.command)
  }

  private def getOkPopupProps(message: String,
                              onClose: () => Unit = () => (),
                              image: Option[String] = None): OkPopupProps = OkPopupProps(
    message = message,
    onClose = onClose,
    image = image
  )

  private def assertOkPopup(result: ShallowInstance, props: OkPopupProps): Unit = {
    val actionCommands = Set(Buttons.OK.command)

    assertComponent(result, Modal)({
      case ModalProps(header, buttons, actions, _, onClose, closable, _) =>
        header shouldBe None
        buttons shouldBe List(Buttons.OK)
        actions.enabledCommands shouldBe actionCommands
        actions.focusedCommand shouldBe None
        onClose shouldBe props.onClose
        closable shouldBe true
    }, { case List(modalChild) =>
      assertNativeComponent(modalChild, <.div(^.className := "row-fluid")(), { children =>
        val (img, p) = children match {
          case List(pElem) => (None, pElem)
          case List(imgElem, pElem) => (Some(imgElem), pElem)
        }
        props.image.foreach { image =>
          img should not be None
          assertNativeComponent(img.get, <.img(^.className := image, ^.src := "")())
        }
        assertNativeComponent(p, <.p()(props.message))
      })
    })
  }
}
