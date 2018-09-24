package scommons.client.ui.popup

import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import scommons.client.test.TestSpec
import scommons.client.test.raw.ShallowRenderer.ComponentInstance
import scommons.client.ui.SimpleButtonData
import scommons.client.ui.icon.IconCss
import scommons.client.ui.popup.YesNoCancelOption._

class YesNoPopupSpec extends TestSpec {

  it should "call onSelect(Yes) function when Yes selected" in {
    //given
    val onSelect = mockFunction[YesNoCancelOption, Unit]
    val props = getYesNoPopupProps("Test message", onSelect = onSelect)
    val component = shallowRender(<(YesNoPopup())(^.wrapped := props)())
    val modalProps = findComponentProps(component, Modal)

    //then
    onSelect.expects(Yes)

    //when
    modalProps.actions.onCommand(_ => ())(Yes.command)
  }

  it should "call onSelect(No) function when No selected" in {
    //given
    val onSelect = mockFunction[YesNoCancelOption, Unit]
    val props = getYesNoPopupProps("Test message", onSelect = onSelect)
    val component = shallowRender(<(YesNoPopup())(^.wrapped := props)())
    val modalProps = findComponentProps(component, Modal)

    //then
    onSelect.expects(No)

    //when
    modalProps.actions.onCommand(_ => ())(No.command)
  }

  it should "render component with image" in {
    //given
    val props = getYesNoPopupProps("Test message", image = Some(IconCss.dialogInformation))
    val component = <(YesNoPopup())(^.wrapped := props)()

    //when
    val result = shallowRender(component)

    //then
    assertYesNoPopup(result, props)
  }

  it should "render component without image" in {
    //given
    val props = getYesNoPopupProps("Test message")
    val component = <(YesNoPopup())(^.wrapped := props)()

    //when
    val result = shallowRender(component)

    //then
    assertYesNoPopup(result, props)
  }

  it should "set focusedCommand when onOpen" in {
    //given
    val props = getYesNoPopupProps("Test message")
    val renderer = createRenderer()
    renderer.render(<(YesNoPopup())(^.wrapped := props)())
    val comp = renderer.getRenderOutput()
    val modalProps = findComponentProps(comp, Modal)
    modalProps.actions.focusedCommand shouldBe None

    //when
    modalProps.onOpen()

    //then
    val updatedComp = renderer.getRenderOutput()
    val updatedModalProps = findComponentProps(updatedComp, Modal)
    updatedModalProps.actions.focusedCommand shouldBe Some(props.selected.command)
  }

  it should "reset focusedCommand when componentWillReceiveProps" in {
    //given
    val prevProps = getYesNoPopupProps("Test message")
    val renderer = createRenderer()
    renderer.render(<(YesNoPopup())(^.wrapped := prevProps)())
    val comp = renderer.getRenderOutput()
    val modalProps = findComponentProps(comp, Modal)
    modalProps.actions.focusedCommand shouldBe None
    modalProps.onOpen()
    val compV2 = renderer.getRenderOutput()
    val modalPropsV2 = findComponentProps(compV2, Modal)
    modalPropsV2.actions.focusedCommand shouldBe Some(prevProps.selected.command)
    val props = getYesNoPopupProps("New message")

    //when
    renderer.render(<(YesNoPopup())(^.wrapped := props)())

    //then
    val compV3 = renderer.getRenderOutput()
    val modalPropsV3 = findComponentProps(compV3, Modal)
    modalPropsV3.actions.focusedCommand shouldBe None
  }

  private def getYesNoPopupProps(message: String,
                                 onSelect: YesNoCancelOption => Unit = _ => (),
                                 selected: YesNoCancelOption = Yes,
                                 image: Option[String] = None,
                                 show: Boolean = true): YesNoPopupProps = YesNoPopupProps(
    show = show,
    message = message,
    onSelect = onSelect,
    selected = selected,
    image = image
  )

  private def assertYesNoPopup(result: ComponentInstance, props: YesNoPopupProps): Unit = {
    val expectedButtons = List(
      SimpleButtonData(Yes.command, "Yes", props.selected == Yes),
      SimpleButtonData(No.command, "No", props.selected == No)
    )
    val enabledCommands = Set(Yes.command, No.command)

    assertComponent(result, Modal(), { modalProps: ModalProps =>
      inside(modalProps) { case ModalProps(show, header, buttons, actions, _, _, closable, _) =>
        show shouldBe props.show
        header shouldBe None
        buttons shouldBe expectedButtons
        actions.enabledCommands shouldBe enabledCommands
        actions.focusedCommand shouldBe None
        closable shouldBe false
      }
    }, { case List(modalChild) =>
      assertDOMComponent(modalChild, <.div(^.className := "row-fluid")(), { children =>
        val (img, p) = children match {
          case List(pElem) => (None, pElem)
          case List(imgElem, pElem) => (Some(imgElem), pElem)
        }
        props.image.foreach { image =>
          img should not be None
          assertDOMComponent(img.get, <.img(^.className := image, ^.src := "")())
        }
        assertDOMComponent(p, <.p()(props.message))
      })
    })
  }
}
