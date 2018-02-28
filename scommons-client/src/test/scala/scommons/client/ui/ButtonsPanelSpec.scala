package scommons.client.ui

import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import scommons.client.TestSpec
import scommons.client.test.raw.ReactTestUtils._
import scommons.client.test.raw.ShallowRenderer.ComponentInstance
import scommons.client.ui.ButtonImagesCss._
import scommons.client.util.ActionsData

class ButtonsPanelSpec extends TestSpec {

  it should "call onCommand when click on button" in {
    //given
    val dispatch = mockFunction[Any, Any]
    val onCommand = mockFunction[(String, Any => Any), Any]
    val onCommandP = new PartialFunction[(String, Any => Any), Any] {
      def isDefinedAt(x: (String, Any => Any)) = true
      def apply(v: (String, Any => Any)) = onCommand(v)
    }
    val data = ImageButtonData("accept", accept, acceptDisabled, "test button")
    val comp = renderIntoDocument(<(ButtonsPanel())(^.wrapped := ButtonsPanelProps(
      List(data), ActionsData(Set(data.command), onCommandP), dispatch
    ))())
    val button = findRenderedDOMComponentWithClass(comp, "btn")

    //then
    onCommand.expects((data.command, dispatch))

    //when
    Simulate.click(button)
  }

  it should "render buttons panel with custom class" in {
    //given
    val b1 = SimpleButtonData("accept", "test button 1")
    val b2 = ImageButtonData("add", add, addDisabled, "test button 2")
    val props = ButtonsPanelProps(
      List(b1, b2),
      ActionsData.empty.copy(enabledCommands = Set(b1.command), focusedCommand = Some(b1.command)),
      className = Some("custom-class")
    )
    val component = <(ButtonsPanel())(^.wrapped := props)()

    //when
    val result = shallowRender(component)

    //then
    assertButtonsPanel(result, props.className.get, group = false, b1, b2)
  }

  it should "render buttons toolbar" in {
    //given
    val b1 = SimpleButtonData("accept", "test button 1")
    val b2 = ImageButtonData("add", add, addDisabled, "test button 2")
    val props = ButtonsPanelProps(
      List(b1, b2),
      ActionsData.empty.copy(enabledCommands = Set(b1.command), focusedCommand = Some(b1.command))
    )
    val component = <(ButtonsPanel())(^.wrapped := props)()

    //when
    val result: ComponentInstance = shallowRender(component)

    //then
    assertButtonsPanel(result, "btn-toolbar", group = false, b1, b2)
  }

  it should "render buttons group" in {
    //given
    val b1 = SimpleButtonData("accept", "test button 1")
    val b2 = ImageButtonData("add", add, addDisabled, "test button 2")
    val props = ButtonsPanelProps(
      List(b1, b2),
      ActionsData.empty.copy(enabledCommands = Set(b1.command), focusedCommand = Some(b1.command)),
      group = true
    )
    val component = <(ButtonsPanel())(^.wrapped := props)()

    //when
    val result: ComponentInstance = shallowRender(component)

    //then
    assertButtonsPanel(result, "btn-group", group = true, b1, b2)
  }

  private def assertButtonsPanel(result: ComponentInstance,
                                 className: String,
                                 group: Boolean,
                                 b1: SimpleButtonData,
                                 b2: ImageButtonData): Unit = {

    assertDOMComponent(result, <.div(^.className := className)(), { case List(simpleBtn, imageBtn) =>
      assertComponent(simpleBtn, SimpleButton(), { simpleBtnProps: SimpleButtonProps =>
        inside(simpleBtnProps) { case SimpleButtonProps(data, _, disabled, requestFocus) =>
          data shouldBe b1
          disabled shouldBe false
          requestFocus shouldBe true
        }
      })
      assertComponent(imageBtn, ImageButton(), { imageBtnProps: ImageButtonProps =>
        inside(imageBtnProps) { case ImageButtonProps(data, _, disabled, showTextAsTitle, requestFocus) =>
          data shouldBe b2
          disabled shouldBe true
          showTextAsTitle shouldBe group
          requestFocus shouldBe false
        }
      })
    })
  }
}
