package scommons.client.test

import io.github.shogowada.scalajs.reactjs.VirtualDOM
import io.github.shogowada.scalajs.reactjs.elements.ReactElement
import org.scalamock.matchers.Matchers
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Inside}
import scommons.client.test.raw.ReactTestUtils
import scommons.client.test.raw.ReactTestUtils._
import scommons.client.test.raw.ShallowRenderer.ComponentInstance
import scommons.client.ui.UiComponent

trait TestSpec extends FlatSpec
  with Matchers
  with Inside
  with ShallowRendererUtils
  with MockFactory {

  lazy val < : VirtualDOM.VirtualDOMElements = VirtualDOM.<

  def findComponentProps[T](renderedComp: ComponentInstance, searchComp: UiComponent[T]): T = {
    getComponentProps[T](findComponentWithType(renderedComp, searchComp.reactClass))
  }

  def renderIntoDocument(element: ReactElement): Instance = ReactTestUtils.renderIntoDocument(element)

  def findRenderedComponentProps[T](tree: Instance, searchComp: UiComponent[T]): T = {
    getComponentProps[T](findRenderedComponentWithType(tree, searchComp.reactClass))
  }
}
