package scommons.client.test.util

import io.github.shogowada.scalajs.reactjs.React
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.classes.ReactClass
import org.scalatest.{Failed, OutcomeOf}
import scommons.client.test.TestSpec
import scommons.client.test.util.ShallowRendererUtilsSpec._
import scommons.react.UiComponent

class ShallowRendererUtilsSpec extends TestSpec
  with OutcomeOf {

  it should "return renderer when createRenderer" in {
    //when
    val renderer = createRenderer()

    //then
    renderer.render(<(TestComp())(^.wrapped := Comp1Props(1))())

    assertDOMComponent(renderer.getRenderOutput(), <.p(^.className := "test1")(), { case List(child) =>
      child shouldBe "test1 child"
    })
  }

  it should "not find component when findComponents" in {
    //given
    val comp = shallowRender(<(TestComp())(^.wrapped := Comp1Props(123))())

    //when & then
    findComponents(comp, TestComp()) shouldBe Nil
    findComponents(comp, comp2Class) shouldBe Nil
  }

  it should "find all components when findComponents" in {
    //given
    val comp = shallowRender(<(comp2Class)(^.wrapped := Comp2Props(true))())

    //when
    val results = findComponents(comp, TestComp())

    //then
    results.map(getComponentProps[Comp1Props]) shouldBe List(Comp1Props(1), Comp1Props(2))
  }

  it should "fail if non-empty when assertComponent" in {
    //given
    val comp = shallowRender(<(comp2Class)(^.wrapped := Comp2Props(true))())

    //when
    assertDOMComponent(comp, <.div(^.className := "test2")(), { case List(comp1, _) =>
      val Failed(e) = outcomeOf {
        assertComponent(comp1, TestComp) { props: Comp1Props =>
          props shouldBe Comp1Props(1)
        }
      }

      //then
      e.getMessage should include ("""List("test2 child1") was not equal to List()""")
    })
  }

  it should "assert props and children when assertComponent" in {
    //given
    val comp = shallowRender(<(comp2Class)(^.wrapped := Comp2Props(true))())

    //when & then
    assertDOMComponent(comp, <.div(^.className := "test2")(), { case List(comp1, comp2) =>
      assertComponent(comp1, TestComp)({ props =>
        props shouldBe Comp1Props(1)
      }, { case List(child) =>
        child shouldBe "test2 child1"
      })

      assertComponent(comp2, TestComp)({ props =>
        props shouldBe Comp1Props(2)
      }, { case List(child) =>
        child shouldBe "test2 child2"
      })
    })
  }

  it should "fail if non-empty when assertDOMComponent" in {
    //given
    val comp = shallowRender(<(TestComp())(^.wrapped := Comp1Props(1))())

    //when
    val Failed(e) = outcomeOf {
      assertDOMComponent(comp, <.p(^.className := "test1")())
    }

    //then
    e.getMessage should include ("""List("test1 child") was not equal to List()""")
  }

  it should "assert props and children when assertDOMComponent" in {
    //given
    val id = System.currentTimeMillis().toString
    val compClass = React.createClass[Unit, Unit] { _ =>
      <.div(
        ^.className := "test1 test2",
        ^.style := Map("display" -> "none"),
        ^.id := id,
        ^.hidden := true,
        ^.height := 10
      )(
        <.div()("child1"),
        <.div()("child2")
      )
    }
    val comp = shallowRender(<(compClass)()())

    //when & then
    assertDOMComponent(comp, <.div(
      ^.className := "test1 test2",
      ^.style := Map("display" -> "none"),
      ^.id := id,
      ^.hidden := true,
      ^.height := 10
    )(
      <.div()("child1"),
      <.div()("child2")
    ))
  }
}

object ShallowRendererUtilsSpec {

  case class Comp1Props(a: Int)
  case class Comp2Props(b: Boolean)

  object TestComp extends UiComponent[Comp1Props] {

    protected def create(): ReactClass = React.createClass[Comp1Props, Unit] { _ =>
      <.p(^.className := "test1")("test1 child")
    }
  }

  private val comp2Class = React.createClass[Comp2Props, Unit] { _ =>
    <.div(^.className := "test2")(
      <(TestComp())(^.wrapped := Comp1Props(1))("test2 child1"),
      <(TestComp())(^.wrapped := Comp1Props(2))("test2 child2")
    )
  }
}
