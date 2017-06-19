package scommons.client.test

import io.github.shogowada.scalajs.reactjs.React
import org.scalatest.{FlatSpec, Matchers}
import scommons.client.test.ReactTestUtils._
import scommons.client.test.ReactTestUtilsSpec._
import scommons.client.test.TestVirtualDOM._

import scala.scalajs.js.JavaScriptException

class ReactTestUtilsSpec extends FlatSpec with Matchers {

  it should "test renderIntoDocument" in {
    //given
    val testProps = TestProps("this is a test")
    val testCompClass = React.createClass[TestProps, Unit] { (self) =>
      E.div()(
        s"Hello, ${self.props.wrapped.test}!"
      )
    }
    val testElem = React.createElement(testCompClass, React.wrap(testProps))

    //when
    val result = renderIntoDocument(testElem)

    //then
    result.props.wrapped shouldBe testProps
  }

  it should "test isElement" in {
    //given
    val testCompClass = React.createClass[Unit, Unit](_ => E.div()("Hello"))
    val testElem = React.createElement(testCompClass)
    val tree = renderIntoDocument(testElem)
    val div = findRenderedDOMComponentWithTag(tree, "div")

    //when & then
    isElement(testElem) shouldBe true

    //when & then
    isElement(div) shouldBe false
  }

  it should "test isElementOfType" in {
    //given
    val testCompClass = React.createClass[Unit, Unit](_ => E.div()("Hello"))
    val testCompClass2 = React.createClass[Unit, Unit](_ => E.div()("Hello2"))
    val testElem = React.createElement(testCompClass)

    //when & then
    isElementOfType(testElem, testCompClass) shouldBe true

    //when & then
    isElementOfType(testElem, testCompClass2) shouldBe false
  }

  it should "test isDOMComponent" in {
    //given
    val testCompClass = React.createClass[Unit, Unit](_ => E.div()("Hello"))
    val testElem = React.createElement(testCompClass)
    val tree = renderIntoDocument(testElem)
    val div = findRenderedDOMComponentWithTag(tree, "div")

    //when & then
    isDOMComponent(div) shouldBe true

    //when & then
    isDOMComponent(tree) shouldBe false
  }

  it should "test isCompositeComponent" in {
    //given
    val testCompClass = React.createClass[Unit, Unit](_ => E.div()("Hello"))
    val testElem = React.createElement(testCompClass)
    val tree = renderIntoDocument(testElem)
    val div = findRenderedDOMComponentWithTag(tree, "div")

    //when & then
    isCompositeComponent(tree) shouldBe true

    //when & then
    isCompositeComponent(div) shouldBe false
  }

  it should "test isCompositeComponentWithType" in {
    //given
    val testCompClass = React.createClass[Unit, Unit](_ => E.div()("Hello"))
    val testCompClass2 = React.createClass[Unit, Unit](_ => E.div()("Hello2"))
    val tree = renderIntoDocument(React.createElement(testCompClass))

    //when & then
    isCompositeComponentWithType(tree, testCompClass) shouldBe true

    //when & then
    isCompositeComponentWithType(tree, testCompClass2) shouldBe false
  }

  it should "test findAllInRenderedTree" in {
    //given
    val testCompClass = React.createClass[Unit, Unit](_ => E.div()(E.span()("Hello")))
    val tree = renderIntoDocument(React.createElement(testCompClass))

    //when & then
    findAllInRenderedTree(tree, isDOMComponent(_)).length shouldBe 2

    //when & then
    findAllInRenderedTree(tree, isCompositeComponentWithType(_, testCompClass)).length shouldBe 1

    //when & then
    findAllInRenderedTree(tree, _ => false).length shouldBe 0
  }

  it should "test scryRenderedDOMComponentsWithClass" in {
    //given
    val tree = renderIntoDocument(React.createElement(React.createClass[Unit, Unit](_ =>
      E.div(A.className := "test")(
        E.div(A.className := "test2")(),
        E.span(A.className := "test")("Hello")
      )
    )))

    //when & then
    scryRenderedDOMComponentsWithClass(tree, "test").length shouldBe 2

    //when & then
    scryRenderedDOMComponentsWithClass(tree, "test2").length shouldBe 1

    //when & then
    scryRenderedDOMComponentsWithClass(tree, "test3").length shouldBe 0
  }

