package scommons.client.app

import scommons.react._

case class AppHeaderProps(name: String = "App",
                          user: String = "user")

object AppHeader extends FunctionComponent[AppHeaderProps] {

  override protected def create(): ReactClass = {
    ReactMemo[Props](super.create(), { (prevProps, nextProps) =>
      prevProps.wrapped == nextProps.wrapped
    })
  }
  
  protected def render(compProps: Props): ReactElement = {
    val props = compProps.wrapped
    
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
  }
}
