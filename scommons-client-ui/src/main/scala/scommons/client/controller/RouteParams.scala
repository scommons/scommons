package scommons.client.controller

import io.github.shogowada.scalajs.reactjs.router.RouterProps.RouterProps

class RouteParams(routerProps: RouterProps) {

  def pathParams: PathParams = PathParams(routerProps.location.pathname)
  
  def push(url: String): Unit = routerProps.history.push(url)
}
