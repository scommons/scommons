package scommons.client.app

import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import scommons.client.TestSpec

class AppHeaderSpec extends TestSpec {

  it should "render the component" in {
    //given
    val props = AppHeaderProps(
      "test app",
      "test user"
    )
    val component = <(AppHeader())(^.wrapped := props)()

    //when
    val result = shallowRender(component)

    //then
    assertDOMComponent(result,
      <.div(^.className := "navbar navbar-inverse navbar-fixed-top")(
        <.div(^.className := "navbar-inner")(
          <.div(^.className := "container-fluid")(
            <.button(
              ^.`type` := "button",
              ^.className := "btn btn-navbar",
              ^("data-toggle") := "collapse",
              ^("data-target") := ".nav-collapse"
            )(
              <.span(^.className := "icon-bar")(),
              <.span(^.className := "icon-bar")(),
              <.span(^.className := "icon-bar")()
            ),
            <.a(^.className := "brand", ^.href := "#")(
              s"${props.name}"
            ),
            <.div(^.className := "nav-collapse collapse")(
              <.ul(^.className := "nav pull-right")(
                <.li(^.className := "dropdown")(
                  <.a(
                    ^.className := "dropdown-toggle",
                    ^("data-toggle") := "dropdown",
                    ^.href := "#"
                  )(
                    <.span()(s"${props.user}"),
                    <.b(^.className := "caret")()
                  )
                )
              )
            )
          )
        )
      )
    )
  }
}