  it should "test findRenderedDOMComponentWithClass" in {
    //given
    val tree = renderIntoDocument(React.createElement(React.createClass[Unit, Unit](_ =>
      E.div(A.className := "test")(
        E.div(A.id := "2", A.className := "test2")(),
        E.span(A.className := "test")("Hello")
      )
    )))

    //when & then
    findRenderedDOMComponentWithClass(tree, "test2").id shouldBe "2"

    //when & then
    (the [JavaScriptException] thrownBy {
      findRenderedDOMComponentWithClass(tree, "test")
    }).getMessage should include("Error: Did not find exactly one match (found: 2) for class:test")

    //when & then
    (the [JavaScriptException] thrownBy {
      findRenderedDOMComponentWithClass(tree, "test3")
    }).getMessage should include("Error: Did not find exactly one match (found: 0) for class:test3")
  }

  it should "test scryRenderedDOMComponentsWithTag" in {
    //given
    val tree = renderIntoDocument(React.createElement(React.createClass[Unit, Unit](_ =>
      E.div()(
        E.div.empty,
        E.span()("Hello")
      )
    )))

    //when & then
    scryRenderedDOMComponentsWithTag(tree, "div").length shouldBe 2

    //when & then
    scryRenderedDOMComponentsWithTag(tree, "span").length shouldBe 1

    //when & then
    scryRenderedDOMComponentsWithTag(tree, "p").length shouldBe 0
  }

  it should "test findRenderedDOMComponentWithTag" in {
    //given
    val testId = "test-id"
    val testClassName = "test-class"
    val testProps = TestProps("this is a test")
    val testCompClass = React.createClass[TestProps, Unit] { (self) =>
      E.div()("div 1",
        E.div()("div 2"),
        E.p(
          A.id := testId,
          A.className := testClassName)(
          s"Hello, ${self.props.wrapped.test}!"
        )
      )
    }
    val tree = renderIntoDocument(React.createElement(testCompClass, React.wrap(testProps)))

    //when & then
    val result = findRenderedDOMComponentWithTag(tree, "p")
    result.id shouldBe testId
    result.className shouldBe testClassName
    result.innerHTML shouldBe s"Hello, ${testProps.test}!"

    //when & then
    (the [JavaScriptException] thrownBy {
      findRenderedDOMComponentWithTag(tree, "div")
    }).getMessage should include("Error: Did not find exactly one match (found: 2) for tag:div")

    //when & then
    (the [JavaScriptException] thrownBy {
      findRenderedDOMComponentWithTag(tree, "span")
    }).getMessage should include("Error: Did not find exactly one match (found: 0) for tag:span")
  }

  it should "test scryRenderedComponentsWithType" in {
    //given
    val testCompClass = React.createClass[Unit, Unit](self => E.div()(self.props.children))
    val testCompClass2 = React.createClass[Unit, Unit](_ => E.div()("Hello2"))
    val testCompClass3 = React.createClass[Unit, Unit](_ => E.div()("Hello3"))
    val tree = renderIntoDocument(React.createElement(React.createClass[Unit, Unit](_ =>
      E(testCompClass)()(
        E(testCompClass2).empty,
        E(testCompClass2).empty
      )
    )))

    //when & then
    scryRenderedComponentsWithType(tree, testCompClass2).length shouldBe 2

    //when & then
    scryRenderedComponentsWithType(tree, testCompClass).length shouldBe 1

    //when & then
    scryRenderedComponentsWithType(tree, testCompClass3).length shouldBe 0
  }

  it should "test findRenderedComponentWithType" in {
    //given
    val testCompClass = React.createClass[Unit, Unit](self => E.div()(self.props.children))
    val testCompClass2 = React.createClass[Unit, Unit](_ => E.div()("Hello2"))
    val testCompClass3 = React.createClass[Unit, Unit](_ => E.div()("Hello3"))
    val tree = renderIntoDocument(React.createElement(React.createClass[Unit, Unit](_ =>
      E(testCompClass)(A.id := "2")(
        E(testCompClass2).empty,
        E(testCompClass2).empty
      )
    )))

    //when & then
    findRenderedComponentWithType(tree, testCompClass).props.id shouldBe "2"

    //when & then
    (the [JavaScriptException] thrownBy {
      findRenderedComponentWithType(tree, testCompClass2)
    }).getMessage should include("Error: Did not find exactly one match (found: 2) for componentType:function")

    //when & then
    (the [JavaScriptException] thrownBy {
      findRenderedComponentWithType(tree, testCompClass3)
    }).getMessage should include("Error: Did not find exactly one match (found: 0) for componentType:function")
  }
}

object ReactTestUtilsSpec {

  case class TestProps(test: String)
}
