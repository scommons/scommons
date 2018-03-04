package scommons.client.ui.popup

import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import scommons.client.TestSpec
import scommons.client.ui.{Buttons, ButtonsPanel, ButtonsPanelProps}
import scommons.client.util.ActionsData

class ModalFooterSpec extends TestSpec {

  it should "render component with correct props" in {
    //given
    val props = ModalFooterProps(
      List(Buttons.OK, Buttons.CANCEL),
      ActionsData.empty
    )
    val component = <(ModalFooter())(^.wrapped := props)()

    //when
    val result = shallowRender(component)

    //then
    assertComponent(result, ButtonsPanel(), { buttonsPanelProps: ButtonsPanelProps =>
      inside(buttonsPanelProps) { case ButtonsPanelProps(buttons, actions, dispatch, group, className) =>
        buttons shouldBe props.buttons
        actions shouldBe props.actions
        dispatch shouldBe props.dispatch
        group shouldBe false
        className shouldBe Some("modal-footer")
      }
    })
  }
}
